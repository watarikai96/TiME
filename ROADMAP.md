# TiME — Operational Roadmap

This file is the human-readable mirror of the YouTrack ticket backlog. Every item below is also a ticket in our self-hosted YouTrack project (prefix `TIME-`). When the two disagree, **YouTrack wins** and this file is updated within the same session.

For the strategic rationale behind every phase, read the BIBLE (private). For the public-facing summary, see the [README](README.md).

---

## Conventions

- Ticket IDs follow `TIME-NNN` (zero-padded to 3 digits while the count is below 1000).
- Each ticket has a **Type** (`Epic`, `Story`, `Task`, `Bug`, `Tech Debt`, `Spike`).
- Each ticket has a **Priority** (`Critical`, `High`, `Medium`, `Low`).
- Each ticket has an **Estimate** in hours (rounded to the nearest 0.5h).
- Each ticket has a **Phase** label (`Phase A`, `Phase B`, `Phase C`, `Phase D`).
- Phase A tickets also carry a **Week** label (`W1` through `W13`) suggesting the calendar week.
- Branches: `TIME-NNN/short-kebab-slug` (e.g. `TIME-001/user-scoped-firestore`).
- Commits: must contain `TIME-NNN` in the subject for VCS integration to link them.
- PRs: title format `TIME-NNN: human description`; body must include `Closes TIME-NNN` to auto-resolve.

The `youtrack-import.csv` file in the repo root is a YouTrack-importable copy of all Phase A tickets. Run an import once at Phase A kickoff; after that, YouTrack is authoritative.

---

## Phase A — Polish and Ship Android

> 2026-05-16 to 2026-08-14 · 13 weeks · 90 days
> Budget: 29.5h baseline to 37.5h surge per week · approximately 350-400h total
> Outcome: 50 to 100 closed-alpha users on Play Store; crash-free rate at or above 99%; D7 retention at or above 50%

### Phase A epics

| ID | Epic | Estimate | Priority |
|---|---|---|---|
| `TIME-100` | Security and Auth Foundation | 32h | Critical |
| `TIME-200` | Architecture Hardening | 28h | Critical |
| `TIME-300` | First-Run Experience | 24h | High |
| `TIME-400` | Internationalization | 32h | High |
| `TIME-500` | Quality and Observability | 26h | High |
| `TIME-600` | Visual Polish (Screen Quality Bar) | 90h | High |
| `TIME-700` | Compliance and Launch Prep | 28h | Critical |
| `TIME-800` | Marketing Site and Store Assets | 24h | High |
| `TIME-900` | Beta Operation | 36h | High |

### Phase A backlog (stories and tasks)

#### Week 1 — Foundation reset and audit

| ID | Type | Title | Estimate | Priority | Notes |
|---|---|---|---|---|---|
| `TIME-001` | Task | Record a 5-minute walkthrough video on a real device | 1h | High | Self-review baseline |
| `TIME-002` | Task | Build the Screen Quality Bar audit checklist | 1.5h | High | See README · §Current Phase |
| `TIME-003` | Task | Audit every existing screen against the Quality Bar; produce QA debt list | 4h | High | Output: 30-50 child tickets |
| `TIME-004` | Task | Create YouTrack project structure and import this CSV | 1h | Critical | One-time setup |
| `TIME-005` | Task | Set up Sentry account; integrate SDK; verify forced crash arrives | 2h | High | Part of `TIME-500` |
| `TIME-006` | Spike | Decide YouTrack VCS integration plugin or webhook approach | 1h | High | See `YOUTRACK_SETUP.md` |
| `TIME-011` | Bug | Remove `@RequiresApi(VANILLA_ICE_CREAM)` annotations in `MainActivity.kt`, `NavGraph.kt`, `SettingsScreen.kt` | 2h | Critical | Crashes on Android 14 or earlier |

#### Week 2 — Architecture preparation for Rails (Phase B foundation)

| ID | Type | Title | Estimate | Priority | Notes |
|---|---|---|---|---|---|
| `TIME-201` | Story | Extract `TimeRepository` interface; rename concrete class `FirestoreTimeRepository` | 3h | Critical | Strangler-Fig prep |
| `TIME-202` | Story | Extract `CategoryRepository` interface; rename concrete class `FirestoreCategoryRepository` | 2h | Critical | |
| `TIME-203` | Task | Update ViewModels to depend on repository interfaces only | 2h | Critical | Compile guarantee against direct Firestore use |
| `TIME-204` | Task | Decide DI approach: manual constructor injection or Hilt | 1h | High | Recommendation: Hilt (TIME-205) |
| `TIME-205` | Story | Migrate to Hilt DI: add deps, Application annotation, ViewModels, Repositories | 6h | Critical | Sub-step deliverables in child tickets |
| `TIME-206` | Task | Write 5 unit tests against a fake `TimeRepository` to establish testing pattern | 2h | High | |

