# Wordglass — Design System

**Status:** v1, slice 01. Values here are decided; the ones marked ⚠ need on-device
validation at the slice noted.

Every screen assembles from this. If a screen needs a value that is not a token here,
**stop and add the token deliberately** — do not inline a hex value, a dp measurement, or
a duration.

---

## 1. Direction

**Instrument, with editorial typography.**

Near-black surfaces. Restrained radius. Precise outlines rather than fills. One cool
accent used sparingly. The typeface does the identity work, because the script is the
product and the prompter tier is the largest visual element in the app.

The reference is a light meter or a field recorder — something a professional trusts
because it is legible and unfussy, not because it is charming.

### Why not the alternatives

**Not Material 3 Expressive's personality.** Expressive pushes toward friendlier, rounder,
more emotional. This is a tool for realtors and corporate comms teams who distrust
visible modification. We take Expressive's token architecture, shape system, and motion
scheme; we decline the playfulness.

**Not a strong brand colour.** The org brand kit is a moat mechanism — teams inject their
own logo and colours into captions. App chrome must sit underneath someone else's brand
without fighting it. Accent is used with high restraint: active selection, progress,
primary action. Nothing else.

**Not dynamic colour.** Material You derives palettes from the user's wallpaper.
Over-camera contrast is a correctness requirement here, and brand consistency is a
business requirement. Wallpaper-driven colour breaks both. **Dynamic colour is off.**

---

## 2. Foundation

Build on **Material 3, `androidx.compose.material3` 1.4.x stable.** Not 1.5.x alpha.

M3 already provides the token architecture, and every component is wired to it. We
override values; we do not invent structure. Compose is now the first-class Android UI
path — the View-based Material library is in maintenance.

Two additions M3 does not cover, defined in section 4 and 5:

- **The over-camera colour set** — a second, parallel surface set for chrome that sits on
  live video
- **The prompter type tier** — sits above M3's entire type scale

**Dark only.** Not a dark mode — there is no light set to maintain. `isSystemInDarkTheme()`
is not consulted.

---

## 3. Typography

**IBM Plex Sans** for everything. **IBM Plex Mono** for timecodes, durations, counters, and
any tabular numeric.

Chosen because it was designed as an engineering and institutional typeface, has a matching
mono sibling, is legible at extreme sizes where letterform character becomes unmissable, and
is uncommon enough in this category to be distinctive without being strange.

**Bundle the fonts in the APK. Do not use downloadable fonts.** Local-first is a product
principle; a prompter that renders in a fallback typeface because the device is offline is
a broken prompter.

### The prompter tier

This is the tier M3 does not have, and it is the reason the type scale exists.

| Token | Value | Notes |
|---|---|---|
| `prompter.min` | 28sp | Floor of the user slider |
| `prompter.default` | 44sp | ⚠ validate slice 02 |
| `prompter.max` | 72sp | Ceiling of the user slider |
| `prompter.lineHeight` | 1.5× | Generous leading. Broadcast practice is explicit that tight line spacing is a top teleprompter error |
| `prompter.letterSpacing` | 0.01em | Marginal opening at large sizes |
| `prompter.weight` | Medium (500) | Regular disappears against a bright background; Bold reads shouty at 60sp |

**Derivation.** Signage research puts comfortable extended reading at 15–20 arc minutes of
subtended vision. At arm's length (~60cm) that is ~23–31dp for foveal reading. But the user
is reading parafoveally — eyes on the lens, text 5–15° off-axis — where acuity falls to a
third or half of foveal. That puts the practical floor at 55–80dp equivalent.

For scale: M3's `displayLarge` is 57sp. **The prompter tier begins roughly where Material's
scale ends.**

Broadcast practice corroborates: 48–60pt on a 1080 canvas for a presenter five to eight feet
from the lens.

### ⚠ The line-length tension — read this before designing the capture UI

Broadcast teleprompter guidance is consistent: 8–10 words per line, no hyphenated breaks,
enough words on screen that the reader can speed up without running out.

**That is not achievable on a phone in portrait.** Usable width is ~320dp. At 44sp, average
character width is ~22dp, giving ~14 characters — two to three words per line.

Broadcast prompters are wide screens. 9:16 is not. **Large text and long lines are mutually
exclusive on this form factor.**

**Decision:** make the tradeoff the user's, with a well-chosen default.

