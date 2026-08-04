# Screen: ScriptStart    Slice: 01 (designed) / 06 (built)    Platform: Android / Compose

**Reconciled to `specs/slice-01/slice-01-rulings.md` (2026-08-04).** Rulings win on conflict.
The pane title, `row.action`, and accessibility (B5) come from that document.

DESIGNED AHEAD OF BUILD. Designed now so the full F03/F01 shape exists; NOT wired in slice 01 —
only "Write it" has a live destination, so the + on ScriptList opens a blank ScriptEditor
directly (B3 rows 4/5). Activates slice 06 when "Generate it" and "Paste or import" gain
destinations. A route-chooser sheet appearing from + in slice 01 is a defect (design/DEFERRED.md).

PURPOSE
  Let the user choose how to start a new script, without preselecting a path — the blank page
  is the real obstacle, and each route removes it differently.

ENTRY / EXIT
  Arrives from: "+" on ScriptList · onboarding "Make your first video" (F01, slice 20)
  Leaves to:
    - "Write it" → ScriptEditor, blank (the only live route in slice 01)
    - "Generate it" → GenerateInput (slice 06)
    - "Paste or import" → ImportPreview / SAF picker (slice 06 / 15)
  Back behaviour: dismiss, return to ScriptList; nothing created. Predictive back applies (§15).

STATES TO DESIGN
  default   Three routes offered, no default preselected [verbatim F03/F01: "No default is
            preselected"]. Equal visual weight across the three.

CONTENT INVENTORY
  Three routes, labels [verbatim F03/F01]: "Write it" · "Generate it" · "Paste or import".
  Pane title: "Start a script" [NEW · APPROVED 2026-08-04] — an accessible name ONLY, spoken
    when the sheet opens, and it MUST NOT render (B5). The no-visible-heading decision stands (a
    pixels argument); the pane title closes the accessibility gap that decision created.
  Longest realistic value: labels are fixed and short — no overflow case. Stated, not omitted.

CONTROLS
  Write it          → ScriptEditor blank. Live in slice 01.
  Generate it       → GenerateInput. Deferred to slice 06.
  Paste or import   → ImportPreview / SAF. Deferred to slice 06 / 15.
  None destructive. No confirmation.

CONSTRAINTS
  Presented as a Bottom sheet over `scrim.modal` (#1 — scrim #000000 at 0.60, full window incl.
  both system bar regions, matching DeleteConfirm's scrim exactly). Routes full-width at
  `row.action` (72dp — #4/B5). The drag handle is decorative and is NOT a target. One-handed:
  yes. Surface set (§4). Dismiss: swipe anywhere on the sheet · scrim tap · back / predictive
  back.

ACCESSIBILITY (B5)
  Pane title: "Start a script" — accessible name only, not rendered.
  Focus: enters the sheet on open, lands on "Generate it", trapped while open; returns to the +
    on every dismiss path.
  Content descriptions: the three labels are self-describing and take NONE — stated so none are
    invented. The drag handle is marked not-important-for-accessibility.
  Targets: routes full-width `row.action` (72dp); the drag handle is not a target.
  At 200%: rows grow, labels wrap; the sheet grows from its ~300dp content wrap, capped at the
    window minus space.6, past which the route list becomes the scrolling region.
  Colour alone: nothing — no route preselected, equal weight, no state carried by colour.
  Reduced motion: inherits the systemic rule (§9).

REUSE
  Existing components (§11): Bottom sheet with `scrim.modal`; routes as `row.action` rows (List
    row or a stack of buttons — reuse one, do not invent a bespoke "route card").
  New components this justifies: none.
  Note: F01 onboarding may present this content full-screen (slice 20); design the sheet form
  here and flag the variant.

TOKENS
  None missing. `scrim` / `scrim.modal` (§4), `row.action` (§10).

OUT OF SCOPE
  The destinations behind "Generate it" / "Paste or import" (slices 06 / 15) · the full-screen
  onboarding variant (slice 20) · a possible fourth "Speak it" route (deferred candidate,
  build-plan slice 06 — the four-row sheet is designed there, not here) · any preselection or
  "recently used" route.
