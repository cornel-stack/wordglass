# Deferred — designed ahead of build

Elements that are **designed now but not rendered/wired until a later slice**. The build loop
allows designing the full flow shape while building only what has a live destination, so the
fidelity sweep would otherwise flag these as "state with no code path" (sweep step 4) or as a
missing affordance. They are not defects. The sweep consults this file before flagging.

**Rules for this file**
- One entry per deferred element. Each states **what a correct build looks like in the current
  slice, and what the sweep should treat as a defect** — not only what is deferred. An entry
  that names the deferral without the expected build leaves the sweep unable to tell a ruling
  from a bug.
- Name the exact screen, the element, the slice it was designed in, and the slice that
  activates it.
- When a slice activates an element, delete its entry in that slice's fidelity sweep — a stale
  entry here is as much a defect as an unreachable button.
- If an element is deferred, its screen's design prompt must also say so in `OUT OF SCOPE`.

---

## Slice 01 → Slice 02 — Accessibility verification incomplete

**Obligation:** Six §11 checks in slice 01 were never run on device because TalkBack and the
200% font-scale pass were not executed before close. The Phase E fixes that address the
mechanisms (fix C: metadata maxLines removed; fix F: AlertDialog dismissButton/confirmButton
split) are unobserved. **Verify all six before slice 02's fidelity sweep closes.**

Items to verify with TalkBack enabled (one session, in order):

1. **DeleteConfirm opens focused on Cancel** — open the dialog, do not touch the screen; confirm
   TalkBack's initial highlight lands on Cancel, not Delete or the dialog container.
2. **Focus returns to `···` after menu dismiss (§8 row 8)** — open a menu, dismiss by tapping
   outside; confirm TalkBack focus snaps back to the `···` that opened it.
3. **Focus returns to `···` after dialog cancel (§8 row 10)** — open Delete dialog, tap Cancel;
   same check.
4. **Focus after delete confirmed (§8 row 9)** — confirm delete; confirm focus lands on the row
   that moved into the deleted row's position, or on the *Write your own* button when the list
   becomes empty.
