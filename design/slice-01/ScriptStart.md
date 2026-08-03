# Screen: ScriptStart    Slice: 01 (designed) / 06 (built)    Platform: Android / Compose

DESIGNED AHEAD OF BUILD. This surface is designed now so the full F03/F01 shape exists, but
it is NOT wired in slice 01 — only the "Write it" route has a live destination, so the "+" on
ScriptList opens a blank ScriptEditor directly. ScriptStart activates in slice 06 when
"Generate it" and "Paste or import" gain destinations. Registered in design/DEFERRED.md.

PURPOSE
  Let the user choose how to start a new script, without preselecting a path — because the
  blank page is the real obstacle, and each route removes it differently.

ENTRY / EXIT
  Arrives from: "+" on ScriptList · onboarding "Make your first video" (F01, slice 20)
  Leaves to:
    - "Write it" → ScriptEditor, blank (the only live route in slice 01)
    - "Generate it" → GenerateInput (slice 06)
    - "Paste or import" → ImportPreview / SAF picker (slice 06 / 15)
  Back behaviour: dismiss, return to ScriptList. Nothing created, nothing to save.

STATES TO DESIGN
  default   Three routes offered, no default preselected [verbatim F03/F01: "No default is
            preselected"]. Equal visual weight across the three.

CONTENT INVENTORY
  Three routes, labels [verbatim F03/F01]:
    - "Write it"
    - "Generate it"
    - "Paste or import"
  No heading string is specified by the flows. If the surface needs a title, propose one and
  mark it [NEW]; do not borrow copy from elsewhere.
  Longest realistic value: labels are fixed and short; no overflow case. State this rather
  than omitting it.

CONTROLS
  Write it          → ScriptEditor blank. Live in slice 01.
  Generate it       → GenerateInput. Deferred to slice 06.
  Paste or import   → ImportPreview / SAF. Deferred to slice 06 / 15.
  None destructive. No confirmation.

CONSTRAINTS
  Thumb-reach: presented as a Bottom sheet rising from the bottom — all three routes fall in
  the lower half. One-handed: yes.
  Glanceable: user is looking at the screen. Standard contrast.
  Over live camera: no. Surface token set (§4).
  Longest-running operation: none.

REUSE
  Existing components (design-system §11): Bottom sheet (the surface) · List row or a stack of
  buttons for the three routes — designer's call, but reuse one of the two, do not invent a
  bespoke "route card".
  New components this justifies: none.
  Note: F01 presents this same content during onboarding, where it may be full-screen rather
  than a sheet. That variant is a slice-20 decision; design the sheet form here and flag the
  onboarding variant rather than designing both now.

TOKENS
  None missing.

OUT OF SCOPE
  The destinations behind "Generate it" and "Paste or import" (slices 06 / 15) · the
  full-screen onboarding variant (slice 20) · any preselection or "recently used" route.
