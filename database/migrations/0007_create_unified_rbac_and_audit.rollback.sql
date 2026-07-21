-- migration:      0007
-- description:    rollback unified RBAC and append-only audit trail
-- author:         Antigravity
-- date:           2026-07-21
-- depends_on:     0006
-- reversible:     YES

DROP TABLE IF EXISTS audit_events CASCADE;
DROP TABLE IF EXISTS effective_permissions_cache CASCADE;
DROP TABLE IF EXISTS policy_conditions CASCADE;
DROP TABLE IF EXISTS role_assignments CASCADE;
DROP TABLE IF EXISTS group_members CASCADE;
DROP TABLE IF EXISTS groups CASCADE;
DROP TABLE IF EXISTS role_permissions CASCADE;
DROP TABLE IF EXISTS roles CASCADE;
DROP TABLE IF EXISTS permissions CASCADE;
DROP TABLE IF EXISTS org_memberships CASCADE;
DROP TABLE IF EXISTS service_accounts CASCADE;
DROP TABLE IF EXISTS users CASCADE;