#### Week 3 — QuietCraft design system pass

| ID | Type | Title | Estimate | Priority | Notes |
|---|---|---|---|---|---|
| `TIME-601` | Story | Codify the type ramp in `QuietCraftTheme`: 6 token names with defined sizes and weights | 2h | High | Token names: titleLarge, titleMedium, bodyLarge, bodyMedium, labelSmall, displayHero |
| `TIME-602` | Story | Codify the spacing scale: 4 / 8 / 12 / 16 / 24 / 32 dp | 1h | High | Replace any ad-hoc `Modifier.padding(11.dp)` cases |
| `TIME-603` | Story | Document the color palette: dark and light themes, accent system | 2h | High | Markdown file in `docs/design-tokens.md` |
| `TIME-604` | Task | Replace ad-hoc spacing or typography across screens with codified tokens | 4h | High | Sweep pass; verify with grep |
| `TIME-605` | Task | Side-by-side visual comparison vs Sunsama, Sessions, Structured (3 screenshots each) | 2h | Medium | Output: 3 axes of weakness, become child tickets |

#### Week 4 — Timeline screen polish

| ID | Type | Title | Estimate | Priority | Notes |
|---|---|---|---|---|---|
| `TIME-610` | Story | Design and implement the "empty timeline" state | 3h | High | Microcopy: "Nothing yet. And that's okay." |
| `TIME-611` | Task | Pull-to-refresh animation timing review | 1.5h | Medium | |
| `TIME-612` | Task | Long-press menu polish (edit / delete / duplicate) | 2h | High | |
| `TIME-613` | Task | Card animation states: running / paused / completed / break | 3h | High | |
| `TIME-614` | Task | Microcopy pass across Timeline; align with BIBLE 5.3 | 2h | High | EN + JP |
| `TIME-615` | Task | Capture 5 marketable screenshots from Timeline screen alone | 1h | Medium | Reuse for Play Store assets |

#### Week 5 — HyperFocus screen polish

| ID | Type | Title | Estimate | Priority | Notes |
|---|---|---|---|---|---|
| `TIME-620` | Task | Carousel animation interruption-recovery review | 2h | High | |
| `TIME-621` | Task | Live timer rendering perf on midrange Android (target 60fps) | 3h | High | Profile on Pixel 6a baseline |
| `TIME-622` | Story | Pause / resume / break logic edge-case audit | 4h | Critical | DataStore restore path after process death |
| `TIME-623` | Task | Session completion animation polish | 1.5h | High | |
| `TIME-624` | Task | HyperFocus carousel empty state | 1h | Medium | |
| `TIME-625` | Spike | 2-hour 4-session stress test; verify state integrity after force kill | 2h | Critical | Output: bug list if any |

#### Week 6 — Calendar and Analytics polish

| ID | Type | Title | Estimate | Priority | Notes |
|---|---|---|---|---|---|
| `TIME-630` | Task | Holiday overlays correctness for JP, US, EU locales | 2h | Medium | |
| `TIME-631` | Task | Analytics charts visual rhythm (Daily, Weekly, Monthly) | 4h | High | |
| `TIME-632` | Task | Category breakdown chart polish (donut or bar) | 2h | High | |
| `TIME-633` | Task | FocusStreakBadge as trophy users screenshot | 1.5h | Medium | |
| `TIME-634` | Task | Analytics empty state for users with under 7 days of data | 1.5h | Medium | |

#### Week 7 — Settings, Categories, AddTiME polish

| ID | Type | Title | Estimate | Priority | Notes |
|---|---|---|---|---|---|
| `TIME-640` | Task | Settings audit: regroup, clarify each toggle | 3h | High | |
| `TIME-641` | Task | Category Manager icon and color picker UX | 2.5h | High | |
| `TIME-642` | Task | AddTiME screen form validation and keyboard handling | 2h | High | |
| `TIME-643` | Task | Time picker UX review on small screens | 1.5h | Medium | |
| `TIME-644` | Spike | Power-user keyboard shortcuts idea spike (defer implementation) | 1h | Low | Phase B candidate |

#### Week 8 — Onboarding flow (TIME-300 epic)

