# Wordglass

Android-first scripted-video recording tool. A script is written or AI-generated, read
aloud from a teleprompter overlay while the camera records, then auto-captioned and cut
into short clips. Solo creators and teams. Native Kotlin, Jetpack Compose.

Reference documents live in `docs/`. Consult them for reasoning; this file is the rules.

- `docs/technical-specification.pdf` — stack decisions, exclusions, billing, iOS port map
- `docs/feature-specification.pdf` — feature scope, free tier, moat mechanisms
- `docs/build-plan.md` — **current** slice sequence and schedule. Supersedes the phase
  ordering in `docs/build-plan.pdf`
- `docs/build-plan.pdf` — the design-to-code loop, design prompt template, universal
  screen states, and fidelity sweep — all still governing. Its phase ordering is
  superseded by `docs/build-plan.md`
- `docs/user-flows.pdf` — 27 flows with verbatim copy strings and error paths
- `docs/design-system.md` — the token authority: colour, type, spacing, shape, elevation,
  motion, components. No screen hardcodes a value not defined here

---

## The standing principle

Teleprompter apps are a commodity. The build is not the hard part. The moat is three
things:

1. **Watermark distribution loop** — free exports carry a subtle corner watermark,
   positioned to survive platform crops on 9:16
2. **Team account structure** — the org is the billing unit, not the individual
3. **Accumulating brand-kit context** — approved scripts become tone samples for future
   AI generation, so a team's output gets more on-brand with use

Every scope decision is tested against whether it strengthens one of those three. A
feature that strengthens none of them makes the app better and the business no more
defensible.

**When a suggestion would add scope, say which mechanism it strengthens, or say that it
strengthens none.**

---

## The build loop

Work proceeds in slices. Every slice that produces UI runs the same six steps:

1. **Scope** — `/specify`. Goal, user-visible behaviour, acceptance criteria, explicit
   out-of-scope list. No screens named yet.
2. **Emit design prompts** — one prompt per screen, covering every state of that screen.
3. **Design** — screens produced externally from those prompts.
4. **Handoff** — designs converted into something buildable deterministically: token
   names not hex values, component names not descriptions, exact copy strings, state
   transition table, measurements in dp.
5. **Build** — `/plan` → `/tasks` → `/implement`, left uncommitted. **The handoff
   document is the source of truth, not the visual.**
6. **Fidelity sweep, then commit.**

Steps 4 and 6 are the ones that get skipped under pressure, and they are the two that
exist to catch silent decisions. Do not skip them. Do not propose skipping them.

---

## The fidelity sweep — commit gate

**Nothing that produces UI commits without a clean sweep.** No exceptions, including
slices that "only changed one thing."

1. Screenshot every designed state on device — not the happy path only
2. Compare against the handoff, not the visual. Check specified values: token names, dp
   measurements, exact copy strings
3. **Check for invented content** — any string, label, icon, or affordance present in the
   build but absent from the handoff. This is the most common defect and it is always
   silent
4. Check for missing states — any designed state with no code path to reach it
5. Check reuse — if the build created a new component where an existing one was
   specified, revert it
6. Over-camera contrast pass, capture-UI slices only — against a white wall and a dark room
7. Fix, re-sweep, commit

### The no-invention rule

Do not add strings, labels, icons, menu items, or affordances that are not in the handoff
document. Placeholder copy that reads plausibly is still invented copy. If something
appears to be missing from a handoff, **stop and ask** rather than filling the gap.

---

## Working style

The project owner is experienced with git, the terminal, and other languages, but is
learning Kotlin and Android on this project. That changes a few things:

- **Explain what changed, not just that it worked.** Name the files and why each was touched.
- **Show before acting** on anything structural — dependency additions, file moves,
  schema changes, config edits.
- **Flag decisions rather than absorbing them.** If two approaches are reasonable, say so
  and say which you'd pick and why. Do not silently choose.
