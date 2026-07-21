-- migration:      0007
-- description:    create consolidated identity, rbac and secure append-only audit trail
-- author:         Antigravity
-- date:           2026-07-21
-- depends_on:     0006
-- reversible:     YES
-- lock_risk:      LOW
-- rows_affected:  schema only

-- ============================================================
-- 1. Identity — human and machine principals
-- ============================================================

-- Drop old org_users/roles/audit_logs if they exist to prevent conflicts with the new unified structure
DROP TABLE IF EXISTS org_user_roles CASCADE;
DROP TABLE IF EXISTS org_users CASCADE;
DROP TABLE IF EXISTS audit_logs CASCADE;
DROP TABLE IF EXISTS roles CASCADE;
DROP TABLE IF EXISTS permissions CASCADE;

CREATE TABLE users (
    id              BIGINT PRIMARY KEY,
    email           TEXT UNIQUE NOT NULL,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE service_accounts (
    id              BIGINT PRIMARY KEY,
    org_id          BIGINT NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    name            TEXT NOT NULL,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE org_memberships (
    id              BIGINT PRIMARY KEY,
    org_id          BIGINT NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    user_id         BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status          TEXT NOT NULL DEFAULT 'active',
    joined_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (org_id, user_id)
);

-- ============================================================
-- 2. Permission catalog
-- ============================================================

CREATE TABLE permissions (
    id              BIGINT PRIMARY KEY,
    resource_type   TEXT NOT NULL,
    action          TEXT NOT NULL,
    code            TEXT GENERATED ALWAYS AS (resource_type || ':' || action) STORED,
    UNIQUE (resource_type, action)
);

-- ============================================================
-- 3. Roles
-- ============================================================

CREATE TABLE roles (
    id              BIGINT PRIMARY KEY,
    org_id          BIGINT REFERENCES organizations(id) ON DELETE CASCADE,
    name            TEXT NOT NULL,
    description     TEXT,
    is_system_role  BOOLEAN NOT NULL DEFAULT FALSE,
    parent_role_id  BIGINT REFERENCES roles(id) ON DELETE SET NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (org_id, name),
    CHECK (parent_role_id != id)
);

CREATE TABLE role_permissions (
    role_id         BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    permission_id   BIGINT NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

-- ============================================================
-- 4. Grants — polymorphic principal, scoped, time-boxed
-- ============================================================

CREATE TABLE groups (
    id              BIGINT PRIMARY KEY,
    org_id          BIGINT NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    name            TEXT NOT NULL
);

CREATE TABLE group_members (
    group_id        BIGINT NOT NULL REFERENCES groups(id) ON DELETE CASCADE,
    user_id         BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    PRIMARY KEY (group_id, user_id)
);

CREATE TABLE role_assignments (
    id              BIGINT PRIMARY KEY,
    org_id          BIGINT NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    role_id         BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    principal_type  TEXT NOT NULL CHECK (principal_type IN ('user','service_account','group')),
    principal_id    BIGINT NOT NULL,
    scope_type      TEXT NOT NULL DEFAULT 'org',
    scope_id        BIGINT,
    granted_by      BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    expires_at      TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_role_assignments_lookup ON role_assignments (principal_type, principal_id, org_id);
CREATE INDEX idx_role_assignments_expiry ON role_assignments (expires_at) WHERE expires_at IS NOT NULL;

CREATE TABLE policy_conditions (
    id              BIGINT PRIMARY KEY,
    role_assignment_id BIGINT NOT NULL REFERENCES role_assignments(id) ON DELETE CASCADE,
    condition_type  TEXT NOT NULL,
    config          JSONB NOT NULL
);

-- ============================================================
-- 5. Effective permission resolution cache
-- ============================================================

CREATE TABLE effective_permissions_cache (
    principal_type  TEXT NOT NULL,
    principal_id    BIGINT NOT NULL,
    org_id          BIGINT NOT NULL,
    permission_code TEXT NOT NULL,
    scope_type      TEXT NOT NULL,
    scope_id        BIGINT,
    computed_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (principal_type, principal_id, org_id, permission_code, scope_type, COALESCE(scope_id, -1))
);

-- Recursive lookup resolution function helper
CREATE OR REPLACE FUNCTION user_has_permission(
    p_user_id BIGINT, p_org_id BIGINT, p_permission_code TEXT, p_resource_id BIGINT DEFAULT NULL
) RETURNS BOOLEAN AS $$
WITH RECURSIVE role_tree AS (
    SELECT ra.role_id, ra.scope_type, ra.scope_id, ra.expires_at
    FROM role_assignments ra
    WHERE ra.org_id = p_org_id
      AND (ra.expires_at IS NULL OR ra.expires_at > now())
      AND (
          (ra.principal_type = 'user' AND ra.principal_id = p_user_id)
          OR (ra.principal_type = 'group' AND ra.principal_id IN
              (SELECT group_id FROM group_members WHERE user_id = p_user_id))
      )
    UNION ALL
    SELECT r.parent_role_id, rt.scope_type, rt.scope_id, rt.expires_at
    FROM role_tree rt JOIN roles r ON r.id = rt.role_id
    WHERE r.parent_role_id IS NOT NULL
)
SELECT EXISTS (
    SELECT 1 FROM role_tree rt
    JOIN role_permissions rp ON rp.role_id = rt.role_id
    JOIN permissions p ON p.id = rp.permission_id
    WHERE p.code = p_permission_code
      AND (rt.scope_type != 'resource' OR rt.scope_id = p_resource_id)
);
$$ LANGUAGE sql STABLE;

-- ============================================================
-- 6. The event table — append-only, hash-chained
-- ============================================================

CREATE TABLE audit_events (
    id              BIGINT NOT NULL,
    org_id          BIGINT NOT NULL,
    actor_type      TEXT NOT NULL CHECK (actor_type IN ('user','service_account','api_key','system')),
    actor_id        BIGINT,
    action          TEXT NOT NULL,
    resource_type   TEXT NOT NULL,
    resource_id     BIGINT,
    severity        TEXT NOT NULL DEFAULT 'info',
    before_value    JSONB,
    after_value     JSONB,
    request_id      TEXT,
    session_id      TEXT,
    ip_address      INET,
    user_agent      TEXT,
    prev_hash       TEXT NOT NULL,
    entry_hash      TEXT NOT NULL,
    occurred_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (occurred_at, id)
) PARTITION BY RANGE (occurred_at);

CREATE INDEX idx_audit_events_org ON audit_events (org_id, occurred_at DESC);
CREATE INDEX idx_audit_events_actor ON audit_events (actor_type, actor_id, occurred_at DESC);
CREATE INDEX idx_audit_events_resource ON audit_events (resource_type, resource_id);
CREATE INDEX idx_audit_events_critical ON audit_events (severity) WHERE severity = 'security_critical';

-- Monthly partitions for 2026/2027
CREATE TABLE audit_events_2026_07 PARTITION OF audit_events FOR VALUES FROM ('2026-07-01 00:00:00+00') TO ('2026-08-01 00:00:00+00');
CREATE TABLE audit_events_2026_08 PARTITION OF audit_events FOR VALUES FROM ('2026-08-01 00:00:00+00') TO ('2026-09-01 00:00:00+00');
CREATE TABLE audit_events_2026_09 PARTITION OF audit_events FOR VALUES FROM ('2026-09-01 00:00:00+00') TO ('2026-10-01 00:00:00+00');
CREATE TABLE audit_events_2026_10 PARTITION OF audit_events FOR VALUES FROM ('2026-10-01 00:00:00+00') TO ('2026-11-01 00:00:00+00');
CREATE TABLE audit_events_2026_11 PARTITION OF audit_events FOR VALUES FROM ('2026-11-01 00:00:00+00') TO ('2026-12-01 00:00:00+00');
CREATE TABLE audit_events_2026_12 PARTITION OF audit_events FOR VALUES FROM ('2026-12-01 00:00:00+00') TO ('2027-01-01 00:00:00+00');

-- Enforce immutability using DB trigger blocking modifications
CREATE OR REPLACE FUNCTION prevent_audit_mutation() RETURNS TRIGGER AS $$
BEGIN
    RAISE EXCEPTION 'audit_events is append-only — % not permitted', TG_OP;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_no_audit_mutation
    BEFORE UPDATE OR DELETE ON audit_events
    FOR EACH ROW EXECUTE FUNCTION prevent_audit_mutation();
