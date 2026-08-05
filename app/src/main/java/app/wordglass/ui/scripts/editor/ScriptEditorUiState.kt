package app.wordglass.ui.scripts.editor

/**
 * What the count line renders. Both values settle on the 2-second debounce (not per keystroke),
 * so the number changing IS the moment the write happened (§6.2).
 *
 * [isLoadingExisting] is true while an existing script is being fetched on first open. The
 * composable uses this to suppress the process-death-restore path for `onInitialText` until the
 * load completes — without it, `onInitialText("")` would stamp `lastPersistedBody = ""` before the
 * real body arrives.
 *
 * [openingBody] and [openingTitle] are non-null only on the first open of an existing script (after
 * [isLoadingExisting] drops to false). null means: let the TextFieldState Saver (process-death
 * restore) or the initial empty state (new script via [openingBody] = "") drive the text.
 */
data class ScriptEditorUiState(
    val wordCount: Int = 0,
    val readTimeSeconds: Int = 0,
    val titleManuallySet: Boolean = false,
    val isLoadingExisting: Boolean = false,
    val openingBody: String? = null,
    val openingTitle: String? = null,
)
