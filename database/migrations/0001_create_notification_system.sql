-- migration:      0001
-- description:    create notification system schema and architecture with strict constraints and consistency guarantees
-- author:         Antigravity
-- date:           2026-07-09
-- depends_on:     
-- reversible:     YES
-- lock_risk:      LOW
-- rows_affected:  schema only
-- reason:         initial notification system tables for feature/notifications with strict database consistency checks
-- nullable_columns_rationale:
--   - notifications.dedup_key: Optional client-provided idempotency key
--   - notifications.target_id: Nullable if target_scope is 'broadcast' (targets all users)
--   - notifications.scheduled_for: Nullable for immediate delivery
--   - notifications.expires_at: Nullable for notifications that do not expire
--   - notifications.deleted_at: Nullable until soft-deleted
--   - notification_recipients.read_at: Nullable until user reads the notification
--   - notification_recipients.dismissed_at: Nullable until user dismisses the notification
--   - notification_recipients.deleted_at: Nullable until soft-deleted
--   - channel_deliveries.next_retry_at: Nullable if delivery is not pending retry
--   - channel_deliveries.provider_msg_id: Nullable until sent to external provider
--   - channel_deliveries.error_code: Nullable if no error occurs
--   - channel_deliveries.error_detail: Nullable if no error occurs
--   - channel_deliveries.sent_at: Nullable until successfully sent
--   - channel_deliveries.delivered_at: Nullable until delivery is confirmed by provider webhook
--   - channel_deliveries.deleted_at: Nullable until soft-deleted
--   - user_notification_preferences.quiet_hours: Nullable if quiet hours are not configured for preference
--   - user_notification_preferences.deleted_at: Nullable until soft-deleted
--   - user_channel_endpoints.deleted_at: Nullable until soft-deleted
--   - digest_batches.sent_at: Nullable until the batch is successfully processed and sent
--   - digest_batches.deleted_at: Nullable until soft-deleted
--   - devices.push_token: Nullable for platforms/configurations that use raw web push endpoint details
--   - devices.web_push_endpoint: Nullable for platforms using push tokens (iOS, Android)
--   - devices.app_version: Nullable if client does not provide app version
--   - devices.os_version: Nullable if client does not provide OS version
--   - devices.deleted_at: Nullable until soft-deleted
--   - routing_rules.escalation_policy_id: Nullable if rule does not trigger escalation
--   - routing_rules.deleted_at: Nullable until soft-deleted
--   - escalation_policies.deleted_at: Nullable until soft-deleted
--   - escalation_runs.acknowledged_at: Nullable until user acknowledges critical alert
--   - escalation_runs.deleted_at: Nullable until soft-deleted
--   - engagement_events.device_id: Nullable if event occurred on an unregistered device
--   - engagement_events.action_id: Nullable if event type is not 'action_taken'
--   - engagement_events.client_metadata: Nullable if client details are not provided
--   - engagement_events.deleted_at: Nullable until soft-deleted
--   - topics.deleted_at: Nullable until soft-deleted
--   - user_topic_subscriptions.deleted_at: Nullable until soft-deleted

-- ============================================================
-- 1. Reference / config tables
-- ============================================================

CREATE TABLE notification_types (
    id              SMALLSERIAL PRIMARY KEY,
    code            VARCHAR(100) UNIQUE NOT NULL,
    category        VARCHAR(50) NOT NULL,
    default_priority SMALLINT NOT NULL DEFAULT 3,
    default_channels VARCHAR(50)[] NOT NULL DEFAULT '{}',
    fan_out_mode    VARCHAR(20) NOT NULL DEFAULT 'write',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at      TIMESTAMPTZ,
    CONSTRAINT chk_notification_types_category CHECK (category IN ('transactional', 'broadcast', 'digest')),
    CONSTRAINT chk_notification_types_fan_out_mode CHECK (fan_out_mode IN ('write', 'read')),
    CONSTRAINT chk_notification_types_priority CHECK (default_priority BETWEEN 1 AND 5)
);

