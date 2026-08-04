# Screen: ScriptEditor    Slice: 01    Platform: Android / Compose

**Reconciled to `specs/slice-01/slice-01-rulings.md` (2026-08-04).** Rulings win on conflict.
Record's `STATE · RESERVED` treatment (#3), the blank-editor / no-record-until-typed rule (B2),
the two-value body length (#9), and "no delete on this screen" (Part 3) come from that document.

PURPOSE
  Write or edit a script and see, live, how long it takes to read aloud. The one place in the
  app where reading is authored; not where reading happens (that is the prompter, slice 02).

ENTRY / EXIT
  Arrives from: ScriptList "+" (blank) · ScriptList row tap (existing; cursor + scroll restored,
    B3 row 3) · ScriptStart "Write it" (slice 06) · PermissionSettings secondary return (F02,
    slice 02) · Share-to-Wordglass text intent, prefilled (later slice)
  Leaves to:
    - back / the "Scripts" affordance → ScriptList. Always safe: autosaved. NO "discard changes?"
      dialog (UX invariant). Predictive back applies app-wide (§15).
    - Record → RESERVED in slice 01: it renders but does not navigate. It activates and leads to
      Capture in slice 02 (#3, B3 row 15).
  Back behaviour:
    - From a NEVER-TYPED editor: no script was created — the record is created on the first
      character committed, on the same 2s debounce as autosave (B2). Back returns to ScriptList
      with nothing added: no row, no dialog, no toast (B3 row 11).
    - From an edited editor: saved, returns to ScriptList, no confirmation (B3 row 13).
    - An editor emptied to zero characters KEEPS its script — deleting content is not deleting a
      script (B2). Its list row then shows "Untitled" / "0 words · ≈ 0 sec".

STATES TO DESIGN
  new          Empty editor, keyboard up, cursor in the body [F03]. Title placeholder; body
               empty; the count line reads "0 words · ≈ 0 sec". No record exists yet (B2).
  editing      Typing. Read-time updates on pause. Title derives from the first line until
               edited directly. The record is created on the first character committed (2s
               debounce — B2, B3 row 12).
  autosaved    The autosave moment (2s idle or background). Deliberately invisible — no
               indicator (see CONTENT · Autosave); rendered identically to editing; no layout
               change.
  interrupted  Process death mid-edit. On relaunch the editor is restored exactly — body,
               scroll position, cursor, selection, and keyboard (B3 row 14). This is the
               slice-01 "done when". No "recovered" banner (none specified).

CONTENT INVENTORY
  Title:
    - Auto-derived from the first line, editable [F03]; stops tracking once edited directly.
    - Longest 74 chars, single line, ellipsis.
    - Uses the TextField component (§11) in its title role. In the NEW state the field is empty
      with no placeholder, so its OUTLINE is the sole thing identifying it as editable — it uses
      `outline` (load-bearing, ≥3:1), never `outlineVariant` (§4 sole-identifier rule, #2).
      Focused swaps the stroke to `primary`.
  Body:
    - Full-bleed writing surface. NOT a TextField — no border, no label, no fill. bodyLarge (IBM
      Plex Sans) on `surface`, 16dp horizontal padding, 1.5× leading (§3 body role).
    - Length is two values (#9): tuned for `content.body.tuned` (~420 words, a 3-min script at
      140 wpm) — leading, measure and scroll are tuned there; must NOT break at
      `content.body.max` (4,012 words), the stress case for the count numerals and ScriptList's
      "≈ 28 min 40 sec". NO length cap in slice 01; nothing truncates. A cap, if ever, belongs
      to generation (slice 06), not the editor.
  Read-time / Word count:
    - [NEW · APPROVED 2026-08-04] one line below the body: "1,247 words · ≈ 8 min 54 sec" — the
      count then the read-time, " · " joined. labelMedium / onSurfaceVariant; the numeric runs
      in numericMedium (Plex Mono, tabular) so they do not jitter on pause. 140 wpm, updates on
      pause not per keystroke [F03]. Comma thousands separator. Empty: "0 words · ≈ 0 sec".
  Autosave:
    - No save button [F03]. No indicator [NEW · APPROVED 2026-08-04] — the absent button is the
      message; a "Saved" flash every two seconds trains anxiety about something that never
      fails. A failed write is a different case and belongs to slice 13 sync, not local autosave.

CONTROLS
  Title field    TextField, editable. Not destructive.
  Body surface   Editable text. Not destructive.
  Record         RESERVED in slice 01 (#3, `STATE · RESERVED`): rendered in its permanent
                 position — container surfaceContainerLow, NO fill, NO outline; label and record
                 dot in onSurfaceVariant, never the record red (that stays reserved for the live
                 control with shape.full, slice 02); `enabled = false`; kept in focus order and
                 announced, never skipped; its description states it arrives in slice 02. The
                 missing outline is what says "not a button yet". It must not shift the rest of
                 the layout when it activates in slice 02.
  (no save)      Explicitly absent [F03].
  (no delete)    Delete is NOT on this screen (Part 3) — it belongs on ScriptList, where the list
                 of things is. Putting a destructive action on the one screen whose purpose is
                 uninterrupted writing is wrong. Out of scope, stated.

CONSTRAINTS
  Thumb-reach: the keyboard owns the bottom; the editor is a typing surface, so thumb-reach is
  secondary. Record holds a fixed position (RESERVED now, live in 02).
  One-handed: no — writing is two-handed. Do not optimise for one hand at the cost of the
  writing surface.
  Glanceable: user is looking at the screen. Standard contrast, surface set (§4).
  Over live camera: no. Predictive back app-wide (§15).
  Longest-running operation: none. Autosave is local and instant — never show a spinner for it.

REUSE
  Existing components (§11): TextField (title only) · numericMedium (count line). Record uses
    `STATE · RESERVED` (§14).
  New components this justifies: none. The body writing surface is deliberately bespoke and is
    documented here, not promoted to §11 — do not turn it into a reusable component or wrap it
    in a TextField.

TOKENS
  None missing. Body bodyLarge on `surface` (1.5× leading). Title TextField with `outline` (not
  `outlineVariant`) in the new state. Count line labelMedium / onSurfaceVariant, numerals
  numericMedium. Record: surfaceContainerLow, onSurfaceVariant, no outline (§14).
  `content.body.tuned` / `content.body.max` are content references (#9), not visual tokens.

OUT OF SCOPE
  Record wiring and Capture (slice 02) · paragraph regenerate / inline rewrite (slice 06, F04) ·
  captions, formatting / rich-text toolbar (not specified — do not invent) · version history
  (slice 15) · delete (belongs to ScriptList) · the prefilled share-intent entry (later slice).
