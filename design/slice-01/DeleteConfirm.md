# Screen: DeleteConfirm    Slice: 01    Platform: Android / Compose

ALL COPY ON THIS SCREEN IS NEW AND NOW APPROVED (2026-08-04). F03 covers writing a script,
not deleting one, and F08's delete flow is for takes (which use undo, not a dialog).
build-plan slice 01 specifies a "Delete confirmation" for scripts. The strings below carry
[NEW · APPROVED 2026-08-04].

PURPOSE
  Confirm deleting a script before it is gone. Scripts use a confirmation dialog, not the
  take-style undo — a deliberate difference [APPROVED 2026-08-04]: a take is an unreproducible
  ~90-second performance, so it gets undo; a script is text that syncs, so it gets a confirm.

ENTRY / EXIT
  Arrives from: the delete affordance on a ScriptList row
  Leaves to:
    - Confirm → script deleted, dismiss, return to ScriptList (the row is gone)
    - Cancel / scrim tap / back → dismiss, nothing deleted
  Back behaviour: back dismisses the dialog (equivalent to Cancel). The script is untouched.

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
  the downtown…" ). The surrounding sentence is fixed; only the title truncates.

CONTROLS
  Delete   Destructive: yes. This IS the confirmation, so no further confirmation. Uses the
           error token treatment.
  Cancel   Not destructive. Dismisses.

CONSTRAINTS
  Thumb-reach: standard dialog; actions in the dialog's action row.
  One-handed: yes.
  Glanceable: user is looking at the screen. Surface token set (§4); dialogs sit on
  surfaceContainerHighest at elevation.3.
  Over live camera: no.
  Longest-running operation: none — deletion is local and instant.

REUSE
  Existing components (design-system §11): Dialog, destructive variant.
  New components this justifies: none.

TOKENS
  None missing. Destructive action uses `error` / `onError`; the dialog uses
  surfaceContainerHighest and elevation.3 per §8.

OUT OF SCOPE
  The take-style undo snackbar (slice 03, F08) · bulk delete · any "recently deleted" /
  trash / restore concept (not specified).