| ID | Type | Title | Estimate | Priority | Notes |
|---|---|---|---|---|---|
| `TIME-301` | Story | Design the 5-screen first-run experience per BIBLE 5.6 | 4h | High | Mockup in Figma |
| `TIME-302` | Story | Implement `OnboardingScreen.kt` with internal navigation | 6h | High | |
| `TIME-303` | Task | `OnboardingViewModel.kt` and DataStore key `hasCompletedOnboarding` | 2h | High | |
| `TIME-304` | Task | First-intention free-text input; persisted into Timeline screen | 2h | High | |
| `TIME-305` | Task | Compose previews for each onboarding screen (light and dark) | 1.5h | Medium | |
| `TIME-306` | Task | Compose UI tests for onboarding navigation | 2h | Medium | |
| `TIME-307` | Task | Privacy Policy and ToS stub pages from inside onboarding | 1.5h | High | Real legal copy in `TIME-701` |

#### Sign-in flow (TIME-100 epic, Week 8 to 9 parallel)

| ID | Type | Title | Estimate | Priority | Notes |
|---|---|---|---|---|---|
| `TIME-101` | Story | `SignInScreen.kt` Compose UI per BIBLE 5.3 copy bible | 4h | Critical | Phase A only; Phase B replaces with Rails auth |
| `TIME-102` | Task | Firebase Auth email + password flow | 2h | Critical | |
| `TIME-103` | Task | Google sign-in integration | 2h | Critical | |
| `TIME-104` | Task | Apple sign-in integration (stub if blocked on Apple dev account) | 2h | High | |
| `TIME-105` | Task | Anonymous mode preserved (offline only) | 1h | High | |
| `TIME-106` | Story | Account deletion (GDPR Article 17) with full Firestore cascade | 3h | Critical | |
| `TIME-107` | Task | `AccountViewModel.kt` + `AuthRepository.kt` (interface ready for Rails swap) | 3h | Critical | Repository pattern carried through |
| `TIME-108` | Task | Settings screen integration: sign in / out / delete account section | 2h | Critical | |
| `TIME-109` | Bug | Fix global `time_entries` collection — scope to authenticated user | 4h | Critical | Highest single-ticket criticality |
| `TIME-110` | Task | Add `firestore.rules` to repo; deny-by-default with auth check | 1.5h | Critical | Per BIBLE 6.3 |

#### Week 9 — Bug hunt and crash hardening

| ID | Type | Title | Estimate | Priority | Notes |
|---|---|---|---|---|---|
| `TIME-650` | Spike | 7-day personal daily-usage run; log every bug and annoyance | 7h | High | Spans the week |
| `TIME-651` | Story | Fix top 20 issues from `TIME-650` | 8h | High | Sub-tickets created per finding |
| `TIME-652` | Task | Memory leak check with Android Studio profiler | 2h | Medium | |
| `TIME-653` | Bug | Verify ProGuard / R8 release build does not break Firebase models | 2h | High | Common minification gotcha |
| `TIME-654` | Task | Pre-release build signed and installed on test device | 1h | High | |

#### Week 10 — Marketing site and Play Store assets (TIME-800 epic)

| ID | Type | Title | Estimate | Priority | Notes |
|---|---|---|---|---|---|
| `TIME-801` | Story | One-page marketing site (static for now; Rails+Hotwire in Phase B) | 5h | High | |
| `TIME-802` | Task | Purchase domain and point DNS | 0.5h | High | |
| `TIME-803` | Task | Waitlist email capture (Buttondown or ConvertKit) | 1h | High | |
| `TIME-804` | Story | Play Store listing: title, short description, full description (5 drafts) | 3h | High | EN + JP both |
| `TIME-805` | Task | 8 marketing screenshots from polished screens | 2h | High | Selected from `TIME-615`, others |
| `TIME-806` | Task | Feature graphic and final app icon | 2h | High | |
| `TIME-807` | Task | 30-second screen recording trailer | 2h | Medium | |

#### Week 11 — Closed beta (TIME-900 epic)

| ID | Type | Title | Estimate | Priority | Notes |
|---|---|---|---|---|---|
| `TIME-901` | Story | Upload signed AAB to Play Console closed-testing track | 1.5h | Critical | |
| `TIME-902` | Task | Recruit 50 testers (X DM, friends, productivity Discords) | 4h | High | |
| `TIME-903` | Task | Feedback channel set up (email + Discord) | 1h | High | |
| `TIME-904` | Task | Daily Sentry + Play Console crash triage (1h × 7 days) | 7h | Critical | Spans the week |
| `TIME-905` | Story | Mid-week patch release responding to first feedback | 4h | High | |

