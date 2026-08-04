# Wordglass — Build Plan

Revised sequencing. Supersedes the phase ordering in `docs/build-plan.pdf`; the loop,
the design prompt template, the universal screen states, and the fidelity sweep in that
document are unchanged and still govern every slice.

**Total: ~30 weeks.** This is not faster than the original. It reorders risk so that if
the schedule runs out, what exists is defensible rather than merely competent.

---

## Why the order changed

The original plan builds the feature list for 17 weeks and starts the moat at week 18.
That is correct if the project finishes. Solo, learning Kotlin, on a plan that already
admits 29.5 weeks is the honest number — finishing on time is not the base case.

So the question is not "what order do I build all of it." It is **if I only get 60% of
the way, which 60%?** Five changes follow from that:

| Change | Reason |
|---|---|
| Design system folded into the scripts slice | Tokens should fall out of real screens, not be guessed in the abstract |
| Watermark moved into the export slice | It is a `BitmapOverlay`. Half a day once export exists. Moat mechanism #1 should be live the first time anything can be shared |
| Thin AI generation pulled to phase 2 | Every other feature assumes a script exists. Testers should not open an empty text box for six months |
| 30ms shader spike pulled to phase 2 | Currently sits inside the 2.5-week slice it is supposed to de-risk. One day to learn whether that slice is viable at all |
| Beauty moved to phase 5 | Cut item #3, hardest performance constraint, and nothing depends on it because effects bake at export |

**Unchanged and deliberate:** auth stays late. The schema is org-scoped from the first
migration so nothing is blocked by it, and F20 is right that an account wall before first
export is the single largest activation loss available to this app.

---

## Test devices

**Galaxy A14 is the floor device and the primary target.** Entry-level, and the phone this
audience actually owns. If it works here it works.

A flagship is a later, optional purchase to confirm the app is not over-constrained for
good hardware. Not on the critical path.

**Consequence:** the 30ms frame budget is a genuinely open question on this hardware, not
a formality. The phase 2 spike may return "beauty is not viable in live preview," in which
case F09's fallback applies — reduce mask resolution, then disable live preview while
keeping the effect at export.

---

# Phase 1 — Prove the loop
**~2 weeks · slices 00–01**

## Slice 00 — Project foundations ✅ COMPLETE
No UI. Everything painful to retrofit.

Compose project, `minSdk 26` / `targetSdk 36` / `compileSdk 37`, `.dev` debug suffix ·
Hilt, Room, Navigation, KSP · `CLAUDE.md` constitution · Supabase project, first migration
with `org_id` on every table, RLS policies via a `SECURITY DEFINER` `org_role()` function ·
GitHub Actions CI running lint and `assembleDebug` on push and PR.

**Done when:** debug build installs alongside nothing, CI green, manual insert blocked by
RLS for the wrong `org_id`. ✅ All three verified.

## Slice 01 — Scripts, list and editor + design system
**2 wks · combines original 00.5 and 01**

The easiest real feature. Its purpose is to prove the design-to-build loop works before
the loop meets the camera. The token system emerges from these screens rather than being
decided in the abstract.

**Design**
- Script list: empty, populated at 40 items, overflowing title
- Script editor: new, editing, unsaved-changes
- Delete confirmation
- Word count and estimated read-time treatment
- **Token system, surface set only:** colour, type scale (including a prompter tier
  reserved but not yet validated), spacing, radius, elevation, motion — two durations, not ten
- **Components:** button, icon button, slider, bottom sheet, list row, chip, dialog, toast,
  progress, empty state
- Icon set selection

**Build**
- Room `Script` entity, DAO, repository
- List and editor screens, navigation
- Autosave on 2s idle and on background
- Read-time estimate at 140 wpm
- Compose theme with the token set; every component with a `@Preview` per state
- Component gallery screen, debug builds only

**Deliberately empty:** the over-camera colour token set. It cannot be validated without a
live camera preview. Defined in slice 02, not guessed here.

**Watch for:** F03 says autosave means back is always safe, and **no "discard changes?"
dialog should exist anywhere in this app**. "Unsaved changes" is a transient visual state,
not a modal. The conventional build is the wrong build.

**Done when:** write a script, kill the app, reopen it, and it is there with a correct
read-time estimate. Gallery renders every component in every state.

