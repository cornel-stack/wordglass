# Slice 01 — Handoff

**Date:** 2026-08-04
**Screens:** ScriptList, ScriptStart, ScriptEditor, DeleteConfirm
**Frames:** thirteen · twelve slice-01 states plus one slice-06 forward reference
**Supersedes:** `slice-01-handout` (design) and `slice-01-rulings` (decisions)

---

## How to use this document

**This is the source of truth for build, not the visual.** Where a frame and this
document disagree, this document wins and the disagreement is a design defect to be
raised, not resolved at the keyboard.

The fidelity sweep in §11 compares the build against **this file**, never against a
screenshot.

**The no-invention rule applies.** Do not add strings, labels, icons, menu items or
affordances that are not in §9. Placeholder copy that reads plausibly is still
invented copy. If something appears to be missing, stop and ask.

---

## §0 — Closed 2026-08-04

Four items, resolved in `design-system.md`. Kept here rather than deleted so the history
stays readable.

| # | Gap (was) | Resolution |
| --- | --- | --- |
| 0.1 | The screen-title role for "Scripts" was unnamed. | **`headlineLarge`** (30 sp / 1.2 / SemiBold) on `onSurface`. Already the "Screen titles" role and the closest to the ~28 sp drawn — no new role added. |
| 0.2 | The empty-state glyph was unspecified. | Material Symbols Outlined **`description`** at **`icon.size.large`** (48), `onSurfaceVariant`. **Decorative · not-important-for-accessibility** — the body copy beneath states the same thing, so announcing it would be redundant. |
| 0.3 | The empty-state body-copy role was unnamed. | **`bodyLarge`** on `onSurfaceVariant`, centred. No new token. |
| 0.4 | The empty-state buttons were unnamed. | Reuse: **`Button`** (filled — `primary` container, `onPrimary` label, 7.08:1) and **`OutlinedButton`** (outlined — `outline` border, `onSurface` label, 16.24:1). Geometry in §2. |

0.2 was the one that mattered most — an undeclared element present since the first draft
that had silently survived four reviews. It is now named, tokenised, and marked decorative.

---

## §1 — Scope

**Built in slice 01:** ScriptList (4 states), ScriptEditor (5 states), DeleteConfirm
(2 states), ScriptList overflow menu (1 state).

**Designed, not built:** ScriptStart. Registered in `DEFERRED.md`, activates slice 06.
No code path reaches it in slice 01.

**Rendered but not functional:** Record on ScriptEditor, in STATE · RESERVED.

**Out of scope, stated so the sweep does not read it as missing:**
- No delete action inside ScriptEditor. Delete lives where the list of things is.
- No search, sort or filter on ScriptList.
- No rename. Titles derive from the body's first line and are not directly editable
  beyond editing that line.
- No sharing, export or duplication.
- The overflow menu has exactly one item.

---

## §2 — Components

| Component | Status | Notes |
| --- | --- | --- |
| `ScriptRow` | **NEW** | Two configurations, one component — see §2.1 |
| `ScriptListOverflowMenu` | **NEW** | M3 `DropdownMenu`. One item in slice 01 |
| `WritingSurface` | **NEW** | Bespoke full-bleed body surface. Documented in `ScriptEditor.md`, **not** promoted to the component set, and **never** wrapped in a `TextField` |
| `ReservedAction` | **NEW** | STATE · RESERVED container. Record uses it now; slices 02–20 will reuse it |
| `TextField` | reuse | ScriptEditor title only, single line |
| `FloatingActionButton` | reuse | `fab.standard`, `shape.sm` |
| `ModalBottomSheet` | reuse | ScriptStart |
| `AlertDialog` | reuse | DeleteConfirm |
| `Button` | reuse | Empty-state filled action — *Generate a script* (slice 06). `primary` container, `onPrimary` label (7.08:1). 48 dp height, `shape.sm`, `space.4` horizontal padding, max width 288 dp, centred |
| `OutlinedButton` | reuse | Empty-state outlined action — *Write your own*. `outline` border, `onSurface` label (16.24:1). Same geometry as `Button`; the two stack with `space.3` between, so slice 06 **adds** a button rather than resizing one. Also DeleteConfirm's Cancel/Delete (§7.4) |

**Four new components in slice 01.** Each is justified above. Any fifth new component
appearing in the build is a sweep defect — revert it and use the specified one.

