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
 * (§6.2, §10.5, addendum ruling 4).
 *
 * The body *text itself* lives in the composable's `TextFieldState` (its `Saver` restores text +
 * cursor + selection across process death). This ViewModel owns only the debounce pipeline, the
 * derived counts, and the persisted-record id — the last held in [SavedStateHandle] so it too
 * survives process death.
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

    private val textFlow = MutableStateFlow("")
    private var lastPersistedBody: String? = null
    private val writeMutex = Mutex()

    private val _uiState = MutableStateFlow(ScriptEditorUiState())
    val uiState: StateFlow<ScriptEditorUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            textFlow
                .drop(1) // skip the seed value; onInitialText already handled the opening text
                .debounce(DEBOUNCE_MS)
                .collect { persist(it) }
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
     * Persist immediately, independent of the debounce — on background (ON_STOP) and on back.
     * Runs in [appScope] so a back-navigation that clears this ViewModel can't cancel the write.
     */
    fun flushNow() {
        val body = textFlow.value
        val id = recordId
        appScope.launch { write(id, body) }
    }

    private suspend fun persist(body: String) {
        recompute(body)
        write(recordId, body)
    }

    private suspend fun write(id: String?, body: String) = writeMutex.withLock {
        if (body == lastPersistedBody) return@withLock
        if (id == null) {
            if (body.isEmpty()) return@withLock // never-typed / empty -> no record (§6.5)
            recordId = repository.create(body) // record-creation on first character committed
        } else {
            repository.updateBody(id, body) // emptied-to-zero keeps the row -> "Untitled"
        }
        lastPersistedBody = body
    }

    private fun recompute(body: String) {
        val words = ReadTime.wordCount(body)
        _uiState.value = ScriptEditorUiState(words, ReadTime.readTimeSeconds(words))
    }

    override fun onCleared() {
        flushNow() // safety net for the last edit before the ViewModel goes away
    }

    private companion object {
        const val KEY_RECORD_ID = "recordId"
        const val DEBOUNCE_MS = 2000L
    }
}
