-- migration:      0006
-- description:    create github_action_templates catalog table
-- author:         Antigravity
-- date:           2026-07-11
-- depends_on:     0004
-- reversible:     YES
-- lock_risk:      LOW
-- rows_affected:  schema only

CREATE TABLE github_action_templates (
    id               BIGINT PRIMARY KEY,
    name             TEXT NOT NULL,
    category         TEXT NOT NULL,
    description      TEXT NOT NULL,
    cron_expression  TEXT NOT NULL,
    event_type       TEXT NOT NULL UNIQUE,
    yaml_content     TEXT NOT NULL,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT now()
);
