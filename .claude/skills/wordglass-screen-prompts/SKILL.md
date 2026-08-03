---
name: wordglass-screen-prompts
description: Emit design prompts for Wordglass screens — one prompt per screen, covering every state, ready to hand to a designer. Use this whenever work begins on a Wordglass slice, whenever the user mentions designing, specifying, or scoping screens, whenever they ask "what screens does this need", and whenever a slice moves from /specify into the design step. Also use it when adding a single new screen to an existing slice, or when an existing screen gains a state that was never designed. If the task involves deciding what a Wordglass screen should contain or look like, use this skill rather than writing prompts freehand.
---

# Wordglass Screen Design Prompts

Step 2 of the six-step build loop. `/specify` has produced a slice scope; this turns that
scope into one design prompt per screen, each covering every applicable state.

**This is the step most likely to be rushed, and a thin prompt produces a screen that has
to be redesigned after build.** The cost of thoroughness here is fifteen minutes. The cost
of skipping it lands three slices later.

---

## Step 1 — Read the context first

Do not write from memory. Read, in this order:

1. **`docs/design-system.md`** — the token authority. Every value named in a prompt must
   exist here. Section 11 lists the components available for reuse.
2. **`docs/user-flows.pdf`** — the flows this slice implements. **Copy strings here are
   verbatim and must be reproduced exactly.** Error paths named here are not optional.
3. **`docs/build-plan.md`** — what is in this slice, what is explicitly not, and the
   "done when" criterion.
4. **The `/specify` output for this slice**, if it exists.

If the slice scope is unclear or the flows do not cover a screen you think is needed,
**say so and stop.** Do not invent the missing piece.

---

## Step 2 — Emit the inventory, then stop

Before writing any prompt, produce a table of every screen and the states that apply to it.
Present it and **wait for approval.**

```
| Screen | States | Over camera | Flows |
|---|---|---|---|
| ScriptList | empty, populated, overflowing | no | F03 |
| ScriptEditor | new, editing, working, interrupted | no | F03 |
| DeleteConfirm | default | no | F03 |
```

A slice with eight screens at eight states each is a lot of output. Discovering at prompt
six that the screen list was wrong wastes the previous five. The inventory is cheap; get it
agreed.

Also list in the inventory:
- **Screens deliberately excluded** and why
- **New components this slice appears to need**, with the existing component each one was
  considered against

---

## Step 3 — Write one prompt per screen

Use the template in the next section. One prompt per **screen**, not per slice.

---

## The template

```
# Screen: [name]    Slice: [n]    Platform: Android / Compose

PURPOSE
  One sentence. What the user is trying to accomplish here.

ENTRY / EXIT
  Arrives from: [screen or event]
  Leaves to: [screens, and what triggers each]
  Back behaviour: [what the system back gesture does — including
  mid-recording and mid-export]

STATES TO DESIGN
  [every applicable state from the matrix below, each with a
  one-line description of what the user sees]

CONTENT INVENTORY
  Every string, verbatim. Every number and its format.
  Longest realistic value for each field.
  Empty-state copy and its call to action.

CONTROLS
  Each control: label, icon, enabled/disabled conditions,
  destructive yes/no, confirmation required yes/no.

CONSTRAINTS
  Thumb-reach: which controls must sit in the bottom third
  One-handed: yes/no
  Glanceable: is the user looking at the screen or the lens
  Over live camera: yes/no (drives contrast requirements)
  Longest-running operation and whether it can be backgrounded

REUSE
  Existing components this must use: [names from docs/design-system.md §11]
  New components this justifies: [name + why nothing existing fits]

TOKENS
  Any token this screen needs that does not yet exist in
  docs/design-system.md. If this section is non-empty, flag it —
  it is a design system change, not a screen decision.

OUT OF SCOPE
  Explicitly not on this screen.
```

### The two fields that carry the most weight

**`Longest realistic value`.** A layout that looks perfect with "My Script" breaks on a
74-character title. Name the actual overflow case for every field: longest script title,
longest org name, longest transcript line, a three-digit duration, a 40-item list. If a
field has no realistic long case, say so explicitly rather than omitting it.

**`REUSE`.** Without an explicit reuse field, every screen quietly grows its own component
set and the app stops looking like one product somewhere around slice 6. Name existing
components by their names in the design system. A new component requires naming the
existing one it was considered against and why that one does not fit.

