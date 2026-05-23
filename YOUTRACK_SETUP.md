# YouTrack Integration — Setup and Conventions

This document is the canonical guide for connecting our self-hosted YouTrack instance to the GitHub repository, importing the Phase A backlog, and enforcing the commit / branch / PR conventions that make the two systems behave as one.

Read it once at Phase A kickoff. Keep it open during the integration work. Update it whenever a convention changes.

---

## Topology

```
┌─────────────────────────────────────────────────────────────────────┐
│                                                                     │
│   YouTrack (self-hosted, Docker, localhost)                         │
│   ─────────────────────────────────────────                         │
│   Project key: TIME                                                 │
│   URL:         http://localhost:8080  (default)                     │
│   Truth for:   tickets, estimates, time-tracking, sprint planning   │
│                                                                     │
│              ▲                              ▲                       │
│              │ webhook + API token          │ commit messages       │
│              │ (push events from GitHub)    │ (TIME-NNN references) │
│              ▼                              ▼                       │
│                                                                     │
│   GitHub (github.com/watarikai96/TiME)                              │
│   ──────────────────────────────────────                            │
│   Truth for:   source code, PRs, CI, release tags                   │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

The integration is **one-way leaning**: GitHub events update YouTrack tickets (link commits, transition state on PR merge). YouTrack does not push back to GitHub except via your manual commits.

---

## Step 1 — Verify YouTrack is reachable

YouTrack is already running in Docker on your machine. Verify before any integration work:

```bash
# Confirm the container is up
docker ps --filter "name=youtrack"

# Confirm the HTTP endpoint responds
curl -I http://localhost:8080 | head -1
# Expect: HTTP/1.1 200 OK (or 302 to login)
```

If YouTrack is **offline-only** (not exposed beyond localhost), the GitHub webhook will need to reach it. Two options:

1. **Self-hosted runner**: Run a small GitHub Actions self-hosted runner on the same machine that hosts YouTrack. The runner is on the LAN, can reach `localhost:8080`, and posts updates directly. This is the recommended path because it keeps YouTrack genuinely offline.
2. **Tunnel** (Cloudflare Tunnel or Tailscale Funnel): Expose YouTrack at a public HTTPS URL just for GitHub webhook reception. Convenient but partially defeats the offline posture.

The setup below assumes **Option 1 (self-hosted runner)**.

---

## Step 2 — Create the YouTrack project

In the YouTrack web UI:

1. **Project** > Create new project
2. Name: `TiME`
3. Project ID / key: `TIME` (tickets will be `TIME-1`, `TIME-2`, …)
4. Description: paste a single sentence: "TiME — calm time management product. Source of truth at github.com/watarikai96/TiME. Strategy in BIBLE.md (private)."

Configure these fields under **Administration > Projects > TiME > Fields**:

| Field | Type | Values |
|---|---|---|
| Type | Enum (single) | Epic, Story, Task, Bug, Tech Debt, Spike |
| Priority | Enum (single) | Critical, High, Medium, Low |
| Estimation | Period | hours (use the built-in Period field) |
| Phase | Enum (single) | Phase A, Phase B, Phase C, Phase D |
| Week | Enum (single) | W1 through W13 (Phase A only — extend later) |
| Tags | Tags (multi) | freeform; see CSV column for starter set |

Configure these states under **Workflow > States**:

```
Open ─▸ In Progress ─▸ In Review ─▸ Done
                 └▸ Blocked ─┘
                 └▸ Cancelled