### §2.1 — `ScriptRow`, two configurations

Same component, different configuration. **Not** two components, and **not** identical.

| Property | `script` config (ScriptList) | `action` config (ScriptStart) |
| --- | --- | --- |
| Min height | `row.script` (88 dp) | `row.action` (72 dp) |
| Title lines | 2, clamp on the second | 1, no clamp |
| Metadata line | yes | none |
| Trailing affordance | `···` | none |
| Target | full width, minus the `···` exclusion | full width |
| Divider | 1 dp `outlineVariant`, inset to `space.4` | none |

---

## §3 — Tokens in use

Colour: `surface` · `surfaceContainerLow` · `surfaceContainerHigh` ·
`surfaceContainerHighest` · `onSurface` · `onSurfaceVariant` · `outline` ·
`outlineVariant` · `primary` · `onPrimary` · `error` · `scrim` · `scrim.modal`

Type: `headlineLarge` · `bodyLarge` · `labelMedium` · `labelSmall` · `numericMedium`

Spacing: `space.1` (4) · `space.3` (12) · `space.4` (16) · `space.6` (24) ·
`space.24` (96)

Shape: `shape.sm` · `shape.lg`

Motion: `motion.fast` (120 ms) · `motion.standard` (240 ms) · `easing.standard` ·
`easing.decelerate`

Component: `row.script` (88) · `row.action` (72) · `fab.standard` (56) ·
`icon.size` (24) · `icon.size.large` (48) · `icon.weight` (400) · `icon.grade` (0)

**`onSurfaceDisabled` is not used in slice 01.** Record uses STATE · RESERVED. If
`onSurfaceDisabled` appears anywhere in the build, that is a defect.

---

## §4 — ScriptList

`F03` · root of the app · five states

### §4.1 Structure, all states

**Header.** Title "Scripts" *(role: 0.1)* on `onSurface`. Padding `space.4`
horizontal, `statusBars` + `space.6` top. Does not collapse or scroll away.

**Insets.** `targetSdk 36` — edge-to-edge is not opt-outable. The surface paints under
both system bars.
- Header top: `statusBars` + `space.6`
- List bottom content padding: **`space.24` + `navigationBars`** (96 dp)
- FAB offset: `space.6` + `navigationBars`

**FAB clearance — the derivation, so it survives a value change:**

```
list bottom padding = fab.standard + space.6 + space.4 + navigationBars
                    = 56 + 24 + 16 + navigationBars
                    = space.24 + navigationBars
```

The last row must scroll to clear the FAB's top edge by 16 dp. Verify **scrolled to
the very end**, not mid-list.

**FAB.** `fab.standard` (56 dp), `shape.sm`, `primary` container, `onPrimary` glyph.
Icon `add`, `icon.size`. Target 56 dp. `shape.full` is reserved for the record button
and must not appear here.

### §4.2 State — empty · slice 01

Vertically centred block: glyph *(0.2)*, `space.6`, body copy *(0.3)* on
`onSurfaceVariant`, `space.6`, one button.

Copy: **"No scripts yet. Write your first one."**
Button: **"Write your own"**, outlined *(0.4)*

The FAB is present but redundant with the button and is **skipped in the focus order**.

### §4.3 State — empty · slice 06 · FORWARD REFERENCE, NOT BUILT

Identical block with two buttons — *Generate a script* filled, *Write your own*
outlined — and the two-line copy naming generation.

**A slice-01 build showing two buttons, or the slice-06 copy string, is a defect.**
The pair is registered in `DEFERRED.md`.

### §4.4 State — populated

Rows newest first. Height is content-driven with `row.script` as the minimum:

```
space.3 top
  title      bodyLarge / onSurface / 2 lines max, clamp with ellipsis on line 2
space.1
  metadata   labelSmall / onSurfaceVariant / one line
space.3 bottom
```

Horizontal padding `space.4`. Trailing `···` occupies a 48 dp square target.

**Metadata format:** `[relative date] · [read time]` — e.g. `2h ago · ≈ 47 sec`.
Numerals in mono tabular so figures do not jitter between rows. Read time computed at
**140 wpm**.

**Relative date rendering:** `2h ago` · `Yesterday` · `Mon` · `Fri` · `28 Jul` · `21 Jul`.