- Font size is a user slider across the full 28–72sp range
- Prompter text is **full-bleed**, 16dp horizontal margins only, to buy back characters
- Default sits at 44sp
- ⚠ **Validate on the A14 at slice 02.** Record a real 90-second read at the default and
  check whether eye travel is visible on camera. If it is, the default moves down and the
  scroll tuning gets more work.

**Mirror mode inverts this entirely** — external glass puts the device further away, so
the same sp value subtends less. Mirror mode may need its own default. Determine at slice 02.

### Screen type scale

| Token | Size | Line height | Weight | Use |
|---|---|---|---|---|
| `displayLarge` | 44sp | 1.15 | SemiBold | Onboarding, single-statement moments |
| `headlineLarge` | 30sp | 1.2 | SemiBold | Screen titles |
| `headlineMedium` | 24sp | 1.25 | Medium | Section headers, sheet titles |
| `titleLarge` | 20sp | 1.3 | Medium | List row primary, dialog titles |
| `titleMedium` | 17sp | 1.35 | Medium | Card titles |
| `bodyLarge` | 16sp | 1.5 | Regular | Script editor body, primary reading |
| `bodyMedium` | 14sp | 1.45 | Regular | Secondary text, descriptions |
| `labelLarge` | 14sp | 1.2 | Medium | Buttons |
| `labelMedium` | 12sp | 1.2 | Medium | Chips, captions, metadata |
| `labelSmall` | 11sp | 1.2 | Medium | Overlines, timestamps |
| `numericMedium` | 14sp | 1.2 | Medium | **Plex Mono, tabular.** Durations, counters |
| `numericLarge` | 20sp | 1.2 | Medium | **Plex Mono, tabular.** Elapsed recording time |

Script editor body is `bodyLarge` at 16sp. It is a writing surface, not a reading surface —
the prompter tier is where reading happens.

---

## 4. Colour — surface set

Screens that are not over camera.

### Surfaces

| Token | Hex | Use |
|---|---|---|
| `surface` | `#0B0C0E` | App background |
| `surfaceContainerLowest` | `#08090A` | Recessed wells, text fields |
| `surfaceContainerLow` | `#101215` | List rows, subtle separation |
| `surfaceContainer` | `#16181C` | Cards, standard containers |
| `surfaceContainerHigh` | `#1D2024` | Bottom sheets, menus |
| `surfaceContainerHighest` | `#25282D` | Dialogs, topmost |

### Content

| Token | Hex | Use |
|---|---|---|
| `onSurface` | `#E8EAED` | Primary text |
| `onSurfaceVariant` | `#9BA1A8` | Secondary text, inactive icons |
| `onSurfaceDisabled` | `#5A6068` | Disabled |
| `outline` | `#3A3F45` | Borders, dividers where visible |
| `outlineVariant` | `#22262A` | Hairlines, subtle separation |

Not pure white on pure black. `#E8EAED` on `#0B0C0E` is ~15:1 — well past AA — without the
halation pure white produces on OLED at large sizes.

### Accent

| Token | Hex | Use |
|---|---|---|
| `primary` | `#5EC8DC` | Active selection, progress, primary action |
| `onPrimary` | `#00323C` | Text on primary fills |
| `primaryContainer` | `#004A57` | Selected chip backgrounds |
| `onPrimaryContainer` | `#A8E8F5` | Text on primary containers |
| `primaryDim` | `#3A8494` | Primary at rest, low emphasis |

One accent. If a screen wants a second, the answer is hierarchy, not colour.

### Record

Record is not the accent. It is its own semantic and it is never used for anything else.

| Token | Hex | Use |
|---|---|---|
| `record` | `#FF4438` | Record affordance, recording edge treatment |
| `recordDim` | `#B32E26` | Record at rest / disabled |

Convention is load-bearing here. Do not be clever with the record colour.

### Semantic

| Token | Hex | Use |
|---|---|---|
| `error` | `#FF6B60` | Terminal errors, destructive confirmation |
| `onError` | `#3D0906` | Text on error fills |
| `errorContainer` | `#4A130E` | Error banners |
| `warning` | `#FFB84D` | Approaching quota, storage pressure |
| `success` | `#5FD98C` | Completion, sync healthy |

---

## 5. Colour — over-camera set

**A separate set, not the surface set with opacity applied.** Chrome here sits on a live
preview that may be a white wall or a dark room, and the same layout must hold in both.

