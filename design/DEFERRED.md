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
