-- migration:      0006
-- description:    rollback github_action_templates catalog table
-- author:         Antigravity
-- date:           2026-07-11
-- depends_on:     0004
-- reversible:     YES

DROP TABLE IF EXISTS github_action_templates CASCADE;
