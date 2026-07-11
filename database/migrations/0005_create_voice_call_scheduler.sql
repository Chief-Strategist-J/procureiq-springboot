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
