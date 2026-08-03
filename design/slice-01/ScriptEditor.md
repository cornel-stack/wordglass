# Screen: ScriptEditor    Slice: 01    Platform: Android / Compose

PURPOSE
  Write or edit a script and see, live, how long it takes to read aloud. The one place in the
  app where reading is authored; not where reading happens (that is the prompter, slice 02).

ENTRY / EXIT
  Arrives from: ScriptList "+" (blank) · ScriptList row tap (existing script) · ScriptStart
    "Write it" (slice 06) · PermissionSettings secondary return (F02, slice 02) · Share-to-
    Wordglass text intent, prefilled (later slice)
  Leaves to:
    - system back → ScriptList. Always safe: the script is already autosaved. There is NO
      "discard changes?" dialog (CLAUDE.md UX invariant).
    - Record → Capture (slice 02). DESIGNED as the editor's primary action, NOT RENDERED in
      slice 01 (design/DEFERRED.md).
  Back behaviour: back persists (autosave already ran) and returns to ScriptList. Mid-typing
    back is safe. No confirmation, ever.

STATES TO DESIGN
  new          Empty editor, keyboard up, cursor in the body [F03]. Title area shows its
               placeholder; body is empty; read-time and word count show their zero form.
  editing      User is typing. Read-time updates on pause (see CONTENT). Title derives from
               the first line until the user edits it directly.
  autosaved    The autosave moment after a 2s idle or on background. Deliberately invisible —
               no indicator, no flash (see CONTENT · Autosave). It is a behavioural state (the
               write happened), rendered identically to editing. Nothing about layout changes.
  interrupted  App backgrounded or killed mid-edit. On return / relaunch the script is intact
               with its content and read-time (this is the slice-01 "done when": write, kill,
               reopen, it is there). Design what the user sees on return: the editor restored
               exactly, no "recovered" banner unless one is specified — it is not, so do not
               add one.

CONTENT INVENTORY
  Title:
    - Auto-derived from the first line of the body, and editable [F03]. Once the user edits
      the title directly, it stops tracking the first line.
    - Longest realistic: 74 characters. Single line, ellipsis on overflow.
    - Uses the TextField component (design-system §11) in its title role — single line.
  Body:
    - A full-bleed writing surface. NOT a TextField — no border, no label, no fill. Body text
      (bodyLarge, IBM Plex Sans) directly on `surface`, 16dp horizontal padding.
    - Longest realistic: a three-minute script (~420 words at 140 wpm) — must scroll cleanly
      under the keyboard.
  Read-time:
    - [verbatim F03 format] "≈ 47 sec" under a minute; [NEW · APPROVED 2026-08-04]
      "≈ 2 min 14 sec" at sixty seconds and above (F03's own min+sec convention). Computed at
      140 wpm. Updates on pause, NOT per keystroke [F03: "updating on pause not per keystroke"].
    - It is not a separate element: read-time is the second half of the single word-count line
      below the body (see Word count). In the new/empty state that line reads "0 words ≈ 0 sec".
  Word count:
    - [NEW · APPROVED 2026-08-04] one line below the editor body: "1,247 words · ≈ 8 min 54 sec"
      — the count, then the read-time, joined by " · ". Line style labelMedium, onSurfaceVariant.
      The numeric runs — the count ("1,247") and the read-time ("≈ 8 min 54 sec") — use tabular
      figures (numericMedium, Plex Mono) so they do not jitter as they update on pause; the
      words "words" and the "·" are labelMedium. Comma thousands separator. Empty: "0 words · ≈
      0 sec". This one line carries both build-plan treatments (word count and read-time).
  Autosave:
    - No save button exists [F03: "No save button exists"]. Do not design one.
    - No autosave indicator [NEW · APPROVED 2026-08-04]. F03 specifies autosave and the absence
      of a save button, and says nothing about an indicator. The absent save button is the
      message; a "Saved" flash every two seconds trains anxiety about something that never
      fails. Show nothing. (A failed write is a different case and belongs to slice 13 sync —
      local autosave here is effectively instant and does not fail.)

CONTROLS
  Title field    Editable text. Not destructive.
  Body surface   Editable text. Not destructive.
  Record         Primary action [F03]. DESIGNED, NOT RENDERED in slice 01 (→ Capture, slice
                 02). When it lands it must not shift the rest of the layout in doing so.
  (no save)      Explicitly absent.

CONSTRAINTS
  Thumb-reach: the keyboard owns the bottom; the editor is a typing surface, so thumb-reach is
  secondary here. When Record renders (slice 02) it takes a fixed, reachable position.
  One-handed: no — writing is two-handed. Do not optimise for one hand at the cost of the
  writing surface.
  Glanceable: user is looking at the screen. Standard contrast, surface token set (§4).
  Over live camera: no.
  Longest-running operation: none. Autosave is local and instant — never show a spinner for
  it.

REUSE
  Existing components (design-system §11): TextField (title only) · numericMedium type token
    (read-time, word count).
  New components this justifies: none. The body writing surface is deliberately bespoke and is
    documented here rather than promoted to §11 — do not turn it into a reusable component or
    wrap it in a TextField.

TOKENS
  None missing. Body: bodyLarge on `surface`. Title: TextField. Word-count line: labelMedium /
  onSurfaceVariant, with its numeric runs in numericMedium (Plex Mono, tabular). No autosave
  indicator (see CONTENT · Autosave).

OUT OF SCOPE
  Record wiring and everything after it (slice 02) · paragraph regenerate and inline rewrite
  (slice 06, F04) · captions, formatting/rich-text toolbar (not specified — do not invent) ·
  version history (slice 15) · the prefilled share-intent entry (later slice).
