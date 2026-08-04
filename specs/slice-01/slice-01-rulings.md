# Slice 01 — Rulings

**Date:** 2026-08-04
**Resolves:** the nine open rulings in `SLICE-01-HANDOUT` plus six items blocking build.
**Status of every item below:** settled. Nothing here is deferred.

Values marked **MEASURE** are targets, not measurements. Compute them against the
actual hex values in `design-system.md` and record the result. A ratio nobody
measured is not a ratio.

---

## Part 1 — The nine open rulings

### 1. Modal scrim token · RESOLVED

Add to `design-system.md`:

```
scrim              #000000
scrim.modal        0.60 opacity
```

One token, both surfaces — `ScriptStart` and `DeleteConfirm`. Coverage is the full
window including both system bar regions.

**Why 0.60 and not Material's 0.32.** M3's default is calibrated for a light theme or
a tonally-lifted dark one. Wordglass is single-theme near-black. A 32% black scrim
over a near-black `surface` is close to invisible — it would fail the only job a scrim
has, which is saying *the layer behind this is inert*. At 60% the list behind
`DeleteConfirm` still reads as context and unmistakably as unavailable.

**It must not borrow the `oc.*` set.** Those are calibrated against a live camera
preview and always pair with an outline. Different problem, different value.

**Build note.** Compose's `Dialog` dims through the platform window rather than a
composable, so hitting exactly 0.60 across system bars needs the dialog drawn as a
full-window overlay, or `DialogProperties(decorFitsSystemWindows = false)`.
`ModalBottomSheet` takes `scrimColor` directly. Both must land on the same value —
if they visibly differ on device, that is a sweep defect.

---

### 2. Outline contrast · RESOLVED — split the token

The research changed this one. WCAG 1.4.11 does **not** require a border to reach 3:1
when the component is identifiable by other means — a button with a visible text
label is identified by its label, not its edge. So `Cancel` and `Delete` on
`DeleteConfirm` conform as drawn.

But two things still fail:

- **The title `TextField` in ScriptEditor's `new` state.** Empty, no placeholder — the
  outline is the *only* thing saying this is editable. That is a real 1.4.11 failure
  at 1.8:1, and it is the first thing a new user sees.
- **1.4:1 on `surfaceContainerHighest`** is below the threshold at which a boundary is
  perceptible at all, conformance aside.

**Ruling — two tokens with separate jobs:**

| Token | Job | Requirement |
| --- | --- | --- |
| `outline` | Load-bearing. Any boundary that identifies an interactive component or its state. | **≥3:1 against both `surface` and `surfaceContainerHighest`**, stated as two measured ratios |
| `outlineVariant` | Decorative only. Dividers, the sheet's top edge, non-informational rules. | No floor. **Never** the sole identifier of a control |

**MEASURE.** Target `outline` ≈ `#767C85`. That should clear 3:1 on both surfaces given
a near-black `surface` and a `surfaceContainerHighest` around `#2A2E33` — verify
against the real values and adjust upward until both ratios pass.

**Standing rule for `design-system.md`:** if a boundary is the only thing that
identifies a control, it uses `outline`. If the control has a visible text label, the
boundary is decorative and may use `outlineVariant` — but prefer `outline` anyway,
because a 1.4:1 edge is invisible whether or not a standard requires it to be visible.

Raising `outline` also fixes `Cancel`'s border and `DeleteConfirm`'s 1 dp container
edge for free.

---

### 3. Disabled text contrast · RESOLVED — Record is not a disabled control

Research: inactive components are **explicitly exempt** from both 1.4.3 and 1.4.11.
So 2.9:1 conforms. The handout is right that this is unsatisfying, and right about
why — the exemption assumes a control that is disabled *momentarily*, pending some
user action. Record is disabled for an entire release.

**Do not raise `onSurfaceDisabled` globally.** Genuinely momentary disabled controls
would start reading as enabled, which is a worse failure and would be everywhere.

