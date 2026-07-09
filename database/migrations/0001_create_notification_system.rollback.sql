-- migration:      0001
-- description:    rollback notification system schema and architecture
-- author:         Antigravity
-- date:           2026-07-09
-- depends_on:     
-- reversible:     YES
-- lock_risk:      LOW
-- rows_affected:  schema only
-- reason:         rollback script to drop all notification system tables

-- Drop tables in reverse dependency order
DROP TABLE IF EXISTS user_topic_subscriptions CASCADE;
DROP TABLE IF EXISTS topics CASCADE;
DROP TABLE IF EXISTS engagement_events CASCADE;
DROP TABLE IF EXISTS escalation_runs CASCADE;
DROP TABLE IF EXISTS devices CASCADE;
DROP TABLE IF EXISTS digest_batches CASCADE;
DROP TABLE IF EXISTS user_channel_endpoints CASCADE;
DROP TABLE IF EXISTS user_notification_preferences CASCADE;
DROP TABLE IF EXISTS channel_deliveries CASCADE;
DROP TABLE IF EXISTS user_read_cursors CASCADE;
DROP TABLE IF EXISTS notification_recipients CASCADE;
DROP TABLE IF EXISTS notifications CASCADE;
DROP TABLE IF EXISTS routing_rules CASCADE;
DROP TABLE IF EXISTS escalation_policies CASCADE;
DROP TABLE IF EXISTS notification_templates CASCADE;
DROP TABLE IF EXISTS notification_types CASCADE;

-- Drop sequences
DROP SEQUENCE IF EXISTS notification_global_seq CASCADE;