Every over-camera element is designed and screenshotted twice — white wall and dark room.
This is a fidelity sweep step, not a suggestion.

| Token | Value | Use |
|---|---|---|
| `oc.text` | `#FFFFFF` | Primary over-camera text. Pure white here, unlike surfaces |
| `oc.textDim` | `rgba(255,255,255,0.72)` | Secondary over-camera text |
| `oc.surface` | `rgba(11,12,14,0.72)` | Control backgrounds, pills |
| `oc.surfaceStrong` | `rgba(11,12,14,0.88)` | Settings sheets over preview |
| `oc.outline` | `rgba(255,255,255,0.28)` | 1dp outline on every over-camera control |
| `oc.scrimTop` | `rgba(0,0,0,0.70)` → transparent over 96dp | Top chrome legibility |
| `oc.scrimBottom` | `rgba(0,0,0,0.80)` → transparent over 180dp | Bottom control cluster |
| `oc.prompterScrim` | `rgba(0,0,0,0.0–0.85)` | **User-adjustable.** Slider maps to this range |
| `oc.recordingEdge` | `record` @ 0.92, 3dp inset border | Persistent recording state, perceivable peripherally |

### Rules

**Scrim plus outline, never colour alone.** A filled control with no outline vanishes against
a matching background. Every over-camera control carries a 1dp `oc.outline` stroke regardless
of its fill.

**Never assume the background is dark.** The failure case is a realtor filming against a
window at midday, not a dark room.

**WCAG 4.5:1 / 3:1 is a floor, not a target.** A static image can be measured; a camera
preview panning to a bright window cannot. Design with margin.

**The recording edge is a border, not a dot.** It must be perceivable while the user is
looking at the lens.

---

## 6. Spacing

4dp base unit.

| Token | Value |
|---|---|
| `space.0` | 0dp |
| `space.1` | 4dp |
| `space.2` | 8dp |
| `space.3` | 12dp |
| `space.4` | 16dp |
| `space.5` | 20dp |
| `space.6` | 24dp |
| `space.8` | 32dp |
| `space.10` | 40dp |
| `space.12` | 48dp |
| `space.16` | 64dp |

**Screen horizontal padding:** `space.4` (16dp). **Over-camera horizontal padding:** `space.4`,
and the prompter uses this as its only margin.

---

## 7. Shape

Restrained. Instrument, not friendly.

| Token | Value | Use |
|---|---|---|
| `shape.none` | 0dp | Full-bleed surfaces, prompter scrim |
| `shape.xs` | 2dp | Chips, small indicators |
| `shape.sm` | 4dp | Buttons, text fields, list rows |
| `shape.md` | 8dp | Cards, over-camera control pills |
| `shape.lg` | 12dp | Bottom sheets (top corners), dialogs |
| `shape.full` | 999dp | **Record button only.** Nothing else |

Deliberately below M3 Expressive defaults. The record button is the single fully-round
element in the app, which is what makes it findable without looking.

---

## 8. Elevation

Flat with outlines. Shadow is used sparingly, and only where something genuinely floats
above content that remains visible.

| Token | Treatment |
|---|---|
| `elevation.0` | `surface`, no shadow |
| `elevation.1` | `surfaceContainerLow`, 1dp `outlineVariant` border, no shadow |
| `elevation.2` | `surfaceContainer`, 1dp `outlineVariant` border, no shadow |
| `elevation.3` | `surfaceContainerHigh`, 2dp shadow. **Bottom sheets and dialogs only** |

Tonal separation carries hierarchy. Shadow only when something is modal.

---

## 9. Motion

Two durations. Not ten.

| Token | Value | Use |
|---|---|---|
| `motion.fast` | 120ms | State changes, toggles, ripples, selection |
| `motion.standard` | 240ms | Sheets, navigation transitions, expansion |
| `easing.standard` | `CubicBezier(0.2, 0.0, 0.0, 1.0)` | Everything |
| `easing.decelerate` | `CubicBezier(0.0, 0.0, 0.0, 1.0)` | Entering elements |

### Where motion is prohibited

**Nothing in the capture UI moves position between states.** A button that shifts when
recording starts is a button the user cannot hit. Capture-UI state changes are expressed by
colour, opacity, and haptics — never by layout.

**Prompter scroll is not animation.** It is continuous linear translation at a user-controlled
rate, and it must not be eased, interpolated, or frame-dropped. Treat it as a separate
concern from the motion system entirely.

