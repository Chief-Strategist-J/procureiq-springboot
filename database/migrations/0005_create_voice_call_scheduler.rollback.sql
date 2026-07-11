-- =============================================================================
-- Rollback: 0005_create_voice_call_scheduler.rollback.sql
-- Description: Drops all objects created by 0005_create_voice_call_scheduler.sql
-- =============================================================================

DROP INDEX IF EXISTS idx_scheduled_calls_status_scheduled_at;
DROP TABLE IF EXISTS scheduled_calls;
