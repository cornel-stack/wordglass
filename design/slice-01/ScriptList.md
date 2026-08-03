# Screen: ScriptList    Slice: 01    Platform: Android / Compose

PURPOSE
  Find, open, create, and delete the user's scripts. The home surface for a signed-out solo
  creator.

ENTRY / EXIT
  Arrives from: cold launch (the app's home for a signed-out user) · system back from
    ScriptEditor · onboarding "Make your first video" (F01, slice 20 — not built here)
  Leaves to:
    - "+" → ScriptEditor, blank (in slice 01 this is direct; the ScriptStart chooser is
      designed but not wired until slice 06 — see design/DEFERRED.md)
    - tap a row → ScriptEditor for that script
    - delete affordance → DeleteConfirm
  Back behaviour: system back from ScriptList exits the app — this is the root. No dialog.

STATES TO DESIGN
  empty        First run, no scripts. Icon, the F03 message, and the two-button call to
               action (generation-first). See CONTENT.
  populated    40 scripts. Each row: title, read-time, word count. Newest first.
  overflowing  A 74-character title in a row; a script whose read-time is minutes not
               seconds (≈ 12 min); the 40-item list scrolled.

CONTENT INVENTORY
  Screen title: [NEW] "Scripts"
  Row, per script:
    - Title — auto-derived from the script's first line, or the user's edited title.
      Longest realistic: 74 characters. Must truncate with ellipsis on one line, never wrap.
    - Read-time — [verbatim F03 format] "≈ 47 sec". At 140 wpm. Minutes form [NEW] "≈ 12 min"
      for longer scripts. Uses numericMedium (IBM Plex Mono, tabular).
    - Word count — [NEW] treatment required by build-plan; F03 gives no string. Proposed
      "128 words". Uses numericMedium. Flag for review.
    - No last-edited timestamp is specified by the flows; do not invent one. If the row looks
      thin without it, raise it rather than adding it.
  Empty state:
    - Message [verbatim F03]: "No scripts yet. Generate one in about thirty seconds, or write
      your own."
    - Two buttons, generation first [F03: "Two buttons, generation first"]:
        1. Primary [NEW label] "Generate one" — DESIGNED, NOT RENDERED in slice 01
           (design/DEFERRED.md; activates slice 06). Present in this handoff so the layout is
           correct when it lands.
        2. Secondary [NEW label] "Write your own" — rendered, → ScriptEditor blank.
      Button labels are NEW (the message is verbatim; F03 does not label the buttons).
      Uses the Empty state component with its optional secondary action (design-system §11).

CONTROLS
  + (create)        Icon button. Enabled always. → ScriptEditor blank. Not destructive.
  Row tap           Opens that script. Not destructive.
  Delete affordance [NEW trigger — F03 does not specify]. Proposed: row long-press, or a
                    trailing overflow action on the row. Destructive: yes. Confirmation
                    required: yes → DeleteConfirm (scripts confirm; they do not use the
                    take-style undo — that is slice 03). Flag the trigger choice for review.
  Empty primary     "Generate one" — deferred (see CONTENT).
  Empty secondary   "Write your own" — → ScriptEditor blank.

CONSTRAINTS
  Thumb-reach: "+" must sit in the bottom third — it is the most-used control here.
  One-handed: yes.
  Glanceable: user is looking at the screen, not a lens. Standard contrast.
  Over live camera: no. Surface token set (§4), not the over-camera set.
  Longest-running operation: none. All reads are local and instant — no spinner, no loading
  state (design-system state matrix: local reads skip the loading state).

REUSE
  Existing components (design-system §11): List row (script rows) · Icon button ("+") ·
    Empty state with optional secondary action (empty screen) · Dialog (via DeleteConfirm,
    its own prompt).
  New components this justifies: none.

TOKENS
  None missing. Read-time and word count use the existing numericMedium type token (Plex
  Mono, tabular). If a review adds a last-edited timestamp, it also uses numericMedium — but
  do not add it without a decision.

OUT OF SCOPE
  Search, folders, and version history (slice 15) · sync / cloud status (slice 13) · the
  ScriptStart route chooser (designed separately; + goes straight to the editor in 01) · the
  rendered "Generate one" button (deferred to slice 06, design/DEFERRED.md) · multi-select or
  bulk actions.
