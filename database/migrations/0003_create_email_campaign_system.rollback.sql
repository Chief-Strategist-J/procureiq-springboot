-- migration:      0003
-- description:    rollback email campaign and scheduling system tables
-- author:         Antigravity
-- date:           2026-07-11
-- depends_on:     0002
-- reversible:     YES

DROP VIEW IF EXISTS campaign_performance CASCADE;
DROP VIEW IF EXISTS link_click_stats CASCADE;

DROP TABLE IF EXISTS report_exports CASCADE;
DROP TABLE IF EXISTS scheduling_batches CASCADE;
DROP TABLE IF EXISTS campaign_exclusions CASCADE;
DROP TABLE IF EXISTS calendar_events CASCADE;
DROP TABLE IF EXISTS spam_check_results CASCADE;
DROP TABLE IF EXISTS template_shares CASCADE;
DROP TABLE IF EXISTS send_time_predictions CASCADE;
DROP TABLE IF EXISTS retention_policies CASCADE;
DROP TABLE IF EXISTS ip_allowlist CASCADE;
DROP TABLE IF EXISTS api_usage_logs CASCADE;
DROP TABLE IF EXISTS subscriptions CASCADE;
DROP TABLE IF EXISTS integration_connections CASCADE;
DROP TABLE IF EXISTS rate_limit_buckets CASCADE;
DROP TABLE IF EXISTS holidays CASCADE;
DROP TABLE IF EXISTS holiday_calendars CASCADE;
DROP TABLE IF EXISTS business_hours_slots CASCADE;
DROP TABLE IF EXISTS business_hours CASCADE;
DROP TABLE IF EXISTS date_triggers CASCADE;
DROP TABLE IF EXISTS comments CASCADE;
DROP TABLE IF EXISTS campaign_approvals CASCADE;
DROP TABLE IF EXISTS attachments CASCADE;

DROP TABLE IF EXISTS webhook_deliveries CASCADE;
DROP TABLE IF EXISTS webhook_subscriptions CASCADE;
DROP TABLE IF EXISTS ab_test_variants CASCADE;
DROP TABLE IF EXISTS ab_tests CASCADE;
DROP TABLE IF EXISTS email_events CASCADE;
DROP TABLE IF EXISTS domain_reputation_snapshots CASCADE;
DROP TABLE IF EXISTS sending_domains CASCADE;
DROP TABLE IF EXISTS email_deliveries CASCADE;
DROP TABLE IF EXISTS send_queue CASCADE;
DROP TABLE IF EXISTS provider_accounts CASCADE;
DROP TABLE IF EXISTS scheduled_emails CASCADE;
DROP TABLE IF EXISTS recurrence_rules CASCADE;

DROP TABLE IF EXISTS workflow_runs CASCADE;
DROP TABLE IF EXISTS workflow_edges CASCADE;
DROP TABLE IF EXISTS workflow_nodes CASCADE;
DROP TABLE IF EXISTS workflows CASCADE;
DROP TABLE IF EXISTS campaign_steps CASCADE;
DROP TABLE IF EXISTS campaigns CASCADE;

DROP TABLE IF EXISTS email_templates CASCADE;
DROP TABLE IF EXISTS consent_records CASCADE;
DROP TABLE IF EXISTS suppressions CASCADE;
DROP TABLE IF EXISTS segments CASCADE;
DROP TABLE IF EXISTS contact_list_members CASCADE;
DROP TABLE IF EXISTS contact_lists CASCADE;

DROP TABLE IF EXISTS audit_logs CASCADE;
DROP TABLE IF EXISTS api_keys CASCADE;
DROP TABLE IF EXISTS org_users CASCADE;
DROP TABLE IF EXISTS organizations CASCADE;
