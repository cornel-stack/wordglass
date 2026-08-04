# Screen: ScriptList    Slice: 01    Platform: Android / Compose

**Superseded by `specs/slice-01/slice-01-handoff.md` for build.** This prompt fed the design;
the handoff is the build source of truth and wins on any conflict (it supersedes the handout
and the rulings). Reconciled to the handoff 2026-08-04 — notably the row metadata is `[relative
date] · [read time]` with **no word count** (handoff §4.4 overruled rulings B2). The B1 overflow
menu, the B2 blank-editor / "Untitled" rule, and B4 accessibility trace to the rulings; the B3
state-transition table lives in handoff §8.

PURPOSE
  Find, open, create, and delete the user's scripts. The home surface for a signed-out solo
  creator — its purpose is finding an existing script; creation is the less frequent action.

ENTRY / EXIT
  Arrives from: cold launch (the app's home for a signed-out user; a recorder-only user lands
    on AssignmentInbox instead — later slice) · system back from ScriptEditor · onboarding
    (F01, slice 20 — not built here)
  Leaves to:
    - FAB (+) → ScriptEditor, blank (slice 01 direct; ScriptStart activates slice 06 — B3 rows
      4/5, design/DEFERRED.md)
    - row tap → ScriptEditor for that script (cursor + scroll restored — B3 row 3)
    - row ⋯ → the overflow menu (this screen's twelfth state — B1)
    - menu Delete → DeleteConfirm
  Back behaviour: system back exits the app — this is the root. No dialog. Predictive back
    applies app-wide (design-system §15).

STATES TO DESIGN
  empty         First run, no scripts. Icon, the slice-01 message, and ONE button. See CONTENT.
                Slice 01 is a one-button state (B6); the two-button generation-first state is
                slice 06 (design/DEFERRED.md). Two buttons here is the slice-01 defect.
  populated     40 scripts, newest first. Each row: title (up to two lines) + a metadata line.
  overflowing   A 74-character title; a long script's minutes read-time ("≈ 28 min 40 sec" at
                content.body.max, #9); the 40-item list scrolled under the FAB.
  untitled row  A script with an empty body has no first line to derive a title from: its row
                renders "Untitled" in the title position, styled identically, metadata
                "Yesterday · ≈ 0 sec" — date · read-time, no word count (handoff §4.4). Reached
                only by deliberate user action, never by app behaviour.
  overflow menu ⋯ open on a row — an anchored dropdown, one item, Delete (B1). See CONTROLS and
                the OVERFLOW MENU block.

CONTENT INVENTORY
  Screen title: "Scripts" [NEW · APPROVED 2026-08-04], role `headlineLarge` on `onSurface`
    (0.1 — the existing "Screen titles" role). The domain noun; survives the team feature,
    unlike "My/Your Scripts" or "Library". Also the exit label ScriptEditor's back affordance
    uses (#8).
  Row, per script (title + metadata read as ONE node for a11y — B4):
    - Title — from the first line, or the user's edited title, or "Untitled" for an empty body
      (B2). Up to two lines, then a LINE clamp with ellipsis (not a height clamp — at 200% the
      row grows and stays two lines). Longest: 74 characters. bodyLarge on onSurface.
    - Metadata line — `[relative date] · [read time]`, e.g. "2h ago · ≈ 47 sec" (handoff §4.4).
      labelSmall on onSurfaceVariant, one line. NO word count in the row — word count lives only
      on the editor's count line (handoff §4.4 overruled rulings B2). Read-time per the
      design-system format: "≈ 47 sec" under a minute, "≈ 2 min 14 sec" above, at 140 wpm.
      Numerals in mono tabular (numericMedium) so figures do not jitter between rows; they never
      truncate and wrap rather than clip at 200%.
    - Relative date rendering (handoff §4.4): "2h ago" · "Yesterday" · "Mon" · "Fri" · "28 Jul"
      · "21 Jul".
  Empty state (slice 01 — B6). The `Empty state` component (§11 / handoff §2) — a
    vertically-centred block: glyph, space.6, body copy, space.6, one button (handoff §4.2):
    - Glyph: Material Symbols Outlined `description` at `icon.size.large` (48), `onSurfaceVariant`
      (0.2). DECORATIVE — not-important-for-accessibility; the body copy states the same thing.
    - Body copy: "No scripts yet. Write your first one." [NEW · APPROVED 2026-08-04], `bodyLarge`
      on `onSurfaceVariant`, centred (0.3).
    - ONE button: "Write your own" [NEW · APPROVED 2026-08-04], an `OutlinedButton` (outline
      border, onSurface label — 0.4) → ScriptEditor blank. The FAB is present but skipped in the
      focus order here.
    - Slice 06 ADDS a filled `Button` "Generate a script" above it (same geometry), restoring the
      two-button generation-first state and its verbatim F03 string — the copy pair in
      design/DEFERRED.md. Two buttons in slice 01 is the defect.

CONTROLS
  FAB (+)           Floating action button, fab.standard (56dp), Material Symbols "+". →
                    ScriptEditor blank. Content description "New script" [NEW · APPROVED
                    2026-08-04]. Bottom-end; LAST in focus order (B4). Not destructive.
  Row tap           Opens that script. Not destructive.
  Row ⋯ (overflow)  [NEW · APPROVED 2026-08-04] Material Symbols overflow glyph; a 48dp square
                    target carved OUT OF the row's target, never overlapping it (B4). Opens the
                    overflow menu. Icon-only is permitted here — platform-conventional, its
                    content description names its object, mark ≥3:1 (§13 icon-only rule).
  Menu · Delete     [NEW · APPROVED 2026-08-04] the menu's single item → DeleteConfirm. NOT
                    tinted error (bodyLarge on onSurface) — the weight lands in the dialog, not
                    a one-item menu (B1).
  Empty · Write your own   → ScriptEditor blank.

OVERFLOW MENU (B1 — the twelfth state)
  Container   surfaceContainerHigh, shape.sm, 1dp `outline` (load-bearing edge — §4 sole-identifier)
  Anchor      trailing edge of the ⋯, opening downward; upward when within 88dp of window bottom
  Min width   112dp · Item height 48dp full-width target · Item padding space.3 horizontal
  Item label  "Delete" — bodyLarge on onSurface, NOT error
  Scrim       none — menus do not scrim; the anchor keeps context
  Motion      motion.fast (120ms), easing.standard, scale + fade from the anchor corner
  Dismiss     tap outside · system back · predictive back
  Focus       enters on open, lands on the item, trapped; returns to the ⋯ on every dismiss (B4)

CONSTRAINTS
  Thumb-reach: FAB bottom-end, within reach; the ⋯ affordances trail each row.
  One-handed: yes. Glanceable: user is looking at the screen. Surface set (§4).
  Over live camera: no.
  FAB clearance (#6): list bottom content padding = fab.standard + space.6 + space.4 +
    navigationBars = 56 + 24 + 16 + navigationBars = 96dp (space.24) + navigationBars — the last
    row clears the FAB's top edge by 16dp. Do NOT shrink the FAB offset.
  Longest-running operation: none. Local reads are instant — no spinner, no loading state.

ACCESSIBILITY (B4)
  Targets: row full-width at row.script (88dp) minimum; the ⋯ is a 48dp square carved OUT OF the
    row target, not overlapping it (nested targets are a defect — specify the exclusion); FAB
    56dp visual and target.
  Focus order: title → row 1 → row 1 ⋯ → row 2 → row 2 ⋯ → … → FAB. FAB LAST (finding beats
    creating). Title + metadata are one node per row, not three. In the empty state the FAB is
    redundant with the body button and is skipped.
  Content descriptions:
    - Row: "[full untruncated title], [relative date], about [read-time] to read aloud." Title
      untruncated (the visual clamp is sighted-only — DeleteConfirm precedent).
    - ⋯: "More options for [title]" — with the title; forty controls all reading "More options"
      are indistinguishable in TalkBack (B1).
    - FAB: "New script."
  At 200%: rows grow, do not clamp; two-line title stays two lines; metadata wraps, numerals
    never truncate; the FAB does not scale with font size.
  Colour alone: nothing — date and read-time are text.
  Contrast — MEASURE and record: title onSurface on surface; metadata onSurfaceVariant on
    surface; the ⋯ mark ≥3:1; the FAB "+" on primary.

REUSE
  Existing components (handoff §2): `ScriptRow` (rows) · `Empty state` (the empty screen —
    composes the glyph + body + `OutlinedButton`; slice 06 adds a filled `Button`) ·
    `AlertDialog` (via DeleteConfirm). The overflow menu is an M3 `DropdownMenu` built to the B1
    spec.
  FAB: M3 `FloatingActionButton` at fab.standard with a Material Symbols glyph — an M3 primitive.
  New components this justifies: none.

TOKENS
  None missing. Type: `headlineLarge` (title), `bodyLarge` (title/empty body), `labelSmall` +
  `numericMedium` (metadata line). `icon.size.large` (empty glyph). row.script / fab.standard
  (§10), outline / surfaceContainerHigh / shape.sm for the menu (§4/§7), motion.fast (§9).

OUT OF SCOPE
  Search, folders, version history (slice 15) · sync / cloud status (slice 13) · the ScriptStart
  chooser (activates slice 06; + goes straight to the editor in 01 — B3) · the two-button
  generation empty state and "Generate a script" (slice 06, design/DEFERRED.md) · multi-select /
  bulk actions · menu items beyond Delete (rename, duplicate, export, move-to-team are plausible
  future items per B1, but not slice 01).