**Ruling — a third state, distinct from enabled and disabled:**

Add to `design-system.md`:

```
STATE · RESERVED

A control rendered in its permanent position before the release that activates it.
Distinct from disabled, which is momentary and pending a user action.

  Container    surfaceContainerLow, no fill, NO OUTLINE
  Label        onSurfaceVariant          [MEASURE — target ≥4.5:1]
  Iconography  onSurfaceVariant, never the semantic colour of the live control
  Semantics    enabled = false; announced, never skipped
  Copy         the description states when it arrives

onSurfaceDisabled is for momentary disabled states only.
```

Record in slice 01 takes RESERVED. The absence of an outline is what says *not a
button yet* — legibility no longer has to carry that meaning, so the label can be
legible. The record dot renders `onSurfaceVariant`, not the record red, which stays
reserved for the real control alongside `shape.full`.

The existing content description and focus-order decisions are correct and unchanged:
it keeps its place in the order and is announced rather than skipped, so its position
is learnable before it works.

This rule will recur — the build plan defers a lot into visible positions.

---

### 4. Row height, FAB size, body leading · RESOLVED

**Row heights were never inventions.** M3's list item heights are 56 / 72 / 88 dp. Both
values already land on the standard scale; they just had no names. Name them by role,
not by line count, so a future row with different content can't misread the token:

```
row.script     88   ScriptList — two title lines plus a metadata line
row.action     72   ScriptStart — a route choice, not a data row
```

Both are touch targets, not just visual heights. State it that way in the handoff.

**FAB.** 56 dp is M3's standard FAB. The observation that 56 is not on the spacing
scale is a category error — a FAB's diameter is a component dimension, not a spacing
step. Add `fab.standard = 56`. Do not add a 56 to the spacing scale.

**Leading.** Every type role gains an explicit line height rather than one global
token, since the roles differ:

```
body roles     1.5×    (matches the prompter tier, and M3's own bodyLarge 16/24)
label roles    1.33×
prompter tier  1.5×    unchanged
```

The consistency between the body tier and the prompter tier is worth having
deliberately: this is a writing tool whose text gets read aloud, and prose that
looks the same in both places is easier to judge while drafting.

---

### 5. Icon set · RESOLVED

**Material Symbols, Outlined, weight 400, grade 0, optical size 24.**

```
icon.size      24
icon.weight    400
icon.grade     0
```

It ships with the platform, it matches the system iconography the design system
already defers to (*the system prefers outlines to fills*), and it closes a whole
class of future decisions in one line.

**The record dot is not an icon.** It stays custom geometry and does not come from the
set — slice 02's real record button owns that mark, and it must not be reachable
through an icon name.

**Standing rule:** no icon-only control in a primary action position. Row-level
affordances may be icon-only if all three hold: the glyph is platform-conventional,
the content description names its object (not just its function), and the mark meets
3:1. The `···` on ScriptList qualifies. A bare icon as a screen's main action does not.

---

### 6. FAB clearance · RESOLVED

The arithmetic in the handout is right and the fix offered is 8 dp short. Express it
as a formula so it stays correct when a value changes:

```
List bottom content padding
  = fab.standard + space.6 + space.4 + navigationBars
  = 56 + 24 + 16 + navigationBars
  = 96 dp + navigationBars
```

96 dp is `space.24`, so it is on the scale. The last row clears the FAB's top edge by
16 dp.

**Rejected:** shrinking the FAB offset. That pushes the FAB toward the gesture pill,
which is the opposite of what edge-to-edge enforcement is asking for, and it trades a
visible problem for a harder-to-see one.

---

### 7. Screen title "Scripts" · APPROVED

`[NEW — APPROVED 2026-08-04]`

The deciding argument is the roadmap, not the aesthetics. *My Scripts* and *Your
Scripts* both become wrong the moment team scripts land — the possessive stops being
true when the org is the billing unit and a marketing lead's scripts appear in a rep's
list. *Library* invents a metaphor the product doesn't use anywhere else. *Scripts* is
the domain noun, it matches the flow language, and it survives the team feature
without an edit.