**The Untitled row.** A script whose body has been emptied to zero characters keeps
existing and has nothing to derive a title from. Its title position renders
**"Untitled"**, styled identically to a derived title, and its metadata line reads
normally — `Yesterday · ≈ 0 sec`.

> Note: `slice-01-rulings` §B2 wrote this metadata as `0 words · ≈ 0 sec`. That was
> wrong — word count belongs to the editor's count line, not to a list row. The
> design's `Yesterday · ≈ 0 sec` is correct and is what ships.

**Divider:** 1 dp `outlineVariant` between rows, inset to `space.4`. Note that
`outlineVariant` changed value on 2026-08-04 and these dividers are more visible than
in any design drawn before that date. Intended.

### §4.5 State — overflowing · worst-case content

74-character titles, three near-identical openings by design. Two lines keep them
distinguishable; the clamp falls on line 2. No layout differs from §4.4 — this state
exists to prove the clamp, not to introduce anything.

### §4.6 State — overflow menu

| Property | Value |
| --- | --- |
| Container | `surfaceContainerHigh`, `shape.sm`, 1 dp `outline` |
| Anchor | trailing edge of the `···`; opens downward, upward when the anchor is within 88 dp of the window bottom |
| Min width | 112 dp |
| Edge margin | `space.4` minimum from the window's trailing edge |
| Item height | 48 dp, full-width target |
| Item padding | `space.3` horizontal |
| Item label | `bodyLarge` on `onSurface` — **not** `error` |
| Scrim | **none** |
| Motion | in and out on `motion.fast` (120 ms), `easing.standard`, scale and fade from the anchor corner |
| Dismiss | tap outside · system back · predictive back |

Copy: **"Delete"**

**Why the item is not tinted `error`:** destructiveness is carried by the word and by
the dialog's sentence stating the consequence. There is no sentence in a menu, and
tinting the only item red is alarming for an action still two taps away. The dialog is
where the weight lands.

### §4.7 Accessibility — ScriptList

**Targets.** Row full-width at `row.script` minimum. The `···` is a 48 dp square target
**carved out of** the row's target, not overlapping it — nested targets are a defect.
FAB 56 dp visual and 56 dp target.

**Focus order.** Screen title → row 1 → row 1 `···` → row 2 → row 2 `···` → … → FAB.
Title and metadata read as **one node per row**, not three.

FAB last, not first: this screen's purpose is finding an existing script, and creation
is the less frequent action per session.

**Content descriptions, verbatim:**

| Element | Description |
| --- | --- |
| Row | `"[full untruncated title], [relative date], about [read time] to read aloud."` |
| `···` | `"More options for [title]."` |
| FAB | `"New script."` |
| Menu item | uses its visible label |

The row title is **not truncated for screen readers.** The two-line clamp is a sighted
affordance. Forty controls all reading "More options" would be indistinguishable,
which is the same findability problem the two-line title solved for sighted users.

**Empty state focus:** body copy → button(s). **The order has one stop in slice 01 and
two in slice 06** — registered in `DEFERRED.md` so the sweep does not read a correct
build as a defect.

**Menu focus:** enters on open, lands on the first item, trapped while open, returns to
the `···` on every dismiss path.

**At 200%.** Rows grow; they do not clamp. The two-line title clamp is a **line** clamp,
not a height clamp — the row is taller and still two lines. The metadata line wraps
rather than truncating; numerals never truncate. The FAB does not scale with font size.

**Colour alone.** Nothing. Date and read time are text.

**Contrast — MEASURE and record before build:** title `onSurface` on `surface`;
metadata `onSurfaceVariant` on `surface` (7.51:1); the `···` mark ≥3:1 as an icon-only
control; `onPrimary` on `primary` for the FAB glyph; menu item `onSurface` on
`surfaceContainerHigh`; menu border `outline` on `surface` (4.65:1).

---

## §5 — ScriptStart

`F03` · `F01` · **designed now, activates slice 06. No code path in slice 01.**

### §5.1 Structure

`surfaceContainerHigh`, top corners `shape.lg`, 1 dp `outlineVariant` top edge, 32 × 4
drag handle. Height wraps content at roughly 300 dp. No fixed height, no
drag-to-expand, no half stops — nothing on the surface can grow.

Three routes in `ScriptRow` `action` configuration, `row.action` (72 dp):

1. **Generate it**
2. **Write it**
3. **Paste or import**

