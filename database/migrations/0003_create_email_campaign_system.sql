-- migration:      0003
-- description:    create email campaign and scheduling system tables
-- author:         Antigravity
-- date:           2026-07-11
-- depends_on:     0002
-- reversible:     YES
-- lock_risk:      LOW
-- rows_affected:  schema only

-- ============================================================
-- 1. Multi-tenancy
-- ============================================================

CREATE TABLE organizations (
    id              BIGINT PRIMARY KEY,
    name            TEXT NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE org_users (
    id              BIGINT PRIMARY KEY,
    org_id          BIGINT NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    user_id         BIGINT NOT NULL REFERENCES app_users(id) ON DELETE CASCADE,
    role            TEXT NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE api_keys (
    id              BIGINT PRIMARY KEY,
    org_id          BIGINT NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    name            TEXT NOT NULL,
    key_hash        TEXT NOT NULL,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    expires_at      TIMESTAMPTZ
);

CREATE TABLE audit_logs (
    id              BIGINT NOT NULL,
    org_id          BIGINT NOT NULL,
    user_id         BIGINT,
    action          TEXT NOT NULL,
    target_type     TEXT NOT NULL,
    target_id       BIGINT,
    changes         JSONB,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (created_at, id)
) PARTITION BY RANGE (created_at);

CREATE TABLE audit_logs_2026_07 PARTITION OF audit_logs FOR VALUES FROM ('2026-07-01 00:00:00+00') TO ('2026-08-01 00:00:00+00');
CREATE TABLE audit_logs_2026_08 PARTITION OF audit_logs FOR VALUES FROM ('2026-08-01 00:00:00+00') TO ('2026-09-01 00:00:00+00');
CREATE TABLE audit_logs_2026_09 PARTITION OF audit_logs FOR VALUES FROM ('2026-09-01 00:00:00+00') TO ('2026-10-01 00:00:00+00');
CREATE TABLE audit_logs_2026_10 PARTITION OF audit_logs FOR VALUES FROM ('2026-10-01 00:00:00+00') TO ('2026-11-01 00:00:00+00');
CREATE TABLE audit_logs_2026_11 PARTITION OF audit_logs FOR VALUES FROM ('2026-11-01 00:00:00+00') TO ('2026-12-01 00:00:00+00');
CREATE TABLE audit_logs_2026_12 PARTITION OF audit_logs FOR VALUES FROM ('2026-12-01 00:00:00+00') TO ('2027-01-01 00:00:00+00');

-- ============================================================
-- 2. Recipients
-- ============================================================

CREATE TABLE contact_lists (
    id              BIGINT PRIMARY KEY,
    org_id          BIGINT NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    name            TEXT NOT NULL,
    description     TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE contact_list_members (
    contact_list_id BIGINT NOT NULL REFERENCES contact_lists(id) ON DELETE CASCADE,
    contact_id      BIGINT NOT NULL REFERENCES contacts(id) ON DELETE CASCADE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (contact_list_id, contact_id)
);

CREATE TABLE segments (
    id              BIGINT PRIMARY KEY,
    org_id          BIGINT NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    name            TEXT NOT NULL,
    query_definition JSONB NOT NULL DEFAULT '{}',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE suppressions (
    id              BIGINT PRIMARY KEY,
    org_id          BIGINT NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    email           TEXT NOT NULL,
    reason          TEXT NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE consent_records (
    id              BIGINT PRIMARY KEY,
    contact_id      BIGINT NOT NULL REFERENCES contacts(id) ON DELETE CASCADE,
    status          TEXT NOT NULL,
    channel         TEXT NOT NULL,
    source          TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ============================================================
-- 3. Templates
-- ============================================================

CREATE TABLE email_templates (
    id              BIGINT PRIMARY KEY,
    org_id          BIGINT NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    name            TEXT NOT NULL,
    locale          TEXT NOT NULL DEFAULT 'en',
    version         INT NOT NULL DEFAULT 1,
    subject_tmpl    TEXT NOT NULL,
    body_tmpl       TEXT NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ============================================================
-- 4. Campaigns & Workflows
-- ============================================================

CREATE TABLE campaigns (
    id              BIGINT PRIMARY KEY,
    org_id          BIGINT NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    name            TEXT NOT NULL,
    status          TEXT NOT NULL DEFAULT 'draft',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE campaign_steps (
    id              BIGINT PRIMARY KEY,
    campaign_id     BIGINT NOT NULL REFERENCES campaigns(id) ON DELETE CASCADE,
    step_order      INT NOT NULL,
    type            TEXT NOT NULL,
    config          JSONB NOT NULL DEFAULT '{}',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE workflows (
    id              BIGINT PRIMARY KEY,
    org_id          BIGINT NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    name            TEXT NOT NULL,
    status          TEXT NOT NULL DEFAULT 'draft',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

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

-- ============================================================
-- 5. Scheduling
-- ============================================================

CREATE TABLE recurrence_rules (
    id              BIGINT PRIMARY KEY,
    campaign_id     BIGINT NOT NULL REFERENCES campaigns(id) ON DELETE CASCADE,
    frequency       TEXT NOT NULL,
    interval        INT NOT NULL DEFAULT 1,
    byday           TEXT,
    until           TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE scheduled_emails (
    id              BIGINT PRIMARY KEY,
    org_id          BIGINT NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    campaign_id     BIGINT REFERENCES campaigns(id) ON DELETE SET NULL,
    contact_id      BIGINT NOT NULL REFERENCES contacts(id) ON DELETE CASCADE,
    template_id     BIGINT REFERENCES email_templates(id) ON DELETE SET NULL,
    scheduled_at    TIMESTAMPTZ NOT NULL,
    status          TEXT NOT NULL DEFAULT 'pending',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ============================================================
-- 6. Delivery
-- ============================================================

CREATE TABLE provider_accounts (
    id              BIGINT PRIMARY KEY,
    org_id          BIGINT NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    provider_name   TEXT NOT NULL,
    credentials     JSONB NOT NULL DEFAULT '{}',
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE send_queue (
    id                  BIGINT PRIMARY KEY,
    org_id              BIGINT NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    scheduled_email_id  BIGINT REFERENCES scheduled_emails(id) ON DELETE SET NULL,
    status              TEXT NOT NULL DEFAULT 'queued',
    attempt_count       INT NOT NULL DEFAULT 0,
    next_attempt_at     TIMESTAMPTZ NOT NULL,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE email_deliveries (
    id                  BIGINT PRIMARY KEY,
    org_id              BIGINT NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    provider_account_id BIGINT REFERENCES provider_accounts(id) ON DELETE SET NULL,
    campaign_id         BIGINT REFERENCES campaigns(id) ON DELETE SET NULL,
    contact_id          BIGINT REFERENCES contacts(id) ON DELETE SET NULL,
    email               TEXT NOT NULL,
    status              TEXT NOT NULL,
    provider_message_id TEXT,
    sent_at             TIMESTAMPTZ,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ============================================================
-- 7. Deliverability
-- ============================================================

CREATE TABLE sending_domains (
    id                  BIGINT PRIMARY KEY,
    org_id              BIGINT NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    domain_name         TEXT NOT NULL,
    verification_status TEXT NOT NULL,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE domain_reputation_snapshots (
    id                  BIGINT PRIMARY KEY,
    sending_domain_id   BIGINT NOT NULL REFERENCES sending_domains(id) ON DELETE CASCADE,
    reputation_score    NUMERIC(5,2) NOT NULL,
    recorded_at         TIMESTAMPTZ NOT NULL,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ============================================================
-- 8. Engagement
-- ============================================================

CREATE TABLE email_events (
    id                  BIGINT PRIMARY KEY,
    email_delivery_id   BIGINT NOT NULL REFERENCES email_deliveries(id) ON DELETE CASCADE,
    event_type          TEXT NOT NULL,
    metadata            JSONB NOT NULL DEFAULT '{}',
    occurred_at         TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE ab_tests (
    id                  BIGINT PRIMARY KEY,
    campaign_id         BIGINT NOT NULL REFERENCES campaigns(id) ON DELETE CASCADE,
    name                TEXT NOT NULL,
    status              TEXT NOT NULL DEFAULT 'pending',
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE ab_test_variants (
    id                  BIGINT PRIMARY KEY,
    ab_test_id          BIGINT NOT NULL REFERENCES ab_tests(id) ON DELETE CASCADE,
    name                TEXT NOT NULL,
    template_id         BIGINT REFERENCES email_templates(id) ON DELETE SET NULL,
    weight              NUMERIC(5,2) NOT NULL,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ============================================================
-- 9. Webhooks
-- ============================================================

CREATE TABLE webhook_subscriptions (
    id                  BIGINT PRIMARY KEY,
    org_id              BIGINT NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    url                 TEXT NOT NULL,
    events              TEXT[] NOT NULL DEFAULT '{}',
    is_active           BOOLEAN NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE webhook_deliveries (
    id                  BIGINT PRIMARY KEY,
    subscription_id     BIGINT NOT NULL REFERENCES webhook_subscriptions(id) ON DELETE CASCADE,
    event_type          TEXT NOT NULL,
    payload             JSONB NOT NULL DEFAULT '{}',
    status_code         INT,
    delivered_at        TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ============================================================
-- 10. Gaps (Remaining requested schema tables)
-- ============================================================

CREATE TABLE attachments (
    id                  BIGINT PRIMARY KEY,
    campaign_id         BIGINT NOT NULL REFERENCES campaigns(id) ON DELETE CASCADE,
    file_name           TEXT NOT NULL,
    file_url            TEXT NOT NULL,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE campaign_approvals (
    id                  BIGINT PRIMARY KEY,
    campaign_id         BIGINT NOT NULL REFERENCES campaigns(id) ON DELETE CASCADE,
    approver_id         BIGINT NOT NULL REFERENCES app_users(id) ON DELETE CASCADE,
    status              TEXT NOT NULL,
    comments            TEXT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE comments (
    id                  BIGINT PRIMARY KEY,
    campaign_id         BIGINT NOT NULL REFERENCES campaigns(id) ON DELETE CASCADE,
    author_id           BIGINT NOT NULL REFERENCES app_users(id) ON DELETE CASCADE,
    content             TEXT NOT NULL,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE date_triggers (
    id                  BIGINT PRIMARY KEY,
    workflow_id         BIGINT NOT NULL REFERENCES workflows(id) ON DELETE CASCADE,
    trigger_date        TIMESTAMPTZ NOT NULL,
    node_id             BIGINT NOT NULL REFERENCES workflow_nodes(id) ON DELETE CASCADE,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE business_hours (
    id                  BIGINT PRIMARY KEY,
    org_id              BIGINT NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    name                TEXT NOT NULL,
    timezone            TEXT NOT NULL DEFAULT 'UTC',
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE business_hours_slots (
    id                  BIGINT PRIMARY KEY,
    business_hours_id   BIGINT NOT NULL REFERENCES business_hours(id) ON DELETE CASCADE,
    day_of_week         INT NOT NULL,
    start_time          TIME NOT NULL,
    end_time            TIME NOT NULL
);

CREATE TABLE holiday_calendars (
    id                  BIGINT PRIMARY KEY,
    org_id              BIGINT NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    name                TEXT NOT NULL,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE holidays (
    id                  BIGINT PRIMARY KEY,
    holiday_calendar_id BIGINT NOT NULL REFERENCES holiday_calendars(id) ON DELETE CASCADE,
    holiday_date        DATE NOT NULL,
    name                TEXT NOT NULL,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE rate_limit_buckets (
    id                  BIGINT PRIMARY KEY,
    org_id              BIGINT NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    rate_limit          INT NOT NULL,
    window_seconds      INT NOT NULL,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE integration_connections (
    id                  BIGINT PRIMARY KEY,
    org_id              BIGINT NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    system_name         TEXT NOT NULL,
    credentials         JSONB NOT NULL DEFAULT '{}',
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE subscriptions (
    id                  BIGINT PRIMARY KEY,
    contact_id          BIGINT NOT NULL REFERENCES contacts(id) ON DELETE CASCADE,
    list_id             BIGINT NOT NULL REFERENCES contact_lists(id) ON DELETE CASCADE,
    status              TEXT NOT NULL,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE api_usage_logs (
    id                  BIGINT PRIMARY KEY,
    api_key_id          BIGINT NOT NULL REFERENCES api_keys(id) ON DELETE CASCADE,
    endpoint            TEXT NOT NULL,
    status_code         INT NOT NULL,
    occurred_at         TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE ip_allowlist (
    id                  BIGINT PRIMARY KEY,
    org_id              BIGINT NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    ip_address          TEXT NOT NULL,
    is_active           BOOLEAN NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE retention_policies (
    id                  BIGINT PRIMARY KEY,
    org_id              BIGINT NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    data_type           TEXT NOT NULL,
    retention_days      INT NOT NULL,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE send_time_predictions (
    id                  BIGINT PRIMARY KEY,
    contact_id          BIGINT NOT NULL REFERENCES contacts(id) ON DELETE CASCADE,
    predicted_hour      INT NOT NULL,
    confidence          NUMERIC(5,2) NOT NULL,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE template_shares (
    id                  BIGINT PRIMARY KEY,
    template_id         BIGINT NOT NULL REFERENCES email_templates(id) ON DELETE CASCADE,
    shared_with_org_id  BIGINT NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE spam_check_results (
    id                  BIGINT PRIMARY KEY,
    template_id         BIGINT NOT NULL REFERENCES email_templates(id) ON DELETE CASCADE,
    score               NUMERIC(5,2) NOT NULL,
    rules_triggered     JSONB NOT NULL DEFAULT '{}',
    checked_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE calendar_events (
    id                  BIGINT PRIMARY KEY,
    org_id              BIGINT NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    name                TEXT NOT NULL,
    start_time          TIMESTAMPTZ NOT NULL,
    end_time            TIMESTAMPTZ NOT NULL,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE campaign_exclusions (
    id                      BIGINT PRIMARY KEY,
    campaign_id             BIGINT NOT NULL REFERENCES campaigns(id) ON DELETE CASCADE,
    excluded_contact_list_id BIGINT NOT NULL REFERENCES contact_lists(id) ON DELETE CASCADE,
    created_at              TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE scheduling_batches (
    id                  BIGINT PRIMARY KEY,
    org_id              BIGINT NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    status              TEXT NOT NULL,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE report_exports (
    id                  BIGINT PRIMARY KEY,
    org_id              BIGINT NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    report_type         TEXT NOT NULL,
    status              TEXT NOT NULL,
    file_url            TEXT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ============================================================
-- 11. Views
-- ============================================================

CREATE VIEW link_click_stats AS
SELECT 
    ee.id,
    ee.email_delivery_id,
    ed.campaign_id,
    ee.metadata->>'link_url' AS link_url,
    ee.occurred_at
FROM email_events ee
JOIN email_deliveries ed ON ee.email_delivery_id = ed.id
WHERE ee.event_type = 'clicked';

CREATE VIEW campaign_performance AS
SELECT 
    c.id AS campaign_id,
    c.name AS campaign_name,
    COUNT(DISTINCT ed.id) AS total_sent,
    COUNT(DISTINCT CASE WHEN ee.event_type = 'opened' THEN ee.email_delivery_id END) AS total_opened,
    COUNT(DISTINCT CASE WHEN ee.event_type = 'clicked' THEN ee.email_delivery_id END) AS total_clicked
FROM campaigns c
LEFT JOIN email_deliveries ed ON c.id = ed.campaign_id
LEFT JOIN email_events ee ON ed.id = ee.email_delivery_id
GROUP BY c.id, c.name;