---

### 8. Exit label "Scripts" · APPROVED

`[NEW — APPROVED 2026-08-04]`

Same string as the screen title, deliberately. The label and the place it goes to
being the same word makes the mapping learnable in one trip. *All scripts* and *My
scripts* fail on the same team-boundary argument as #7.

The split between the visible label naming the destination and the content
description naming the direction — "Back to scripts" — is correct as specified and
stays. Sighted users get the place; screen reader users get the movement, which is
what the linearised context needs.

---

### 9. Longest realistic body · RESOLVED — the two-value reading is confirmed

Both numbers are real and neither replaces the other. Name them separately so no one
collapses them:

```
content.body.tuned    ~420 words     what the writing surface's leading, measure
                                     and scroll behaviour are tuned for — a
                                     three-minute script at 140 wpm

content.body.max      4,012 words    the stress case for the count line's numerals
                                     and for ScriptList's ≈ 28 min 40 sec read time
```

**No length cap in slice 01.** The writing surface must not break at
`content.body.max`, and nothing truncates. If a cap is ever introduced it belongs to
generation in slice 06, not to the editor.

---

## Part 2 — What blocks the build

### B1. The overflow menu · BUILD IT, one item

`···` opens an anchored dropdown menu containing a single item, **Delete**, which
opens `DeleteConfirm`.

**Why a menu and not a direct action.** `···` means *more options* everywhere on the
platform. A control that means "more" and does "delete" is a trap, and a confirmation
behind it doesn't undo the mislearning. The container is also known to grow — rename,
duplicate, export, move to team are all plausible — and the affordance's meaning has
to be stable across the roadmap. Two taps before a destructive action is a soft speed
bump before the hard one, not a cost.

**Spec — this is a twelfth state of ScriptList, not a new screen.**

| Property | Value |
| --- | --- |
| Container | `surfaceContainerHigh`, `shape.sm`, 1 dp `outline` |
| Anchor | trailing edge of the `···`, opening downward; upward when within 88 dp of the window bottom |
| Min width | 112 dp |
| Item height | 48 dp · full-width target |
| Item padding | `space.3` horizontal |
| Item label | `bodyLarge` on `onSurface` — **not** `error` |
| Scrim | none. Menus don't scrim; the anchor keeps context |
| Motion | `motion.fast` 120 ms, `easing.standard`, scale and fade from the anchor corner |
| Dismiss | tap outside · system back · predictive back |

**Why the item is not tinted `error`.** `DeleteConfirm`'s own reasoning applies:
destructiveness is carried by the word and by the sentence stating the consequence.
There is no sentence in a menu, and tinting the only item red is alarming for
something still two taps from happening. The dialog is where the weight lands.

**Copy.** "Delete" — `[NEW — APPROVED 2026-08-04]`, same verb already approved on the
dialog.

**Accessibility.**
- Focus enters the menu on open and lands on the first item. Trapped while open.
- Focus returns to the `···` that opened it on every dismiss path.
- `···` content description: **"More options for [title]"** — with the title. Forty
  controls all reading "More options" are indistinguishable in TalkBack, which is
  exactly the findability problem the two-line title fixed for sighted users.
- The `···` mark is icon-only, so 3:1 is mandatory. **MEASURE.**
- At 200%: item height is a minimum, labels wrap rather than truncate.

---

### B2. Back from a blank editor · RESOLVED

**A never-typed editor does not create a script.** The record is created on the first
character committed to the body — the same two-second debounce that already drives
autosave and the count line. Back from an untouched editor returns to ScriptList with
nothing added: no dialog, no toast, no row.

**An editor emptied to zero characters keeps its script.** Deleting content is not
deleting a script. Silently destroying a saved record because the last character was
removed is data loss with no confirmation, and the user already has an explicit Delete
for that intent.