#### Week 12 — Polish from feedback and perf

| ID | Type | Title | Estimate | Priority | Notes |
|---|---|---|---|---|---|
| `TIME-910` | Story | Address every bug raised by 2 or more independent testers | 8h | Critical | Sub-tickets per bug |
| `TIME-911` | Task | Cold-start time reduction (target under 1.5s on Pixel 6a) | 3h | High | |
| `TIME-912` | Task | Timeline first-paint after cold start (target under 500ms) | 2h | High | |
| `TIME-913` | Task | Battery profile on multi-hour HyperFocus session | 2h | Medium | |

#### Week 13 — Soft launch and retrospective

| ID | Type | Title | Estimate | Priority | Notes |
|---|---|---|---|---|---|
| `TIME-920` | Task | Promote closed beta to open beta (quality permitting) | 1h | High | Hold the bar if needed |
| `TIME-921` | Task | Public soft-launch tweet and Product Hunt teaser | 2h | High | |
| `TIME-922` | Story | Phase A retrospective: what shipped, what slipped, KPI status | 3h | Critical | Update BIBLE Decision Log |
| `TIME-923` | Story | Phase B kickoff doc drafted (do not start work yet) | 4h | High | Rails backend spec |

### Crosscutting tickets (run continuously through Phase A)

| ID | Type | Title | Estimate | Priority | Notes |
|---|---|---|---|---|---|
| `TIME-401` | Story | Externalize every Compose `Text("…")` to `stringResource()` | 12h | High | Sweep with `grep "Text(\""` |
| `TIME-402` | Task | Populate `res/values/strings.xml` with all EN strings | 4h | High | |
| `TIME-403` | Task | Create `res/values-ja/strings.xml` with JP translations | 6h | High | Use BIBLE UI Copy Bible (§5.3) |
| `TIME-404` | Task | Test JP locale end-to-end on device | 2h | High | |
| `TIME-405` | Task | Plurals and date-format locale audit | 2h | Medium | |
| `TIME-501` | Story | GitHub Actions CI: ktlint, detekt, lint, unit tests, build APK | 4h | Critical | `TIME-008` parent |
| `TIME-502` | Task | `.editorconfig`, ktlint config, detekt config committed | 1h | High | |
| `TIME-503` | Task | Dependabot config; weekly cron for security scans | 1h | Medium | |
| `TIME-504` | Story | Custom analytics events per BIBLE NFR-8 | 4h | High | session_started, completed, paused, etc. |
| `TIME-505` | Task | Performance Monitoring traces (cold start, screen transitions, Firestore) | 2h | High | |
| `TIME-701` | Story | Privacy Policy real legal copy (lawyer-reviewed) | 6h | Critical | GDPR + CCPA + APPI |
| `TIME-702` | Story | Terms of Service real legal copy | 4h | Critical | |
| `TIME-703` | Task | Age gate per BIBLE CR-3 | 1.5h | High | |
| `TIME-704` | Task | Trademark notices in app: TiME App™, HyperFocus™, QuietCraft™ | 1h | High | About screen |

---

## Phase B — Rails Backend + AI + Web V1 (placeholder, expanded at Phase B kickoff)

> 2026-08-15 to 2027-02-12 · 26 weeks
> Outcome: Firebase fully retired; first paying customers; Web V1 live; AI feature set shipped

Phase B tickets will be created at the Phase A retrospective (`TIME-922`). Sub-phases B1 (Rails foundation), B2 (Migration), B3 (AI + Journal + Habits), B4 (Web V1 + Public Beta) are scoped in the BIBLE.

---

## Phase C — SwiftUI iOS Native (placeholder)

> 2027-02-13 to 2027-08-14 · 26 weeks
> Outcome: App Store launch with feature parity to Android

---

## Phase D — Teams + Enterprise + Hires (placeholder)

> 2027-08-15 to 2028-05-16 · 36 weeks
> Outcome: First contractors hired; Teams plan live; Series A optionality

---

## Updating this file

1. Move a ticket in YouTrack (status change, estimate change, scope split).
2. Reflect the change in this file in the same session.
3. Commit with subject `TIME-XXX: <change>` or `ROADMAP: <change>` if multi-ticket.
4. If the change crosses a phase boundary, also update the BIBLE Decision Log (Appendix B).

**Source of conflict resolution:** YouTrack wins. This file is a generated mirror, regenerated quarterly from a YouTrack export.
