-- migration:      0004
-- description:    rollback job scheduler and workflow automation system tables
-- author:         Antigravity
-- date:           2026-07-11
-- depends_on:     0003
-- reversible:     YES

DROP TABLE IF EXISTS trigger_events CASCADE;
DROP TABLE IF EXISTS triggers CASCADE;
DROP TABLE IF EXISTS workflow_approvals CASCADE;
DROP TABLE IF EXISTS workflow_step_runs CASCADE;
DROP TABLE IF EXISTS workflow_runs CASCADE;
DROP TABLE IF EXISTS workflow_edges CASCADE;
DROP TABLE IF EXISTS workflow_steps CASCADE;

-- Recreate the original tables dropped from 0003 for compatibility
CREATE TABLE workflow_nodes (
    id              BIGINT PRIMARY KEY,
    workflow_id     BIGINT NOT NULL REFERENCES workflows(id) ON DELETE CASCADE,
    type            TEXT NOT NULL,
    config          JSONB NOT NULL DEFAULT '{}',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE workflow_edges (
    id              BIGINT PRIMARY KEY,
    workflow_id     BIGINT NOT NULL REFERENCES workflows(id) ON DELETE CASCADE,
    from_node_id    BIGINT NOT NULL REFERENCES workflow_nodes(id) ON DELETE CASCADE,
    to_node_id      BIGINT NOT NULL REFERENCES workflow_nodes(id) ON DELETE CASCADE,
    condition       JSONB NOT NULL DEFAULT '{}',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE workflow_runs (
    id              BIGINT PRIMARY KEY,
    workflow_id     BIGINT NOT NULL REFERENCES workflows(id) ON DELETE CASCADE,
    contact_id      BIGINT NOT NULL REFERENCES contacts(id) ON DELETE CASCADE,
    current_node_id BIGINT REFERENCES workflow_nodes(id) ON DELETE SET NULL,
    status          TEXT NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

DROP TABLE IF EXISTS job_dependencies CASCADE;
DROP TABLE IF EXISTS schedules CASCADE;
DROP TABLE IF EXISTS job_run_logs CASCADE;
DROP TABLE IF EXISTS job_runs CASCADE;
DROP TABLE IF EXISTS jobs CASCADE;
DROP TABLE IF EXISTS job_categories CASCADE;
DROP TABLE IF EXISTS job_type_definitions CASCADE;

DROP TABLE IF EXISTS inbound_webhook_endpoints CASCADE;
DROP TABLE IF EXISTS plugins CASCADE;
DROP TABLE IF EXISTS variables CASCADE;

DROP TABLE IF EXISTS system_backups CASCADE;
DROP TABLE IF EXISTS usage_limits CASCADE;
DROP TABLE IF EXISTS workspaces CASCADE;
DROP TABLE IF EXISTS team_members CASCADE;
DROP TABLE IF EXISTS teams CASCADE;
DROP TABLE IF EXISTS org_user_roles CASCADE;
DROP TABLE IF EXISTS role_permissions CASCADE;
DROP TABLE IF EXISTS permissions CASCADE;
DROP TABLE IF EXISTS roles CASCADE;
