# Screen: DeleteConfirm    Slice: 01    Platform: Android / Compose

**Reconciled to `specs/slice-01/slice-01-rulings.md` (2026-08-04).** Rulings win on conflict.
The modal scrim (#1) and the focus-return behaviour (B1 / B3) come from that document.

ALL COPY ON THIS SCREEN IS NEW AND APPROVED (2026-08-04). F03 covers writing a script, not
deleting one, and F08's delete flow is for takes (which use undo, not a dialog). build-plan
slice 01 specifies a "Delete confirmation" for scripts. The strings below carry
[NEW · APPROVED 2026-08-04].

PURPOSE
  Confirm deleting a script before it is gone. Scripts use a confirmation dialog, not the
  take-style undo — a deliberate difference [APPROVED 2026-08-04]: a take is an unreproducible
  ~90-second performance, so it gets undo; a script is text that syncs, so it gets a confirm.

ENTRY / EXIT
  Arrives from: the overflow menu's Delete item on a ScriptList row (B1).
  Leaves to:
    - Delete → script deleted; return to ScriptList (populated or empty). Focus goes to the row
      that took the deleted row's position, or the empty state's first action (B3 row 9).
    - Cancel / scrim tap / back → dismiss, nothing deleted; focus returns to the ⋯ that opened
      the menu (B3 row 10).
  Back behaviour: back / predictive back dismisses (equivalent to Cancel); the script is
    untouched. Predictive back applies app-wide (§15).

STATES TO DESIGN
  default   The destructive confirmation. See CONTENT.

CONTENT INVENTORY
  All [NEW · APPROVED 2026-08-04]:
    - Title: "Delete this script?"
    - Body: "[title]" will be removed from all your devices. This can't be undone.
      ([title] is the script's own title, echoed into the body between the quote marks.)
    - Primary button: "Delete" — destructive, error token.
    - Secondary button: "Cancel"
  Overflow case: the echoed [title] IS this screen's overflow case. Truncate the title at ~40
  characters with an ellipsis inside the body sentence (e.g. "My listing walkthrough script for
  the downtown…"). The surrounding sentence is fixed; only the title truncates.

CONTROLS
  Delete   Destructive: yes. This IS the confirmation, so no further confirmation. error token.
  Cancel   Not destructive. Dismisses.

CONSTRAINTS
  Modal dialog over `scrim.modal` (#1): `scrim` #000000 at 0.60 over the full window including
  both system bar regions — not the `oc.*` set. The dialog sits on surfaceContainerHighest at
  elevation.3 (§8). Standard contrast. One-handed: yes. Not over camera. Deletion is local and
  instant.
  Build note (#1): Compose's Dialog dims via the platform window; to hit exactly 0.60 across the
  system bars, draw it as a full-window overlay or `DialogProperties(decorFitsSystemWindows =
  false)`. It must match the ScriptStart sheet's scrim exactly — a visible difference on device
  is a sweep defect.

ACCESSIBILITY
  Focus: on open, into the dialog; trapped while open. On Cancel / dismiss, returns to the ⋯
    that opened the menu. On Delete, to the row that took the deleted row's position, or the
    empty-state first action (B3 rows 9 / 10).
  Delete is destructive and is itself the confirmation — no further step, no undo (scripts, not
    takes).

REUSE
  Existing components (§11): Dialog, destructive variant, with `scrim.modal`.
  New components this justifies: none.

TOKENS
  None missing. `error` / `onError` (destructive); surfaceContainerHighest + elevation.3 (§8);
  `scrim` / `scrim.modal` (§4).

OUT OF SCOPE
  The take-style undo snackbar (slice 03, F08) · bulk delete · any "recently deleted" / trash /
  restore concept (not specified).
