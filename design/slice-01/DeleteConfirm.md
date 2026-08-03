# Screen: DeleteConfirm    Slice: 01    Platform: Android / Compose

ALL COPY ON THIS SCREEN IS NEW. F03 covers writing a script, not deleting one, and F08's
delete flow is for takes (which use undo, not a dialog). build-plan slice 01 specifies a
"Delete confirmation" for scripts. Every string below is [NEW] and must be reviewed, not
absorbed.

PURPOSE
  Confirm deleting a script before it is gone. Scripts use a confirmation dialog; they do not
  use the take-style 6-second undo (that is slice 03, F08). Flag this choice for review — if
  scripts should instead use undo for consistency with takes, that is a product decision.

ENTRY / EXIT
  Arrives from: the delete affordance on a ScriptList row
  Leaves to:
    - Confirm → script deleted, dismiss, return to ScriptList (the row is gone)
    - Cancel / scrim tap / back → dismiss, nothing deleted
  Back behaviour: back dismisses the dialog (equivalent to Cancel). The script is untouched.

STATES TO DESIGN
  default   The destructive confirmation. See CONTENT.

CONTENT INVENTORY
  All [NEW] — propose and mark for review:
    - Title: "Delete this script?"
    - Body: "This can't be undone."
    - Confirm button: "Delete" (destructive)
    - Cancel button: "Cancel"
  Longest realistic value: the dialog does not echo the script title, so there is no overflow
  case. If review decides the title should appear in the body ("Delete 'My listing walkthrough
  script…'?"), that reintroduces the 74-character overflow case — flag it rather than adding
  it silently.

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