**Consequence — the empty-title row.** A script with an empty body has nothing to
derive a title from. Its row renders **"Untitled"** in the title position, styled
identically, with the metadata line reading `0 words · ≈ 0 sec`.
`[NEW — APPROVED 2026-08-04]`.

The list can therefore still accumulate untitled rows, but only through deliberate
user action, never through app behaviour. That is the distinction the original
objection was after.

---

### B3. State transition table · WRITTEN

This goes into the handoff as a table, replacing the exit prose distributed across
four sections.

| # | From | Trigger | To | Slice | Notes |
| --- | --- | --- | --- | --- | --- |
| 1 | ScriptList · empty | *Write your own* | ScriptEditor · new | 01 | no record created yet |
| 2 | ScriptList · empty | *Generate a script* | — | 06 | not rendered in 01 |
| 3 | ScriptList · populated | row tap | ScriptEditor · editing | 01 | restores cursor and scroll if returning |
| 4 | ScriptList · populated | `+` | ScriptEditor · new | **01** | sheet bypassed |
| 5 | ScriptList · populated | `+` | ScriptStart | **06** | replaces row 4 |
| 6 | ScriptList · populated | `···` | ScriptList · menu | 01 | B1 |
| 7 | ScriptList · menu | *Delete* | DeleteConfirm | 01 | |
| 8 | ScriptList · menu | outside tap / back | ScriptList · populated | 01 | focus returns to `···` |
| 9 | DeleteConfirm | *Delete* | ScriptList · populated or empty | 01 | focus to the row that took its position, or the empty state's first action |
| 10 | DeleteConfirm | *Cancel* / scrim / back | ScriptList · populated | 01 | focus returns to the `···` |
| 11 | ScriptEditor · new | back / *Scripts*, nothing typed | ScriptList · unchanged | 01 | **no record created** — B2 |
| 12 | ScriptEditor · new | first character committed | ScriptEditor · editing | 01 | record created on the 2 s debounce |
| 13 | ScriptEditor · editing | back / *Scripts* | ScriptList · populated | 01 | saved, no confirmation |
| 14 | ScriptEditor · editing | process death | ScriptEditor · interrupted | 01 | body, scroll, cursor, selection, keyboard restored |
| 15 | ScriptEditor · any | *Record* | — | 02 | RESERVED in 01 |
| 16 | ScriptStart | any of three routes | — | 06 | |
| 17 | ScriptStart | scrim / swipe / back | ScriptList · populated | 06 | nothing created |

Rows 4 and 5 are the pair that has to exist separately. A builder reading one row
would ship the wrong one and nobody would find out until the sweep.

---

### B4. Accessibility — ScriptList

**Targets.** Row is full-width at `row.script` (88 dp) minimum. The `···` is a 48 dp
square target *carved out of* the row's target, not overlapping it — two nested
targets is a real defect and it must be specified as an exclusion. FAB is 56 dp
visual and 56 dp target.

**Focus order.** Screen title → row 1 → row 1 `···` → row 2 → row 2 `···` → … → FAB.
Title and metadata read as one node per row, not three.

**FAB last, not first.** This screen's purpose is finding an existing script; creation
is the less frequent action per session. In the empty state the FAB is redundant with
the body buttons and is skipped.

**Content descriptions.**
- Row: "[full untruncated title], [relative date], about [read time] to read aloud."
  Untruncated, matching the `DeleteConfirm` precedent — the visual clamp is a sighted
  affordance and must not become a screen reader limitation.
- `···`: "More options for [title]."
- FAB: "New script." `[NEW — APPROVED 2026-08-04]`

**Empty state.** Focus lands on the body copy, then the action buttons. In slice 01
there is one button, in slice 06 there are two — **the focus order changes between
slices** and DEFERRED.md must say so, or the sweep reads a correct build as a defect.

**At 200%.** Rows grow; they do not clamp. The two-line title clamp is a *line* clamp,
not a height clamp — at 200% the row is taller and still two lines. The metadata line
wraps rather than truncating; numerals never truncate. The FAB does not scale with
font size.

