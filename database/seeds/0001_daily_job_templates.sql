-- seed:           0001
-- description:    50 reusable daily job templates covering common recurring ops work
--                  (comms triage, code review, CI/CD, security, infra, data, vendor, customer success)
-- depends_on:      migration 0004 (job scheduler system)
-- usage:           psql -f database/seeds/0001_daily_job_templates.sql
--                  Idempotent: safe to re-run (ON CONFLICT DO NOTHING on all inserts).

-- ============================================================
-- 0. Default organization (required FK target for jobs.org_id)
-- ============================================================

INSERT INTO organizations (id, name) VALUES
    (1, 'Default Organization')
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 1. Job categories
-- ============================================================

INSERT INTO job_categories (id, name) VALUES
    (9001, 'Communication & Support'),
    (9002, 'Code Quality & Review'),
    (9003, 'CI/CD & Build'),
    (9004, 'Security & Compliance'),
    (9005, 'Infrastructure & Ops'),
    (9006, 'Data & Reporting'),
    (9007, 'Vendor & Contract Management'),
    (9008, 'Customer Success')
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 2. Job templates
-- ============================================================
-- config JSONB carries a human-readable description; the actual cron
-- schedule lives in the `schedules` table below (one row per job).

INSERT INTO jobs (id, org_id, category_id, name, status, config) VALUES
    -- Communication & Support (9001)
    (90001, 1, 9001, 'Email Inbox Triage',              'active', '{"description": "Sweep the shared inbox and flag emails needing same-day response"}'),
    (90002, 1, 9001, 'Slack Support Channel Sweep',      'active', '{"description": "Check support/on-call Slack channels for unanswered threads"}'),
    (90003, 1, 9001, 'Customer Support Ticket Triage',   'active', '{"description": "Triage and prioritize new support tickets by severity"}'),
    (90004, 1, 9001, 'Voicemail Transcription Check',    'active', '{"description": "Review transcribed voicemails and route to the right owner"}'),
    (90005, 1, 9001, 'Newsletter Digest Compile',        'active', '{"description": "Compile the daily internal newsletter digest"}'),
    (90006, 1, 9001, 'Team Standup Notes Compile',       'active', '{"description": "Aggregate async standup updates into a single summary"}'),

    -- Code Quality & Review (9002)
    (90007, 1, 9002, 'Open Pull Request Review Reminder','active', '{"description": "Remind reviewers of pull requests open longer than 24 hours"}'),
    (90008, 1, 9002, 'Stale Branch Cleanup Check',       'active', '{"description": "Flag branches with no commits in 30+ days for cleanup"}'),
    (90009, 1, 9002, 'Code Coverage Report',             'active', '{"description": "Generate and publish the daily code coverage report"}'),
    (90010, 1, 9002, 'Lint & Static Analysis Sweep',      'active', '{"description": "Run linters and static analysis across active branches"}'),
    (90011, 1, 9002, 'Dead Code Detection Scan',         'active', '{"description": "Scan for unused exports and dead code paths"}'),
    (90012, 1, 9002, 'Dependency Outdated Packages Check','active', '{"description": "Check for outdated dependencies across all packages"}'),
    (90013, 1, 9002, 'Technical Debt Backlog Review',    'active', '{"description": "Review and re-prioritize the technical debt backlog"}'),

    -- CI/CD & Build (9003)
    (90014, 1, 9003, 'Nightly Full Test Suite Run',      'active', '{"description": "Run the full unit/integration/e2e test suite overnight"}'),
    (90015, 1, 9003, 'Nightly Build Verification',       'active', '{"description": "Verify all packages build cleanly from a fresh checkout"}'),
    (90016, 1, 9003, 'Staging Deployment Health Check',  'active', '{"description": "Verify staging environment health after nightly deploys"}'),
    (90017, 1, 9003, 'Production Smoke Test',            'active', '{"description": "Run smoke tests against production critical paths"}'),
    (90018, 1, 9003, 'Build Artifact Cleanup',           'active', '{"description": "Purge expired build artifacts and Docker images"}'),
    (90019, 1, 9003, 'CI Pipeline Failure Digest',        'active', '{"description": "Compile a digest of failed CI runs from the last 24 hours"}'),

    -- Security & Compliance (9004)
    (90020, 1, 9004, 'Secret & Credential Scan',         'active', '{"description": "Scan repositories for committed secrets or credentials"}'),
    (90021, 1, 9004, 'Dependency Vulnerability Scan',    'active', '{"description": "Scan dependencies for known CVEs"}'),
    (90022, 1, 9004, 'SSL Certificate Expiry Check',     'active', '{"description": "Check TLS certificates expiring within 30 days"}'),
    (90023, 1, 9004, 'Access & Permissions Review',      'active', '{"description": "Review user access and permission grants for anomalies"}'),
    (90024, 1, 9004, 'Firewall Rule Audit',              'active', '{"description": "Audit firewall and security group rules for drift"}'),
    (90025, 1, 9004, 'Audit Log Review',                 'active', '{"description": "Review audit logs for suspicious or unauthorized activity"}'),
    (90026, 1, 9004, 'Password Rotation Reminder',       'active', '{"description": "Remind owners of service accounts due for password rotation"}'),

    -- Infrastructure & Ops (9005)
    (90027, 1, 9005, 'Server Health Check',              'active', '{"description": "Check CPU, memory, and disk health across all servers"}'),
    (90028, 1, 9005, 'Database Backup Verification',     'active', '{"description": "Verify the previous night''s database backup completed and is restorable"}'),
    (90029, 1, 9005, 'Disk Usage Check',                 'active', '{"description": "Check disk usage thresholds across all environments"}'),
    (90030, 1, 9005, 'Log Rotation & Cleanup',           'active', '{"description": "Rotate and archive application logs past retention"}'),
    (90031, 1, 9005, 'Uptime & Latency Monitor Sweep',   'active', '{"description": "Review uptime and latency monitors for regressions"}'),
    (90032, 1, 9005, 'Cache Invalidation Sweep',         'active', '{"description": "Sweep stale cache entries flagged for invalidation"}'),
    (90033, 1, 9005, 'Queue Backlog Check',              'active', '{"description": "Check background job/message queue backlog depth"}'),

    -- Data & Reporting (9006)
    (90034, 1, 9006, 'Daily Sales Report Compile',       'active', '{"description": "Compile and distribute the daily sales report"}'),
    (90035, 1, 9006, 'Analytics Rollup Job',             'active', '{"description": "Roll up event analytics into daily aggregate tables"}'),
    (90036, 1, 9006, 'Data Quality Validation',          'active', '{"description": "Run data quality checks against warehouse tables"}'),
    (90037, 1, 9006, 'ETL Pipeline Health Check',        'active', '{"description": "Verify ETL pipelines completed without errors"}'),
    (90038, 1, 9006, 'Revenue Reconciliation Report',    'active', '{"description": "Reconcile daily revenue figures against billing records"}'),
    (90039, 1, 9006, 'KPI Dashboard Refresh',            'active', '{"description": "Refresh executive KPI dashboards with latest metrics"}'),

    -- Vendor & Contract Management (9007)
    (90040, 1, 9007, 'Vendor Insurance Certificate Sweep','active', '{"description": "Check for vendor insurance certificates expiring soon"}'),
    (90041, 1, 9007, 'Contract Expiry Sweep',            'active', '{"description": "Flag vendor contracts approaching their expiry date"}'),
    (90042, 1, 9007, 'Vendor SLA Compliance Check',      'active', '{"description": "Check vendor performance against contracted SLAs"}'),
    (90043, 1, 9007, 'Purchase Order Reconciliation',    'active', '{"description": "Reconcile open purchase orders against received goods"}'),
    (90044, 1, 9007, 'Invoice Approval Reminder Sweep',  'active', '{"description": "Remind approvers of invoices pending sign-off"}'),
    (90045, 1, 9007, 'Procurement Budget Threshold Check','active', '{"description": "Check procurement spend against budget thresholds"}'),

    -- Customer Success (9008)
    (90046, 1, 9008, 'Customer Churn Risk Scan',         'active', '{"description": "Scan customer usage data for churn risk signals"}'),
    (90047, 1, 9008, 'Renewal Reminder Sweep',           'active', '{"description": "Remind account owners of upcoming renewal dates"}'),
    (90048, 1, 9008, 'Onboarding Progress Check',        'active', '{"description": "Check new customer onboarding progress against milestones"}'),
    (90049, 1, 9008, 'NPS Survey Dispatch Check',        'active', '{"description": "Verify scheduled NPS surveys were dispatched"}'),
    (90050, 1, 9008, 'Support SLA Breach Sweep',         'active', '{"description": "Flag open support tickets at risk of breaching SLA"}')
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 3. Daily cron schedules (one per job, staggered across the day)
-- ============================================================