Equal weight, none filled, none preselected. Order is deliberate: `F03`'s empty state
puts generation first, and first position is reading order, not preselection.

**No visible heading.** `F03` and `F01` specify no title and nothing is borrowed. The
sheet takes its meaning from the `+` that opened it.

### §5.2 Scrim and motion

Scrim `scrim` at `scrim.modal` (0.60), full window including both system bar regions.
**Identical to DeleteConfirm's.** If they differ visibly on device, that is a defect.

Enter: slides up from the bottom edge, scrim fading over the same interval —
`motion.standard` (240 ms), `easing.decelerate`.
Exit: reverses on `motion.fast` (120 ms), `easing.standard`.
Reduced motion inherits the systemic rule.

**Dismiss — three equivalent paths.** Scrim tap, swipe down past the handle, system
back including predictive back. All non-destructive, none confirmed, each returning to
ScriptList with nothing created.

### §5.3 Accessibility — ScriptStart

**Targets.** Routes full-width at `row.action`. The drag handle is decorative and is
not a target — mark it not-important-for-accessibility.

**Accessible name.** The no-heading decision leaves a screen reader with nothing to
announce on open. The sheet carries a pane title that is **spoken and never rendered**:

> **"Start a script"** — accessible name only. It must not appear on screen.

**Focus.** Enters on open, lands on *Generate it*. Trapped while open. Returns to the
`+` on every dismiss path.

**Content descriptions.** The three route labels are self-describing and take none.
Stated explicitly so none are invented.

**At 200%.** Rows grow, labels wrap, the sheet grows from its content wrap, capped at
the window minus `space.6`; past that the route list becomes the scrolling region.

**Colour alone.** Nothing — no route is preselected, so no state is carried by colour.

---

## §6 — ScriptEditor

`F03` · built in slice 01 · Record in STATE · RESERVED

### §6.1 Structure

**Title.** Single-line `TextField`, ellipsis at overflow, **no placeholder**. 56 dp.
Derived from the body's first line. `outline` boundary — this field is the reason
`outline` was raised, because in the `new` state it is empty and the boundary is the
only thing saying it is editable.

**Body.** `WritingSurface` — bespoke, full-bleed, `bodyLarge` on `surface`, no border,
no label, no fill, `space.4` horizontal padding, leading 1.5×. Never wrapped in a
`TextField`.

**Cursor lands in the body on entry**, not the title. The title derives; it is not
typed into first.

**No top app bar and no back arrow.** Both affordances are in the bottom action row.

### §6.2 Count line

One line below the body, in flow.

Format: `[count] words · ≈ [read time]`. Empty reads `0 words · ≈ 0 sec`.

Figures in `numericMedium`, mono tabular, so they do not jitter as they settle.
"words" and the separator in `labelMedium`. Computed at **140 wpm**.

**One two-second debounce drives the count, the read time and the autosave together**,
so the number settling is the same moment the write happened. Never per keystroke.

### §6.3 Bottom action row

Last in flow. Sits above the keyboard when it is up, and at `space.6` +
`navigationBars` when dismissed.

**Left — exit.** Label **"Scripts"** beneath an outlined document mark. 96 × 72 dp
target, no container.

**Right — Record.** Takes the remaining width at 72 dp. **STATE · RESERVED:**

```
Container    surfaceContainerLow, no fill, NO OUTLINE
Label        onSurfaceVariant
Record dot   onSurfaceVariant — NOT the record red
Semantics    enabled = false, announced, never skipped
Shape        shape.sm
```

Rendered in its permanent position so nothing reflows when slice 02 wires it — only
the fill, the label colour, the dot colour and the description change.
`shape.full` and the record red stay reserved for the real record button.

**Why navigation is left and secondary.** Record is the primary action and leaving is
not; navigation should not outrank the thing the screen exists to enable. The top-left
corner is also the hardest target to reach one-handed at 412 dp, and an edge swipe
there collides with dragging a text selection.

### §6.4 States

| State | Difference |
| --- | --- |
| **new** | Empty title, empty body, keyboard up, cursor in body, count `0 words · ≈ 0 sec` |
| **editing** | Title derived, body present, keyboard up |
| **autosaved** | **Identical to editing, by design.** No indicator, no flash, no layout change |
| **interrupted** | **Identical to editing, by design.** Body, scroll offset, cursor, selection and keyboard state all restored. No recovered banner |
| **keyboard dismissed** | Row moves to `space.6` + `navigationBars`. Body gains the vacated height |