These two are what get dropped when a prompt is written in a hurry. They are not optional.

---

## The state matrix

Not every state applies to every screen. **List the ones that do and say nothing about the
rest — an unmentioned state is a state nobody built.**

| State | Applies when |
|---|---|
| Empty | No content yet. Needs copy and a call to action, not a shrug |
| Loading | Local reads are fast enough to skip. Anything network-bound needs a designed state |
| Populated | Design at realistic volume — 40 scripts, not 3 |
| Overflowing | Longest realistic string in every field |
| Error — retryable | Network failed. Must offer the retry, not just report the failure |
| Error — terminal | DRM audio, unsupported codec, quota exhausted. Explain plainly and offer the alternative |
| Offline | Recording works offline. Captions queue. The UI must say which |
| Permission pre-prompt | Before the system dialog. Explains why, in the user's terms |
| Permission denied | Soft denial — ask again with context |
| Permission denied permanently | Deep link to Settings. Different screen, different copy |
| Working / progress | Anything over ~2s. Honest progress, cancellable where possible |
| Quota reached | Free-tier minutes or generations exhausted. This is a paywall moment — design it deliberately |
| Interrupted | Call arrived, app backgrounded, storage full. What the user sees on return |

---

## Do not design these

System-owned. They cannot be restyled, and time spent designing them is wasted.

- **The Android runtime permission dialog.** Design the *pre-prompt* that precedes it and
  the *denied* state that follows — never the dialog itself
- The system share sheet
- The Storage Access Framework file picker
- The Google Play purchase flow
- System notifications beyond icon, title, body, and actions

---

## Wordglass constraints every prompt must respect

These override generic mobile patterns. A prompt that ignores them produces a screen that
has to be rebuilt.

### If the screen sits over the camera preview

- Say so explicitly in `CONSTRAINTS`. It changes the entire colour treatment.
- Specify the over-camera token set from `docs/design-system.md` §5, not the surface set
- **Every over-camera element must be designed twice** — against a white wall and against a
  dark room. State this in the prompt
- Touch targets are **64dp minimum**, 72dp for record — not the platform 48dp
- **Nothing may change position between states.** A control that shifts when recording
  starts is a control the user cannot hit. If a state change implies a layout change, the
  prompt is wrong — express it in colour, opacity, or haptics instead
- Haptic confirmation replaces visual confirmation. The user is looking at the lens
- Scrim plus outline, never colour alone

### Copy

- **Strings from `docs/user-flows.pdf` are verbatim.** Do not improve them, shorten them,
  or make them friendlier
- Where the flows do not specify a string, write it and **mark it as new** so it gets
  reviewed rather than absorbed
- **No "discard changes?" dialog exists anywhere in this app.** Autosave means back is
  always safe. If a screen seems to need one, the screen is wrong
- Every paywall offers a real second action — *Export without captions*, *Write it myself*.
  A dead end loses the user entirely
- Errors explain in plain language and offer the alternative

### Destructive actions

Takes are expensive to reproduce. Every destructive action on a take needs confirmation or
undo — undo preferred, with the window stated. Never a bare delete.

### Progress

Anything over ~2s gets a designed working state with real percentage, elapsed, and estimated
remaining. **Indeterminate spinners are only acceptable where the app genuinely cannot know**
— ASR transcription is the one legitimate case. Export is not.

---

## Common failures to check before handing over

Run this list against every prompt before presenting it.

1. **A state was listed but not described.** Naming a state without saying what the user
   sees leaves the decision to whoever designs it
2. **Longest realistic value omitted for a field.** The most common omission, and the one
   that surfaces at build time as a broken layout
3. **`REUSE` left vague.** "Use existing components" is not a reuse field. Name them
4. **Copy invented where the flows specify it.** Check the flows document again
5. **An over-camera screen without the white-wall / dark-room instruction**
6. **A `TOKENS` entry that was quietly resolved by picking a similar existing token.** If a
   screen needs a value the system does not have, that is a design system decision and it
   gets flagged, not absorbed
7. **A permission dialog described as designable**
8. **Layout change implied between capture-UI states**

---

## Output

Write the prompts to `design/slice-[n]/[ScreenName].md`, one file per screen, plus
`design/slice-[n]/inventory.md` holding the approved table.

They go to the designer as-is. Do not summarise them in chat — say how many were written
and where they are.