**Checkpoint:** if design-to-build took more than two rounds, fix the handoff format before
slice 02. Do not carry a broken handoff format into the camera.

---

# Phase 2 — Prove a video can be made
**~7 weeks · slices 02–07**

At the end of this phase a stranger can generate a script, read it, trim it, and export a
watermarked video. Everything after this improves the product rather than establishing it.

## Slice 02 — Teleprompter capture
**2.5 wks · highest-risk slice in the project. Do not compress it.**

**Design**
- Camera permission pre-prompt, denied, denied-permanently (F02 — three distinct screens)
- Capture HUD: idle, countdown, recording, paused
- Prompter overlay: speed, size, opacity, scrim
- Settings sheet: ratio, resolution, mirror, countdown
- Exposure and focus affordances
- Audio level meter
- Interrupted-recording recovery (F07)
- **Over-camera token set defined here**, and every over-camera element designed twice —
  against a white wall and against a dark room

**Build**
- CameraX preview, `VideoCapture`, aspect-ratio guide frame (9:16, 1:1, 16:9)
- Scrolling overlay as a View above the preview — **not composited into the video**
- Tap-to-pause, drag-to-reposition, mirror mode
- Front/rear switch mid-session, exposure lock, tap-to-focus
- Level meter, headphone monitoring
- Keep-screen-awake; auto-save on interruption; storage pre-check against a real estimate

**Watch for:** F06 — pausing the prompter and pausing the recording are separate concepts
and must never share a control. F07 — restore the prompter to where the interruption
occurred, not the top.

**Done when:** record a 90-second read outdoors, take a phone call mid-recording, return,
and the partial take is intact and playable.

**Risk:** CameraX device fragmentation. Budget two extra days.

## Slice 03 — Takes, review and manage
**0.5 wk**

**Design:** take list — empty, populated with durations and thumbnails, partial-take marker ·
take player with scrub · rename · delete with undo · storage-used footer

**Build:** Room `Take` entity linked to script · ExoPlayer playback · thumbnail extraction
and caching · 6-second undo window, file cleanup on commit

**Watch for:** F08 — never delete without an undo, never auto-delete under storage pressure.

**Done when:** three takes of one script are listed, playable, and a deletion can be undone
within the window.

## Slice 04 — Export pipeline + watermark
**1.5 wks · watermark moved forward from original slice 13**

First end-to-end value. Everything downstream hangs off this pipeline.

**Design:** export sheet (quality, aspect ratios as multi-select, watermark line) · progress
with determinate percentage, elapsed, estimated remaining, cancel · backgrounded notification ·
complete with preview and share · failure states — out of storage, encoder failure (F19)

**Build**
- Media3 `Transformer`, `Composition`, `EditedMediaItemSequence`
- WorkManager + foreground service
- MediaStore write; share sheet via `ACTION_SEND` + FileProvider
- Multi-ratio export in one pass
- **Watermark as a `BitmapOverlay`**, positioned to survive platform crops on 9:16, shown
  in preview beforehand
- Export history

**Done when:** a 5-minute take exports at 1080p while backgrounded, notifies on completion,
appears in the gallery, and carries the watermark.

**Risk:** this is where you learn real export times on the A14. If they are worse than
expected, revisit quality defaults before building anything on top.

## Slice 05 — Shader spike
**1 day · not a feature. A question with a yes/no answer.**

Write a trivial GLSL shader, attach it as a `CameraEffect`, instrument per-frame timing on
the A14. Then again with an ML Kit face-contour mask attached.

**Answers:** can this device hold 30ms per frame at 30fps with a mask enabled?

**Outcomes:** holds → slice 16 proceeds as planned · holds without mask only → mask
resolution reduction is mandatory, not a fallback · fails either way → beauty is
export-only on this class of device, and F09's copy path becomes the default rather than
the exception.

Throwaway code. The point is the number.

## Slice 06 — AI script generation, thin + script import
**1 wk · pulled forward from original slice 12**

No brand kit. That upgrade lands in phase 5 once orgs exist.

**Design:** generation input (topic, duration, tone, audience — defaults preselected) ·
generating state · variant picker, two or three side by side, none preselected ·
paragraph regenerate · inline rewrite on selection · paste and file import · quota-exhausted
state (designed now, enforced in phase 4)

