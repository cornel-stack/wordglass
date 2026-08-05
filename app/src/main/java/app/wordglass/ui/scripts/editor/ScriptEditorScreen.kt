package app.wordglass.ui.scripts.editor

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import app.wordglass.ui.theme.WgSpacing
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.drop

/**
 * The scripts editor (§6). The body's and title's `TextFieldState`s live here in the composable
 * (their `Saver`s restore text + cursor + selection across process death); the ViewModel owns the
 * debounce pipelines, the persisted-record id and the title-takeover flag.
 *
 * **Opening an existing script.** When the nav arg `scriptId` is present and this is the first
 * open (not a process-death restore), the VM loads the script and exposes it via `uiState.openingBody`
 * / `uiState.openingTitle`. [LaunchedEffect(uiState.openingBody)] sets the text states from those
 * values and calls `onInitialText`. For new scripts `openingBody` is set to `""` synchronously in
 * VM init, so the same LaunchedEffect fires immediately. For process-death restores `openingBody`
 * is null and `isLoadingExisting` is false — [LaunchedEffect(Unit)] handles those by calling
 * `onInitialText` with the Saver-restored body.
 *
 * **Title.** Editable (§11 `TextField`, title role). It auto-fills from the body's first line
 * until the user edits it here, then decouples ([ScriptEditorViewModel.onTitleEdited]). The mirror
 * runs only while the title is still auto; a title text change *while the title field holds focus*
 * is the takeover signal — the mirror only ever writes while the body is focused, so focus cleanly
 * separates a user edit from a programmatic mirror.
 */
@Composable
fun ScriptEditorScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ScriptEditorViewModel = hiltViewModel(),
) {
    val textState = rememberTextFieldState()
    val titleState = rememberTextFieldState()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }
    var titleFocused by remember { mutableStateOf(false) }
    // The last value we mirrored into the title, so the detector below can tell our own write from
    // a user edit — needed because a reset-to-auto re-mirror happens while the title still holds
    // focus, which the focus gate alone can't distinguish.
    var lastMirrored by remember { mutableStateOf<String?>(null) }

    // Primary opening-text path: fires for new scripts (openingBody="") and first-open existing
    // scripts (openingBody=script.body). null openingBody = process-death restore handled below.
    LaunchedEffect(uiState.openingBody) {
        val body = uiState.openingBody ?: return@LaunchedEffect
        if (body.isNotEmpty()) textState.setTextAndPlaceCursorAtEnd(body)
        viewModel.onInitialText(body)
    }
    // Process-death restore path: openingBody stays null and isLoadingExisting is false.
    // TextFieldState Saver has already restored the body text.
    LaunchedEffect(Unit) {
        if (uiState.openingBody == null && !uiState.isLoadingExisting) {
            viewModel.onInitialText(textState.text.toString())
        }
    }
    // Opening title for existing scripts with a user-owned title (mirror won't fire for those).
    LaunchedEffect(uiState.openingTitle) {
        val title = uiState.openingTitle ?: return@LaunchedEffect
        if (title.isNotEmpty()) titleState.setTextAndPlaceCursorAtEnd(title)
    }

    LaunchedEffect(textState) {
        snapshotFlow { textState.text.toString() }
            .drop(1)
            .collect { viewModel.onBodyChanged(it) }
    }
    // Mirror the body's first line into the title while it is still auto. Keyed on the flag too, so
    // a reset-to-auto re-derives immediately without waiting for the next body change.
    LaunchedEffect(textState) {
        snapshotFlow {
            textState.text.toString().substringBefore('\n').trim() to uiState.titleManuallySet
        }.collect { (firstLine, manual) ->
            if (!manual) {
                lastMirrored = firstLine
                titleState.setTextAndPlaceCursorAtEnd(firstLine)
            }
        }
    }
    // A title change *while the title field holds focus* is a user edit — except our own mirror
    // write, which we recognise by value (lastMirrored). The mirror otherwise only writes while the
    // body is focused, so the two signals together cleanly separate a user edit from a mirror.
    LaunchedEffect(titleState) {
        snapshotFlow { titleState.text.toString() }
            .drop(1)
            .collect { if (titleFocused && it != lastMirrored) viewModel.onTitleEdited(it) }
    }
    // Cursor lands in the body on entry and on restore (§6.1) — not the title.
    LaunchedEffect(Unit) { focusRequester.requestFocus() }
    // Background autosave (a debounce may have ~1.9s left when the user backgrounds).
    LifecycleEventEffect(Lifecycle.Event.ON_STOP) { viewModel.flushNow() }

    val exit = {
        viewModel.flushNow() // final autosave before leaving; runs in the app scope, so the
        onNavigateBack()     // ViewModel being cleared can't cancel it
    }
    BackHandler(onBack = exit)

    Scaffold(modifier = modifier) { insets ->
        // Traversal order body → title → count → exit → Record (§6.6): the body reads first even
        // though the title sits above it, because entry focus lands in the body and the title only
        // auto-fills. traversalIndex overrides the default top-to-bottom order for that one swap.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets)
                .imePadding()
                .semantics { isTraversalGroup = true },
        ) {
            TitleField(
                state = titleState,
                onFocusChanged = { titleFocused = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = WgSpacing.s4, vertical = WgSpacing.s2)
                    .semantics { traversalIndex = 1f },
            )
            WritingSurface(
                state = textState,
                focusRequester = focusRequester,
                modifier = Modifier
                    .weight(1f)
                    .semantics { traversalIndex = 0f },
            )
            CountLine(uiState, modifier = Modifier.semantics { traversalIndex = 2f })
            BottomActionRow(onExit = exit, modifier = Modifier.semantics { traversalIndex = 3f })
        }
    }
}