**Three frames, one appearance.** Editing, autosaved and interrupted are visually
identical on purpose. This is a documented non-difference, not three designs that
happen to match — do not add an indicator to distinguish them.

The absent save button is the message. A "Saved" pulse every two seconds would train
anxiety about something that never fails.

### §6.5 Record creation — the rule that stops empty scripts

**A never-typed editor does not create a script.** The record is created on the first
character committed to the body, on the same two-second debounce.

Back from an untouched editor returns to ScriptList unchanged: no row, no dialog, no
toast.

**An editor emptied to zero characters keeps its script.** Deleting content is not
deleting a script; the user has an explicit Delete for that intent, and silently
destroying a saved record because the last character went is data loss without
confirmation. Its row renders per §4.4.

### §6.6 Accessibility — ScriptEditor

**Targets.** Exit 96 × 72 dp around a visual block of roughly 72 × 46 dp. Record 72 dp
tall across the remaining width. Title field 56 dp. Nothing below 48 dp.

**Non-gesture paths.** The system back gesture does the same thing as *Scripts* and is
the alternative, not the only route. No long-press, swipe action or custom gesture
exists on this screen.

**Content descriptions, verbatim:**

| Element | Description |
| --- | --- |
| Exit | `"Back to scripts"` |
| Record (RESERVED) | `"Record. Not available yet — recording arrives in a later version."` |
| Count line | `"420 words, about 3 minutes to read aloud"` — pattern, values substituted |

The visible exit label names the destination; its description names the direction.
That split is deliberate — sighted users get the place, screen reader users get the
movement their linearised context needs.

**Focus order.** Title → body → count line → exit → Record. Record keeps its place and
is announced as unavailable rather than skipped, so its position is learnable before
it works. Focus and cursor land in the body on entry, and at the restored cursor on
return.

**At 200%.** Everything is in flow, so nothing can overlap. Row height is a minimum;
labels wrap rather than truncate and targets never drop below 72 dp. The count line
wraps — **numerals must never be what truncates.**

**Contrast — MEASURE and record:** body and title on `surface`; count line
`onSurfaceVariant`; exit label; caret; **Record label `onSurfaceVariant` on
`surfaceContainerLow` — 7.20:1.** The old 2.9:1 disabled figure no longer applies and
must not reappear.

---

## §7 — DeleteConfirm

Two states. All copy `[NEW — APPROVED 2026-08-04]`.

### §7.1 Why a dialog and not undo

Takes get undo; scripts get a confirmation.

A take is an unreproducible ninety-second performance, so the app must never make you
perform it again — it acts, then offers undo. A script is text that syncs. It can be
rewritten, but deleting it removes it from every device at once, so the cost has to
land **before** the action rather than after it.

### §7.2 Structure

`elevation.3`, expressed as `surfaceContainerHighest` plus a 1 dp `outline` rather
than a shadow — the system is flat with outlines, the scrim separates and the outline
edges. `shape.lg` corners, `space.6` padding, `space.6` from the screen edges. No
fixed height; grows from its centre.

Scrim `scrim` at `scrim.modal` (0.60), full window including both system bar regions.
**Identical to ScriptStart's.**

### §7.3 Copy

| Slot | String |
| --- | --- |
| Title | `Delete this script?` |
| Body | `"[title]" will be removed from all your devices. This can't be undone.` |
| Primary | `Delete` |
| Secondary | `Cancel` |

**The echoed title is the only variable string, so it is the only overflow case.** It
truncates at about forty characters with **the ellipsis inside the closing quote mark**
and the sentence around it untouched. Both quote marks always render, so the echo
always reads as a quotation and never as the app's own words.

### §7.4 Both actions outlined

Delete began as a filled `error` button. That had two problems: the safe path was
quieter than the dangerous one, and white on that red measured 2.8:1.

Both are now outlined at 88 × 48 dp:

| Action | Border | Label |
| --- | --- | --- |
| Cancel | `outline` | `onSurface` |
| Delete | `error` | `error` |

Equal weight, and the system prefers outlines to fills. Cancel is left, Delete right.

### §7.5 Accessibility — DeleteConfirm