**Build:** Edge Function calling the Anthropic API · word count targeted to duration,
reconciled with prompter scroll speed · SAF import for `.txt` and `.docx` · variant
discard warning · **repurpose an existing script to a different length or platform** —
same Edge Function, existing script as input rather than a topic

**Deferred to phase 4:** URL import. It is a writer feature per F05, and writers do not
exist until the web dashboard.

**Watch for:** the prompt constraints are non-negotiable — short sentences, second person,
contractions, no corporate filler. Test by reading three generations aloud on camera. If it
sounds like an LLM, the constraint prompt is wrong and no amount of UI fixes it.

**Done when:** a generated 30-second script reads aloud naturally at the estimated duration
and contains none of the banned filler.

### Deferred feature candidate — "Speak it"

**Status: DEFERRED.** A fourth route on `ScriptStart`. The user records audio; it runs through
ASR and the existing spoken-form conversion prompt — the same path as URL import — and lands in
`ScriptEditor` as an editable script.

- **Design now, build only if promoted.** At slice 06, design the `ScriptStart` sheet with
  **four rows** so the layout is settled whether or not the route is built. Build the "Speak
  it" route only on promotion.
- **Decide at:** the phase 3 checkpoint, when five real creators see the product and ASR is
  already working.
- **Rationale for deferral:** it strengthens none of the three moat mechanisms, so it fails
  the standing-principle test as a build-now item. But it is a second solution to the
  blank-page problem that §2.1 says generation exists to solve, and it suits the audience — a
  realtor speaks about a property fluently and writes about it badly.
- **Unresolved before promotion — metering.** ASR is the primary cost constraint. Does a
  two-minute ramble consume the same allowance as captioning a finished video, or does this
  need a third metered resource? Business decision, not technical.
- **Naming collision it avoids:** *Record* in `ScriptEditor` means "record video of yourself
  reading this script." Voice-to-script belongs on `ScriptStart`, upstream, so the two never
  share a screen.

## Slice 07 — Trim and timeline
**1 wk**

**Design:** timeline with waveform — idle, scrubbing, handles engaged · trim handles and
touch targets · split and delete interior section · undo/redo · multi-take sequence reorder

**Build:** waveform extraction and caching · frame-accurate scrub via `CompositionPlayer` ·
trim and split mapped to `EditedMediaItem` clipping · undo stack

**Done when:** head and tail trim plus one interior cut survive an export, and undo returns
to the previous edit exactly.

**PHASE 2 CHECKPOINT — first real demo build.** A stranger can make a video. Measure export
times on the A14 and revisit quality defaults if needed.

---

# Phase 3 — Prove the differentiator
**~6.5 weeks · slices 08–12**

## Slice 08 — Telemetry
**0.3 wk · Sentry and PostHog, deferred from slice 00**

Wired but silent. Crash and ANR reporting, event tracking. Placed here because phase 3 ends
with a real user test, and that test is worth much less without it. Export analytics —
which features are used before a paid conversion — cannot be reconstructed retroactively.

Instrument the events named in the user flows document as each feature lands.

## Slice 09 — Captions, generation and correction
**2 wks · first network-dependent feature**

**Design:** generate entry point with cost disclosure · queued, uploading, processing,
complete, failed · offline queued-until-connected · transcript editor with word-level
selection, correction, timing nudge · caption overlay preview · quota exhausted — first
paywall moment

**Build:** signed upload to R2 via Edge Function · AssemblyAI submission and callback
webhook · word-level timestamp storage in Room and Postgres · transcript editor with
re-sync on edit · caption rendering Canvas → Bitmap → `BitmapOverlay` · learned corrections
stored per org and passed as custom vocabulary on future jobs (F12) · **export the
transcript as `.srt` or `.txt`** — belongs here rather than slice 04, since slice 04 has no
transcript to export

**Watch for:** F11 — captions never generate automatically; that would spend quota without
consent. Cost is stated before committing. Quota is reserved on request and charged only on
success; refunded if ASR returns nothing usable.

**Done when:** a take with two deliberately mispronounced proper nouns produces captions you
can correct, and the corrections appear burned into the export.

## Slice 10 — Caption styling and brand kit
**1 wk**