```

---

## Step 3 — Import the Phase A backlog

The Phase A backlog is shipped as `youtrack-import.csv` in the repo root.

1. In YouTrack: **Administration** > **Data Import** > **Import from CSV**
2. Upload `youtrack-import.csv`
3. Map the columns:

| CSV column | YouTrack field |
|---|---|
| `summary` | Summary |
| `description` | Description |
| `type` | Type |
| `priority` | Priority |
| `estimation` | Estimation (hours) |
| `tags` | Tags |

4. Preview a few rows; confirm field mapping; click **Import**.
5. Verify ticket count matches: expect **105 tickets** (9 epics + 96 stories / tasks / bugs / spikes).

Manually link epics to their child tickets via the YouTrack UI after import — child relationship is not in the CSV because it depends on the auto-generated IDs YouTrack assigns. A second pass after import is faster than scripting it.

---

## Step 4 — Generate an API token

YouTrack needs a token so GitHub events can be posted by the runner.

1. Click your avatar (top right) > **Profile**
2. **Authentication** tab > **New token**
3. Name: `github-integration`
4. Scope: `YouTrack` (read + write)
5. Copy the token. You will paste it as a GitHub Secret in Step 6.

Never commit the token. Never paste it into chat.

---

## Step 5 — Configure VCS Integration (read-only side)

YouTrack's native **VCS Integrations** feature can fetch commits from a Git repo and link them to tickets without any GitHub-side configuration. For our self-hosted setup with GitHub as the source, the practical approach is:

1. In YouTrack project: **Project Settings** > **VCS Integrations** > **New**
2. **Type**: GitHub
3. **URL**: `https://github.com/watarikai96/TiME`
4. **Authentication**: GitHub personal access token (create at github.com/settings/tokens with `repo` scope; paste here)
5. **Polling interval**: 5 minutes (acceptable for solo dev velocity)

This gets you **commit-to-ticket linking automatically** based on the `TIME-NNN` references in commit messages.

For real-time updates (state transitions on PR events), continue to Step 6.

---

## Step 6 — Set up the GitHub Actions self-hosted runner

On the same machine that runs YouTrack:

1. In GitHub repo: **Settings** > **Actions** > **Runners** > **New self-hosted runner**
2. Choose macOS (or Linux if running Docker on Linux); follow the displayed commands. Roughly:

```bash
# Create a runner directory
mkdir -p ~/actions-runner-time && cd ~/actions-runner-time

# Download (URL is generated by GitHub for your repo)
curl -o actions-runner.tar.gz -L https://github.com/actions/runner/releases/download/v2.319.1/actions-runner-osx-x64-2.319.1.tar.gz
tar xzf actions-runner.tar.gz

# Configure (use the token shown in the GitHub UI)
./config.sh --url https://github.com/watarikai96/TiME --token <REGISTRATION_TOKEN>

# Run (or install as a service)
./run.sh
```

3. Add a label to the runner: `youtrack-host`

4. In the GitHub repo settings, add secrets:
   - `YOUTRACK_URL` = `http://localhost:8080`
   - `YOUTRACK_TOKEN` = (the token from Step 4)

Now create `.github/workflows/youtrack-sync.yml`:

```yaml
name: YouTrack Sync

on:
  pull_request:
    types: [opened, closed, ready_for_review, converted_to_draft]
  push:
    branches: [main]

jobs:
  transition-state:
    runs-on: [self-hosted, youtrack-host]
    steps:
      - name: Extract TIME-NNN refs from commits and PR
        id: refs
        run: |
          # Gather TIME-NNN references from the commit messages or PR title
          if [ "${{ github.event_name }}" = "pull_request" ]; then
            REFS=$(echo "${{ github.event.pull_request.title }} ${{ github.event.pull_request.body }}" | grep -oE 'TIME-[0-9]+' | sort -u || true)
          else
            REFS=$(git log -1 --pretty=%B | grep -oE 'TIME-[0-9]+' | sort -u || true)
          fi
          echo "refs=$REFS" >> $GITHUB_OUTPUT
          echo "Detected refs: $REFS"

      - name: Update YouTrack issue state
        if: steps.refs.outputs.refs != ''
        env:
          YOUTRACK_URL: ${{ secrets.YOUTRACK_URL }}
          YOUTRACK_TOKEN: ${{ secrets.YOUTRACK_TOKEN }}
        run: |
          set -e
          for ID in ${{ steps.refs.outputs.refs }}; do
            if [ "${{ github.event.action }}" = "opened" ] || [ "${{ github.event.action }}" = "ready_for_review" ]; then
              NEW_STATE="In Review"
            elif [ "${{ github.event_name }}" = "push" ] && [ "${{ github.ref }}" = "refs/heads/main" ]; then
              NEW_STATE="Done"
            else
              continue
            fi
            curl -sS -X POST "$YOUTRACK_URL/api/issues/$ID" \
              -H "Authorization: Bearer $YOUTRACK_TOKEN" \
              -H "Content-Type: application/json" \
              -d "{\"customFields\":[{\"name\":\"State\",\"\$type\":\"StateIssueCustomField\",\"value\":{\"name\":\"$NEW_STATE\"}}]}"
            echo "Set $ID to $NEW_STATE"
          done
```