**Focus on open lands on Cancel** — not Delete, and not the dialog container. One stray
Enter should keep the script, not lose it. Order is Cancel then Delete, trapped while
open.

**Focus return.** After confirming, focus moves to the row that took the deleted row's
position, or to the empty state's first action if the list is now empty. After
cancelling, a scrim tap or system back, it returns to the `···` that opened the chain.
Never the top of the list, never nowhere.

**TalkBack reads the echoed title untruncated and without its quote marks**, so the
user hears which script they are deleting in full.

| Element | Description |
| --- | --- |
| Cancel | `"Cancel. Keep this script."` |
| Delete | `"Delete this script permanently."` |

**At 200%.** No fixed height; the dialog grows from its centre, capped at the window
minus `space.6`. Past that the **body becomes the scrolling region while the title and
action row stay pinned**, so the buttons can never be what is pushed off screen. The
row wraps to two stacked full-width buttons, **Cancel above Delete**.

**Colour alone.** Nothing. Destructiveness is carried by the `error` colour, by the word
"Delete", and by the sentence stating the consequence.

---

## §8 — State transition table

| # | From | Trigger | To | Slice | Notes |
| --- | --- | --- | --- | --- | --- |
| 1 | ScriptList · empty | *Write your own* | ScriptEditor · new | 01 | no record created yet |
| 2 | ScriptList · empty | *Generate a script* | — | 06 | not rendered in 01 |
| 3 | ScriptList · populated | row tap | ScriptEditor · editing | 01 | restores cursor and scroll |
| 4 | ScriptList · populated | `+` | ScriptEditor · new | **01** | sheet bypassed |
| 5 | ScriptList · populated | `+` | ScriptStart | **06** | replaces row 4 |
| 6 | ScriptList · populated | `···` | ScriptList · overflow menu | 01 | |
| 7 | ScriptList · overflow menu | *Delete* | DeleteConfirm | 01 | |
| 8 | ScriptList · overflow menu | outside tap / back | ScriptList · populated | 01 | focus returns to `···` |
| 9 | DeleteConfirm | *Delete* | ScriptList · populated or empty | 01 | focus to the row taking its position, or the empty state's first action |
| 10 | DeleteConfirm | *Cancel* / scrim / back | ScriptList · populated | 01 | focus returns to `···` |
| 11 | ScriptEditor · new | back or *Scripts*, nothing typed | ScriptList · unchanged | 01 | **no record created** |
| 12 | ScriptEditor · new | first character committed | ScriptEditor · editing | 01 | record created on the 2 s debounce |
| 13 | ScriptEditor · editing | back or *Scripts* | ScriptList · populated | 01 | saved, no confirmation |
| 14 | ScriptEditor · editing | keyboard dismissed | ScriptEditor · keyboard dismissed | 01 | row moves to `space.6` + `navigationBars` |
| 15 | ScriptEditor · editing | process death | ScriptEditor · interrupted | 01 | body, scroll, cursor, selection, keyboard restored |
| 16 | ScriptEditor · any | *Record* | — | 02 | RESERVED in 01 |
| 17 | ScriptStart | any route / dismiss | ScriptEditor or ScriptList | 06 | nothing created on dismiss |

**Rows 4 and 5 are the pair that must exist separately.** A builder reading only one
would ship the wrong behaviour and nobody would find out until the sweep.

---

## §9 — Copy inventory

Every string in the build. Nothing outside this table may appear.

| String | Screen | Status |
| --- | --- | --- |
| `Scripts` (screen title) | ScriptList | `[NEW — APPROVED 2026-08-04]` |
| `No scripts yet. Write your first one.` | ScriptList empty · 01 | `[NEW — APPROVED 2026-08-04]` |
| `Write your own` | ScriptList empty | verbatim F03 — **confirm** |
| `Generate a script` | ScriptList empty · 06 | verbatim F03 — **confirm** · not built in 01 |
| `No scripts yet. Generate one in about thirty seconds, or write your own.` | ScriptList empty · 06 | verbatim F03 — **confirm** · not built in 01 |
| `Untitled` | ScriptList row | `[NEW — APPROVED 2026-08-04]` |
| `Delete` (menu item) | Overflow menu | `[NEW — APPROVED 2026-08-04]` |
| `Generate it` / `Write it` / `Paste or import` | ScriptStart | verbatim F03/F01 · not built in 01 |
| `Scripts` (exit label) | ScriptEditor | `[NEW — APPROVED 2026-08-04]` |
| `words` · `sec` · `min` | count line, rows | derived units, not copy |
| `Delete this script?` | DeleteConfirm | `[NEW — APPROVED 2026-08-04]` |
| `"[title]" will be removed from all your devices. This can't be undone.` | DeleteConfirm | `[NEW — APPROVED 2026-08-04]` |
| `Delete` / `Cancel` | DeleteConfirm | `[NEW — APPROVED 2026-08-04]` |
| `Start a script` | ScriptStart | `[NEW — APPROVED 2026-08-04]` · **accessible name only, never rendered** |