**Design:** five themes each rendered on the user's actual footage, not a mock · theme
picker · brand colour picker · logo upload, position, size · per-caption repositioning ·
captions off as a first-class option

**Build:** theme definitions as data, not hardcoded layouts · highlight styles — karaoke,
pop-on, single-line · logo overlay compositing · brand kit persisted at org level · logo and
caption positions stored ratio-relative so a 9:16 placement is not cropped out at 1:1

**Done when:** switching theme and brand colour changes the export, and the logo lands where
it was placed at all three aspect ratios.

## Slice 11 — Smart clipping
**2.5 wks · the differentiator. Entirely dependent on slice 09. Do not start early.**

**Design:** filler review — strike-through in transcript, remove-all vs individual · silence
threshold control with live cut count · segment cards, ranked, with hook preview and
duration · target length selector · auto-reframe preview with manual crop override

**Build:** filler detection from word timestamps · silence detection, 700ms default · LLM
segment extraction via Edge Function, cut on word boundaries · face tracking with
low-pass-filtered pan → Media3 `Crop` · **cut list as a reversible edit layer, never a
destructive operation**

**Watch for:** F15 — if proposed removals exceed roughly 25% of duration, default to fillers
only. An over-eager first result reads as broken. F16 — hide the clip entry point entirely
under ~45 seconds rather than showing a disabled control.

**Done when:** a 3-minute take yields three ranked clips, every automated cut is individually
reversible, and the reframe pan does not visibly jitter.

## Slice 12 — Audio import and mixing
**0.5 wk**

**Design:** import entry point · mixer with voice and bed levels, ducking toggle · bed trim,
fade in/out · mute original · **DRM-protected file, plain-language terminal error**

**Build:** SAF picker `audio/*` · `AudioProcessor` mixing · auto-duck −12 to −15dB using
caption timestamps to locate speech · explicit DRM and unsupported-codec handling

**Done when:** a bed ducks correctly under speech, and attempting to import from Spotify
produces a clear explanation rather than a crash.

**PHASE 3 CHECKPOINT — the differentiator exists. Put it in front of five real creators
before building the team layer.** This is the honest go/no-go moment.

---

# Phase 4 — Prove the moat
**~9 weeks · slices 13–17**

## Slice 13 — Auth and organisations
**1.5 wks**

**Design:** sign in/up, email + Google · email verification, six-digit code, non-blocking ·
org creation and naming · profile and account settings · sync status indicator · sign-out
with local-data warning

**Build:** Supabase Auth · org created on signup, solo user is an org of one · script and
metadata sync, video stays local · conflict resolution, last-write-wins with local precedence

**Watch for:** F20 — the account prompt appears only at the first sync-requiring action,
never at launch. **All local content created before signup is adopted into the new org.**
This is the step most implementations get wrong.

**Done when:** scripts written offline on one device appear on a second after sign-in, and
RLS blocks cross-org reads.

## Slice 14 — Metering and paywall
**1 wk · watermark already shipped in slice 04**

**Design:** usage meter — caption minutes and generations remaining · approaching-limit
state · **one paywall design, reached from both quota triggers** · watermark-free export as
the paid difference

**Build:** provider-agnostic subscription and usage tables · server-side quota enforcement,
never client-side · entitlement check reads only the local table

**Watch for:** paywalls fire **before** the call, never after, and always offer a real second
action — *Export without captions*, *Write it myself*. A dead end here loses the user entirely.

**Done when:** the free tier blocks a caption job at the limit, and a manually-flipped
entitlement row removes the watermark.

## Slice 15 — Web dashboard, writer surface
**3.5 wks · different platform, same loop**

**Design:** script library with folders — empty, populated, search · script editor with
generation and version history · brand kit editor · member list, roles, invite flow ·
assignment: pick script, pick recorder · review inbox · **desktop breakpoints — this is not
a responsive phone layout**

**Build:** Next.js on Vercel, shared Supabase Auth session · roles owner/writer/recorder ·
folders, version history · invite by email link · **same Edge Functions as mobile, no
duplicated logic** · URL import (deferred from slice 06)

**Watch for:** F23 — bulk assign is the writer's actual working pattern. Single-assign only
would make the dashboard unusable at ten scripts a week.

**Done when:** a writer creates ten scripts, assigns three to a recorder, and the recorder's
phone receives all three.