INSERT INTO schedules (id, job_id, cron_expression, next_run_at) VALUES
    (90001, 90001, '0 6 * * *',  '2026-07-12 06:00:00+00'),
    (90002, 90002, '5 6 * * *',  '2026-07-12 06:05:00+00'),
    (90003, 90003, '10 6 * * *', '2026-07-12 06:10:00+00'),
    (90004, 90004, '15 6 * * *', '2026-07-12 06:15:00+00'),
    (90005, 90005, '20 6 * * *', '2026-07-12 06:20:00+00'),
    (90006, 90006, '25 6 * * *', '2026-07-12 06:25:00+00'),

    (90007, 90007, '0 7 * * *',  '2026-07-12 07:00:00+00'),
    (90008, 90008, '5 7 * * *',  '2026-07-12 07:05:00+00'),
    (90009, 90009, '10 7 * * *', '2026-07-12 07:10:00+00'),
    (90010, 90010, '15 7 * * *', '2026-07-12 07:15:00+00'),
    (90011, 90011, '20 7 * * *', '2026-07-12 07:20:00+00'),
    (90012, 90012, '25 7 * * *', '2026-07-12 07:25:00+00'),
    (90013, 90013, '30 7 * * *', '2026-07-12 07:30:00+00'),

    (90014, 90014, '0 2 * * *',  '2026-07-12 02:00:00+00'),
    (90015, 90015, '5 2 * * *',  '2026-07-12 02:05:00+00'),
    (90016, 90016, '30 8 * * *', '2026-07-12 08:30:00+00'),
    (90017, 90017, '35 8 * * *', '2026-07-12 08:35:00+00'),
    (90018, 90018, '0 3 * * *',  '2026-07-12 03:00:00+00'),
    (90019, 90019, '40 8 * * *', '2026-07-12 08:40:00+00'),

    (90020, 90020, '0 9 * * *',  '2026-07-12 09:00:00+00'),
    (90021, 90021, '5 9 * * *',  '2026-07-12 09:05:00+00'),
    (90022, 90022, '10 9 * * *', '2026-07-12 09:10:00+00'),
    (90023, 90023, '15 9 * * *', '2026-07-12 09:15:00+00'),
    (90024, 90024, '20 9 * * *', '2026-07-12 09:20:00+00'),
    (90025, 90025, '25 9 * * *', '2026-07-12 09:25:00+00'),
    (90026, 90026, '30 9 * * *', '2026-07-12 09:30:00+00'),

    (90027, 90027, '0 10 * * *', '2026-07-12 10:00:00+00'),
    (90028, 90028, '5 10 * * *', '2026-07-12 10:05:00+00'),
    (90029, 90029, '10 10 * * *','2026-07-12 10:10:00+00'),
    (90030, 90030, '15 10 * * *','2026-07-12 10:15:00+00'),
    (90031, 90031, '20 10 * * *','2026-07-12 10:20:00+00'),
    (90032, 90032, '25 10 * * *','2026-07-12 10:25:00+00'),
    (90033, 90033, '30 10 * * *','2026-07-12 10:30:00+00'),

    (90034, 90034, '0 11 * * *', '2026-07-12 11:00:00+00'),
    (90035, 90035, '5 11 * * *', '2026-07-12 11:05:00+00'),
    (90036, 90036, '10 11 * * *','2026-07-12 11:10:00+00'),
    (90037, 90037, '15 11 * * *','2026-07-12 11:15:00+00'),
    (90038, 90038, '20 11 * * *','2026-07-12 11:20:00+00'),
    (90039, 90039, '25 11 * * *','2026-07-12 11:25:00+00'),

    (90040, 90040, '0 12 * * *', '2026-07-12 12:00:00+00'),
    (90041, 90041, '5 12 * * *', '2026-07-12 12:05:00+00'),
    (90042, 90042, '10 12 * * *','2026-07-12 12:10:00+00'),
    (90043, 90043, '15 12 * * *','2026-07-12 12:15:00+00'),
    (90044, 90044, '20 12 * * *','2026-07-12 12:20:00+00'),
    (90045, 90045, '25 12 * * *','2026-07-12 12:25:00+00'),

    (90046, 90046, '0 13 * * *', '2026-07-12 13:00:00+00'),
    (90047, 90047, '5 13 * * *', '2026-07-12 13:05:00+00'),
    (90048, 90048, '10 13 * * *','2026-07-12 13:10:00+00'),
    (90049, 90049, '15 13 * * *','2026-07-12 13:15:00+00'),
    (90050, 90050, '20 13 * * *','2026-07-12 13:20:00+00')
ON CONFLICT (id) DO NOTHING;