Plus every content description in §4.7, §5.3, §6.6 and §7.5 — all approved, all verbatim.

**Three strings need confirming against F03 before build.** If they are not verbatim,
tag them `[NEW]` and approve them as-is; they read correctly either way, but they must
not reach the build untagged if they were invented.

---

## §10 — Build constraints

### 10.1 Deletion must propagate as a delete

`"removed from all your devices"` is a promise the implementation has to honour.
Deletion propagates as a **tombstone that syncs**. It must not delete locally and then
have the record sync back down from another device — a script reappearing makes the
dialog a lie.

Local-first still applies: the row disappears immediately, the tombstone syncs when
there is a network, and offline deletion is neither blocked nor given a spinner.

**If propagation cannot be guaranteed, the copy changes before the build.**

### 10.2 Edge-to-edge is mandatory

`targetSdk 36` — `windowOptOutEdgeToEdgeEnforcement` is deprecated and ignored. Every
screen handles `statusBars` and `navigationBars` explicitly per §4.1.

### 10.3 Predictive back is mandatory, app-wide

At `targetSdk 36` the predictive back system animations are on by default,
`onBackPressed()` is no longer called and `KEYCODE_BACK` is no longer dispatched.
`OnBackInvokedCallback` is required. Every dismissable surface in slice 01 —
the menu, the sheet, the dialog, the editor — inherits this.

### 10.4 Scrim implementation

Compose's `Dialog` dims through the platform window rather than a composable. Hitting
exactly `scrim.modal` across both system bar regions needs the dialog drawn as a
full-window overlay, or `DialogProperties(decorFitsSystemWindows = false)`.
`ModalBottomSheet` takes `scrimColor` directly. **Both must land on the same value.**

### 10.5 One debounce, three consumers

A single two-second debounce drives the word count, the read time and the autosave.
Not three timers. Not per keystroke.

---

## §11 — Fidelity sweep · commit gate

Nothing commits without a clean sweep. No exceptions, including "only changed one thing."

1. **Screenshot every designed state on device** — twelve slice-01 states, not the
   happy path only.
2. **Compare against this document, not the frames.** Token names, dp, exact copy.
3. **Check for invented content** — any string, label, icon or affordance in the build
   but absent from §9. Most common defect, always silent.
4. **Check for missing states** — any designed state with no code path to reach it.
5. **Check reuse** — a fifth new component beyond §2 gets reverted.
6. Over-camera contrast pass — **N/A in slice 01**, no capture UI.
7. Fix, re-sweep, commit.

### Slice-01-specific checks

- [ ] `onSurfaceDisabled` appears **nowhere**. Record uses STATE · RESERVED.
- [ ] Record has **no outline** and its dot is `onSurfaceVariant`, not the record red.
- [ ] `shape.full` appears nowhere.
- [ ] ScriptList empty shows **one** button and the slice-01 string.
- [ ] `+` opens a blank ScriptEditor. It does **not** open ScriptStart.
- [ ] ScriptStart has no reachable code path.
- [ ] Backing out of a never-typed editor adds **no row**.
- [ ] Scrolled fully to the end, the last row clears the FAB by 16 dp.
- [ ] The `···` target does not overlap the row target.
- [ ] Every `···` description names its script.
- [ ] Row titles read untruncated in TalkBack.
- [ ] DeleteConfirm opens with focus on **Cancel**.
- [ ] Focus returns to the originating `···` after every dismiss path.
- [ ] The two scrims are visibly identical.
- [ ] At 200%: nothing overlaps, no numerals truncate, dialog buttons stay on screen.
- [ ] Deleting a script does not resurrect it after a sync.
