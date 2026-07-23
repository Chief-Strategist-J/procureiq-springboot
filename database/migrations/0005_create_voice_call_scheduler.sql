-- =============================================================================
-- Migration: 0005_create_voice_call_scheduler.sql
-- Description: Creates the scheduled_calls table for provider-agnostic voice
--              call scheduling. Supports Twilio, Vapi, and mock providers.
-- =============================================================================

CREATE TABLE IF NOT EXISTS scheduled_calls (
    id           BIGSERIAL PRIMARY KEY,
    phone_number TEXT        NOT NULL,
    instructions TEXT        NOT NULL,
    scheduled_at TIMESTAMPTZ NOT NULL,
    status       TEXT        NOT NULL DEFAULT 'PENDING'
                              CHECK (status IN ('PENDING', 'CALLED', 'FAILED')),
    provider     TEXT        NOT NULL DEFAULT 'mock',
    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_scheduled_calls_status_scheduled_at
    ON scheduled_calls (status, scheduled_at);

CREATE TABLE IF NOT EXISTS reminders (
    id                 BIGSERIAL PRIMARY KEY,
    title              TEXT NOT NULL DEFAULT '',
    description        TEXT DEFAULT '',
    due_at             TIMESTAMPTZ NOT NULL,
    recurrence         TEXT NOT NULL DEFAULT 'NONE',
    priority           TEXT NOT NULL DEFAULT 'MEDIUM',
    contact_preference TEXT NOT NULL DEFAULT 'CALL',
    assignee_name      TEXT NOT NULL DEFAULT '',
    assignee_contact   TEXT NOT NULL DEFAULT '',
    status             TEXT NOT NULL DEFAULT 'PENDING',
    snooze_count       INT NOT NULL DEFAULT 0,
    created_at         TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