- **Keep diffs reviewable.** Prefer several small commits over one large one.
- If an error message suggests a fix, say whether the suggested fix is correct here.
  Compiler and Gradle suggestions are often locally right and globally wrong.

---

## Hard technical constraints

### Baseline — do not change without being asked

| Setting | Value | Reason |
|---|---|---|
| `applicationId` | `app.wordglass` | Permanent once published |
| `minSdk` | 26 | API 21 adds ~3.7 points of reach and 2014-era devices |
| `targetSdk` | 36 | Play requirement 31 Aug 2026. **Do not bump to 37.** |
| `compileSdk` | 37 | Raised only to satisfy library requirements |
| Debug suffix | `.dev` | Dev and release builds coexist on device |
| `material3` | 1.4.0 stable | Pinned explicitly (not inherited from the Compose BOM). Design system requires 1.4.x stable, never 1.5.x alpha |

`compileSdk` may rise when a dependency requires it. `targetSdk` moves only as a
deliberate, separate decision — it opts the app into new runtime behaviour.

### Chosen stack

Camera: CameraX 1.5+ · Composition and export: Media3 Transformer 1.10+ · Editor preview:
`CompositionPlayer` · Face detection: ML Kit face contours, thin variant · Async:
Coroutines + Flow · Background export: WorkManager + foreground service · DI: Hilt ·
Local: Room · Crash: Sentry · Analytics: PostHog · Backend: Supabase Postgres with RLS on
`org_id` · Storage: Cloudflare R2 · ASR: AssemblyAI · LLM: Anthropic API · Push: FCM ·
Web: Next.js on Vercel

### Ruled out — do not propose these

- **FFmpegKit** — retired Jan 2025, binaries pulled from Maven. Media3 covers the need
- **On-device Whisper** — too slow on the mid-range hardware this audience owns
- **Third-party beauty SDKs** (DeepAR, Banuba) — several thousand per year, unjustified
- **React Native / Flutter / Kotlin Multiplatform** — the media pipeline is the app and it
  does not share
- **Firestore / Firebase RTDB** — wrong fit for a relational, role-based org model
- **Job orchestrators** (Inngest, Railway, FastAPI) — one webhook per video needs none
- **Bundled music library** — licensing liability. Users import their own audio
- **Weekly subscription** — Play scrutiny plus predictable review damage
- **Face reshaping / jaw slimming / eye enlargement** — wrong for the audience, expensive

### Architectural rules

- **`org_id` on every row, from the first migration.** RLS policies enforce tenancy at the
  database, never in application code. A solo user is an org of one.
- **Keep the media layer behind clean interfaces** — `CaptureEngine`, `ExportPipeline`,
  `EffectStack`. **Never let CameraX or Media3 types leak into view models.** This is what
  makes the iOS port a second implementation rather than an untangling.
- **Local is the source of truth.** Cloud is a sync target. Video files stay on device
  unless explicitly uploaded for review.
- **Record clean, bake at export.** Effects are applied at export, never baked into the
  recorded file, so a bad filter choice never costs a take.
- **Entitlement reads the local subscription table only** — never query a billing provider
  directly. Both Play and Stripe write into one provider-agnostic table.
- **Server-side quota enforcement.** Never client-side.

### Performance budget

**30ms per frame at 30fps input** is the hard ceiling for the beauty shader on the
slowest supported device. Validate with a trivial shader before writing the real one. If
a mid-range device cannot hold it with the mask enabled, reduce mask resolution before
reducing the feature.

---

## UX invariants

These override generic mobile patterns. Where they conflict, these win.

### The user is not looking at the screen

During recording their eyes are on the lens. This changes almost everything in the
capture UI.

- Prompter text readable at arm's length in peripheral vision — larger than any type
  scale you would otherwise pick, and adjustable
- Controls findable without looking: fixed positions, hit targets well above the 48dp
  minimum, haptic confirmation rather than visual
- **Nothing may move position between states.** A button that shifts when recording starts
  is a button the user cannot hit