### Reduced motion

Reduced-motion behaviour is **systemic, not per-screen.** When the system reduced-motion
setting is on, positional animation is dropped **everywhere** — elements appear at their final
position with opacity transitions only, at the same durations (`motion.fast` / `motion.standard`
unchanged). No screen opts in, opts out, or defines its own reduced-motion variant; it is
handled once, globally.

**One exception, and it is absolute: prompter scroll is unaffected by reduced motion.** As
above, it is not animation — it is continuous translation at a user-controlled rate — and it
must never be disabled, paused, or degraded by an accessibility setting. Reduced motion turns
off decorative movement; it must not turn off the thing the user is reading from.

---

## 10. Touch targets

| Context | Minimum |
|---|---|
| Standard UI | 48dp |
| **Capture UI** | **64dp**, and 72dp for record |
| Spacing between adjacent capture-UI targets | 16dp minimum |

The capture minimum is deliberately well above the platform 48dp. The user is not looking at
the screen. Generous targets and fixed positions are what make a control findable by feel,
and haptic confirmation replaces the visual confirmation the user will not see.

---

## 11. Components — slice 01 deliverables

Each built as a composable with a `@Preview` per state, and rendered in a debug-only gallery
screen.

| Component | States to build |
|---|---|
| Button (filled, outlined, text) | enabled, pressed, disabled, loading |
| Icon button | enabled, pressed, disabled, selected |
| **Slider** | default, dragging, disabled, with value label |
| **TextField** | default, focused, filled, error, disabled |
| Bottom sheet | collapsed, expanded, dragging |
| List row | default, pressed, selected, with trailing action, overflowing text |
| Chip | default, selected, disabled |
| Dialog | standard, destructive |
| Toast / snackbar | default, with action, with undo |
| Progress | indeterminate, determinate with percentage and remaining |
| Empty state | icon, message, primary action, optional secondary action |

**One slider serves beauty sliders, audio mixing, prompter speed, prompter size, and scrim
opacity.** If a later slice wants a second slider, that is a fidelity sweep failure — revert it.

Same rule for the bottom sheet, the progress pattern, and the destructive-confirm pattern.

**`TextField` is for discrete fields** — a script title, a search box, an org name. Fill
`surfaceContainerLowest`, 1dp `outline` stroke, `shape.sm`, label in `onSurfaceVariant`;
focused swaps the stroke to `primary`, error to `error` with helper text. **The full-bleed
writing surface in `ScriptEditor` is deliberately not a `TextField`** — no border, no label,
body text directly on `surface`. That distinction is documented in the `ScriptEditor` design
prompt; do not reach for `TextField` there.

**The `Empty state` secondary action is optional and lower-emphasis** — a text or outlined
button beneath the filled primary, never two filled buttons competing.

---

## 12. Designing against this

Notes for screen design prompts and for the fidelity sweep.

**Name tokens, never values.** A handoff says `surfaceContainer` and `space.4`, not `#16181C`
and `16dp`. Colour picked from a screenshot is not evidence.

**Design every state.** A screen designed only in its happy state is a third of a screen. The
universal state matrix — empty, loading, populated, overflowing, error-retryable,
error-terminal, offline, permission pre-prompt, permission denied, permission denied
permanently, working, quota reached, interrupted — applies per screen. State the ones that
apply; say nothing about the rest.

**Populate realistically.** 40 scripts, not 3. Longest realistic value in every field — a
74-character script title, a long org name, a three-minute transcript.

**Over-camera elements get designed twice.** White wall and dark room. Both go in the sweep.

**Reuse is a field in every prompt.** Existing components this must use, by name. New
components this justifies, with why nothing existing fits. Without it, every screen grows its
own component set and the app stops looking like one product somewhere around slice 6.

---

## 13. Open items

| Item | Resolve at |
|---|---|
| ⚠ Prompter default size — validate 44sp with a real 90-second read on the A14; check whether eye travel is visible on camera | Slice 02 |
| ⚠ Mirror mode may need its own default size, since external glass increases viewing distance | Slice 02 |
| ⚠ Prompter scrim default within the 0–0.85 range | Slice 02 |
| Web dashboard type scale and density — desktop breakpoints, not a scaled phone layout | Slice 15 |
| Caption theme definitions — five themes, defined as data | Slice 10 |
