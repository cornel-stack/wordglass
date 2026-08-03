# Slice 01 — Screen inventory (approved)

Step 2 of the build loop. Scope in `specs/slice-01/spec.md`. Sources read: `docs/design-system.md`
(v1, §11 now includes `TextField` and an optional `Empty state` secondary action),
`docs/build-plan.md` (slice 01), `docs/user-flows.pdf` (F03).

**Resolution of the forward-reference conflict:** design the full F03 shape, build only what
has a live destination. Designed-but-not-built elements are registered in `design/DEFERRED.md`
and marked `OUT OF SCOPE` in their screen prompt.

## Screens

| Screen | States | Over camera | Flows | Built in 01 |
|---|---|---|---|---|
| `ScriptList` | empty · populated (40) · overflowing (74-char title) | no | F03 | yes — empty state shows both buttons in the handoff; only *write your own* is rendered |
| `ScriptStart` | default (three routes, none preselected) | no | F03 · F01 | **designed only** — activates slice 06. In 01, `+` opens a blank `ScriptEditor` directly |
| `ScriptEditor` | new · editing · autosaved · interrupted | no | F03 | yes — *Record* button designed but not rendered (activates slice 02) |
| `DeleteConfirm` | default (destructive) | no | F03 · build-plan | yes — all copy is NEW; F03 specifies no script-delete strings |

No screen sits over the camera. The over-camera token set (§5) is untouched until slice 02.

## Deliberately excluded (no prompt written)

| Excluded | Why |
|---|---|
| `GenerateInput`, `VariantPicker` | Slice 06 (F04) |
| `ImportPreview` + SAF picker | Slice 06 / 15 (F05); SAF is OS-owned, never designed |
| `Capture` and everything after *Record* | Slice 02 (F06) |
| `TakeList`, `TakePlayer` | Slice 03 (F08) |
| Share-to-Wordglass prefilled editor entry | Later slice — share-target intent |
| Component gallery | Debug-only dev harness, not a user-facing designed screen. Its content is the §11 components, covered by component work |

## New components — vs design-system §11

| Component | Decision |
|---|---|
| `TextField` (default, focused, filled, error, disabled) | **Added to §11.** Used for the `ScriptEditor` title and future search/org fields. **Not** used for the editor body |
| `Empty state` secondary action | **Extended in §11** to an optional lower-emphasis second action, for the F03 two-button empty state |
| `ScriptEditor` body writing surface | **Not a component.** A bespoke full-bleed writing surface — no border, no label, body text on `surface`. Documented in `ScriptEditor.md`, not promoted to §11 |

Everything else reuses existing §11 components. No other new components.

## Strings that are NOT verbatim from the flows

Tagged `[NEW]` in their prompts. Five resolved and now `[NEW · APPROVED 2026-08-04]`; one
still pending. The tag is permanent provenance — approval is appended, never removed (see the
`[NEW]` rule in `CLAUDE.md`).

**Approved 2026-08-04:**
- `ScriptList` empty-state button labels — "Generate a script" (primary, first, wider) /
  "Write your own" (secondary). The empty-state *message* stays verbatim F03.
- Read-time min+sec format — "≈ 47 sec" under a minute, "≈ 2 min 14 sec" at/above sixty.
- Word count — `ScriptEditor` only, one line below the body: "1,247 words · ≈ 8 min 54 sec".
  Not shown in `ScriptList` rows.
- Autosave indicator — none, deliberately.
- `DeleteConfirm` — all copy, plus row-overflow delete trigger and confirm-not-undo.

**Still pending `[NEW]` (not among the resolved five):**
- `ScriptList` screen title "Scripts". Remains `[NEW]` unapproved; must not reach the build
  until approved, per the `CLAUDE.md` rule.
