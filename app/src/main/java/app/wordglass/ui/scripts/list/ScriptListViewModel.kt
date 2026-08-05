package app.wordglass.ui.scripts.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.wordglass.data.model.ReadTime
import app.wordglass.data.repository.ScriptRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * One row's worth of list data. Read-time is precomputed from the body here (§4.4 says the row
 * shows a read-time, not a word count), so the row composable stays presentational — it never sees
 * the body text. `titleRaw` is the stored title ("" for an empty body); the row substitutes
 * "Untitled" at render via [app.wordglass.data.model.ScriptDisplay].
 */
data class ScriptListItem(
    val id: String,
    val titleRaw: String,
    val updatedAt: Long,
    val readTimeSeconds: Int,
)

/**
 * `ScriptList` state (handoff §4). Observes the repository's live, tombstone-filtered, most-recent-
 * first flow (Room re-emits on any change, so delete/edit reflect with no manual refresh) and maps
 * each row to a [ScriptListItem]. Delete is a soft-delete through the repository (§10.1).
 */
@HiltViewModel
class ScriptListViewModel @Inject constructor(
    private val repository: ScriptRepository,
) : ViewModel() {

    val scripts: StateFlow<List<ScriptListItem>> =
        repository.observeScripts()
            .map { list ->
                list.map {
                    ScriptListItem(
                        id = it.id,
                        titleRaw = it.title,
                        updatedAt = it.updatedAt,
                        readTimeSeconds = ReadTime.readTimeSeconds(ReadTime.wordCount(it.body)),
                    )
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun delete(id: String) {
        viewModelScope.launch { repository.softDelete(id) }
    }
}