CREATE TABLE notification_templates (
    id              BIGSERIAL PRIMARY KEY,
    type_id         SMALLINT NOT NULL,
    channel         VARCHAR(50) NOT NULL,
    locale          VARCHAR(10) NOT NULL DEFAULT 'en',
    version         INT NOT NULL DEFAULT 1,
    subject_tmpl    TEXT NOT NULL,
    body_tmpl       TEXT NOT NULL,
    variables_schema JSONB NOT NULL DEFAULT '{}',
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at      TIMESTAMPTZ,
    CONSTRAINT fk_templates_type FOREIGN KEY (type_id) REFERENCES notification_types(id) ON DELETE RESTRICT,
    CONSTRAINT chk_templates_channel CHECK (channel IN ('email', 'push', 'sms', 'in_app', 'webhook')),
    CONSTRAINT uq_type_channel_locale_version UNIQUE (type_id, channel, locale, version)
);
CREATE INDEX idx_notification_templates_type_id ON notification_templates(type_id);

-- ============================================================
-- 2. Escalation Policies (defined early for routing references)
-- ============================================================

CREATE TABLE escalation_policies (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    steps           JSONB NOT NULL DEFAULT '[]',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at      TIMESTAMPTZ
);

-- ============================================================
-- 3. Routing Rules
-- ============================================================

CREATE TABLE routing_rules (
    id              BIGSERIAL PRIMARY KEY,
    type_id         SMALLINT NOT NULL,
    priority_order  INT NOT NULL DEFAULT 100,
    condition       JSONB NOT NULL DEFAULT '{}',
    channels        VARCHAR(50)[] NOT NULL,
    escalation_policy_id BIGINT,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at      TIMESTAMPTZ,
    CONSTRAINT fk_routing_rules_type FOREIGN KEY (type_id) REFERENCES notification_types(id) ON DELETE RESTRICT,
    CONSTRAINT fk_routing_rules_escalation FOREIGN KEY (escalation_policy_id) REFERENCES escalation_policies(id) ON DELETE RESTRICT
);
CREATE INDEX idx_routing_rules_type_id ON routing_rules(type_id);
CREATE INDEX idx_routing_rules_escalation ON routing_rules(escalation_policy_id);

-- ============================================================
-- 4. The notification itself — immutable, write-once, partitioned by range
-- ============================================================

CREATE TABLE notifications (
    id              BIGINT NOT NULL,
    type_id         SMALLINT NOT NULL,
    source_service  VARCHAR(100) NOT NULL,
    dedup_key       VARCHAR(255),
    payload         JSONB NOT NULL DEFAULT '{}',
    metadata        JSONB NOT NULL DEFAULT '{}',
    priority        SMALLINT NOT NULL DEFAULT 3,
    target_scope    VARCHAR(50) NOT NULL,
    target_id       BIGINT,
    global_seq      BIGINT NOT NULL,
    scheduled_for   TIMESTAMPTZ,
    expires_at      TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at      TIMESTAMPTZ,
    PRIMARY KEY (created_at, id),
    CONSTRAINT fk_notifications_type FOREIGN KEY (type_id) REFERENCES notification_types(id) ON DELETE RESTRICT,
    CONSTRAINT chk_notifications_priority CHECK (priority BETWEEN 1 AND 5),
    CONSTRAINT chk_notifications_scope CHECK (target_scope IN ('user', 'org', 'broadcast', 'segment')),
    -- Consistency check: target_id must be null for broadcast, but not null for user/org/segment
    CONSTRAINT chk_notifications_target_id CHECK (
        (target_scope = 'broadcast' AND target_id IS NULL) OR 
        (target_scope IN ('user', 'org', 'segment') AND target_id IS NOT NULL)
    )
) PARTITION BY RANGE (created_at);

-- Partitions
CREATE TABLE notifications_2026_07 PARTITION OF notifications
    FOR VALUES FROM ('2026-07-01 00:00:00+00') TO ('2026-08-01 00:00:00+00');
