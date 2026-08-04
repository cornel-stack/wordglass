package app.wordglass.ui.scripts.editor

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.drop

/**
 * The scripts editor (§6). The body's `TextFieldState` lives here in the composable (its `Saver`
 * restores text + cursor + selection across process death); the ViewModel owns the debounce and
 * the persisted-record id.
 *
 * The title is a **read-only display** that mirrors the body's first line — not a `TextField`,
 * not a focus stop (addendum / §6.6). "Untitled" when the body is empty.
 */
@Composable
fun ScriptEditorScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ScriptEditorViewModel = hiltViewModel(),
) {
    val textState = rememberTextFieldState()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }

    // Immediate count + restore seed, once. Then feed subsequent changes to the debounce
    // (drop the initial snapshot emission — onInitialText already handled the opening text).
    LaunchedEffect(Unit) { viewModel.onInitialText(textState.text.toString()) }
    LaunchedEffect(textState) {
        snapshotFlow { textState.text.toString() }
            .drop(1)
            .collect { viewModel.onBodyChanged(it) }
    }
    // Cursor lands in the body on entry and on restore.
    LaunchedEffect(Unit) { focusRequester.requestFocus() }
    // Background autosave (a debounce may have ~1.9s left when the user backgrounds).
    LifecycleEventEffect(Lifecycle.Event.ON_STOP) { viewModel.flushNow() }

    val exit = {
        viewModel.flushNow() // final autosave before leaving; runs in the app scope, so the
        onNavigateBack()     // ViewModel being cleared can't cancel it
    }
    BackHandler(onBack = exit)

    val firstLine = textState.text.toString().substringBefore('\n').trim()

    Scaffold(modifier = modifier) { insets ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets)
                .imePadding(),
        ) {
            // Read-only, field-styled title. clearAndSetSemantics {} keeps it out of the focus
            // order and out of TalkBack — the body (focusable) already carries the first line.
            Text(
                text = firstLine.ifBlank { "Untitled" },
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .clearAndSetSemantics { },
            )
            WritingSurface(
                state = textState,
                focusRequester = focusRequester,
                modifier = Modifier.weight(1f),
            )
            CountLine(uiState)
            BottomActionRow(onExit = exit)
        }
    }
}
