package app.wordglass.data.model

/**
 * The single source of a script's **display title** — the "Untitled" substitution, in ONE place
 * (owner ruling, Phase C). A script with an empty body (or a user-cleared, auto-derived title)
 * persists `title = ""`; wherever that title is shown as a *label* it renders "Untitled" instead.
 *
 * "Untitled" is a **presentation-layer substitution, never stored** (handoff §4.4, Addendum
 * 2026-08-05): the record keeps an empty title so nothing writes a fake title that would sync at
 * slice 13. Both call sites that show the title as a label — `ScriptRow` and `DeleteConfirm`'s echo
 * — go through here, so they can never disagree on the empty case (empty vs whitespace-only, trim).
 *
 * The **editor title** deliberately does not call this: it is an editable `TextField` showing the
 * raw title (empty, no placeholder, when empty — the outline identifies it), not a label.
 */
object ScriptDisplay {

    const val UNTITLED = "Untitled"

    /** The title to show as a label: the stored title, or "Untitled" when it is blank. */
    fun title(storedTitle: String): String = storedTitle.ifBlank { UNTITLED }
}