CREATE TABLE notifications_2026_08 PARTITION OF notifications
    FOR VALUES FROM ('2026-08-01 00:00:00+00') TO ('2026-09-01 00:00:00+00');
CREATE TABLE notifications_2026_09 PARTITION OF notifications
    FOR VALUES FROM ('2026-09-01 00:00:00+00') TO ('2026-10-01 00:00:00+00');
CREATE TABLE notifications_2026_10 PARTITION OF notifications
    FOR VALUES FROM ('2026-10-01 00:00:00+00') TO ('2026-11-01 00:00:00+00');
CREATE TABLE notifications_2026_11 PARTITION OF notifications
    FOR VALUES FROM ('2026-11-01 00:00:00+00') TO ('2026-12-01 00:00:00+00');
CREATE TABLE notifications_2026_12 PARTITION OF notifications
    FOR VALUES FROM ('2026-12-01 00:00:00+00') TO ('2027-01-01 00:00:00+00');

CREATE UNIQUE INDEX idx_notifications_dedup_key ON notifications (created_at, type_id, dedup_key) WHERE dedup_key IS NOT NULL;
CREATE INDEX idx_notifications_target ON notifications (target_scope, target_id, created_at DESC);
CREATE INDEX idx_notifications_group_key ON notifications ((metadata->>'group_key'));
CREATE SEQUENCE notification_global_seq;

-- ============================================================
-- 5. Targeted fan-out: hash partitioned by user_id
-- ============================================================

CREATE TABLE notification_recipients (
    id                      BIGSERIAL,
    notification_id         BIGINT NOT NULL,
    notification_created_at TIMESTAMPTZ NOT NULL,
    user_id                 BIGINT NOT NULL,
    status                  VARCHAR(50) NOT NULL DEFAULT 'pending',
    read_at                 TIMESTAMPTZ,
    dismissed_at            TIMESTAMPTZ,
    created_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at              TIMESTAMPTZ,
    PRIMARY KEY (user_id, notification_id),
    CONSTRAINT chk_recipients_status CHECK (status IN ('pending', 'delivered', 'read', 'dismissed', 'failed')),
    -- Consistency check: read_at must be populated if status is read
    CONSTRAINT chk_recipients_read_at CHECK (
        (status = 'read' AND read_at IS NOT NULL) OR (status != 'read')
    ),
    -- Consistency check: dismissed_at must be populated if status is dismissed
    CONSTRAINT chk_recipients_dismissed_at CHECK (
        (status = 'dismissed' AND dismissed_at IS NOT NULL) OR (status != 'dismissed')
    )
) PARTITION BY HASH (user_id);

-- Partitions
CREATE TABLE notification_recipients_p0 PARTITION OF notification_recipients FOR VALUES WITH (MODULUS 4, REMAINDER 0);
CREATE TABLE notification_recipients_p1 PARTITION OF notification_recipients FOR VALUES WITH (MODULUS 4, REMAINDER 1);
CREATE TABLE notification_recipients_p2 PARTITION OF notification_recipients FOR VALUES WITH (MODULUS 4, REMAINDER 2);
CREATE TABLE notification_recipients_p3 PARTITION OF notification_recipients FOR VALUES WITH (MODULUS 4, REMAINDER 3);

CREATE INDEX idx_notification_recipients_unread ON notification_recipients (user_id, status) WHERE status != 'read';

-- ============================================================
-- 6. Broadcast read cursors
-- ============================================================

