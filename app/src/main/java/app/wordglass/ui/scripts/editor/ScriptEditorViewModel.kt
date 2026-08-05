package app.wordglass.ui.scripts.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.wordglass.data.model.ReadTime
import app.wordglass.data.repository.ScriptRepository
import app.wordglass.di.ApplicationScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

/**
 * The editor's stateful core. ONE 2-second debounce with four consumers — word count, read time,
 * title derivation and autosave — so the count settles at the same instant the write happens
 * (§6.2, §10.5).
 *
 * **Opening an existing script (§8 rows 3, 1).** When a `scriptId` nav arg is present and no
 * `recordId` is in [SavedStateHandle] yet (first open, not a process-death restore), the VM loads
 * the script from the repository, stamps [lastPersistedBody], and exposes the content via
 * [uiState]'s `openingBody` / `openingTitle`. The composable sets its `TextFieldState`s from those
 * values. On process-death restore, [recordId] is already in `SavedStateHandle`, `openingBody`
 * stays null, and the composable's `TextFieldState` `Saver` handles text restoration.
 *
 * **Title.** The title auto-fills from the body's first line until the user edits it directly;
 * from that point it is user-owned and the body stops overwriting it (`titleSetByUser`). The
 * takeover flag lives in [SavedStateHandle] so it survives process death, and in the record so it
 * survives sync. Manual title edits persist on their own 2-second debounce, and flush with the
 * body on background / back. While the title is still auto, it is derived server-side by
 * [ScriptRepository.updateBody]; the editor only mirrors the first line for display.
 *
 * The body *text itself* (and the title text) live in the composables' `TextFieldState`s (their
 * `Saver`s restore text + cursor + selection across process death). This ViewModel owns the
 * debounce pipelines, the derived counts, the persisted-record id and the takeover flag.
 */
