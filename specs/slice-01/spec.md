# Slice 01 — Scripts, list and editor — Scope

**Build loop step 1 (`/specify`).** `/specify` is not installed as a command in this
project, so this document is the scope artifact that step produces, written by hand against
`docs/build-plan.md` (slice 01) and `docs/user-flows.pdf` (F03). No screens are named as a
build target here; the screen inventory is the next step.

---

## Goal

The easiest real feature, built to prove the design-to-build loop works before that loop
meets the camera. A user can write, save, find, re-open, and delete a plain-text script,
and see a live estimate of how long it takes to read aloud. The surface-set token system
and the shared component set emerge from these screens rather than being decided in the
abstract (tokens already captured in `docs/design-system.md` v1).

## User-visible behaviour

- From a list of their scripts, the user can create a new one and open an existing one.
- The editor opens empty with the keyboard up and the cursor in the body. The title is
  auto-derived from the first line and is editable.
- As the user types, a read-time estimate at **140 wpm** updates **on pause, not per
  keystroke**, displayed as `≈ 47 sec` (verbatim format from F03).
- The script **autosaves on 2s idle and on background. There is no save button.**
- Leaving the editor is always safe — autosave guarantees it. There is **no "discard
  changes?" dialog** anywhere.
- The list shows the user's scripts; an empty list shows a first-run state; a populated
  list holds realistic volume (40 items) and survives an overflowing title.
- The user can delete a script through a confirmation.
- Everything here works with no connectivity — it is entirely local.

## Acceptance criteria

Slice-level "done when" (from `docs/build-plan.md`):

1. Write a script, kill the app, reopen it — it is present, with a correct read-time
   estimate.
2. The debug-only component gallery renders every component in every state.

Feature-level, from F03:

3. A new script opens to an empty editor, keyboard up, cursor in body; the title derives
   from the first line and can be edited.
4. The read-time estimate reads `≈ N sec`/`≈ N min` at 140 wpm and updates on pause.
5. No save button exists; autosave fires at 2s idle and on background; back is always safe.
6. The list's empty state and a 40-item populated state both render; a 74-character title
   does not break the row.

## Out of scope — deferred to the slice noted

- **AI generation** and the `GenerateInput` / `VariantPicker` screens — slice 06 (F04).
- **Paste / file / URL import** and `ImportPreview` — slice 06 / 15 (F05).
- **Recording** — the `Capture` screen and everything F03 step 6 (`Taps Record`) leads to —
  slice 02 (F06).
- **Takes** — `TakeList`, `TakePlayer`, take review/undo — slice 03 (F08).
- **Share-to-Wordglass** text intent that prefills `ScriptEditor` — later slice.
- **Auth / orgs / sync** — slice 13. Scripts are local-only here; `org_id` already exists
  in the schema and is populated with the solo user's org, but nothing syncs.

## Open scope questions — must resolve before the design prompts

F03's happy path threads through three screens that **do not exist until later slices**.
Slice 01 has to decide what stands in their place, and the honest-UI rules
(no dead ends, no invented copy) make this a real decision rather than a default. See the
inventory hand-off for the specific question; the three forward-references are:

1. **The `+` route chooser.** F03 step 1 is "Taps `+`, then *Write it*," implying a chooser
   offering *Write it / Generate it / Paste or import*. Two of those three routes are slice
   06. Does slice 01 show the chooser, or does `+` open a blank editor directly?
2. **The empty-state copy.** F03's verbatim empty state is *No scripts yet. Generate one in
   about thirty seconds, or write your own.* — **two buttons, generation first.** The
   *Generate* button cannot work until slice 06.
3. **The editor's `Record` button.** F03 makes *Record* the editor's primary action, but it
   leads to `Capture` (slice 02). In slice 01 it has nowhere to go.

All three point at the same underlying fact: slice 01 is upstream of the slices its happy
path depends on. Resolved at the inventory checkpoint, not invented here.