5. **ReservedAction (Record) announced as unavailable, not skipped** — in the editor, swipe
   through TalkBack focus order; confirm Record is announced ("Record. Not available yet —
   recording arrives in a later version.") and is not silently omitted.

Items to verify at 200% font scale (Settings → Accessibility → Font size → Largest):

6. **Metadata wraps rather than truncates** — open a populated ScriptList; confirm each row's
   date/read-time line wraps to a second line rather than cutting off with an ellipsis.
7. **Dialog buttons stack Cancel above Delete** — open DeleteConfirm; confirm buttons are
   stacked vertically with Cancel on top, not side-by-side or clipped.

- **Designed:** slice 01.
- **Obligation activates:** slice 02 — verify before that sweep closes, then delete this entry.
- **Sweep · current slice:** `[UNVERIFIED]` in §11 is the correct state. Do not flag the
  absence of verification as a sweep defect while this entry is present.

---

## Slice 01 — Scripts, list and editor

### `ScriptList` empty state — copy pair (01 → 06)

The empty state is a **different state in each slice**, not the slice-06 state with a button
hidden. The string itself changes (ruling B6), so the sweep checks the copy against the slice,
not against the slice-06 design.

- **Slice 01 renders:** the message *No scripts yet. Write your first one.* and **one** button,
  *Write your own*, opening a blank `ScriptEditor`. `[NEW · APPROVED 2026-08-04]`
- **Slice 06 renders:** the message *No scripts yet. Generate one in about thirty seconds, or
  write your own.* (verbatim F03) and **two** buttons — *Generate a script* (primary, first,
  wider) then *Write your own* (secondary). Delete this entry when slice 06 ships it.
- **Sweep · slice 01:** one button and the short string is correct. **Two buttons, or any
  string that mentions generation, is the defect** — the app cannot generate in slice 01 and
  must not promise it on the first screen a new user sees.
- **Sweep · slice 06:** the two-button, generation-first state is correct; the slice-01
  one-button string surviving into slice 06 is the defect.

### `ScriptList` empty-state focus order — changes between slices

- **Slice 01:** body copy → the single action button — **one action stop.**
- **Slice 06:** body copy → two action buttons — **two action stops.**
- **Sweep:** the stop count is slice-dependent. One stop is correct in 01 and a defect in 06;
  two stops is correct in 06 and a defect in 01. A correct build changing its focus order
  between these slices is not a regression — do not flag it as one.

### `ScriptEditor` Record button — RESERVED in 01, enabled in 02

- **Slice 01 renders Record in `STATE · RESERVED`:** container `surfaceContainerLow`, no fill,
  **no outline**; label and record dot in `onSurfaceVariant`, never record red; `enabled =
  false`; kept in focus order and announced, never skipped; its description states it arrives
  next version.
- **Slice 02 renders the live control:** enabled, `shape.full`, record red. Delete this entry
  when slice 02 ships it.
- **Sweep · slice 01:** the RESERVED treatment above is correct. **A filled, outlined, or
  primary-styled Record, a record-red dot, a Record missing from the focus order, or a Record
  that navigates anywhere, is a defect** — it has no destination until slice 02.

### `ScriptStart` — designed in 01, activates in 06

- **Slice 01:** `ScriptStart` is designed but **not rendered.** `+` on `ScriptList` opens a
  blank `ScriptEditor` directly (state-transition table row 4).
- **Slice 06:** `+` opens `ScriptStart` (row 5 replaces row 4): *Write it · Generate it · Paste
  or import*. Delete this entry when slice 06 ships it.
- **Sweep · slice 01:** `+` going straight to a blank editor is correct. **A route-chooser
  sheet appearing from `+` in slice 01 is the defect** — two of its three routes have no
  destination yet.

---

## Component gallery — outstanding components (01 → their respective slices)

`docs/build-plan.md` slice 01 lists ten components. The ruling (Phase E, 2026-08-05) is:
**the gallery renders every component that exists in the current slice, in every state** — not
components that have been listed but not yet built. The forward-ref principle applies: build only
components with a confirmed destination. The four below are tracked here so the shortfall is
visible rather than silent.

| Component | Currently | Activates | Rationale |
| --- | --- | --- | --- |
| **Slider** | not rendered | **Slice 02** | Prompter speed, opacity, size, and scrim controls (§02 design) |
| **Chip** | not rendered | **Slice 06** | Generation input — tone and audience as selection chips |
| **Toast / Snackbar** | not rendered | **Slice 03** | Take deletion 6-second undo window |
| **Progress (determinate)** | not rendered | **Slice 04** | Export progress — percentage, elapsed, estimated remaining |

**ModalBottomSheet** (ScriptStart) is already tracked in the slice above.

- **Sweep · current slice:** the gallery showing Button, OutlinedButton, FAB, ScriptRow, ScriptListOverflowMenu, AlertDialog, EmptyState, ReservedAction, WritingSurface, and TitleField is **correct** — do not flag the absence of the four above as a defect.
- **Sweep · activating slice:** when each component first appears in the build, add it to the gallery, then delete its entry here.

---

## Implementation deferrals — Phase B (not designed-ahead UI, but tracked so they resurface)

These are not deferred *designs*; they are deliberate implementation shortcuts taken during the
slice-01 build, recorded here so a later slice picks them up rather than trusting memory.

### IBM Plex OFL license — not yet in the shipped APK

- **Now:** the bundled fonts (`res/font/*.ttf`) are covered by `licenses/IBM-Plex-OFL.txt`, tracked
  **in-repo** for provenance. It is **not** packaged into the APK, because slice 01 has no surface
  that shows open-source notices.
- **Activates:** when an OSS-licenses / "About" surface exists (unslotted). Bundle the OFL text
  (and Material Symbols' Apache-2.0 notice) there or in `assets/`, then delete this entry.
- **Sweep:** the in-repo license with no in-app notices screen is correct for slice 01. Do **not**
  flag the missing in-app license text as a defect while no such surface exists.

### `ScriptList` multi-select and bulk delete — 2026-08-05, deferred out of slice 01

Multi-select is a **selection mode**, not a single action. Its full scope:

- **Selected row state** — visual diff from un-selected (not designed in slice 01).
- **Selection app bar** — replaces the "Scripts" header while a selection is active.
- **FAB suppression** — FAB hidden or disabled while selecting.
- **Back exits selection** — system back collapses the selection mode before exiting the screen.
- **Plural DeleteConfirm variant** — a selection of N scripts cannot echo forty titles; needs its
  own copy and a count string ("Delete 3 scripts?"). Distinct from the single-script dialog.
- **Selection-announcement accessibility contract** — TalkBack must announce "N of M selected" on
  each toggle; standard a11y pattern for multi-select.

Five new states and at least five new copy strings. **Design it before building it — it is a slice,
not an addition to slice 01.**

- **Sweep · slice 01:** no multi-select, no selection state, no selection app bar. The `···` is the
  only per-row action. A long-press that enters selection, or a checkbox, is the defect.

---

### `ScriptEditor` title — inline field until the shared §11 `TextField` lands

- **Now:** the editable title (`TitleField`) is a **styled `OutlinedTextField` inline** — the §11
  `TextField` in its title role (fill `surfaceContainerLowest`, `outline` → `primary` on focus,
  `shape.sm`). The shared, gallery-backed §11 `TextField` component is not built yet.
- **Activates:** when the §11 `TextField` component (design-system §11 deliverable) is built. Move
  `TitleField` onto it so there is one implementation, then delete this entry.
- **Sweep:** the inline field is the **correct §11 `TextField` usage**, not a fifth new component —
  do **not** flag it under the §2 "any fifth new component is a defect" rule. Two divergent
  `TextField` implementations once the shared one exists **would** be the defect.