## Slice 16 — Team review loop
**1.5 wks**

**Design:** push notification → assignment inbox · assignment detail with a **single primary
action** · submit for review · writer review: approve / request retake · comments thread ·
retake-requested state on the recorder's device

**Build:** FCM assignment notifications · video upload to R2 on submit, resumable · review
state machine: assigned → submitted → approved / retake · comments

**Watch for:** F24 — notification to recording started is **two taps**. No settings, no
choices, no theme picker; the brand kit is already applied. Editing sits behind a secondary
control, never on the default path.

**Done when:** notification to recording started is two taps, and a retake request returns
the recorder to the same script with the writer's comment visible.

## Slice 17 — Billing
**1.5 wks**

**Design:** plan picker — monthly, annual, **no weekly** · purchase in progress, success,
failure · restore purchases, findable · manage subscription · seat management on web with
prorate notice

**Build:** Play Billing Library 8+ for solo subscriptions · Stripe on web for team seats ·
both write to the same subscription table; the app never queries a provider directly ·
server-side purchase verification

**Watch for:** F21 — if verification fails after successful payment, grant entitlement
optimistically for 24 hours and retry in the background. Never leave a paying user blocked.
F22 — a different Google account than the purchase is the genuinely hard case; explain
plainly and offer a support route rather than a dead end.

**Done when:** a Play internal-testing purchase and a Stripe web purchase produce identical
entitlement state.

**Risk:** Play Billing only behaves correctly in builds distributed through Play. Budget
time on the internal testing track — this cannot be validated from a sideloaded debug build.

**PHASE 4 CHECKPOINT — monetisation and team loop complete. Play internal testing live.**
All three moat mechanisms now exist.

---

# Phase 5 — Commodity features and hardening
**~5.5 weeks · slices 18–21**

Everything here improves the product. None of it is load-bearing for the business. If the
schedule has run out, this is what gets cut, in this order.

## Slice 18 — Beauty and camera effects
**2.5 wks · cut item #3**

Informed by the slice 05 spike. If the spike said live preview is not viable on the A14,
this slice ships export-only and F09's fallback copy becomes the default path.

**Design:** effects sheet — master toggle off, three sliders at zero · LUT gallery with None
selected · before/after compare on press-and-hold · reset to default · applied-at-export
indicator on existing takes · unsupported-device fallback

**Build:** GLSL — skin smooth, warmth, brightness lift · ML Kit face contours, thin variant,
for the mask · `CameraEffect` + `SurfaceProcessor` for preview · **same GLSL source as a
Media3 `GlEffect` at export** · `SingleColorLut` for grade presets · per-frame timing
instrumentation

**Excluded:** face reshaping, jaw slimming, eye enlargement.

**Done when:** preview holds under 30ms per frame at 30fps on the A14 with all three sliders
at maximum, and the exported file matches the preview — or the export-only path works and
says so plainly.

## Slice 19 — Brand-kit-aware generation
**0.5 wk**

The third moat mechanism, completed. Org brand kit injected as context: company description,
product, audience, banned phrases, and tone samples from previously approved scripts.
Approved scripts feed forward.

**Done when:** two orgs with different brand kits produce visibly different scripts from the
same topic prompt.

## Slice 20 — Onboarding
**0.5 wk · designed last, once the product is known**

Three-panel welcome, swipeable, skippable, **no account wall** · script-route chooser with no
default preselected · one-time capture coach overlay naming three things only — speed, pause,
record — dismissed on first tap and never returning.

**Done when:** F01 completes end to end in under 8 minutes for a 60-second video.

## Slice 21 — Hardening and device matrix
**2 wks · not a buffer. A real slice with acceptance criteria.**

Any state discovered missing during a sweep · Play Store listing screenshots · edge-to-edge
inset handling audit, required by `targetSdk 36` · **interruption matrix: call, low battery,
storage full, app killed mid-export** · offline behaviour across every network-dependent
feature (F27) · ANR and cold-start audit · all three aspect ratios · crash-free rate baseline

**Done when:** every entry in the interruption matrix has a defined, tested outcome, and the
app survives a full record-caption-clip-export cycle on the A14 with no ANR.

---

# Feature coverage audit

## Feature specification, all 12 areas

