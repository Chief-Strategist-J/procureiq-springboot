-- seed:           0003
-- description:    50 GitHub Action templates (name/category/description/cron reused from
--                  0001_daily_job_templates.sql) with ready-to-deploy workflow YAML, for the
--                  GitHub dashboard's Deploy/Trigger actions (create-workflow / dispatch endpoints)
-- depends_on:      migration 0006 (github_action_templates)
-- usage:           psql -f database/seeds/0003_github_action_templates.sql
--                  Idempotent: safe to re-run (ON CONFLICT DO NOTHING).

INSERT INTO github_action_templates (id, name, category, description, cron_expression, event_type, yaml_content) VALUES
    (95001, 'Email Inbox Triage', 'Communication & Support', 'Sweep the shared inbox and flag emails needing same-day response', '0 6 * * *', 'email_inbox_triage.requested', 'name: "[Daily] Email Inbox Triage"

# category: Communication & Support
# description: Sweep the shared inbox and flag emails needing same-day response
# template: this workflow is one of 50 daily job templates generated to match the
#            job template seeded in database/seeds/0001_daily_job_templates.sql
#
# Disabled by default (schedule is commented out) so it does not start running the
# moment this file is merged. Uncomment the `schedule:` block below to run it daily,
# or trigger it manually / via the ProcureIQ GitHub dispatch UI (repository_dispatch).
on:
#  schedule:
#    - cron: ''0 6 * * *''
  workflow_dispatch:
  repository_dispatch:
    types: [email_inbox_triage.requested]

permissions:
  contents: read
  issues: write

jobs:
  daily-checklist:
    runs-on: ubuntu-latest
    steps:
      - name: Create or update daily checklist issue
        uses: actions/github-script@v7
        with:
          script: |
            const title = "[Daily] Email Inbox Triage";
            const body = [
              "**Category:** Communication & Support",
              "",
              "Sweep the shared inbox and flag emails needing same-day response",
              "",
              `_Triggered by ${context.eventName} on ${new Date().toISOString().slice(0, 10)}._`,
              "",
              "- [ ] Reviewed and completed for today"
            ].join("\n");

            const { data: issues } = await github.rest.issues.listForRepo({
              owner: context.repo.owner,
              repo: context.repo.repo,
              state: "open",
              labels: "daily-job",
            });

            const existing = issues.find((i) => i.title === title);
            if (existing) {
              await github.rest.issues.createComment({
                owner: context.repo.owner,
                repo: context.repo.repo,
                issue_number: existing.number,
                body: `Reminder: this daily task is still open.\n\n${body}`,
              });
            } else {
              await github.rest.issues.create({
                owner: context.repo.owner,
                repo: context.repo.repo,
                title,
                body,
                labels: ["daily-job"],
              });
            }
'),
    (95002, 'Slack Support Channel Sweep', 'Communication & Support', 'Check support/on-call Slack channels for unanswered threads', '5 6 * * *', 'slack_support_channel_sweep.requested', 'name: "[Daily] Slack Support Channel Sweep"

# category: Communication & Support
# description: Check support/on-call Slack channels for unanswered threads
# template: this workflow is one of 50 daily job templates generated to match the
#            job template seeded in database/seeds/0001_daily_job_templates.sql
#
# Disabled by default (schedule is commented out) so it does not start running the
# moment this file is merged. Uncomment the `schedule:` block below to run it daily,
# or trigger it manually / via the ProcureIQ GitHub dispatch UI (repository_dispatch).
on:
#  schedule:
#    - cron: ''5 6 * * *''
  workflow_dispatch:
  repository_dispatch:
    types: [slack_support_channel_sweep.requested]

permissions:
  contents: read
  issues: write

jobs:
  daily-checklist:
    runs-on: ubuntu-latest
    steps:
      - name: Create or update daily checklist issue
        uses: actions/github-script@v7
        with:
          script: |
            const title = "[Daily] Slack Support Channel Sweep";
            const body = [
              "**Category:** Communication & Support",
              "",
              "Check support/on-call Slack channels for unanswered threads",
              "",
              `_Triggered by ${context.eventName} on ${new Date().toISOString().slice(0, 10)}._`,
              "",
              "- [ ] Reviewed and completed for today"
            ].join("\n");

            const { data: issues } = await github.rest.issues.listForRepo({
              owner: context.repo.owner,
              repo: context.repo.repo,
              state: "open",
              labels: "daily-job",
            });

            const existing = issues.find((i) => i.title === title);
            if (existing) {
              await github.rest.issues.createComment({
                owner: context.repo.owner,
                repo: context.repo.repo,
                issue_number: existing.number,
                body: `Reminder: this daily task is still open.\n\n${body}`,
              });
            } else {
              await github.rest.issues.create({
                owner: context.repo.owner,
                repo: context.repo.repo,
                title,
                body,
                labels: ["daily-job"],
              });
            }
'),
    (95003, 'Customer Support Ticket Triage', 'Communication & Support', 'Triage and prioritize new support tickets by severity', '10 6 * * *', 'customer_support_ticket_triage.requested', 'name: "[Daily] Customer Support Ticket Triage"

# category: Communication & Support
# description: Triage and prioritize new support tickets by severity
# template: this workflow is one of 50 daily job templates generated to match the
#            job template seeded in database/seeds/0001_daily_job_templates.sql
#
# Disabled by default (schedule is commented out) so it does not start running the
# moment this file is merged. Uncomment the `schedule:` block below to run it daily,
# or trigger it manually / via the ProcureIQ GitHub dispatch UI (repository_dispatch).
on:
#  schedule:
#    - cron: ''10 6 * * *''
  workflow_dispatch:
  repository_dispatch:
    types: [customer_support_ticket_triage.requested]

permissions:
  contents: read
  issues: write

jobs:
  daily-checklist:
    runs-on: ubuntu-latest
    steps:
      - name: Create or update daily checklist issue
        uses: actions/github-script@v7
        with:
          script: |
            const title = "[Daily] Customer Support Ticket Triage";
            const body = [
              "**Category:** Communication & Support",
              "",
              "Triage and prioritize new support tickets by severity",
              "",
              `_Triggered by ${context.eventName} on ${new Date().toISOString().slice(0, 10)}._`,
              "",
              "- [ ] Reviewed and completed for today"
            ].join("\n");

            const { data: issues } = await github.rest.issues.listForRepo({
              owner: context.repo.owner,
              repo: context.repo.repo,
              state: "open",
              labels: "daily-job",
            });

            const existing = issues.find((i) => i.title === title);
            if (existing) {
              await github.rest.issues.createComment({
                owner: context.repo.owner,
                repo: context.repo.repo,
                issue_number: existing.number,
                body: `Reminder: this daily task is still open.\n\n${body}`,
              });
            } else {
              await github.rest.issues.create({
                owner: context.repo.owner,
                repo: context.repo.repo,
                title,
                body,
                labels: ["daily-job"],
              });
            }
'),
    (95004, 'Voicemail Transcription Check', 'Communication & Support', 'Review transcribed voicemails and route to the right owner', '15 6 * * *', 'voicemail_transcription_check.requested', 'name: "[Daily] Voicemail Transcription Check"

# category: Communication & Support
# description: Review transcribed voicemails and route to the right owner
# template: this workflow is one of 50 daily job templates generated to match the
#            job template seeded in database/seeds/0001_daily_job_templates.sql
#
# Disabled by default (schedule is commented out) so it does not start running the
# moment this file is merged. Uncomment the `schedule:` block below to run it daily,
# or trigger it manually / via the ProcureIQ GitHub dispatch UI (repository_dispatch).
on:
#  schedule:
#    - cron: ''15 6 * * *''
  workflow_dispatch:
  repository_dispatch:
    types: [voicemail_transcription_check.requested]

permissions:
  contents: read
  issues: write

jobs:
  daily-checklist:
    runs-on: ubuntu-latest
    steps:
      - name: Create or update daily checklist issue
        uses: actions/github-script@v7
        with:
          script: |
            const title = "[Daily] Voicemail Transcription Check";
            const body = [
              "**Category:** Communication & Support",
              "",
              "Review transcribed voicemails and route to the right owner",
              "",
              `_Triggered by ${context.eventName} on ${new Date().toISOString().slice(0, 10)}._`,
              "",
              "- [ ] Reviewed and completed for today"
            ].join("\n");

            const { data: issues } = await github.rest.issues.listForRepo({
              owner: context.repo.owner,
              repo: context.repo.repo,
              state: "open",
              labels: "daily-job",
            });

            const existing = issues.find((i) => i.title === title);
            if (existing) {
              await github.rest.issues.createComment({
                owner: context.repo.owner,
                repo: context.repo.repo,
                issue_number: existing.number,
                body: `Reminder: this daily task is still open.\n\n${body}`,
              });
            } else {
              await github.rest.issues.create({
                owner: context.repo.owner,
                repo: context.repo.repo,
                title,
                body,
                labels: ["daily-job"],
              });
            }
'),
    (95005, 'Newsletter Digest Compile', 'Communication & Support', 'Compile the daily internal newsletter digest', '20 6 * * *', 'newsletter_digest_compile.requested', 'name: "[Daily] Newsletter Digest Compile"

# category: Communication & Support
# description: Compile the daily internal newsletter digest
# template: this workflow is one of 50 daily job templates generated to match the
#            job template seeded in database/seeds/0001_daily_job_templates.sql
#
# Disabled by default (schedule is commented out) so it does not start running the
# moment this file is merged. Uncomment the `schedule:` block below to run it daily,
# or trigger it manually / via the ProcureIQ GitHub dispatch UI (repository_dispatch).
on:
#  schedule:
#    - cron: ''20 6 * * *''
  workflow_dispatch:
  repository_dispatch:
    types: [newsletter_digest_compile.requested]

permissions:
  contents: read
  issues: write

jobs:
  daily-checklist:
    runs-on: ubuntu-latest
    steps:
      - name: Create or update daily checklist issue
        uses: actions/github-script@v7
        with:
          script: |
            const title = "[Daily] Newsletter Digest Compile";
            const body = [
              "**Category:** Communication & Support",
              "",
              "Compile the daily internal newsletter digest",
              "",
              `_Triggered by ${context.eventName} on ${new Date().toISOString().slice(0, 10)}._`,
              "",
              "- [ ] Reviewed and completed for today"
            ].join("\n");

            const { data: issues } = await github.rest.issues.listForRepo({
              owner: context.repo.owner,
              repo: context.repo.repo,
              state: "open",
              labels: "daily-job",
            });

            const existing = issues.find((i) => i.title === title);
            if (existing) {
              await github.rest.issues.createComment({
                owner: context.repo.owner,
                repo: context.repo.repo,
                issue_number: existing.number,
                body: `Reminder: this daily task is still open.\n\n${body}`,
              });
            } else {
              await github.rest.issues.create({
                owner: context.repo.owner,
                repo: context.repo.repo,
                title,
                body,
                labels: ["daily-job"],
              });
            }
'),
    (95006, 'Team Standup Notes Compile', 'Communication & Support', 'Aggregate async standup updates into a single summary', '25 6 * * *', 'team_standup_notes_compile.requested', 'name: "[Daily] Team Standup Notes Compile"

# category: Communication & Support
# description: Aggregate async standup updates into a single summary
# template: this workflow is one of 50 daily job templates generated to match the
#            job template seeded in database/seeds/0001_daily_job_templates.sql
#
# Disabled by default (schedule is commented out) so it does not start running the
# moment this file is merged. Uncomment the `schedule:` block below to run it daily,
# or trigger it manually / via the ProcureIQ GitHub dispatch UI (repository_dispatch).
on:
#  schedule:
#    - cron: ''25 6 * * *''
  workflow_dispatch:
  repository_dispatch:
    types: [team_standup_notes_compile.requested]

permissions:
  contents: read
  issues: write

jobs:
  daily-checklist:
    runs-on: ubuntu-latest
    steps:
      - name: Create or update daily checklist issue
        uses: actions/github-script@v7
        with:
          script: |
            const title = "[Daily] Team Standup Notes Compile";
            const body = [
              "**Category:** Communication & Support",
              "",
              "Aggregate async standup updates into a single summary",
              "",
              `_Triggered by ${context.eventName} on ${new Date().toISOString().slice(0, 10)}._`,
              "",
              "- [ ] Reviewed and completed for today"
            ].join("\n");

            const { data: issues } = await github.rest.issues.listForRepo({
              owner: context.repo.owner,
              repo: context.repo.repo,
              state: "open",
              labels: "daily-job",
            });

            const existing = issues.find((i) => i.title === title);
            if (existing) {
              await github.rest.issues.createComment({
                owner: context.repo.owner,
                repo: context.repo.repo,
                issue_number: existing.number,
                body: `Reminder: this daily task is still open.\n\n${body}`,
              });
            } else {
              await github.rest.issues.create({
                owner: context.repo.owner,
                repo: context.repo.repo,
                title,
                body,
                labels: ["daily-job"],
              });
            }
'),
    (95007, 'Open Pull Request Review Reminder', 'Code Quality & Review', 'Remind reviewers of pull requests open longer than 24 hours', '0 7 * * *', 'open_pull_request_review_reminder.requested', 'name: "[Daily] Open Pull Request Review Reminder"

# category: Code Quality & Review
# description: Remind reviewers of pull requests open longer than 24 hours
# template: this workflow is one of 50 daily job templates generated to match the
#            job template seeded in database/seeds/0001_daily_job_templates.sql
#
# Disabled by default (schedule is commented out) so it does not start running the
# moment this file is merged. Uncomment the `schedule:` block below to run it daily,
# or trigger it manually / via the ProcureIQ GitHub dispatch UI (repository_dispatch).
on:
#  schedule:
#    - cron: ''0 7 * * *''
  workflow_dispatch:
  repository_dispatch:
    types: [open_pull_request_review_reminder.requested]

permissions:
  contents: read
  pull-requests: write

jobs:
  pr-review-reminder:
    runs-on: ubuntu-latest
    steps:
      - name: Remind reviewers of stale open pull requests
        uses: actions/github-script@v7
        with:
          script: |
            const { data: pulls } = await github.rest.pulls.list({
              owner: context.repo.owner,
              repo: context.repo.repo,
              state: "open",
            });

            const ONE_DAY_MS = 24 * 60 * 60 * 1000;
            const now = Date.now();

            for (const pr of pulls) {
              const ageMs = now - new Date(pr.created_at).getTime();
              if (ageMs > ONE_DAY_MS) {
                await github.rest.issues.createComment({
                  owner: context.repo.owner,
                  repo: context.repo.repo,
                  issue_number: pr.number,
                  body: "Remind reviewers of pull requests open longer than 24 hours\n\nThis pull request has been open for more than 24 hours and needs review.",
                });
              }
            }
'),
    (95008, 'Stale Branch Cleanup Check', 'Code Quality & Review', 'Flag branches with no commits in 30+ days for cleanup', '5 7 * * *', 'stale_branch_cleanup_check.requested', 'name: "[Daily] Stale Branch Cleanup Check"

# category: Code Quality & Review
# description: Flag branches with no commits in 30+ days for cleanup
# template: this workflow is one of 50 daily job templates generated to match the
#            job template seeded in database/seeds/0001_daily_job_templates.sql
#
# Disabled by default (schedule is commented out) so it does not start running the
# moment this file is merged. Uncomment the `schedule:` block below to run it daily,
# or trigger it manually / via the ProcureIQ GitHub dispatch UI (repository_dispatch).
on:
#  schedule:
#    - cron: ''5 7 * * *''
  workflow_dispatch:
  repository_dispatch:
    types: [stale_branch_cleanup_check.requested]

permissions:
  contents: read
  issues: write

jobs:
  stale-branch-check:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout Code
        uses: actions/checkout@v4
        with:
          fetch-depth: 0

      - name: Find and report stale branches
        uses: actions/github-script@v7
        with:
          script: |
            const { data: branches } = await github.rest.repos.listBranches({
              owner: context.repo.owner,
              repo: context.repo.repo,
            });

            const THIRTY_DAYS_MS = 30 * 24 * 60 * 60 * 1000;
            const now = Date.now();
            const stale = [];

            for (const branch of branches) {
              const { data: commit } = await github.rest.repos.getCommit({
                owner: context.repo.owner,
                repo: context.repo.repo,
                ref: branch.commit.sha,
              });
              const commitDate = new Date(commit.commit.committer.date).getTime();
              if (now - commitDate > THIRTY_DAYS_MS) {
                stale.push(branch.name);
              }
            }

            if (stale.length > 0) {
              await github.rest.issues.create({
                owner: context.repo.owner,
                repo: context.repo.repo,
                title: "[Daily] Stale Branch Cleanup Check",
                body: "The following branches have had no commits in 30+ days and are candidates for cleanup:\n\n" + stale.map((b) => `- ${b}`).join("\n"),
                labels: ["daily-job"],
              });
            } else {
              core.info("No stale branches found.");
            }
'),
    (95009, 'Code Coverage Report', 'Code Quality & Review', 'Generate and publish the daily code coverage report', '10 7 * * *', 'code_coverage_report.requested', 'name: "[Daily] Code Coverage Report"

# category: Code Quality & Review
# description: Generate and publish the daily code coverage report
# template: this workflow is one of 50 daily job templates generated to match the
#            job template seeded in database/seeds/0001_daily_job_templates.sql
#
# Disabled by default (schedule is commented out) so it does not start running the
# moment this file is merged. Uncomment the `schedule:` block below to run it daily,
# or trigger it manually / via the ProcureIQ GitHub dispatch UI (repository_dispatch).
on:
#  schedule:
#    - cron: ''10 7 * * *''
  workflow_dispatch:
  repository_dispatch:
    types: [code_coverage_report.requested]

permissions:
  contents: read

jobs:
  test-suite:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout Code
        uses: actions/checkout@v4

      - name: Set up Node
        uses: actions/setup-node@v4
        with:
          node-version: ''20''
          cache: ''npm''
          cache-dependency-path: packages/node/procureiq-nextjs/package-lock.json

      - name: Install Dependencies
        working-directory: packages/node/procureiq-nextjs
        run: npm ci

      - name: Run Test Suite
        working-directory: packages/node/procureiq-nextjs
        run: npx vitest run
'),
    (95010, 'Lint & Static Analysis Sweep', 'Code Quality & Review', 'Run linters and static analysis across active branches', '15 7 * * *', 'lint_static_analysis_sweep.requested', 'name: "[Daily] Lint & Static Analysis Sweep"

# category: Code Quality & Review
# description: Run linters and static analysis across active branches
# template: this workflow is one of 50 daily job templates generated to match the
#            job template seeded in database/seeds/0001_daily_job_templates.sql
#
# Disabled by default (schedule is commented out) so it does not start running the
# moment this file is merged. Uncomment the `schedule:` block below to run it daily,
# or trigger it manually / via the ProcureIQ GitHub dispatch UI (repository_dispatch).
on:
#  schedule:
#    - cron: ''15 7 * * *''
  workflow_dispatch:
  repository_dispatch:
    types: [lint_static_analysis_sweep.requested]

permissions:
  contents: read

jobs:
  lint-sweep:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout Code
        uses: actions/checkout@v4

      - name: Set up Node
        uses: actions/setup-node@v4
        with:
          node-version: ''20''
          cache: ''npm''
          cache-dependency-path: packages/node/procureiq-nextjs/package-lock.json

      - name: Install Dependencies
        working-directory: packages/node/procureiq-nextjs
        run: npm ci

      - name: Run Lint Sweep
        working-directory: packages/node/procureiq-nextjs
        run: npm run lint
'),
    (95011, 'Dead Code Detection Scan', 'Code Quality & Review', 'Scan for unused exports and dead code paths', '20 7 * * *', 'dead_code_detection_scan.requested', 'name: "[Daily] Dead Code Detection Scan"

# category: Code Quality & Review
# description: Scan for unused exports and dead code paths
# template: this workflow is one of 50 daily job templates generated to match the
#            job template seeded in database/seeds/0001_daily_job_templates.sql
#
# Disabled by default (schedule is commented out) so it does not start running the
# moment this file is merged. Uncomment the `schedule:` block below to run it daily,
# or trigger it manually / via the ProcureIQ GitHub dispatch UI (repository_dispatch).
on:
#  schedule:
#    - cron: ''20 7 * * *''
  workflow_dispatch:
  repository_dispatch:
    types: [dead_code_detection_scan.requested]

permissions:
  contents: read

jobs:
  lint-sweep:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout Code
        uses: actions/checkout@v4

      - name: Set up Node
        uses: actions/setup-node@v4
        with:
          node-version: ''20''
          cache: ''npm''
          cache-dependency-path: packages/node/procureiq-nextjs/package-lock.json

      - name: Install Dependencies
        working-directory: packages/node/procureiq-nextjs
        run: npm ci

      - name: Run Lint Sweep
        working-directory: packages/node/procureiq-nextjs
        run: npm run lint
'),
    (95012, 'Dependency Outdated Packages Check', 'Code Quality & Review', 'Check for outdated dependencies across all packages', '25 7 * * *', 'dependency_outdated_packages_check.requested', 'name: "[Daily] Dependency Outdated Packages Check"

# category: Code Quality & Review
# description: Check for outdated dependencies across all packages
# template: this workflow is one of 50 daily job templates generated to match the
#            job template seeded in database/seeds/0001_daily_job_templates.sql
#
# Disabled by default (schedule is commented out) so it does not start running the
# moment this file is merged. Uncomment the `schedule:` block below to run it daily,
# or trigger it manually / via the ProcureIQ GitHub dispatch UI (repository_dispatch).
on:
#  schedule:
#    - cron: ''25 7 * * *''
  workflow_dispatch:
  repository_dispatch:
    types: [dependency_outdated_packages_check.requested]

permissions:
  contents: read

jobs:
  outdated-check:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout Code
        uses: actions/checkout@v4

      - name: Set up Node
        uses: actions/setup-node@v4
        with:
          node-version: ''20''
          cache: ''npm''
          cache-dependency-path: packages/node/procureiq-nextjs/package-lock.json

      - name: Install Dependencies
        working-directory: packages/node/procureiq-nextjs
        run: npm ci

      - name: Check Outdated Packages
        working-directory: packages/node/procureiq-nextjs
        run: npm outdated || true
'),
    (95013, 'Technical Debt Backlog Review', 'Code Quality & Review', 'Review and re-prioritize the technical debt backlog', '30 7 * * *', 'technical_debt_backlog_review.requested', 'name: "[Daily] Technical Debt Backlog Review"

# category: Code Quality & Review
# description: Review and re-prioritize the technical debt backlog
# template: this workflow is one of 50 daily job templates generated to match the
#            job template seeded in database/seeds/0001_daily_job_templates.sql
#
# Disabled by default (schedule is commented out) so it does not start running the
# moment this file is merged. Uncomment the `schedule:` block below to run it daily,
# or trigger it manually / via the ProcureIQ GitHub dispatch UI (repository_dispatch).
on:
#  schedule:
#    - cron: ''30 7 * * *''
  workflow_dispatch:
  repository_dispatch:
    types: [technical_debt_backlog_review.requested]

permissions:
  contents: read
  issues: write

jobs:
  daily-checklist:
    runs-on: ubuntu-latest
    steps:
      - name: Create or update daily checklist issue
        uses: actions/github-script@v7
        with:
          script: |
            const title = "[Daily] Technical Debt Backlog Review";
            const body = [
              "**Category:** Code Quality & Review",
              "",
              "Review and re-prioritize the technical debt backlog",
              "",
              `_Triggered by ${context.eventName} on ${new Date().toISOString().slice(0, 10)}._`,
              "",
              "- [ ] Reviewed and completed for today"
            ].join("\n");

            const { data: issues } = await github.rest.issues.listForRepo({
              owner: context.repo.owner,
              repo: context.repo.repo,
              state: "open",
              labels: "daily-job",
            });

            const existing = issues.find((i) => i.title === title);
            if (existing) {
              await github.rest.issues.createComment({
                owner: context.repo.owner,
                repo: context.repo.repo,
                issue_number: existing.number,
                body: `Reminder: this daily task is still open.\n\n${body}`,
              });
            } else {
              await github.rest.issues.create({
                owner: context.repo.owner,
                repo: context.repo.repo,
                title,
                body,
                labels: ["daily-job"],
              });
            }
'),
    (95014, 'Nightly Full Test Suite Run', 'CI/CD & Build', 'Run the full unit/integration/e2e test suite overnight', '0 2 * * *', 'nightly_full_test_suite_run.requested', 'name: "[Daily] Nightly Full Test Suite Run"

# category: CI/CD & Build
# description: Run the full unit/integration/e2e test suite overnight
# template: this workflow is one of 50 daily job templates generated to match the
#            job template seeded in database/seeds/0001_daily_job_templates.sql
#
# Disabled by default (schedule is commented out) so it does not start running the
# moment this file is merged. Uncomment the `schedule:` block below to run it daily,
# or trigger it manually / via the ProcureIQ GitHub dispatch UI (repository_dispatch).
on:
#  schedule:
#    - cron: ''0 2 * * *''
  workflow_dispatch:
  repository_dispatch:
    types: [nightly_full_test_suite_run.requested]

permissions:
  contents: read

jobs:
  test-suite:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout Code
        uses: actions/checkout@v4

      - name: Set up Node
        uses: actions/setup-node@v4
        with:
          node-version: ''20''
          cache: ''npm''
          cache-dependency-path: packages/node/procureiq-nextjs/package-lock.json

      - name: Install Dependencies
        working-directory: packages/node/procureiq-nextjs
        run: npm ci

      - name: Run Test Suite
        working-directory: packages/node/procureiq-nextjs
        run: npx vitest run
'),
    (95015, 'Nightly Build Verification', 'CI/CD & Build', 'Verify all packages build cleanly from a fresh checkout', '5 2 * * *', 'nightly_build_verification.requested', 'name: "[Daily] Nightly Build Verification"

# category: CI/CD & Build
# description: Verify all packages build cleanly from a fresh checkout
# template: this workflow is one of 50 daily job templates generated to match the
#            job template seeded in database/seeds/0001_daily_job_templates.sql
#
# Disabled by default (schedule is commented out) so it does not start running the
# moment this file is merged. Uncomment the `schedule:` block below to run it daily,
# or trigger it manually / via the ProcureIQ GitHub dispatch UI (repository_dispatch).
on:
#  schedule:
#    - cron: ''5 2 * * *''
  workflow_dispatch:
  repository_dispatch:
    types: [nightly_build_verification.requested]

permissions:
  contents: read

jobs:
  build-verify:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout Code
        uses: actions/checkout@v4

      - name: Set up Node
        uses: actions/setup-node@v4
        with:
          node-version: ''20''
          cache: ''npm''
          cache-dependency-path: packages/node/procureiq-nextjs/package-lock.json

      - name: Install Dependencies
        working-directory: packages/node/procureiq-nextjs
        run: npm ci

      - name: Verify Build
        working-directory: packages/node/procureiq-nextjs
        run: npm run build
'),
    (95016, 'Staging Deployment Health Check', 'CI/CD & Build', 'Verify staging environment health after nightly deploys', '30 8 * * *', 'staging_deployment_health_check.requested', 'name: "[Daily] Staging Deployment Health Check"

# category: CI/CD & Build
# description: Verify staging environment health after nightly deploys
# template: this workflow is one of 50 daily job templates generated to match the
#            job template seeded in database/seeds/0001_daily_job_templates.sql
#
# Disabled by default (schedule is commented out) so it does not start running the
# moment this file is merged. Uncomment the `schedule:` block below to run it daily,
# or trigger it manually / via the ProcureIQ GitHub dispatch UI (repository_dispatch).
on:
#  schedule:
#    - cron: ''30 8 * * *''
  workflow_dispatch:
  repository_dispatch:
    types: [staging_deployment_health_check.requested]

permissions:
  contents: read

jobs:
  url-health-check:
    runs-on: ubuntu-latest
    steps:
      - name: Check configured URL (skips gracefully if not set)
        env:
          CHECK_URL: ${{ secrets.HEALTHCHECK_URL }}
        run: |
          if [ -z "$CHECK_URL" ]; then
            echo "No HEALTHCHECK_URL secret configured."
            echo "Set the HEALTHCHECK_URL repository secret to the endpoint you want this job to monitor."
            echo "Skipping: Verify staging environment health after nightly deploys"
            exit 0
          fi

          echo "Checking $CHECK_URL ..."
          status=$(curl -s -o /dev/null -w "%{http_code}" "$CHECK_URL")
          echo "HTTP status: $status"
          if [ "$status" -lt 200 ] || [ "$status" -ge 400 ]; then
            echo "::error::$CHECK_URL returned HTTP $status"
            exit 1
          fi
'),
    (95017, 'Production Smoke Test', 'CI/CD & Build', 'Run smoke tests against production critical paths', '35 8 * * *', 'production_smoke_test.requested', 'name: "[Daily] Production Smoke Test"

# category: CI/CD & Build
# description: Run smoke tests against production critical paths
# template: this workflow is one of 50 daily job templates generated to match the
#            job template seeded in database/seeds/0001_daily_job_templates.sql
#
# Disabled by default (schedule is commented out) so it does not start running the
# moment this file is merged. Uncomment the `schedule:` block below to run it daily,
# or trigger it manually / via the ProcureIQ GitHub dispatch UI (repository_dispatch).
on:
#  schedule:
#    - cron: ''35 8 * * *''
  workflow_dispatch:
  repository_dispatch:
    types: [production_smoke_test.requested]

permissions:
  contents: read

jobs:
  url-health-check:
    runs-on: ubuntu-latest
    steps:
      - name: Check configured URL (skips gracefully if not set)
        env:
          CHECK_URL: ${{ secrets.HEALTHCHECK_URL }}
        run: |
          if [ -z "$CHECK_URL" ]; then
            echo "No HEALTHCHECK_URL secret configured."
            echo "Set the HEALTHCHECK_URL repository secret to the endpoint you want this job to monitor."
            echo "Skipping: Run smoke tests against production critical paths"
            exit 0
          fi

          echo "Checking $CHECK_URL ..."
          status=$(curl -s -o /dev/null -w "%{http_code}" "$CHECK_URL")
          echo "HTTP status: $status"
          if [ "$status" -lt 200 ] || [ "$status" -ge 400 ]; then
            echo "::error::$CHECK_URL returned HTTP $status"
            exit 1
          fi
'),
    (95018, 'Build Artifact Cleanup', 'CI/CD & Build', 'Purge expired build artifacts and Docker images', '0 3 * * *', 'build_artifact_cleanup.requested', 'name: "[Daily] Build Artifact Cleanup"

# category: CI/CD & Build
# description: Purge expired build artifacts and Docker images
# template: this workflow is one of 50 daily job templates generated to match the
#            job template seeded in database/seeds/0001_daily_job_templates.sql
#
# Disabled by default (schedule is commented out) so it does not start running the
# moment this file is merged. Uncomment the `schedule:` block below to run it daily,
# or trigger it manually / via the ProcureIQ GitHub dispatch UI (repository_dispatch).
on:
#  schedule:
#    - cron: ''0 3 * * *''
  workflow_dispatch:
  repository_dispatch:
    types: [build_artifact_cleanup.requested]

permissions:
  contents: read
  actions: write

jobs:
  artifact-cleanup:
    runs-on: ubuntu-latest
    steps:
      - name: Delete artifacts older than 7 days
        uses: actions/github-script@v7
        with:
          script: |
            const SEVEN_DAYS_MS = 7 * 24 * 60 * 60 * 1000;
            const now = Date.now();
            let page = 1;
            let deleted = 0;

            while (true) {
              const { data } = await github.rest.actions.listArtifactsForRepo({
                owner: context.repo.owner,
                repo: context.repo.repo,
                per_page: 100,
                page,
              });
              if (data.artifacts.length === 0) break;

              for (const artifact of data.artifacts) {
                if (now - new Date(artifact.created_at).getTime() > SEVEN_DAYS_MS) {
                  await github.rest.actions.deleteArtifact({
                    owner: context.repo.owner,
                    repo: context.repo.repo,
                    artifact_id: artifact.id,
                  });
                  deleted += 1;
                }
              }
              page += 1;
            }

            core.info(`Deleted ${deleted} expired artifact(s).`);
'),
    (95019, 'CI Pipeline Failure Digest', 'CI/CD & Build', 'Compile a digest of failed CI runs from the last 24 hours', '40 8 * * *', 'ci_pipeline_failure_digest.requested', 'name: "[Daily] CI Pipeline Failure Digest"

# category: CI/CD & Build
# description: Compile a digest of failed CI runs from the last 24 hours
# template: this workflow is one of 50 daily job templates generated to match the
#            job template seeded in database/seeds/0001_daily_job_templates.sql
#
# Disabled by default (schedule is commented out) so it does not start running the
# moment this file is merged. Uncomment the `schedule:` block below to run it daily,
# or trigger it manually / via the ProcureIQ GitHub dispatch UI (repository_dispatch).
on:
#  schedule:
#    - cron: ''40 8 * * *''
  workflow_dispatch:
  repository_dispatch:
    types: [ci_pipeline_failure_digest.requested]

permissions:
  contents: read
  issues: write
  actions: read

jobs:
  ci-failure-digest:
    runs-on: ubuntu-latest
    steps:
      - name: Compile digest of failed workflow runs
        uses: actions/github-script@v7
        with:
          script: |
            const ONE_DAY_MS = 24 * 60 * 60 * 1000;
            const since = new Date(Date.now() - ONE_DAY_MS).toISOString();

            const { data } = await github.rest.actions.listWorkflowRunsForRepo({
              owner: context.repo.owner,
              repo: context.repo.repo,
              status: "failure",
              created: `>=${since}`,
              per_page: 50,
            });

            if (data.workflow_runs.length === 0) {
              core.info("No CI failures in the last 24 hours.");
              return;
            }

            const body = data.workflow_runs
              .map((run) => `- [${run.name}](${run.html_url}) on \`${run.head_branch}\` (${run.created_at})`)
              .join("\n");

            await github.rest.issues.create({
              owner: context.repo.owner,
              repo: context.repo.repo,
              title: "[Daily] CI Pipeline Failure Digest",
              body: `The following CI runs failed in the last 24 hours:\n\n${body}`,
              labels: ["daily-job", "ci-failure"],
            });
'),
    (95020, 'Secret & Credential Scan', 'Security & Compliance', 'Scan repositories for committed secrets or credentials', '0 9 * * *', 'secret_credential_scan.requested', 'name: "[Daily] Secret & Credential Scan"

# category: Security & Compliance
# description: Scan repositories for committed secrets or credentials
# template: this workflow is one of 50 daily job templates generated to match the
#            job template seeded in database/seeds/0001_daily_job_templates.sql
#
# Disabled by default (schedule is commented out) so it does not start running the
# moment this file is merged. Uncomment the `schedule:` block below to run it daily,
# or trigger it manually / via the ProcureIQ GitHub dispatch UI (repository_dispatch).
on:
#  schedule:
#    - cron: ''0 9 * * *''
  workflow_dispatch:
  repository_dispatch:
    types: [secret_credential_scan.requested]

permissions:
  contents: read

jobs:
  secret-scan:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout Code
        uses: actions/checkout@v4
        with:
          fetch-depth: 0

      - name: Run TruffleHog Secret Scan
        # trufflehog''s own docs recommend @main; pin to a release SHA instead if your
        # security policy requires immutable third-party action references.
        uses: trufflesecurity/trufflehog@main
        with:
          extra_args: --only-verified
'),
    (95021, 'Dependency Vulnerability Scan', 'Security & Compliance', 'Scan dependencies for known CVEs', '5 9 * * *', 'dependency_vulnerability_scan.requested', 'name: "[Daily] Dependency Vulnerability Scan"

# category: Security & Compliance
# description: Scan dependencies for known CVEs
# template: this workflow is one of 50 daily job templates generated to match the
#            job template seeded in database/seeds/0001_daily_job_templates.sql
#
# Disabled by default (schedule is commented out) so it does not start running the
# moment this file is merged. Uncomment the `schedule:` block below to run it daily,
# or trigger it manually / via the ProcureIQ GitHub dispatch UI (repository_dispatch).
on:
#  schedule:
#    - cron: ''5 9 * * *''
  workflow_dispatch:
  repository_dispatch:
    types: [dependency_vulnerability_scan.requested]

permissions:
  contents: read

jobs:
  vulnerability-scan:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout Code
        uses: actions/checkout@v4

      - name: Set up Node
        uses: actions/setup-node@v4
        with:
          node-version: ''20''
          cache: ''npm''
          cache-dependency-path: packages/node/procureiq-nextjs/package-lock.json

      - name: Install Dependencies
        working-directory: packages/node/procureiq-nextjs
        run: npm ci

      - name: Run npm audit
        working-directory: packages/node/procureiq-nextjs
        run: npm audit --audit-level=high
'),
    (95022, 'SSL Certificate Expiry Check', 'Security & Compliance', 'Check TLS certificates expiring within 30 days', '10 9 * * *', 'ssl_certificate_expiry_check.requested', 'name: "[Daily] SSL Certificate Expiry Check"

# category: Security & Compliance
# description: Check TLS certificates expiring within 30 days
# template: this workflow is one of 50 daily job templates generated to match the
#            job template seeded in database/seeds/0001_daily_job_templates.sql
#
# Disabled by default (schedule is commented out) so it does not start running the
# moment this file is merged. Uncomment the `schedule:` block below to run it daily,
# or trigger it manually / via the ProcureIQ GitHub dispatch UI (repository_dispatch).
on:
#  schedule:
#    - cron: ''10 9 * * *''
  workflow_dispatch:
  repository_dispatch:
    types: [ssl_certificate_expiry_check.requested]

permissions:
  contents: read

jobs:
  url-health-check:
    runs-on: ubuntu-latest
    steps:
      - name: Check configured URL (skips gracefully if not set)
        env:
          CHECK_URL: ${{ secrets.HEALTHCHECK_URL }}
        run: |
          if [ -z "$CHECK_URL" ]; then
            echo "No HEALTHCHECK_URL secret configured."
            echo "Set the HEALTHCHECK_URL repository secret to the endpoint you want this job to monitor."
            echo "Skipping: Check TLS certificates expiring within 30 days"
            exit 0
          fi

          echo "Checking $CHECK_URL ..."
          status=$(curl -s -o /dev/null -w "%{http_code}" "$CHECK_URL")
          echo "HTTP status: $status"
          if [ "$status" -lt 200 ] || [ "$status" -ge 400 ]; then
            echo "::error::$CHECK_URL returned HTTP $status"
            exit 1
          fi
'),
    (95023, 'Access & Permissions Review', 'Security & Compliance', 'Review user access and permission grants for anomalies', '15 9 * * *', 'access_permissions_review.requested', 'name: "[Daily] Access & Permissions Review"

# category: Security & Compliance
# description: Review user access and permission grants for anomalies
# template: this workflow is one of 50 daily job templates generated to match the
#            job template seeded in database/seeds/0001_daily_job_templates.sql
#
# Disabled by default (schedule is commented out) so it does not start running the
# moment this file is merged. Uncomment the `schedule:` block below to run it daily,
# or trigger it manually / via the ProcureIQ GitHub dispatch UI (repository_dispatch).
on:
#  schedule:
#    - cron: ''15 9 * * *''
  workflow_dispatch:
  repository_dispatch:
    types: [access_permissions_review.requested]

permissions:
  contents: read
  issues: write

jobs:
  daily-checklist:
    runs-on: ubuntu-latest
    steps:
      - name: Create or update daily checklist issue
        uses: actions/github-script@v7
        with:
          script: |
            const title = "[Daily] Access & Permissions Review";
            const body = [
              "**Category:** Security & Compliance",
              "",
              "Review user access and permission grants for anomalies",
              "",
              `_Triggered by ${context.eventName} on ${new Date().toISOString().slice(0, 10)}._`,
              "",
              "- [ ] Reviewed and completed for today"
            ].join("\n");

            const { data: issues } = await github.rest.issues.listForRepo({
              owner: context.repo.owner,
              repo: context.repo.repo,
              state: "open",
              labels: "daily-job",
            });

            const existing = issues.find((i) => i.title === title);
            if (existing) {
              await github.rest.issues.createComment({
                owner: context.repo.owner,
                repo: context.repo.repo,
                issue_number: existing.number,
                body: `Reminder: this daily task is still open.\n\n${body}`,
              });
            } else {
              await github.rest.issues.create({
                owner: context.repo.owner,
                repo: context.repo.repo,
                title,
                body,
                labels: ["daily-job"],
              });
            }
'),
    (95024, 'Firewall Rule Audit', 'Security & Compliance', 'Audit firewall and security group rules for drift', '20 9 * * *', 'firewall_rule_audit.requested', 'name: "[Daily] Firewall Rule Audit"

# category: Security & Compliance
# description: Audit firewall and security group rules for drift
# template: this workflow is one of 50 daily job templates generated to match the
#            job template seeded in database/seeds/0001_daily_job_templates.sql
#
# Disabled by default (schedule is commented out) so it does not start running the
# moment this file is merged. Uncomment the `schedule:` block below to run it daily,
# or trigger it manually / via the ProcureIQ GitHub dispatch UI (repository_dispatch).
on:
#  schedule:
#    - cron: ''20 9 * * *''
  workflow_dispatch:
  repository_dispatch:
    types: [firewall_rule_audit.requested]

permissions:
  contents: read
  issues: write

jobs:
  daily-checklist:
    runs-on: ubuntu-latest
    steps:
      - name: Create or update daily checklist issue
        uses: actions/github-script@v7
        with:
          script: |
            const title = "[Daily] Firewall Rule Audit";
            const body = [
              "**Category:** Security & Compliance",
              "",
              "Audit firewall and security group rules for drift",
              "",
              `_Triggered by ${context.eventName} on ${new Date().toISOString().slice(0, 10)}._`,
              "",
              "- [ ] Reviewed and completed for today"
            ].join("\n");

            const { data: issues } = await github.rest.issues.listForRepo({
              owner: context.repo.owner,
              repo: context.repo.repo,
              state: "open",
              labels: "daily-job",
            });

            const existing = issues.find((i) => i.title === title);
            if (existing) {
              await github.rest.issues.createComment({
                owner: context.repo.owner,
                repo: context.repo.repo,
                issue_number: existing.number,
                body: `Reminder: this daily task is still open.\n\n${body}`,
              });
            } else {
              await github.rest.issues.create({
                owner: context.repo.owner,
                repo: context.repo.repo,
                title,
                body,
                labels: ["daily-job"],
              });
            }
'),
    (95025, 'Audit Log Review', 'Security & Compliance', 'Review audit logs for suspicious or unauthorized activity', '25 9 * * *', 'audit_log_review.requested', 'name: "[Daily] Audit Log Review"

# category: Security & Compliance
# description: Review audit logs for suspicious or unauthorized activity
# template: this workflow is one of 50 daily job templates generated to match the
#            job template seeded in database/seeds/0001_daily_job_templates.sql
#
# Disabled by default (schedule is commented out) so it does not start running the
# moment this file is merged. Uncomment the `schedule:` block below to run it daily,
# or trigger it manually / via the ProcureIQ GitHub dispatch UI (repository_dispatch).
on:
#  schedule:
#    - cron: ''25 9 * * *''
  workflow_dispatch:
  repository_dispatch:
    types: [audit_log_review.requested]

permissions:
  contents: read
  issues: write

jobs:
  daily-checklist:
    runs-on: ubuntu-latest
    steps:
      - name: Create or update daily checklist issue
        uses: actions/github-script@v7
        with:
          script: |
            const title = "[Daily] Audit Log Review";
            const body = [
              "**Category:** Security & Compliance",
              "",
              "Review audit logs for suspicious or unauthorized activity",
              "",
              `_Triggered by ${context.eventName} on ${new Date().toISOString().slice(0, 10)}._`,
              "",
              "- [ ] Reviewed and completed for today"
            ].join("\n");

            const { data: issues } = await github.rest.issues.listForRepo({
              owner: context.repo.owner,
              repo: context.repo.repo,
              state: "open",
              labels: "daily-job",
            });

            const existing = issues.find((i) => i.title === title);
            if (existing) {
              await github.rest.issues.createComment({
                owner: context.repo.owner,
                repo: context.repo.repo,
                issue_number: existing.number,
                body: `Reminder: this daily task is still open.\n\n${body}`,
              });
            } else {
              await github.rest.issues.create({
                owner: context.repo.owner,
                repo: context.repo.repo,
                title,
                body,
                labels: ["daily-job"],
              });
            }
'),
    (95026, 'Password Rotation Reminder', 'Security & Compliance', 'Remind owners of service accounts due for password rotation', '30 9 * * *', 'password_rotation_reminder.requested', 'name: "[Daily] Password Rotation Reminder"

# category: Security & Compliance
# description: Remind owners of service accounts due for password rotation
# template: this workflow is one of 50 daily job templates generated to match the
#            job template seeded in database/seeds/0001_daily_job_templates.sql
#
# Disabled by default (schedule is commented out) so it does not start running the
# moment this file is merged. Uncomment the `schedule:` block below to run it daily,
# or trigger it manually / via the ProcureIQ GitHub dispatch UI (repository_dispatch).
on:
#  schedule:
#    - cron: ''30 9 * * *''
  workflow_dispatch:
  repository_dispatch:
    types: [password_rotation_reminder.requested]

permissions:
  contents: read
  issues: write

jobs:
  daily-checklist:
    runs-on: ubuntu-latest
    steps:
      - name: Create or update daily checklist issue
        uses: actions/github-script@v7
        with:
          script: |
            const title = "[Daily] Password Rotation Reminder";
            const body = [
              "**Category:** Security & Compliance",
              "",
              "Remind owners of service accounts due for password rotation",
              "",
              `_Triggered by ${context.eventName} on ${new Date().toISOString().slice(0, 10)}._`,
              "",
              "- [ ] Reviewed and completed for today"
            ].join("\n");

            const { data: issues } = await github.rest.issues.listForRepo({
              owner: context.repo.owner,
              repo: context.repo.repo,
              state: "open",
              labels: "daily-job",
            });

            const existing = issues.find((i) => i.title === title);
            if (existing) {
              await github.rest.issues.createComment({
                owner: context.repo.owner,
                repo: context.repo.repo,
                issue_number: existing.number,
                body: `Reminder: this daily task is still open.\n\n${body}`,
              });
            } else {
              await github.rest.issues.create({
                owner: context.repo.owner,
                repo: context.repo.repo,
                title,
                body,
                labels: ["daily-job"],
              });
            }
'),
    (95027, 'Server Health Check', 'Infrastructure & Ops', 'Check CPU, memory, and disk health across all servers', '0 10 * * *', 'server_health_check.requested', 'name: "[Daily] Server Health Check"

# category: Infrastructure & Ops
# description: Check CPU, memory, and disk health across all servers
# template: this workflow is one of 50 daily job templates generated to match the
#            job template seeded in database/seeds/0001_daily_job_templates.sql
#
# Disabled by default (schedule is commented out) so it does not start running the
# moment this file is merged. Uncomment the `schedule:` block below to run it daily,
# or trigger it manually / via the ProcureIQ GitHub dispatch UI (repository_dispatch).
on:
#  schedule:
#    - cron: ''0 10 * * *''
  workflow_dispatch:
  repository_dispatch:
    types: [server_health_check.requested]

permissions:
  contents: read

jobs:
  url-health-check:
    runs-on: ubuntu-latest
    steps:
      - name: Check configured URL (skips gracefully if not set)
        env:
          CHECK_URL: ${{ secrets.HEALTHCHECK_URL }}
        run: |
          if [ -z "$CHECK_URL" ]; then
            echo "No HEALTHCHECK_URL secret configured."
            echo "Set the HEALTHCHECK_URL repository secret to the endpoint you want this job to monitor."
            echo "Skipping: Check CPU, memory, and disk health across all servers"
            exit 0
          fi

          echo "Checking $CHECK_URL ..."
          status=$(curl -s -o /dev/null -w "%{http_code}" "$CHECK_URL")
          echo "HTTP status: $status"
          if [ "$status" -lt 200 ] || [ "$status" -ge 400 ]; then
            echo "::error::$CHECK_URL returned HTTP $status"
            exit 1
          fi
'),
    (95028, 'Database Backup Verification', 'Infrastructure & Ops', 'Verify the previous night''s database backup completed and is restorable', '5 10 * * *', 'database_backup_verification.requested', 'name: "[Daily] Database Backup Verification"

# category: Infrastructure & Ops
# description: Verify the previous night''s database backup completed and is restorable
# template: this workflow is one of 50 daily job templates generated to match the
#            job template seeded in database/seeds/0001_daily_job_templates.sql
#
# Disabled by default (schedule is commented out) so it does not start running the
# moment this file is merged. Uncomment the `schedule:` block below to run it daily,
# or trigger it manually / via the ProcureIQ GitHub dispatch UI (repository_dispatch).
on:
#  schedule:
#    - cron: ''5 10 * * *''
  workflow_dispatch:
  repository_dispatch:
    types: [database_backup_verification.requested]

permissions:
  contents: read
  issues: write

jobs:
  daily-checklist:
    runs-on: ubuntu-latest
    steps:
      - name: Create or update daily checklist issue
        uses: actions/github-script@v7
        with:
          script: |
            const title = "[Daily] Database Backup Verification";
            const body = [
              "**Category:** Infrastructure & Ops",
              "",
              "Verify the previous night''s database backup completed and is restorable",
              "",
              `_Triggered by ${context.eventName} on ${new Date().toISOString().slice(0, 10)}._`,
              "",
              "- [ ] Reviewed and completed for today"
            ].join("\n");

            const { data: issues } = await github.rest.issues.listForRepo({
              owner: context.repo.owner,
              repo: context.repo.repo,
              state: "open",
              labels: "daily-job",
            });

            const existing = issues.find((i) => i.title === title);
            if (existing) {
              await github.rest.issues.createComment({
                owner: context.repo.owner,
                repo: context.repo.repo,
                issue_number: existing.number,
                body: `Reminder: this daily task is still open.\n\n${body}`,
              });
            } else {
              await github.rest.issues.create({
                owner: context.repo.owner,
                repo: context.repo.repo,
                title,
                body,
                labels: ["daily-job"],
              });
            }
'),
    (95029, 'Disk Usage Check', 'Infrastructure & Ops', 'Check disk usage thresholds across all environments', '10 10 * * *', 'disk_usage_check.requested', 'name: "[Daily] Disk Usage Check"

# category: Infrastructure & Ops
# description: Check disk usage thresholds across all environments
# template: this workflow is one of 50 daily job templates generated to match the
#            job template seeded in database/seeds/0001_daily_job_templates.sql
#
# Disabled by default (schedule is commented out) so it does not start running the
# moment this file is merged. Uncomment the `schedule:` block below to run it daily,
# or trigger it manually / via the ProcureIQ GitHub dispatch UI (repository_dispatch).
on:
#  schedule:
#    - cron: ''10 10 * * *''
  workflow_dispatch:
  repository_dispatch:
    types: [disk_usage_check.requested]

permissions:
  contents: read
  issues: write

jobs:
  daily-checklist:
    runs-on: ubuntu-latest
    steps:
      - name: Create or update daily checklist issue
        uses: actions/github-script@v7
        with:
          script: |
            const title = "[Daily] Disk Usage Check";
            const body = [
              "**Category:** Infrastructure & Ops",
              "",
              "Check disk usage thresholds across all environments",
              "",
              `_Triggered by ${context.eventName} on ${new Date().toISOString().slice(0, 10)}._`,
              "",
              "- [ ] Reviewed and completed for today"
            ].join("\n");

            const { data: issues } = await github.rest.issues.listForRepo({
              owner: context.repo.owner,
              repo: context.repo.repo,
              state: "open",
              labels: "daily-job",
            });

            const existing = issues.find((i) => i.title === title);
            if (existing) {
              await github.rest.issues.createComment({
                owner: context.repo.owner,
                repo: context.repo.repo,
                issue_number: existing.number,
                body: `Reminder: this daily task is still open.\n\n${body}`,
              });
            } else {
              await github.rest.issues.create({
                owner: context.repo.owner,
                repo: context.repo.repo,
                title,
                body,
                labels: ["daily-job"],
              });
            }
'),
    (95030, 'Log Rotation & Cleanup', 'Infrastructure & Ops', 'Rotate and archive application logs past retention', '15 10 * * *', 'log_rotation_cleanup.requested', 'name: "[Daily] Log Rotation & Cleanup"

# category: Infrastructure & Ops
# description: Rotate and archive application logs past retention
# template: this workflow is one of 50 daily job templates generated to match the
#            job template seeded in database/seeds/0001_daily_job_templates.sql
#
# Disabled by default (schedule is commented out) so it does not start running the
# moment this file is merged. Uncomment the `schedule:` block below to run it daily,
# or trigger it manually / via the ProcureIQ GitHub dispatch UI (repository_dispatch).
on:
#  schedule:
#    - cron: ''15 10 * * *''
  workflow_dispatch:
  repository_dispatch:
    types: [log_rotation_cleanup.requested]

permissions:
  contents: read
  issues: write

jobs:
  daily-checklist:
    runs-on: ubuntu-latest
    steps:
      - name: Create or update daily checklist issue
        uses: actions/github-script@v7
        with:
          script: |
            const title = "[Daily] Log Rotation & Cleanup";
            const body = [
              "**Category:** Infrastructure & Ops",
              "",
              "Rotate and archive application logs past retention",
              "",
              `_Triggered by ${context.eventName} on ${new Date().toISOString().slice(0, 10)}._`,
              "",
              "- [ ] Reviewed and completed for today"
            ].join("\n");

            const { data: issues } = await github.rest.issues.listForRepo({
              owner: context.repo.owner,
              repo: context.repo.repo,
              state: "open",
              labels: "daily-job",
            });

            const existing = issues.find((i) => i.title === title);
            if (existing) {
              await github.rest.issues.createComment({
                owner: context.repo.owner,
                repo: context.repo.repo,
                issue_number: existing.number,
                body: `Reminder: this daily task is still open.\n\n${body}`,
              });
            } else {
              await github.rest.issues.create({
                owner: context.repo.owner,
                repo: context.repo.repo,
                title,
                body,
                labels: ["daily-job"],
              });
            }
'),
    (95031, 'Uptime & Latency Monitor Sweep', 'Infrastructure & Ops', 'Review uptime and latency monitors for regressions', '20 10 * * *', 'uptime_latency_monitor_sweep.requested', 'name: "[Daily] Uptime & Latency Monitor Sweep"

# category: Infrastructure & Ops
# description: Review uptime and latency monitors for regressions
# template: this workflow is one of 50 daily job templates generated to match the
#            job template seeded in database/seeds/0001_daily_job_templates.sql
#
# Disabled by default (schedule is commented out) so it does not start running the
# moment this file is merged. Uncomment the `schedule:` block below to run it daily,
# or trigger it manually / via the ProcureIQ GitHub dispatch UI (repository_dispatch).
on:
#  schedule:
#    - cron: ''20 10 * * *''
  workflow_dispatch:
  repository_dispatch:
    types: [uptime_latency_monitor_sweep.requested]

permissions:
  contents: read

jobs:
  url-health-check:
    runs-on: ubuntu-latest
    steps:
      - name: Check configured URL (skips gracefully if not set)
        env:
          CHECK_URL: ${{ secrets.HEALTHCHECK_URL }}
        run: |
          if [ -z "$CHECK_URL" ]; then
            echo "No HEALTHCHECK_URL secret configured."
            echo "Set the HEALTHCHECK_URL repository secret to the endpoint you want this job to monitor."
            echo "Skipping: Review uptime and latency monitors for regressions"
            exit 0
          fi

          echo "Checking $CHECK_URL ..."
          status=$(curl -s -o /dev/null -w "%{http_code}" "$CHECK_URL")
          echo "HTTP status: $status"
          if [ "$status" -lt 200 ] || [ "$status" -ge 400 ]; then
            echo "::error::$CHECK_URL returned HTTP $status"
            exit 1
          fi
'),
    (95032, 'Cache Invalidation Sweep', 'Infrastructure & Ops', 'Sweep stale cache entries flagged for invalidation', '25 10 * * *', 'cache_invalidation_sweep.requested', 'name: "[Daily] Cache Invalidation Sweep"

# category: Infrastructure & Ops
# description: Sweep stale cache entries flagged for invalidation
# template: this workflow is one of 50 daily job templates generated to match the
#            job template seeded in database/seeds/0001_daily_job_templates.sql
#
# Disabled by default (schedule is commented out) so it does not start running the
# moment this file is merged. Uncomment the `schedule:` block below to run it daily,
# or trigger it manually / via the ProcureIQ GitHub dispatch UI (repository_dispatch).
on:
#  schedule:
#    - cron: ''25 10 * * *''
  workflow_dispatch:
  repository_dispatch:
    types: [cache_invalidation_sweep.requested]

permissions:
  contents: read
  issues: write

jobs:
  daily-checklist:
    runs-on: ubuntu-latest
    steps:
      - name: Create or update daily checklist issue
        uses: actions/github-script@v7
        with:
          script: |
            const title = "[Daily] Cache Invalidation Sweep";
            const body = [
              "**Category:** Infrastructure & Ops",
              "",
              "Sweep stale cache entries flagged for invalidation",
              "",
              `_Triggered by ${context.eventName} on ${new Date().toISOString().slice(0, 10)}._`,
              "",
              "- [ ] Reviewed and completed for today"
            ].join("\n");

            const { data: issues } = await github.rest.issues.listForRepo({
              owner: context.repo.owner,
              repo: context.repo.repo,
              state: "open",
              labels: "daily-job",
            });

            const existing = issues.find((i) => i.title === title);
            if (existing) {
              await github.rest.issues.createComment({
                owner: context.repo.owner,
                repo: context.repo.repo,
                issue_number: existing.number,
                body: `Reminder: this daily task is still open.\n\n${body}`,
              });
            } else {
              await github.rest.issues.create({
                owner: context.repo.owner,
                repo: context.repo.repo,
                title,
                body,
                labels: ["daily-job"],
              });
            }
'),
    (95033, 'Queue Backlog Check', 'Infrastructure & Ops', 'Check background job/message queue backlog depth', '30 10 * * *', 'queue_backlog_check.requested', 'name: "[Daily] Queue Backlog Check"

# category: Infrastructure & Ops
# description: Check background job/message queue backlog depth
# template: this workflow is one of 50 daily job templates generated to match the
#            job template seeded in database/seeds/0001_daily_job_templates.sql
#
# Disabled by default (schedule is commented out) so it does not start running the
# moment this file is merged. Uncomment the `schedule:` block below to run it daily,
# or trigger it manually / via the ProcureIQ GitHub dispatch UI (repository_dispatch).
on:
#  schedule:
#    - cron: ''30 10 * * *''
  workflow_dispatch:
  repository_dispatch:
    types: [queue_backlog_check.requested]

permissions:
  contents: read
  issues: write

jobs:
  daily-checklist:
    runs-on: ubuntu-latest
    steps:
      - name: Create or update daily checklist issue
        uses: actions/github-script@v7
        with:
          script: |
            const title = "[Daily] Queue Backlog Check";
            const body = [
              "**Category:** Infrastructure & Ops",
              "",
              "Check background job/message queue backlog depth",
              "",
              `_Triggered by ${context.eventName} on ${new Date().toISOString().slice(0, 10)}._`,
              "",
              "- [ ] Reviewed and completed for today"
            ].join("\n");

            const { data: issues } = await github.rest.issues.listForRepo({
              owner: context.repo.owner,
              repo: context.repo.repo,
              state: "open",
              labels: "daily-job",
            });

            const existing = issues.find((i) => i.title === title);
            if (existing) {
              await github.rest.issues.createComment({
                owner: context.repo.owner,
                repo: context.repo.repo,
                issue_number: existing.number,
                body: `Reminder: this daily task is still open.\n\n${body}`,
              });
            } else {
              await github.rest.issues.create({
                owner: context.repo.owner,
                repo: context.repo.repo,
                title,
                body,
                labels: ["daily-job"],
              });
            }
'),
    (95034, 'Daily Sales Report Compile', 'Data & Reporting', 'Compile and distribute the daily sales report', '0 11 * * *', 'daily_sales_report_compile.requested', 'name: "[Daily] Daily Sales Report Compile"

# category: Data & Reporting
# description: Compile and distribute the daily sales report
# template: this workflow is one of 50 daily job templates generated to match the
#            job template seeded in database/seeds/0001_daily_job_templates.sql
#
# Disabled by default (schedule is commented out) so it does not start running the
# moment this file is merged. Uncomment the `schedule:` block below to run it daily,
# or trigger it manually / via the ProcureIQ GitHub dispatch UI (repository_dispatch).
on:
#  schedule:
#    - cron: ''0 11 * * *''
  workflow_dispatch:
  repository_dispatch:
    types: [daily_sales_report_compile.requested]

permissions:
  contents: read
  issues: write

jobs:
  daily-checklist:
    runs-on: ubuntu-latest
    steps:
      - name: Create or update daily checklist issue
        uses: actions/github-script@v7
        with:
          script: |
            const title = "[Daily] Daily Sales Report Compile";
            const body = [
              "**Category:** Data & Reporting",
              "",
              "Compile and distribute the daily sales report",
              "",
              `_Triggered by ${context.eventName} on ${new Date().toISOString().slice(0, 10)}._`,
              "",
              "- [ ] Reviewed and completed for today"
            ].join("\n");

            const { data: issues } = await github.rest.issues.listForRepo({
              owner: context.repo.owner,
              repo: context.repo.repo,
              state: "open",
              labels: "daily-job",
            });

            const existing = issues.find((i) => i.title === title);
            if (existing) {
              await github.rest.issues.createComment({
                owner: context.repo.owner,
                repo: context.repo.repo,
                issue_number: existing.number,
                body: `Reminder: this daily task is still open.\n\n${body}`,
              });
            } else {
              await github.rest.issues.create({
                owner: context.repo.owner,
                repo: context.repo.repo,
                title,
                body,
                labels: ["daily-job"],
              });
            }
'),
    (95035, 'Analytics Rollup Job', 'Data & Reporting', 'Roll up event analytics into daily aggregate tables', '5 11 * * *', 'analytics_rollup_job.requested', 'name: "[Daily] Analytics Rollup Job"

# category: Data & Reporting
# description: Roll up event analytics into daily aggregate tables
# template: this workflow is one of 50 daily job templates generated to match the
#            job template seeded in database/seeds/0001_daily_job_templates.sql
#
# Disabled by default (schedule is commented out) so it does not start running the
# moment this file is merged. Uncomment the `schedule:` block below to run it daily,
# or trigger it manually / via the ProcureIQ GitHub dispatch UI (repository_dispatch).
on:
#  schedule:
#    - cron: ''5 11 * * *''
  workflow_dispatch:
  repository_dispatch:
    types: [analytics_rollup_job.requested]

permissions:
  contents: read
  issues: write

jobs:
  daily-checklist:
    runs-on: ubuntu-latest
    steps:
      - name: Create or update daily checklist issue
        uses: actions/github-script@v7
        with:
          script: |
            const title = "[Daily] Analytics Rollup Job";
            const body = [
              "**Category:** Data & Reporting",
              "",
              "Roll up event analytics into daily aggregate tables",
              "",
              `_Triggered by ${context.eventName} on ${new Date().toISOString().slice(0, 10)}._`,
              "",
              "- [ ] Reviewed and completed for today"
            ].join("\n");

            const { data: issues } = await github.rest.issues.listForRepo({
              owner: context.repo.owner,
              repo: context.repo.repo,
              state: "open",
              labels: "daily-job",
            });

            const existing = issues.find((i) => i.title === title);
            if (existing) {
              await github.rest.issues.createComment({
                owner: context.repo.owner,
                repo: context.repo.repo,
                issue_number: existing.number,
                body: `Reminder: this daily task is still open.\n\n${body}`,
              });
            } else {
              await github.rest.issues.create({
                owner: context.repo.owner,
                repo: context.repo.repo,
                title,
                body,
                labels: ["daily-job"],
              });
            }
'),
    (95036, 'Data Quality Validation', 'Data & Reporting', 'Run data quality checks against warehouse tables', '10 11 * * *', 'data_quality_validation.requested', 'name: "[Daily] Data Quality Validation"

# category: Data & Reporting
# description: Run data quality checks against warehouse tables
# template: this workflow is one of 50 daily job templates generated to match the
#            job template seeded in database/seeds/0001_daily_job_templates.sql
#
# Disabled by default (schedule is commented out) so it does not start running the
# moment this file is merged. Uncomment the `schedule:` block below to run it daily,
# or trigger it manually / via the ProcureIQ GitHub dispatch UI (repository_dispatch).
on:
#  schedule:
#    - cron: ''10 11 * * *''
  workflow_dispatch:
  repository_dispatch:
    types: [data_quality_validation.requested]

permissions:
  contents: read
  issues: write

jobs:
  daily-checklist:
    runs-on: ubuntu-latest
    steps:
      - name: Create or update daily checklist issue
        uses: actions/github-script@v7
        with:
          script: |
            const title = "[Daily] Data Quality Validation";
            const body = [
              "**Category:** Data & Reporting",
              "",
              "Run data quality checks against warehouse tables",
              "",
              `_Triggered by ${context.eventName} on ${new Date().toISOString().slice(0, 10)}._`,
              "",
              "- [ ] Reviewed and completed for today"
            ].join("\n");

            const { data: issues } = await github.rest.issues.listForRepo({
              owner: context.repo.owner,
              repo: context.repo.repo,
              state: "open",
              labels: "daily-job",
            });

            const existing = issues.find((i) => i.title === title);
            if (existing) {
              await github.rest.issues.createComment({
                owner: context.repo.owner,
                repo: context.repo.repo,
                issue_number: existing.number,
                body: `Reminder: this daily task is still open.\n\n${body}`,
              });
            } else {
              await github.rest.issues.create({
                owner: context.repo.owner,
                repo: context.repo.repo,
                title,
                body,
                labels: ["daily-job"],
              });
            }
'),
    (95037, 'ETL Pipeline Health Check', 'Data & Reporting', 'Verify ETL pipelines completed without errors', '15 11 * * *', 'etl_pipeline_health_check.requested', 'name: "[Daily] ETL Pipeline Health Check"

# category: Data & Reporting
# description: Verify ETL pipelines completed without errors
# template: this workflow is one of 50 daily job templates generated to match the
#            job template seeded in database/seeds/0001_daily_job_templates.sql
#
# Disabled by default (schedule is commented out) so it does not start running the
# moment this file is merged. Uncomment the `schedule:` block below to run it daily,
# or trigger it manually / via the ProcureIQ GitHub dispatch UI (repository_dispatch).
on:
#  schedule:
#    - cron: ''15 11 * * *''
  workflow_dispatch:
  repository_dispatch:
    types: [etl_pipeline_health_check.requested]

permissions:
  contents: read
  issues: write

jobs:
  daily-checklist:
    runs-on: ubuntu-latest
    steps:
      - name: Create or update daily checklist issue
        uses: actions/github-script@v7
        with:
          script: |
            const title = "[Daily] ETL Pipeline Health Check";
            const body = [
              "**Category:** Data & Reporting",
              "",
              "Verify ETL pipelines completed without errors",
              "",
              `_Triggered by ${context.eventName} on ${new Date().toISOString().slice(0, 10)}._`,
              "",
              "- [ ] Reviewed and completed for today"
            ].join("\n");

            const { data: issues } = await github.rest.issues.listForRepo({
              owner: context.repo.owner,
              repo: context.repo.repo,
              state: "open",
              labels: "daily-job",
            });

            const existing = issues.find((i) => i.title === title);
            if (existing) {
              await github.rest.issues.createComment({
                owner: context.repo.owner,
                repo: context.repo.repo,
                issue_number: existing.number,
                body: `Reminder: this daily task is still open.\n\n${body}`,
              });
            } else {
              await github.rest.issues.create({
                owner: context.repo.owner,
                repo: context.repo.repo,
                title,
                body,
                labels: ["daily-job"],
              });
            }
'),
    (95038, 'Revenue Reconciliation Report', 'Data & Reporting', 'Reconcile daily revenue figures against billing records', '20 11 * * *', 'revenue_reconciliation_report.requested', 'name: "[Daily] Revenue Reconciliation Report"

# category: Data & Reporting
# description: Reconcile daily revenue figures against billing records
# template: this workflow is one of 50 daily job templates generated to match the
#            job template seeded in database/seeds/0001_daily_job_templates.sql
#
# Disabled by default (schedule is commented out) so it does not start running the
# moment this file is merged. Uncomment the `schedule:` block below to run it daily,
# or trigger it manually / via the ProcureIQ GitHub dispatch UI (repository_dispatch).
on:
#  schedule:
#    - cron: ''20 11 * * *''
  workflow_dispatch:
  repository_dispatch:
    types: [revenue_reconciliation_report.requested]

permissions:
  contents: read
  issues: write

jobs:
  daily-checklist:
    runs-on: ubuntu-latest
    steps:
      - name: Create or update daily checklist issue
        uses: actions/github-script@v7
        with:
          script: |
            const title = "[Daily] Revenue Reconciliation Report";
            const body = [
              "**Category:** Data & Reporting",
              "",
              "Reconcile daily revenue figures against billing records",
              "",
              `_Triggered by ${context.eventName} on ${new Date().toISOString().slice(0, 10)}._`,
              "",
              "- [ ] Reviewed and completed for today"
            ].join("\n");

            const { data: issues } = await github.rest.issues.listForRepo({
              owner: context.repo.owner,
              repo: context.repo.repo,
              state: "open",
              labels: "daily-job",
            });

            const existing = issues.find((i) => i.title === title);
            if (existing) {
              await github.rest.issues.createComment({
                owner: context.repo.owner,
                repo: context.repo.repo,
                issue_number: existing.number,
                body: `Reminder: this daily task is still open.\n\n${body}`,
              });
            } else {
              await github.rest.issues.create({
                owner: context.repo.owner,
                repo: context.repo.repo,
                title,
                body,
                labels: ["daily-job"],
              });
            }
'),
    (95039, 'KPI Dashboard Refresh', 'Data & Reporting', 'Refresh executive KPI dashboards with latest metrics', '25 11 * * *', 'kpi_dashboard_refresh.requested', 'name: "[Daily] KPI Dashboard Refresh"

# category: Data & Reporting
# description: Refresh executive KPI dashboards with latest metrics
# template: this workflow is one of 50 daily job templates generated to match the
#            job template seeded in database/seeds/0001_daily_job_templates.sql
#
# Disabled by default (schedule is commented out) so it does not start running the
# moment this file is merged. Uncomment the `schedule:` block below to run it daily,
# or trigger it manually / via the ProcureIQ GitHub dispatch UI (repository_dispatch).
on:
#  schedule:
#    - cron: ''25 11 * * *''
  workflow_dispatch:
  repository_dispatch:
    types: [kpi_dashboard_refresh.requested]

permissions:
  contents: read
  issues: write

jobs:
  daily-checklist:
    runs-on: ubuntu-latest
    steps:
      - name: Create or update daily checklist issue
        uses: actions/github-script@v7
        with:
          script: |
            const title = "[Daily] KPI Dashboard Refresh";
            const body = [
              "**Category:** Data & Reporting",
              "",
              "Refresh executive KPI dashboards with latest metrics",
              "",
              `_Triggered by ${context.eventName} on ${new Date().toISOString().slice(0, 10)}._`,
              "",
              "- [ ] Reviewed and completed for today"
            ].join("\n");

            const { data: issues } = await github.rest.issues.listForRepo({
              owner: context.repo.owner,
              repo: context.repo.repo,
              state: "open",
              labels: "daily-job",
            });

            const existing = issues.find((i) => i.title === title);
            if (existing) {
              await github.rest.issues.createComment({
                owner: context.repo.owner,
                repo: context.repo.repo,
                issue_number: existing.number,
                body: `Reminder: this daily task is still open.\n\n${body}`,
              });
            } else {
              await github.rest.issues.create({
                owner: context.repo.owner,
                repo: context.repo.repo,
                title,
                body,
                labels: ["daily-job"],
              });
            }
'),
    (95040, 'Vendor Insurance Certificate Sweep', 'Vendor & Contract Management', 'Check for vendor insurance certificates expiring soon', '0 12 * * *', 'vendor_insurance_certificate_sweep.requested', 'name: "[Daily] Vendor Insurance Certificate Sweep"

# category: Vendor & Contract Management
# description: Check for vendor insurance certificates expiring soon
# template: this workflow is one of 50 daily job templates generated to match the
#            job template seeded in database/seeds/0001_daily_job_templates.sql
#
# Disabled by default (schedule is commented out) so it does not start running the
# moment this file is merged. Uncomment the `schedule:` block below to run it daily,
# or trigger it manually / via the ProcureIQ GitHub dispatch UI (repository_dispatch).
on:
#  schedule:
#    - cron: ''0 12 * * *''
  workflow_dispatch:
  repository_dispatch:
    types: [vendor_insurance_certificate_sweep.requested]

permissions:
  contents: read
  issues: write

jobs:
  daily-checklist:
    runs-on: ubuntu-latest
    steps:
      - name: Create or update daily checklist issue
        uses: actions/github-script@v7
        with:
          script: |
            const title = "[Daily] Vendor Insurance Certificate Sweep";
            const body = [
              "**Category:** Vendor & Contract Management",
              "",
              "Check for vendor insurance certificates expiring soon",
              "",
              `_Triggered by ${context.eventName} on ${new Date().toISOString().slice(0, 10)}._`,
              "",
              "- [ ] Reviewed and completed for today"
            ].join("\n");

            const { data: issues } = await github.rest.issues.listForRepo({
              owner: context.repo.owner,
              repo: context.repo.repo,
              state: "open",
              labels: "daily-job",
            });

            const existing = issues.find((i) => i.title === title);
            if (existing) {
              await github.rest.issues.createComment({
                owner: context.repo.owner,
                repo: context.repo.repo,
                issue_number: existing.number,
                body: `Reminder: this daily task is still open.\n\n${body}`,
              });
            } else {
              await github.rest.issues.create({
                owner: context.repo.owner,
                repo: context.repo.repo,
                title,
                body,
                labels: ["daily-job"],
              });
            }
'),
    (95041, 'Contract Expiry Sweep', 'Vendor & Contract Management', 'Flag vendor contracts approaching their expiry date', '5 12 * * *', 'contract_expiry_sweep.requested', 'name: "[Daily] Contract Expiry Sweep"

# category: Vendor & Contract Management
# description: Flag vendor contracts approaching their expiry date
# template: this workflow is one of 50 daily job templates generated to match the
#            job template seeded in database/seeds/0001_daily_job_templates.sql
#
# Disabled by default (schedule is commented out) so it does not start running the
# moment this file is merged. Uncomment the `schedule:` block below to run it daily,
# or trigger it manually / via the ProcureIQ GitHub dispatch UI (repository_dispatch).
on:
#  schedule:
#    - cron: ''5 12 * * *''
  workflow_dispatch:
  repository_dispatch:
    types: [contract_expiry_sweep.requested]

permissions:
  contents: read
  issues: write

jobs:
  daily-checklist:
    runs-on: ubuntu-latest
    steps:
      - name: Create or update daily checklist issue
        uses: actions/github-script@v7
        with:
          script: |
            const title = "[Daily] Contract Expiry Sweep";
            const body = [
              "**Category:** Vendor & Contract Management",
              "",
              "Flag vendor contracts approaching their expiry date",
              "",
              `_Triggered by ${context.eventName} on ${new Date().toISOString().slice(0, 10)}._`,
              "",
              "- [ ] Reviewed and completed for today"
            ].join("\n");

            const { data: issues } = await github.rest.issues.listForRepo({
              owner: context.repo.owner,
              repo: context.repo.repo,
              state: "open",
              labels: "daily-job",
            });

            const existing = issues.find((i) => i.title === title);
            if (existing) {
              await github.rest.issues.createComment({
                owner: context.repo.owner,
                repo: context.repo.repo,
                issue_number: existing.number,
                body: `Reminder: this daily task is still open.\n\n${body}`,
              });
            } else {
              await github.rest.issues.create({
                owner: context.repo.owner,
                repo: context.repo.repo,
                title,
                body,
                labels: ["daily-job"],
              });
            }
'),
    (95042, 'Vendor SLA Compliance Check', 'Vendor & Contract Management', 'Check vendor performance against contracted SLAs', '10 12 * * *', 'vendor_sla_compliance_check.requested', 'name: "[Daily] Vendor SLA Compliance Check"

# category: Vendor & Contract Management
# description: Check vendor performance against contracted SLAs
# template: this workflow is one of 50 daily job templates generated to match the
#            job template seeded in database/seeds/0001_daily_job_templates.sql
#
# Disabled by default (schedule is commented out) so it does not start running the
# moment this file is merged. Uncomment the `schedule:` block below to run it daily,
# or trigger it manually / via the ProcureIQ GitHub dispatch UI (repository_dispatch).
on:
#  schedule:
#    - cron: ''10 12 * * *''
  workflow_dispatch:
  repository_dispatch:
    types: [vendor_sla_compliance_check.requested]

permissions:
  contents: read
  issues: write

jobs:
  daily-checklist:
    runs-on: ubuntu-latest
    steps:
      - name: Create or update daily checklist issue
        uses: actions/github-script@v7
        with:
          script: |
            const title = "[Daily] Vendor SLA Compliance Check";
            const body = [
              "**Category:** Vendor & Contract Management",
              "",
              "Check vendor performance against contracted SLAs",
              "",
              `_Triggered by ${context.eventName} on ${new Date().toISOString().slice(0, 10)}._`,
              "",
              "- [ ] Reviewed and completed for today"
            ].join("\n");

            const { data: issues } = await github.rest.issues.listForRepo({
              owner: context.repo.owner,
              repo: context.repo.repo,
              state: "open",
              labels: "daily-job",
            });

            const existing = issues.find((i) => i.title === title);
            if (existing) {
              await github.rest.issues.createComment({
                owner: context.repo.owner,
                repo: context.repo.repo,
                issue_number: existing.number,
                body: `Reminder: this daily task is still open.\n\n${body}`,
              });
            } else {
              await github.rest.issues.create({
                owner: context.repo.owner,
                repo: context.repo.repo,
                title,
                body,
                labels: ["daily-job"],
              });
            }
'),
    (95043, 'Purchase Order Reconciliation', 'Vendor & Contract Management', 'Reconcile open purchase orders against received goods', '15 12 * * *', 'purchase_order_reconciliation.requested', 'name: "[Daily] Purchase Order Reconciliation"

# category: Vendor & Contract Management
# description: Reconcile open purchase orders against received goods
# template: this workflow is one of 50 daily job templates generated to match the
#            job template seeded in database/seeds/0001_daily_job_templates.sql
#
# Disabled by default (schedule is commented out) so it does not start running the
# moment this file is merged. Uncomment the `schedule:` block below to run it daily,
# or trigger it manually / via the ProcureIQ GitHub dispatch UI (repository_dispatch).
on:
#  schedule:
#    - cron: ''15 12 * * *''
  workflow_dispatch:
  repository_dispatch:
    types: [purchase_order_reconciliation.requested]

permissions:
  contents: read
  issues: write

jobs:
  daily-checklist:
    runs-on: ubuntu-latest
    steps:
      - name: Create or update daily checklist issue
        uses: actions/github-script@v7
        with:
          script: |
            const title = "[Daily] Purchase Order Reconciliation";
            const body = [
              "**Category:** Vendor & Contract Management",
              "",
              "Reconcile open purchase orders against received goods",
              "",
              `_Triggered by ${context.eventName} on ${new Date().toISOString().slice(0, 10)}._`,
              "",
              "- [ ] Reviewed and completed for today"
            ].join("\n");

            const { data: issues } = await github.rest.issues.listForRepo({
              owner: context.repo.owner,
              repo: context.repo.repo,
              state: "open",
              labels: "daily-job",
            });

            const existing = issues.find((i) => i.title === title);
            if (existing) {
              await github.rest.issues.createComment({
                owner: context.repo.owner,
                repo: context.repo.repo,
                issue_number: existing.number,
                body: `Reminder: this daily task is still open.\n\n${body}`,
              });
            } else {
              await github.rest.issues.create({
                owner: context.repo.owner,
                repo: context.repo.repo,
                title,
                body,
                labels: ["daily-job"],
              });
            }
'),
    (95044, 'Invoice Approval Reminder Sweep', 'Vendor & Contract Management', 'Remind approvers of invoices pending sign-off', '20 12 * * *', 'invoice_approval_reminder_sweep.requested', 'name: "[Daily] Invoice Approval Reminder Sweep"

# category: Vendor & Contract Management
# description: Remind approvers of invoices pending sign-off
# template: this workflow is one of 50 daily job templates generated to match the
#            job template seeded in database/seeds/0001_daily_job_templates.sql
#
# Disabled by default (schedule is commented out) so it does not start running the
# moment this file is merged. Uncomment the `schedule:` block below to run it daily,
# or trigger it manually / via the ProcureIQ GitHub dispatch UI (repository_dispatch).
on:
#  schedule:
#    - cron: ''20 12 * * *''
  workflow_dispatch:
  repository_dispatch:
    types: [invoice_approval_reminder_sweep.requested]

permissions:
  contents: read
  issues: write

jobs:
  daily-checklist:
    runs-on: ubuntu-latest
    steps:
      - name: Create or update daily checklist issue
        uses: actions/github-script@v7
        with:
          script: |
            const title = "[Daily] Invoice Approval Reminder Sweep";
            const body = [
              "**Category:** Vendor & Contract Management",
              "",
              "Remind approvers of invoices pending sign-off",
              "",
              `_Triggered by ${context.eventName} on ${new Date().toISOString().slice(0, 10)}._`,
              "",
              "- [ ] Reviewed and completed for today"
            ].join("\n");

            const { data: issues } = await github.rest.issues.listForRepo({
              owner: context.repo.owner,
              repo: context.repo.repo,
              state: "open",
              labels: "daily-job",
            });

            const existing = issues.find((i) => i.title === title);
            if (existing) {
              await github.rest.issues.createComment({
                owner: context.repo.owner,
                repo: context.repo.repo,
                issue_number: existing.number,
                body: `Reminder: this daily task is still open.\n\n${body}`,
              });
            } else {
              await github.rest.issues.create({
                owner: context.repo.owner,
                repo: context.repo.repo,
                title,
                body,
                labels: ["daily-job"],
              });
            }
'),
    (95045, 'Procurement Budget Threshold Check', 'Vendor & Contract Management', 'Check procurement spend against budget thresholds', '25 12 * * *', 'procurement_budget_threshold_check.requested', 'name: "[Daily] Procurement Budget Threshold Check"

# category: Vendor & Contract Management
# description: Check procurement spend against budget thresholds
# template: this workflow is one of 50 daily job templates generated to match the
#            job template seeded in database/seeds/0001_daily_job_templates.sql
#
# Disabled by default (schedule is commented out) so it does not start running the
# moment this file is merged. Uncomment the `schedule:` block below to run it daily,
# or trigger it manually / via the ProcureIQ GitHub dispatch UI (repository_dispatch).
on:
#  schedule:
#    - cron: ''25 12 * * *''
  workflow_dispatch:
  repository_dispatch:
    types: [procurement_budget_threshold_check.requested]

permissions:
  contents: read
  issues: write

jobs:
  daily-checklist:
    runs-on: ubuntu-latest
    steps:
      - name: Create or update daily checklist issue
        uses: actions/github-script@v7
        with:
          script: |
            const title = "[Daily] Procurement Budget Threshold Check";
            const body = [
              "**Category:** Vendor & Contract Management",
              "",
              "Check procurement spend against budget thresholds",
              "",
              `_Triggered by ${context.eventName} on ${new Date().toISOString().slice(0, 10)}._`,
              "",
              "- [ ] Reviewed and completed for today"
            ].join("\n");

            const { data: issues } = await github.rest.issues.listForRepo({
              owner: context.repo.owner,
              repo: context.repo.repo,
              state: "open",
              labels: "daily-job",
            });

            const existing = issues.find((i) => i.title === title);
            if (existing) {
              await github.rest.issues.createComment({
                owner: context.repo.owner,
                repo: context.repo.repo,
                issue_number: existing.number,
                body: `Reminder: this daily task is still open.\n\n${body}`,
              });
            } else {
              await github.rest.issues.create({
                owner: context.repo.owner,
                repo: context.repo.repo,
                title,
                body,
                labels: ["daily-job"],
              });
            }
'),
    (95046, 'Customer Churn Risk Scan', 'Customer Success', 'Scan customer usage data for churn risk signals', '0 13 * * *', 'customer_churn_risk_scan.requested', 'name: "[Daily] Customer Churn Risk Scan"

# category: Customer Success
# description: Scan customer usage data for churn risk signals
# template: this workflow is one of 50 daily job templates generated to match the
#            job template seeded in database/seeds/0001_daily_job_templates.sql
#
# Disabled by default (schedule is commented out) so it does not start running the
# moment this file is merged. Uncomment the `schedule:` block below to run it daily,
# or trigger it manually / via the ProcureIQ GitHub dispatch UI (repository_dispatch).
on:
#  schedule:
#    - cron: ''0 13 * * *''
  workflow_dispatch:
  repository_dispatch:
    types: [customer_churn_risk_scan.requested]

permissions:
  contents: read
  issues: write

jobs:
  daily-checklist:
    runs-on: ubuntu-latest
    steps:
      - name: Create or update daily checklist issue
        uses: actions/github-script@v7
        with:
          script: |
            const title = "[Daily] Customer Churn Risk Scan";
            const body = [
              "**Category:** Customer Success",
              "",
              "Scan customer usage data for churn risk signals",
              "",
              `_Triggered by ${context.eventName} on ${new Date().toISOString().slice(0, 10)}._`,
              "",
              "- [ ] Reviewed and completed for today"
            ].join("\n");

            const { data: issues } = await github.rest.issues.listForRepo({
              owner: context.repo.owner,
              repo: context.repo.repo,
              state: "open",
              labels: "daily-job",
            });

            const existing = issues.find((i) => i.title === title);
            if (existing) {
              await github.rest.issues.createComment({
                owner: context.repo.owner,
                repo: context.repo.repo,
                issue_number: existing.number,
                body: `Reminder: this daily task is still open.\n\n${body}`,
              });
            } else {
              await github.rest.issues.create({
                owner: context.repo.owner,
                repo: context.repo.repo,
                title,
                body,
                labels: ["daily-job"],
              });
            }
'),
    (95047, 'Renewal Reminder Sweep', 'Customer Success', 'Remind account owners of upcoming renewal dates', '5 13 * * *', 'renewal_reminder_sweep.requested', 'name: "[Daily] Renewal Reminder Sweep"

# category: Customer Success
# description: Remind account owners of upcoming renewal dates
# template: this workflow is one of 50 daily job templates generated to match the
#            job template seeded in database/seeds/0001_daily_job_templates.sql
#
# Disabled by default (schedule is commented out) so it does not start running the
# moment this file is merged. Uncomment the `schedule:` block below to run it daily,
# or trigger it manually / via the ProcureIQ GitHub dispatch UI (repository_dispatch).
on:
#  schedule:
#    - cron: ''5 13 * * *''
  workflow_dispatch:
  repository_dispatch:
    types: [renewal_reminder_sweep.requested]

permissions:
  contents: read
  issues: write

jobs:
  daily-checklist:
    runs-on: ubuntu-latest
    steps:
      - name: Create or update daily checklist issue
        uses: actions/github-script@v7
        with:
          script: |
            const title = "[Daily] Renewal Reminder Sweep";
            const body = [
              "**Category:** Customer Success",
              "",
              "Remind account owners of upcoming renewal dates",
              "",
              `_Triggered by ${context.eventName} on ${new Date().toISOString().slice(0, 10)}._`,
              "",
              "- [ ] Reviewed and completed for today"
            ].join("\n");

            const { data: issues } = await github.rest.issues.listForRepo({
              owner: context.repo.owner,
              repo: context.repo.repo,
              state: "open",
              labels: "daily-job",
            });

            const existing = issues.find((i) => i.title === title);
            if (existing) {
              await github.rest.issues.createComment({
                owner: context.repo.owner,
                repo: context.repo.repo,
                issue_number: existing.number,
                body: `Reminder: this daily task is still open.\n\n${body}`,
              });
            } else {
              await github.rest.issues.create({
                owner: context.repo.owner,
                repo: context.repo.repo,
                title,
                body,
                labels: ["daily-job"],
              });
            }
'),
    (95048, 'Onboarding Progress Check', 'Customer Success', 'Check new customer onboarding progress against milestones', '10 13 * * *', 'onboarding_progress_check.requested', 'name: "[Daily] Onboarding Progress Check"

# category: Customer Success
# description: Check new customer onboarding progress against milestones
# template: this workflow is one of 50 daily job templates generated to match the
#            job template seeded in database/seeds/0001_daily_job_templates.sql
#
# Disabled by default (schedule is commented out) so it does not start running the
# moment this file is merged. Uncomment the `schedule:` block below to run it daily,
# or trigger it manually / via the ProcureIQ GitHub dispatch UI (repository_dispatch).
on:
#  schedule:
#    - cron: ''10 13 * * *''
  workflow_dispatch:
  repository_dispatch:
    types: [onboarding_progress_check.requested]

permissions:
  contents: read
  issues: write

jobs:
  daily-checklist:
    runs-on: ubuntu-latest
    steps:
      - name: Create or update daily checklist issue
        uses: actions/github-script@v7
        with:
          script: |
            const title = "[Daily] Onboarding Progress Check";
            const body = [
              "**Category:** Customer Success",
              "",
              "Check new customer onboarding progress against milestones",
              "",
              `_Triggered by ${context.eventName} on ${new Date().toISOString().slice(0, 10)}._`,
              "",
              "- [ ] Reviewed and completed for today"
            ].join("\n");

            const { data: issues } = await github.rest.issues.listForRepo({
              owner: context.repo.owner,
              repo: context.repo.repo,
              state: "open",
              labels: "daily-job",
            });

            const existing = issues.find((i) => i.title === title);
            if (existing) {
              await github.rest.issues.createComment({
                owner: context.repo.owner,
                repo: context.repo.repo,
                issue_number: existing.number,
                body: `Reminder: this daily task is still open.\n\n${body}`,
              });
            } else {
              await github.rest.issues.create({
                owner: context.repo.owner,
                repo: context.repo.repo,
                title,
                body,
                labels: ["daily-job"],
              });
            }
'),
    (95049, 'NPS Survey Dispatch Check', 'Customer Success', 'Verify scheduled NPS surveys were dispatched', '15 13 * * *', 'nps_survey_dispatch_check.requested', 'name: "[Daily] NPS Survey Dispatch Check"

# category: Customer Success
# description: Verify scheduled NPS surveys were dispatched
# template: this workflow is one of 50 daily job templates generated to match the
#            job template seeded in database/seeds/0001_daily_job_templates.sql
#
# Disabled by default (schedule is commented out) so it does not start running the
# moment this file is merged. Uncomment the `schedule:` block below to run it daily,
# or trigger it manually / via the ProcureIQ GitHub dispatch UI (repository_dispatch).
on:
#  schedule:
#    - cron: ''15 13 * * *''
  workflow_dispatch:
  repository_dispatch:
    types: [nps_survey_dispatch_check.requested]

permissions:
  contents: read
  issues: write

jobs:
  daily-checklist:
    runs-on: ubuntu-latest
    steps:
      - name: Create or update daily checklist issue
        uses: actions/github-script@v7
        with:
          script: |
            const title = "[Daily] NPS Survey Dispatch Check";
            const body = [
              "**Category:** Customer Success",
              "",
              "Verify scheduled NPS surveys were dispatched",
              "",
              `_Triggered by ${context.eventName} on ${new Date().toISOString().slice(0, 10)}._`,
              "",
              "- [ ] Reviewed and completed for today"
            ].join("\n");

            const { data: issues } = await github.rest.issues.listForRepo({
              owner: context.repo.owner,
              repo: context.repo.repo,
              state: "open",
              labels: "daily-job",
            });

            const existing = issues.find((i) => i.title === title);
            if (existing) {
              await github.rest.issues.createComment({
                owner: context.repo.owner,
                repo: context.repo.repo,
                issue_number: existing.number,
                body: `Reminder: this daily task is still open.\n\n${body}`,
              });
            } else {
              await github.rest.issues.create({
                owner: context.repo.owner,
                repo: context.repo.repo,
                title,
                body,
                labels: ["daily-job"],
              });
            }
'),
    (95050, 'Support SLA Breach Sweep', 'Customer Success', 'Flag open support tickets at risk of breaching SLA', '20 13 * * *', 'support_sla_breach_sweep.requested', 'name: "[Daily] Support SLA Breach Sweep"

# category: Customer Success
# description: Flag open support tickets at risk of breaching SLA
# template: this workflow is one of 50 daily job templates generated to match the
#            job template seeded in database/seeds/0001_daily_job_templates.sql
#
# Disabled by default (schedule is commented out) so it does not start running the
# moment this file is merged. Uncomment the `schedule:` block below to run it daily,
# or trigger it manually / via the ProcureIQ GitHub dispatch UI (repository_dispatch).
on:
#  schedule:
#    - cron: ''20 13 * * *''
  workflow_dispatch:
  repository_dispatch:
    types: [support_sla_breach_sweep.requested]

permissions:
  contents: read
  issues: write

jobs:
  daily-checklist:
    runs-on: ubuntu-latest
    steps:
      - name: Create or update daily checklist issue
        uses: actions/github-script@v7
        with:
          script: |
            const title = "[Daily] Support SLA Breach Sweep";
            const body = [
              "**Category:** Customer Success",
              "",
              "Flag open support tickets at risk of breaching SLA",
              "",
              `_Triggered by ${context.eventName} on ${new Date().toISOString().slice(0, 10)}._`,
              "",
              "- [ ] Reviewed and completed for today"
            ].join("\n");

            const { data: issues } = await github.rest.issues.listForRepo({
              owner: context.repo.owner,
              repo: context.repo.repo,
              state: "open",
              labels: "daily-job",
            });

            const existing = issues.find((i) => i.title === title);
            if (existing) {
              await github.rest.issues.createComment({
                owner: context.repo.owner,
                repo: context.repo.repo,
                issue_number: existing.number,
                body: `Reminder: this daily task is still open.\n\n${body}`,
              });
            } else {
              await github.rest.issues.create({
                owner: context.repo.owner,
                repo: context.repo.repo,
                title,
                body,
                labels: ["daily-job"],
              });
            }
')
ON CONFLICT (id) DO NOTHING;