@HiltViewModel
class ScriptEditorViewModel @Inject constructor(
    private val repository: ScriptRepository,
    private val savedStateHandle: SavedStateHandle,
    @param:ApplicationScope private val appScope: CoroutineScope,
) : ViewModel() {

    // null = no row created yet (never-typed editor). Survives process death via SavedStateHandle.
    private var recordId: String?
        get() = savedStateHandle[KEY_RECORD_ID]
        set(value) {
            savedStateHandle[KEY_RECORD_ID] = value
        }

    // true once the user has taken the title over. Survives process death via SavedStateHandle.
    private var titleManuallySet: Boolean
        get() = savedStateHandle[KEY_TITLE_MANUAL] ?: false
        set(value) {
            savedStateHandle[KEY_TITLE_MANUAL] = value
        }

    private val textFlow = MutableStateFlow("")
    private val titleFlow = MutableStateFlow("")
    private var lastPersistedBody: String? = null
    private val writeMutex = Mutex()

    private val _uiState = MutableStateFlow(ScriptEditorUiState())
    val uiState: StateFlow<ScriptEditorUiState> = _uiState.asStateFlow()

    init {
        val scriptId = savedStateHandle.get<String?>(ARG_SCRIPT_ID)
        val isFirstOpen = scriptId != null && recordId == null

        when {
            isFirstOpen -> {
                // First open of an existing script — load it async, tell the composable to wait.
                recordId = scriptId
                _uiState.value = ScriptEditorUiState(
                    isLoadingExisting = true,
                    titleManuallySet = titleManuallySet,
                )
                viewModelScope.launch {
                    val script = repository.getById(scriptId!!)
                    if (script == null) {
                        // Script not found (deleted on another device) — fall back to blank editor.
                        _uiState.value = _uiState.value.copy(
                            isLoadingExisting = false,
                            openingBody = "",
                        )
                        return@launch
                    }
                    titleManuallySet = script.titleSetByUser
                    lastPersistedBody = script.body
                    recompute(script.body)
                    _uiState.value = _uiState.value.copy(
                        isLoadingExisting = false,
                        titleManuallySet = script.titleSetByUser,
                        openingBody = script.body,
                        openingTitle = script.title,
                    )
                }
            }
            scriptId == null && recordId == null -> {
                // New script — signal immediately so the composable calls onInitialText right away.
                _uiState.value = ScriptEditorUiState(
                    titleManuallySet = titleManuallySet,
                    openingBody = "",
                )
            }
            else -> {
                // Process-death restore (recordId is already set). TextFieldState Saver restores
                // the text; the composable calls onInitialText with the restored body.
                _uiState.value = ScriptEditorUiState(titleManuallySet = titleManuallySet)
            }
        }

        viewModelScope.launch {
            textFlow
                .drop(1) // skip the seed value; onInitialText already handled the opening text
                .debounce(DEBOUNCE_MS)
                .collect { persist(it) }
        }
        viewModelScope.launch {
            titleFlow
                .drop(1) // skip the seed; a manual title only ever flows from a user edit
                .debounce(DEBOUNCE_MS)
                // Only persist while the title is user-owned. A stale value left in the flow after
                // a reset-to-auto must not re-persist and re-take-over the title.
                .collect { if (titleManuallySet) persistTitle(it) }
        }
    }

    /** Called once when the editor opens or restores, with the current body text. */
    fun onInitialText(body: String) {
        recompute(body)
        // On a restore/existing row, treat the opening text as already persisted so an unchanged
        // restore doesn't re-write and needlessly bump updatedAt.
        if (recordId != null) lastPersistedBody = body
        textFlow.value = body
    }

    /** Called on every subsequent body change (fed from the composable's snapshotFlow). */
    fun onBodyChanged(body: String) {
        textFlow.value = body
    }

    /**
     * Called when the user edits the title field directly (its text changed while it held focus).
     * Marks the title user-owned — the body stops re-deriving it from here on — and queues the new
     * value for the title debounce.
     *
     * **Emptying a taken-over title resumes automatic derivation** (Addendum 2026-08-05): clearing
     * a custom name is a request to go back to auto, not a request for no name — otherwise a script
     * with a full body could render "Untitled". (An empty body while auto still renders "Untitled",
     * per the existing rule.)
     */
    fun onTitleEdited(title: String) {
        if (title.isBlank()) {
            resumeTitleDerivation()
            return
        }
        if (!titleManuallySet) {
            titleManuallySet = true
            _uiState.value = _uiState.value.copy(titleManuallySet = true)
        }
        titleFlow.value = title
    }

    /** Reset to auto: the body re-derives the title again, starting from its current first line. */
    private fun resumeTitleDerivation() {
        if (!titleManuallySet) return
        titleManuallySet = false
        _uiState.value = _uiState.value.copy(titleManuallySet = false)
        val id = recordId
        appScope.launch { if (id != null) persistTitleReset(id) }
    }

    /**
     * Persist immediately, independent of the debounce — on background (ON_STOP) and on back.
     * Runs in [appScope] so a back-navigation that clears this ViewModel can't cancel the write.
     */
    fun flushNow() {
        val body = textFlow.value
        val id = recordId
        val manual = titleManuallySet
        val title = titleFlow.value
        appScope.launch {
            write(id, body)
            if (manual) persistTitle(title) // recordId is set by write() if the body just created it
        }
    }

    private suspend fun persist(body: String) {
        recompute(body)
        write(recordId, body)
    }

    private suspend fun write(id: String?, body: String) = writeMutex.withLock {
        if (body == lastPersistedBody) return@withLock
        if (id == null) {
            if (body.isEmpty()) return@withLock // never-typed / empty -> no record (§6.5)
            val newId = repository.create(body) // record-creation on first character committed
            recordId = newId
            // A title set before any body existed was held in titleFlow — apply it now.
            if (titleManuallySet) repository.updateTitle(newId, titleFlow.value)
        } else {
            repository.updateBody(id, body) // emptied-to-zero keeps the row -> "Untitled"
        }
        lastPersistedBody = body
    }

    private suspend fun persistTitle(title: String) = writeMutex.withLock {
        val id = recordId ?: return@withLock // no record yet -> applied when the body creates it
        repository.updateTitle(id, title)
    }

    private suspend fun persistTitleReset(id: String) = writeMutex.withLock {
        repository.resetTitleToAuto(id) // clear the flag and re-derive from the stored body
    }

    private fun recompute(body: String) {
        val words = ReadTime.wordCount(body)
        _uiState.value = _uiState.value.copy(
            wordCount = words,
            readTimeSeconds = ReadTime.readTimeSeconds(words),
        )
    }

    override fun onCleared() {
        flushNow() // safety net for the last edit before the ViewModel goes away
    }

    private companion object {
        const val ARG_SCRIPT_ID = "scriptId"
        const val KEY_RECORD_ID = "recordId"
        const val KEY_TITLE_MANUAL = "titleManuallySet"
        const val DEBOUNCE_MS = 2000L
    }
}