CREATE TABLE user_read_cursors (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT UNIQUE NOT NULL,
    last_read_seq   BIGINT NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_user_read_cursors_user_id ON user_read_cursors(user_id);

-- ============================================================
-- 7. Per-channel delivery attempts — retry/audit log, partitioned by range
-- ============================================================

CREATE TABLE channel_deliveries (
    id                      BIGSERIAL,
    notification_id         BIGINT NOT NULL,
    notification_created_at TIMESTAMPTZ NOT NULL,
    user_id                 BIGINT NOT NULL,
    channel                 VARCHAR(50) NOT NULL,
    provider                VARCHAR(100) NOT NULL,
    status                  VARCHAR(50) NOT NULL DEFAULT 'queued',
    attempt_count           SMALLINT NOT NULL DEFAULT 0,
    max_attempts            SMALLINT NOT NULL DEFAULT 5,
    next_retry_at           TIMESTAMPTZ,
    provider_msg_id         VARCHAR(255),
    error_code              VARCHAR(100),
    error_detail            TEXT,
    sent_at                 TIMESTAMPTZ,
    delivered_at            TIMESTAMPTZ,
    created_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at              TIMESTAMPTZ,
    PRIMARY KEY (created_at, id),
    CONSTRAINT chk_deliveries_channel CHECK (channel IN ('email', 'push', 'sms', 'webhook')),
    CONSTRAINT chk_deliveries_status CHECK (status IN ('queued', 'sent', 'delivered', 'bounced', 'failed')),
    -- Consistency check: if status is sent or delivered, sent_at must be populated
    CONSTRAINT chk_deliveries_sent_at CHECK (
        (status IN ('sent', 'delivered') AND sent_at IS NOT NULL) OR (status NOT IN ('sent', 'delivered'))
    ),
    -- Consistency check: if status is delivered, delivered_at must be populated
    CONSTRAINT chk_deliveries_delivered_at CHECK (
        (status = 'delivered' AND delivered_at IS NOT NULL) OR (status != 'delivered')
    ),
    -- Consistency check: if status is failed/bounced, error_code must be populated
    CONSTRAINT chk_deliveries_error CHECK (
        (status IN ('failed', 'bounced') AND error_code IS NOT NULL) OR (status NOT IN ('failed', 'bounced'))
    )
) PARTITION BY RANGE (created_at);

-- Partitions
CREATE TABLE channel_deliveries_2026_07 PARTITION OF channel_deliveries
    FOR VALUES FROM ('2026-07-01 00:00:00+00') TO ('2026-08-01 00:00:00+00');
CREATE TABLE channel_deliveries_2026_08 PARTITION OF channel_deliveries
    FOR VALUES FROM ('2026-08-01 00:00:00+00') TO ('2026-09-01 00:00:00+00');
CREATE TABLE channel_deliveries_2026_09 PARTITION OF channel_deliveries
    FOR VALUES FROM ('2026-09-01 00:00:00+00') TO ('2026-10-01 00:00:00+00');
CREATE TABLE channel_deliveries_2026_10 PARTITION OF channel_deliveries
    FOR VALUES FROM ('2026-10-01 00:00:00+00') TO ('2026-11-01 00:00:00+00');
CREATE TABLE channel_deliveries_2026_11 PARTITION OF channel_deliveries
    FOR VALUES FROM ('2026-11-01 00:00:00+00') TO ('2026-12-01 00:00:00+00');
CREATE TABLE channel_deliveries_2026_12 PARTITION OF channel_deliveries
    FOR VALUES FROM ('2026-12-01 00:00:00+00') TO ('2027-01-01 00:00:00+00');

CREATE INDEX idx_channel_deliveries_retry ON channel_deliveries (status, next_retry_at) WHERE status IN ('queued', 'failed');
CREATE INDEX idx_channel_deliveries_user_channel ON channel_deliveries (user_id, channel, created_at DESC);

-- ============================================================
-- 8. Routing / preferences
-- ============================================================

CREATE TABLE user_notification_preferences (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    type_id         SMALLINT NOT NULL,
    channel         VARCHAR(50) NOT NULL,
    enabled         BOOLEAN NOT NULL DEFAULT TRUE,
    quiet_hours     JSONB,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at      TIMESTAMPTZ,
    CONSTRAINT fk_preferences_type FOREIGN KEY (type_id) REFERENCES notification_types(id) ON DELETE RESTRICT,
    CONSTRAINT chk_preferences_channel CHECK (channel IN ('email', 'push', 'sms', 'in_app', 'webhook')),
    CONSTRAINT uq_user_type_channel UNIQUE (user_id, type_id, channel)
);
CREATE INDEX idx_user_notification_preferences_type ON user_notification_preferences(type_id);

CREATE TABLE user_channel_endpoints (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    channel         VARCHAR(50) NOT NULL,
    endpoint        VARCHAR(512) NOT NULL,
    is_verified     BOOLEAN NOT NULL DEFAULT FALSE,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at      TIMESTAMPTZ,
    CONSTRAINT chk_endpoints_channel CHECK (channel IN ('email', 'push', 'sms', 'webhook')),
    CONSTRAINT uq_user_channel_endpoint UNIQUE (user_id, channel, endpoint),
    -- Basic syntax validation constraint for emails
    CONSTRAINT chk_endpoints_email CHECK (channel != 'email' OR endpoint LIKE '%@%')
);

-- ============================================================
-- 9. Digest / batching queue
-- ============================================================

CREATE TABLE digest_batches (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    frequency       VARCHAR(50) NOT NULL,
    notification_ids BIGINT[] NOT NULL,
    scheduled_for   TIMESTAMPTZ NOT NULL,
    sent_at         TIMESTAMPTZ,
    status          VARCHAR(50) NOT NULL DEFAULT 'pending',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at      TIMESTAMPTZ,
    CONSTRAINT chk_digest_batches_frequency CHECK (frequency IN ('hourly', 'daily', 'weekly')),
    CONSTRAINT chk_digest_batches_status CHECK (status IN ('pending', 'sent')),
    -- Consistency check: if status is sent, sent_at must be set
    CONSTRAINT chk_digest_batches_sent CHECK (
        (status = 'sent' AND sent_at IS NOT NULL) OR (status != 'sent')
    )
);
CREATE INDEX idx_digest_batches_user ON digest_batches(user_id);

-- ============================================================
-- 10. Device / platform registry
-- ============================================================

CREATE TABLE devices (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    platform        VARCHAR(50) NOT NULL,
    push_token      VARCHAR(512),
    web_push_endpoint JSONB,
    app_version     VARCHAR(50),
    os_version      VARCHAR(50),
    capabilities    JSONB NOT NULL DEFAULT '{}',
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    last_seen_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at      TIMESTAMPTZ,
    CONSTRAINT chk_devices_platform CHECK (platform IN ('web', 'ios', 'android')),
    CONSTRAINT uq_user_platform_token UNIQUE (user_id, platform, push_token),
    -- Consistency check: web push needs web_push_endpoint, mobile push platforms need push_token
    CONSTRAINT chk_devices_tokens CHECK (
        (platform = 'web' AND web_push_endpoint IS NOT NULL) OR
        (platform IN ('ios', 'android') AND push_token IS NOT NULL)
    )
);
CREATE INDEX idx_devices_active_user ON devices(user_id) WHERE is_active;

-- ============================================================
-- 11. Escalation Runs
-- ============================================================

CREATE TABLE escalation_runs (
    id                      BIGSERIAL PRIMARY KEY,
    notification_id         BIGINT NOT NULL,
    notification_created_at TIMESTAMPTZ NOT NULL,
    user_id                 BIGINT NOT NULL,
    policy_id               BIGINT NOT NULL,
    current_step            SMALLINT NOT NULL DEFAULT 0,
    acknowledged_at         TIMESTAMPTZ,
    status                  VARCHAR(50) NOT NULL DEFAULT 'active',
    created_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at              TIMESTAMPTZ,
    CONSTRAINT fk_escalation_runs_policy FOREIGN KEY (policy_id) REFERENCES escalation_policies(id) ON DELETE RESTRICT,
    CONSTRAINT chk_escalation_runs_status CHECK (status IN ('active', 'acknowledged', 'exhausted')),
    -- Consistency check: if acknowledged, acknowledged_at must be populated
    CONSTRAINT chk_escalation_runs_ack CHECK (
        (status = 'acknowledged' AND acknowledged_at IS NOT NULL) OR (status != 'acknowledged')
    )
);
CREATE INDEX idx_escalation_runs_policy ON escalation_runs(policy_id);

-- ============================================================
-- 12. Engagement Events (partitioned by range)
-- ============================================================

CREATE TABLE engagement_events (
    id              BIGINT NOT NULL,
    notification_id BIGINT NOT NULL,
    user_id         BIGINT NOT NULL,
    device_id       BIGINT,
    channel         VARCHAR(50) NOT NULL,
    event_type      VARCHAR(50) NOT NULL,
    action_id       VARCHAR(100),
    client_metadata JSONB,
    occurred_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at      TIMESTAMPTZ,
    PRIMARY KEY (occurred_at, id),
    CONSTRAINT fk_engagement_events_device FOREIGN KEY (device_id) REFERENCES devices(id) ON DELETE SET NULL,
    CONSTRAINT chk_engagement_events_channel CHECK (channel IN ('email', 'push', 'sms', 'webhook')),
    CONSTRAINT chk_engagement_events_type CHECK (event_type IN ('delivered', 'opened', 'clicked', 'action_taken', 'dismissed')),
    -- Consistency check: action_id is populated only when action_taken event occurs
    CONSTRAINT chk_engagement_events_action CHECK (
        (event_type = 'action_taken' AND action_id IS NOT NULL) OR (event_type != 'action_taken')
    )
) PARTITION BY RANGE (occurred_at);

-- Partitions
CREATE TABLE engagement_events_2026_07 PARTITION OF engagement_events
    FOR VALUES FROM ('2026-07-01 00:00:00+00') TO ('2026-08-01 00:00:00+00');
CREATE TABLE engagement_events_2026_08 PARTITION OF engagement_events
    FOR VALUES FROM ('2026-08-01 00:00:00+00') TO ('2026-09-01 00:00:00+00');
CREATE TABLE engagement_events_2026_09 PARTITION OF engagement_events
    FOR VALUES FROM ('2026-09-01 00:00:00+00') TO ('2026-10-01 00:00:00+00');
CREATE TABLE engagement_events_2026_10 PARTITION OF engagement_events
    FOR VALUES FROM ('2026-10-01 00:00:00+00') TO ('2026-11-01 00:00:00+00');
CREATE TABLE engagement_events_2026_11 PARTITION OF engagement_events
    FOR VALUES FROM ('2026-11-01 00:00:00+00') TO ('2026-12-01 00:00:00+00');
CREATE TABLE engagement_events_2026_12 PARTITION OF engagement_events
    FOR VALUES FROM ('2026-12-01 00:00:00+00') TO ('2027-01-01 00:00:00+00');

CREATE INDEX idx_engagement_events_device_id ON engagement_events(device_id);

-- ============================================================
-- 13. Topic-based subscriptions
-- ============================================================

CREATE TABLE topics (
    id              BIGSERIAL PRIMARY KEY,
    type_id         SMALLINT NOT NULL,
    scope_key       VARCHAR(255) NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at      TIMESTAMPTZ,
    CONSTRAINT fk_topics_type FOREIGN KEY (type_id) REFERENCES notification_types(id) ON DELETE RESTRICT
);
CREATE INDEX idx_topics_type ON topics(type_id);

CREATE TABLE user_topic_subscriptions (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    topic_id        BIGINT NOT NULL,
    channel         VARCHAR(50) NOT NULL,
    enabled         BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at      TIMESTAMPTZ,
    CONSTRAINT fk_subscriptions_topic FOREIGN KEY (topic_id) REFERENCES topics(id) ON DELETE RESTRICT,
    CONSTRAINT chk_subscriptions_channel CHECK (channel IN ('email', 'push', 'sms', 'webhook')),
    CONSTRAINT uq_user_topic_channel UNIQUE (user_id, topic_id, channel)
);
CREATE INDEX idx_user_topic_subscriptions_topic ON user_topic_subscriptions(topic_id);