| Area | Slice |
|---|---|
| 2.1 Script Generation (AI) | 06 thin · 19 brand-kit-aware |
| 2.2 Script Import | 06 paste, file, repurpose · 15 URL and Google Docs |
| 2.3 Teleprompter & Recording | 02 |
| 2.4 Beauty & Camera Effects | 05 spike · 18 build |
| 2.5 Captions | 09 generation and correction · 10 styling |
| 2.6 Editing | 07 |
| 2.7 Smart Clipping | 11 |
| 2.8 Audio | 12 |
| 2.9 Export & Share | 04 video export · 09 transcript export |
| 2.10 Free Tier & Watermark | 04 watermark · 14 metering · 17 billing |
| 2.11 Accounts & Teams | 13 auth · 15 web · 16 review loop |
| 2.12 Cross-Cutting | 08 telemetry · 13 sync · 21 offline audit · offline per-feature as built |

## User flows, all 27

| Flow | Slice | Flow | Slice |
|---|---|---|---|
| F01 First launch to first export | 20 | F15 Filler and silence removal | 11 |
| F02 Camera and mic permissions | 02 | F16 Extracting clips | 11 |
| F03 Writing by hand | 01 | F17 Reframing to vertical | 11 |
| F04 Generating with AI | 06 · 19 | F18 Exporting and sharing | 04 |
| F05 Blog post to script | 06 · 15 | F19 Export fails on storage | 04 |
| F06 Recording a take | 02 | F20 Signup and org | 13 |
| F07 Interrupted by a call | 02 | F21 Caption limit and subscribing | 14 · 17 |
| F08 Reviewing takes | 03 | F22 Restoring a purchase | 17 |
| F09 Beauty effects | 18 | F23 Writer assigns | 15 |
| F10 Trimming | 07 | F24 Recorder completes | 16 |
| F11 Generating captions | 09 | F25 Writer requests retake | 16 |
| F12 Correcting transcript | 09 | F26 Inviting a teammate | 15 · 17 |
| F13 Caption theme and brand | 10 | F27 No connectivity | per-feature · 21 audit |
| F14 Importing a music bed | 12 | | |

---

# Schedule

| Phase | Slices | Weeks | Cumulative | Checkpoint |
|---|---|---|---|---|
| 1 Prove the loop | 00–01 | 2 | 2 | Loop works. If design-to-build took more than two rounds, fix the handoff format |
| 2 Prove a video can be made | 02–07 | 7 | 9 | First demo build. Real export times measured on the A14 |
| 3 Prove the differentiator | 08–12 | 6.5 | 15.5 | Five real creators. Honest go/no-go |
| 4 Prove the moat | 13–17 | 9 | 24.5 | All three moat mechanisms live. Play internal testing |
| 5 Commodity and hardening | 18–21 | 5.5 | 30 | Submission-ready |

## If the schedule slips

In this order, and only in this order.

1. **Multi-take stitching (slice 07)** — take picker plus head/tail trim is sufficient. ~0.5 wk
2. **Beauty effects (slice 18)** — table stakes, not a reason anyone switches. Removes the
   hardest performance constraint in the project. ~2.5 wks
3. **Review loop (slice 16)** — keep assignment and notification, drop approve/retake and
   comments. Assignment alone still makes the org the billing unit. ~1 wk

## Do not cut

**Slice 04's watermark, slice 14's metering, slice 15's web dashboard, slice 19's brand-kit
context.** These are the three moat mechanisms. Shipping without them produces a competent
teleprompter app with no defensible position — precisely the outcome the whole plan is
arranged to avoid.

> **Unresolved:** the markdown version of the feature specification calls brand-kit context
> "a third, softer moat" alongside two primary mechanisms; the PDF version and the standing
> principle in both documents treat all three as equal. This plan follows the PDF and
> protects slice 19. Settle this in one place — it is the difference between slice 19 being
> load-bearing and being optional.

---

# The discipline that makes this work

The loop is worth more than any individual slice. Scope, prompt, design, handoff, build,
sweep. Run it identically twenty-one times and the app will look and behave like one product.

The two steps that get skipped under pressure are the handoff and the sweep — the two that
exist specifically to catch silent decisions you did not make. Skipping them does not save
time; it moves the cost to slice 16, where it is five times more expensive to fix.