**Colour alone.** Nothing. Date and read time are text.

**Contrast. MEASURE and record:** title `onSurface` on `surface`; metadata
`onSurfaceVariant` on `surface`; the `···` mark ≥3:1; the FAB's `+` on `primary`.

---

### B5. Accessibility — ScriptStart

**Targets.** Routes are full-width at `row.action` (72 dp). The drag handle is
decorative and is not a target — dismissal is by swipe anywhere on the sheet, scrim
tap, or back.

**The no-heading ruling creates an accessibility gap, and this closes it.** With no
visible title, TalkBack has nothing to announce when the sheet opens. Give the sheet a
pane title that is spoken and not shown:

> **"Start a script"** — `[NEW — APPROVED 2026-08-04]`, accessible name only. It must
> not render.

The visual reasoning for no heading stands; it was only ever an argument about pixels.

**Focus.** Enters the sheet on open and lands on *Generate it*. Trapped while open.
Returns to the `+` on every dismiss path.

**Content descriptions.** The three labels are self-describing and take none. State
that explicitly so no one invents them. Drag handle is marked not-important for
accessibility.

**At 200%.** Rows grow, labels wrap. The sheet grows from its ~300 dp content wrap,
capped at the window minus `space.6`; past that the route list becomes the scrolling
region.

**Colour alone.** Nothing — no route is preselected, and equal weight means no state
is carried by colour in the first place.

**Reduced motion.** Inherits the systemic rule.

---

### B6. Empty state copy · slice-01 variant

The current string promises generation, which does not exist in slice 01. This is the
first screen a new user ever sees, which makes it the worst possible place for a
promise the app can't keep.

**Slice 01:**
> "No scripts yet. Write your first one."
> `[NEW — APPROVED 2026-08-04]` · one button: *Write your own*

**Slice 06:** the existing two-button state and its full string, restored.

Register both in `DEFERRED.md` as a copy pair, so the sweep checks the string against
the slice rather than against the design.

**Rejected:** rendering *Generate a script* disabled. A greyed-out primary action on
first run reads as a broken app, and RESERVED (#3) is for controls in an established
layout, not for a first-run empty state.

**Confirm rather than rule:** if *Generate a script* and *Write your own* are not
verbatim F03, tag both `[NEW]` and approve them as-is — they read correctly either
way, but they should not reach the build untagged if they were invented.

---

## Part 3 — Smaller items

**ScriptEditor has no delete — out of scope, stated.** Delete belongs where the list
of things is. Duplicating it into the editor puts a destructive action on the one
screen whose entire purpose is uninterrupted writing. Revisit only if the editor
gains an overflow menu for some other reason.

**Predictive back is app-wide, not per screen.** At `targetSdk 36` the predictive back
system animations are on by default and `onBackPressed()` is no longer called —
`OnBackInvokedCallback` is mandatory. This belongs in `design-system.md` as a build
constraint, not in ScriptStart's spec where it currently sits alone. Every dismissable
surface inherits it.

---

## Apply

**`design-system.md`** — `scrim` + `scrim.modal`; `outline` raised with both ratios
recorded, `outlineVariant` demoted to decorative; STATE · RESERVED; `row.script`,
`row.action`, `fab.standard`; per-role leading; icon set tokens; the icon-only rule;
the sole-identifier outline rule; predictive back as a build constraint. Commit as a
design system change.

**`design/DEFERRED.md`** — the empty-state copy pair; the empty-state focus order
change at slice 06; Record's RESERVED → enabled transition at slice 02; ScriptStart's
activation at slice 06.

**Back to Claude Design** — B1 (the menu, as a twelfth ScriptList state), B4 and B5
(the two accessibility sections), B6 (the slice-01 empty state), and the redraw needed
by the raised `outline` and by Record's RESERVED treatment.

**Then** the handoff, with the B3 table in it.