- Recording status perceivable peripherally — a persistent edge treatment, not a small dot

### Dark-first and camera-aware

A light UI beside a live camera preview blows out the user's perception of the exposure
they are recording. This is a dark **product**, not a dark **mode**. Chrome over the
preview holds contrast with scrims and outlines, never colour alone.

### Takes are precious

A user who has delivered a good 90-second read will not cheerfully do it again.

- Every destructive action on a take needs confirmation or undo
- Auto-save on interruption is a feature, not error handling
- Never let a network failure, a filter change, or a navigation mistake destroy a recording
- Partial takes are always kept, never discarded

### Local first

Recording, trimming, take management, and playback work with no connectivity. Only
captions, generation, and sync need the network. Never block a local action on a remote
call. Never show a spinner for something that is instant.

### Honest progress

Export takes minutes on mid-range hardware. Indeterminate spinners are dishonest. Show
real percentage, elapsed, and estimated remaining. Allow backgrounding. Notify on
completion.

### Copy and state rules

- **No "discard changes?" dialog anywhere in this app.** Autosave means back is always safe
- Paywalls fire *before* the call, never after, and always offer a real second action
- Every automated edit is proposed and reviewable before it is applied
- Error states explain in plain language and offer the alternative
- Copy strings in `docs/user-flows.pdf` are verbatim where specified — use them exactly

### System-owned surfaces — do not attempt to restyle

Android runtime permission dialogs · the system share sheet · the Storage Access Framework
picker · the Google Play purchase flow · system notifications beyond icon, title, body,
and actions.

---

## Design system — DECIDED (v1, slice 01)

The design system is decided and lives in **`docs/design-system.md`. That document is the
authority** for every token — colour (surface set and over-camera set), the type scale and
its prompter tier, spacing, shape, elevation, motion, touch targets, and the shared
components. Read it before designing or building any screen.

**Blocking gate, unchanged.** No screen may hardcode a colour, size, spacing value, or
duration. Everything comes from a token.

**If a screen needs a value that is not a token in `docs/design-system.md`, stop and say
so.** Do not invent a hex value, a dp measurement, or a duration. Do not create a second
slider. Add the token deliberately to the design system first, then use it.

A handful of values are marked ⚠ in the design system — they are decided as defaults but
await on-device validation at the slice noted (mostly prompter sizing at slice 02). Those
are still tokens; they may move once validated.

---

## Current state

**Slice 00 — project foundations. Complete.**

All three acceptance criteria met:

- **Debug build installs with the `.dev` suffix** alongside nothing (`minSdk 26` /
  `targetSdk 36` / `compileSdk 37`).
- **RLS verified blocking cross-org read and write** — a member of one org can neither
  select nor insert against another org's `org_id`
  (`supabase/verify/rls_cross_org.sql`, run under `set role authenticated` with a forged
  `auth.uid()`).
- **CI green on both push to `main` and pull requests** — `.github/workflows/ci.yml`
  runs `lint` + `assembleDebug` on JDK 21 via the Gradle wrapper.

Shipped: Compose project with the SDK levels and `.dev` debug suffix above, git
initialised · Hilt (`@HiltAndroidApp WordglassApp`, `MainActivity` `@AndroidEntryPoint`),
Room, Navigation Compose, and Compose ViewModel/lifecycle artifacts via the version
catalog — dependencies and wiring only, no Room entities/DAOs yet · first Supabase
migration (`supabase/migrations/`): `orgs`, `org_members`, `scripts`, RLS on `org_id`,
membership resolved through a SECURITY DEFINER `org_role()` function to avoid policy
recursion · GitHub Actions CI.

Deferred to a later slice — **not dropped**:

- **Sentry and PostHog** — intentionally moved to a later slice rather than wired silent
  now. Both remain in the chosen stack.
- **Explicit Coroutines dependency** — still transitive only; promote to a direct
  dependency when a slice first uses it directly.

Update this section at the end of every slice.