This workflow:
- Watches PR open / close / push-to-main events
- Extracts every `TIME-NNN` reference from the commit message or PR title
- Updates the corresponding YouTrack ticket's state via the YouTrack REST API
- Runs on the self-hosted runner so it can reach `localhost:8080`

Test it: open a PR titled `TIME-001: walkthrough video recording` — within a minute, ticket TIME-001 should transition to **In Review**.

---

## Step 7 — Commit, branch, and PR conventions

These conventions are not optional. They are what makes the integration tight.

### Branch naming

```
TIME-NNN/short-kebab-slug

Examples:
  TIME-001/walkthrough-video
  TIME-109/user-scoped-firestore
  TIME-205/hilt-migration
```

One ticket per branch. If a ticket needs more than one branch's worth of work, split the ticket in YouTrack first.

### Commit message format

```
TIME-NNN: short imperative subject (max 72 chars)

Optional body explaining why, not what. The diff already shows what.

Co-authored-by: Claude <noreply@anthropic.com>     # only when AI pair-programmed
```

The `TIME-NNN` reference is mandatory in every commit subject. YouTrack's VCS Integration uses it to link the commit to the ticket.

### Pull request format

PR title:
```
TIME-NNN: human description of change
```

PR body (template — install via `.github/PULL_REQUEST_TEMPLATE.md`):
```markdown
## What
Brief description of the change.

## Why
Cite BIBLE section or ticket if relevant.

## Closes
Closes TIME-NNN
(Use "Closes" for the primary ticket. List additional tickets with "Refs TIME-NNN".)

## Checklist
- [ ] ktlint passes locally
- [ ] detekt passes locally
- [ ] Unit tests added or updated
- [ ] Strings externalized (if user-facing copy changed)
- [ ] Compose preview added (if new composable)
- [ ] No new lint warnings
- [ ] BIBLE.md updated (if architectural change)
- [ ] Tested on a real device (not just emulator) for UI changes
```

When the PR merges to `main`, the workflow in Step 6 transitions the YouTrack ticket to **Done**.

---

## Step 8 — Daily progress ritual

The two-hour-day rule from the README needs an actual ritual to enforce it. Here is the minimum:

**Every working session, start with:**

1. Open YouTrack.
2. Filter to: `Phase A` + status `In Progress` assigned to you.
3. Confirm what you're working on. If nothing is `In Progress`, transition one ticket to `In Progress` from `Open`.
4. In a terminal, note the JST time. Mentally bound your session.

**Every working session, end with:**

1. Make sure your branch has at least one commit referencing the ticket.
2. Move the ticket if its state has changed (`In Progress` to `In Review` via PR, or `In Progress` to `Blocked` with a comment explaining why).
3. Push the branch. If a PR is ready, open it.
4. Stop at 22:00 JST. Sleep is not optional.

**Every Sunday evening:**

1. Generate the weekly summary in YouTrack: tickets closed, hours logged, hours remaining for the phase.
2. Post the public weekly thread on X (see README — Build in Public).
3. Update `LOG.md` with five lines about the week.

---

## Troubleshooting

| Symptom | Likely cause | Fix |
|---|---|---|
| GitHub webhook events not appearing in YouTrack | Self-hosted runner offline | Restart the runner: `~/actions-runner-time/run.sh` |
| `401 Unauthorized` from YouTrack API | Token expired or wrong scope | Regenerate token (Step 4); update GitHub secret |
| Commits not linking to tickets | `TIME-NNN` not in commit message subject | Amend the commit and force-push the branch |
| YouTrack VCS poll runs but finds nothing | Polling URL wrong or GitHub token lacks `repo` scope | Reconfigure VCS Integration (Step 5) |
| Workflow runs but skips state update | `if: steps.refs.outputs.refs != ''` is empty | The PR title and commit message both lack a `TIME-NNN` reference — fix the convention |

---

## When this guide is wrong

Update it. It is a living document. Commit changes with subject `YOUTRACK_SETUP: <what changed>`.

If the integration approach itself needs to change (different webhook strategy, different runner host, different YouTrack version), update the **Topology** diagram first, then the steps. Keep the document linear — a person setting up the integration for the first time should be able to follow it top-to-bottom.
