package app.wordglass.ui.scripts.editor

/**
 * What the count line renders. Both values settle on the 2-second debounce (not per keystroke),
 * so the number changing IS the moment the write happened (§6.2).
 */
data class ScriptEditorUiState(
    val wordCount: Int = 0,
    val readTimeSeconds: Int = 0,
    /** true once the user has taken the title over — the editor stops mirroring the first line. */
    val titleManuallySet: Boolean = false,
)
