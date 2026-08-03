# Deferred — designed ahead of build

Elements that are **designed now but not rendered/wired until a later slice**. The build loop
allows designing the full flow shape while building only what has a live destination, so the
fidelity sweep would otherwise flag these as "state with no code path" (sweep step 4) or as a
missing affordance. They are not defects. The sweep consults this file before flagging.

**Rules for this file**
- One row per deferred element. Name the exact screen, the element, the slice it was designed
  in, and the slice that activates it.
- When a slice activates an element, delete its row in that slice's fidelity sweep — a stale
  row here is as much a defect as an unreachable button.
- If an element is deferred, its screen's design prompt must also say so in `OUT OF SCOPE`.

---

## Slice 01 — Scripts, list and editor

| Screen | Element | Designed in | Activates in | Reason |
|---|---|---|---|---|
| `ScriptList` | Empty-state **primary** button *Generate one* (verbatim F03: *No scripts yet. Generate one in about thirty seconds, or write your own.*) | 01 | 06 | AI generation is slice 06 (F04). Both buttons are designed with verbatim copy; only the secondary *write your own* is rendered in 01 |
| `ScriptStart` | The whole route-chooser surface (*Write it · Generate it · Paste or import*) | 01 | 06 | Only *Write it* has a live destination in 01, so `+` opens a blank `ScriptEditor` directly. The chooser activates when *Generate it* / *Paste or import* exist (slice 06 / 15). Designed now because it is the shared entry for F01 onboarding and F04/F05 |
| `ScriptEditor` | Primary **Record** button | 01 | 02 | Record leads to `Capture`, which is slice 02 (F06). Designed as the editor's primary action; not rendered in 01. In 01 the editor's exit is autosave + system back |

---

## How the two empty-state buttons resolve across slices

- **Slice 01 build:** render only *write your own* (the secondary). The `ScriptList` empty
  state uses the F03 verbatim string in full; the *Generate one* primary is designed and
  present in the handoff but commented out of the build.
- **Slice 06 build:** wire *Generate one* to `ScriptStart` / `GenerateInput`; the empty state
  becomes the two-button, generation-first layout exactly as F03 specifies, and this row is
  deleted from DEFERRED.md.
