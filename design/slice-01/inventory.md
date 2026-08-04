# Slice 01 — Screen inventory (approved)

Step 2 of the build loop. Scope in `specs/slice-01/spec.md`; rulings in
`specs/slice-01/slice-01-rulings.md` (authoritative on any conflict). Sources: `docs/design-
system.md`, `docs/build-plan.md` (slice 01), `docs/user-flows.pdf` (F03).

**Reconciled to the rulings (2026-08-04):** the overflow menu is `ScriptList`'s twelfth state
(B1); the blank-editor / "Untitled" rule (B2); the state-transition table (B3) lives in the
handoff; accessibility for `ScriptList` (B4) and `ScriptStart` (B5); the slice-01 one-button
empty state (B6); Record renders in `STATE · RESERVED` (#3); "Scripts" title / exit label
approved (#7 / #8).

**Forward-reference resolution:** design the full F03 shape, build only what has a live
destination. Designed-but-not-built elements are registered in `design/DEFERRED.md`
(sweep-facing) and marked `OUT OF SCOPE` in their screen prompt.

## Screens

| Screen | States | Over camera | Flows | Built in 01 |
|---|---|---|---|---|
| `ScriptList` | empty (one button) · populated (40) · overflowing (74-char) · untitled row · overflow menu | no | F03 | yes |
| `ScriptStart` | default (three routes, none preselected) | no | F03 · F01 | designed only — activates slice 06; `+` opens a blank `ScriptEditor` in 01 |
| `ScriptEditor` | new · editing · autosaved · interrupted | no | F03 | yes — Record rendered in `STATE · RESERVED`, activates slice 02 |
| `DeleteConfirm` | default (destructive) | no | F03 · build-plan | yes |

No screen sits over the camera. The over-camera token set (§5) is untouched until slice 02.

## Deliberately excluded (no prompt written)

| Excluded | Why |
|---|---|
| `GenerateInput`, `VariantPicker` | Slice 06 (F04) |
| `ImportPreview` + SAF picker | Slice 06 / 15 (F05); SAF is OS-owned, never designed |
| `Capture` and everything after *Record* | Slice 02 (F06) |
| `TakeList`, `TakePlayer` | Slice 03 (F08) |
| Share-to-Wordglass prefilled editor entry | Later slice — share-target intent |
| Component gallery | Debug-only dev harness, not a user-facing designed screen. Its content is the §11 components |

## New components / primitives — vs design-system §11

| Component | Decision |
|---|---|
| `TextField` (default, focused, filled, error, disabled) | **Added to §11.** `ScriptEditor` title and future search/org fields. **Not** the editor body. Its new-state outline is load-bearing — `outline`, not `outlineVariant` (#2) |
| `Empty state` secondary action | **Extended in §11.** Used by the slice-06 two-button empty state. Slice 01 uses ONE button, no secondary action (B6) |
| `ScriptEditor` body writing surface | **Not a component.** Bespoke full-bleed writing surface — no border, no label, on `surface`. Documented in `ScriptEditor.md` |
| FAB | M3 `FloatingActionButton` at `fab.standard` + a Material Symbols glyph — an M3 primitive (§2), not a new §11 component |
| Overflow menu | M3 `DropdownMenu` built to the B1 spec — not a new component |

Everything else reuses existing §11 components.

## Strings — [NEW], all APPROVED 2026-08-04

The rulings approved the last open item ("Scripts"), so **nothing is pending.** The `[NEW]` tag
stays as provenance; `· APPROVED <date>` marks clearance (CLAUDE.md rule).

- `ScriptList`: title / exit label "Scripts" (#7 / #8); empty state "No scripts yet. Write your
  first one." + "Write your own" (B6); FAB "New script", ⋯ "More options for [title]", menu
  "Delete" (B1 / B4); "Untitled" row (B2); read-time min+sec format; word count now shown in
  rows (B2 reversal of the earlier "no word count in rows" resolution).
- `ScriptStart`: pane title "Start a script" — accessible-name only, not rendered (B5).
- `ScriptEditor`: the word-count line; no autosave indicator (deliberate).
- `DeleteConfirm`: all copy.
- The slice-06 copy pair (two-button generation empty state, "Generate a script") stays in
  `design/DEFERRED.md`, checked against the slice, not the design.
