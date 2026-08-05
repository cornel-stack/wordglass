package app.wordglass.ui.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.style.TextAlign
import app.wordglass.ui.components.EmptyState
import app.wordglass.ui.scripts.editor.BottomActionRow
import app.wordglass.ui.scripts.editor.CountLine
import app.wordglass.ui.scripts.editor.ScriptEditorUiState
import app.wordglass.ui.scripts.editor.TitleField
import app.wordglass.ui.scripts.editor.WritingSurface
import app.wordglass.ui.scripts.list.DeleteConfirm
import app.wordglass.ui.scripts.list.ScriptRow
import app.wordglass.ui.theme.WgIcons
import app.wordglass.ui.theme.WgSpacing

/**
 * Debug-only gallery — all eleven slice-01 states reachable in one place (§11 step 1).
 * Reached by triple-tapping the "Scripts" header. Not compiled into release builds —
 * this file lives in src/debug/ only; [app.wordglass.navigation.GalleryNavBuilder] provides
 * the route in debug and a no-op in release.
 *
 * Eleven states (not twelve — ScriptStart has no code path in slice 01 and must not appear
 * here even as a placeholder; §11 asserts it is unreachable, DEFERRED.md tracks it).
 * Gallery scope ruling (§11): the gallery renders every component that EXISTS, in every
 * state. The four not yet built (Slider, Chip, Toast, Progress) are in DEFERRED.md.
 */
@Composable
fun ComponentGalleryScreen(onBack: () -> Unit) {
    var selected by remember { mutableStateOf<Int?>(null) }
    var showDeleteStandard by remember { mutableStateOf(false) }
    var showDeleteTruncated by remember { mutableStateOf(false) }

    if (showDeleteStandard) {
        DeleteConfirm(
            title = "Downtown listing walkthrough",
            onConfirm = { showDeleteStandard = false },
            onDismiss = { showDeleteStandard = false },
        )
    }
    if (showDeleteTruncated) {
        DeleteConfirm(
            title = "A script title that is deliberately long enough to trigger the ellipsis inside the closing quote in the dialog body copy",
            onConfirm = { showDeleteTruncated = false },
            onDismiss = { showDeleteTruncated = false },
        )
    }

    when (selected) {
        null -> GalleryMenu(onBack = onBack, onSelect = { idx ->
            when (idx) {
                10 -> showDeleteStandard = true
                11 -> showDeleteTruncated = true
                else -> selected = idx
            }
        })
        1 -> State01Empty(onBack = { selected = null })
        2 -> State02Populated(onBack = { selected = null })
        3 -> State03Overflowing(onBack = { selected = null })
        4 -> State04OverflowMenu(onBack = { selected = null })
        5 -> State05EditorNew(onBack = { selected = null })
        6 -> State06EditorEditing(label = "06 · ScriptEditor · editing", onBack = { selected = null })
        7 -> State06EditorEditing(label = "07 · ScriptEditor · autosaved  (= 06 by design)", onBack = { selected = null })
        8 -> State06EditorEditing(label = "08 · ScriptEditor · interrupted  (= 06 by design)", onBack = { selected = null })
        9 -> State09EditorKbdDismissed(onBack = { selected = null })
        else -> selected = null
    }
}

private val ENTRIES = listOf(
    1 to "01 · ScriptList · empty",
    2 to "02 · ScriptList · populated",
    3 to "03 · ScriptList · overflowing",
    4 to "04 · ScriptList · overflow menu",
    5 to "05 · ScriptEditor · new",
    6 to "06 · ScriptEditor · editing",
    7 to "07 · ScriptEditor · autosaved  (= 06)",
    8 to "08 · ScriptEditor · interrupted  (= 06)",
    9 to "09 · ScriptEditor · keyboard dismissed",
    10 to "10 · DeleteConfirm · standard  [dialog]",
    11 to "11 · DeleteConfirm · truncated title  [dialog]",
)

@Composable
private fun GalleryMenu(onBack: () -> Unit, onSelect: (Int) -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(
                top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + WgSpacing.s6,
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
            ),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = WgSpacing.s4, vertical = WgSpacing.s2),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Component Gallery", style = MaterialTheme.typography.headlineLarge)
            OutlinedButton(onClick = onBack) { Text("← Back") }
        }
        LazyColumn(contentPadding = PaddingValues(vertical = WgSpacing.s4)) {
            items(ENTRIES, key = { it.first }) { (idx, label) ->
                Surface(
                    onClick = { onSelect(idx) },
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(horizontal = WgSpacing.s4, vertical = WgSpacing.s4),
                    )
                }
                HorizontalDivider(Modifier.padding(horizontal = WgSpacing.s4))
            }
        }
    }
}

// ---------- individual state renderers ----------

@Composable
private fun State01Empty(onBack: () -> Unit) =
    GalleryFrame(label = "01 · ScriptList · empty", onBack = onBack) {
        EmptyState(
            glyph = WgIcons.Description,
            message = "No scripts yet. Write your first one.",
            modifier = Modifier.weight(1f),
        ) {
            OutlinedButton(onClick = {}) { Text("Write your own") }
        }
    }

private const val NOW = 1_785_890_000_000L
private const val HOUR = 3_600_000L
private const val DAY = 86_400_000L

@Composable
private fun State02Populated(onBack: () -> Unit) =
    GalleryFrame(label = "02 · ScriptList · populated", onBack = onBack) {
        val rows = listOf(
            Triple("Downtown listing walkthrough", NOW - 2 * HOUR, 54),
            Triple("Brand intro — 60 second cut", NOW - DAY, 210),
        )
        LazyColumn(Modifier.weight(1f).fillMaxWidth()) {
            items(rows) { (title, updated, secs) ->
                ScriptRow(
                    title = title, updatedAtMillis = updated, readTimeSeconds = secs,
                    nowMillis = NOW, onOpen = {}, onOverflow = {},
                    menuExpanded = false, onMenuDismiss = {}, onDelete = {},
                )
                HorizontalDivider(Modifier.padding(horizontal = WgSpacing.s4))
            }
        }
    }

@Composable
private fun State03Overflowing(onBack: () -> Unit) =
    GalleryFrame(label = "03 · ScriptList · overflowing (74-char worst-case)", onBack = onBack) {
        val base = "Annual homeowner association report for eastern district properties 2024XX"
        val rows = listOf(
            Triple(base, NOW - 2 * HOUR, 1719),
            Triple(base.replace("eastern", "western"), NOW - 3 * HOUR, 1723),
            Triple(base.replace("eastern", "northern"), NOW - 4 * HOUR, 1740),
        )
        LazyColumn(Modifier.weight(1f).fillMaxWidth()) {
            items(rows) { (title, updated, secs) ->
                ScriptRow(
                    title = title, updatedAtMillis = updated, readTimeSeconds = secs,
                    nowMillis = NOW, onOpen = {}, onOverflow = {},
                    menuExpanded = false, onMenuDismiss = {}, onDelete = {},
                )
                HorizontalDivider(Modifier.padding(horizontal = WgSpacing.s4))
            }
        }
    }

@Composable
private fun State04OverflowMenu(onBack: () -> Unit) =
    GalleryFrame(label = "04 · ScriptList · overflow menu (pre-opened)", onBack = onBack) {
        var expanded by remember { mutableStateOf(true) }
        ScriptRow(
            title = "Downtown listing walkthrough",
            updatedAtMillis = NOW - 2 * HOUR, readTimeSeconds = 54, nowMillis = NOW,
            onOpen = {}, onOverflow = { expanded = true }, menuExpanded = expanded,
            onMenuDismiss = { expanded = false }, onDelete = {},
        )
        HorizontalDivider(Modifier.padding(horizontal = WgSpacing.s4))
    }

@Composable
private fun State05EditorNew(onBack: () -> Unit) =
    GalleryFrame(label = "05 · ScriptEditor · new (empty)", onBack = onBack) {
        val bodyState = rememberTextFieldState()
        val titleState = rememberTextFieldState()
        val focusRequester = remember { FocusRequester() }
        LaunchedEffect(Unit) { focusRequester.requestFocus() }
        TitleField(
            state = titleState, onFocusChanged = {},
            modifier = Modifier.fillMaxWidth().padding(horizontal = WgSpacing.s4, vertical = WgSpacing.s2),
        )
        WritingSurface(state = bodyState, focusRequester = focusRequester, modifier = Modifier.weight(1f))
        CountLine(ScriptEditorUiState(wordCount = 0, readTimeSeconds = 0))
        BottomActionRow(onExit = onBack)
    }

private const val SAMPLE_BODY =
    "Today we're looking at a fantastic downtown property. The open-plan living space has been " +
        "fully renovated with high-end finishes throughout. Large windows flood the rooms with " +
        "natural light and the rooftop terrace offers unobstructed city views."

@Composable
private fun State06EditorEditing(label: String, onBack: () -> Unit) =
    GalleryFrame(label = label, onBack = onBack) {
        val bodyState = rememberTextFieldState()
        val titleState = rememberTextFieldState()
        val focusRequester = remember { FocusRequester() }
        LaunchedEffect(Unit) {
            bodyState.setTextAndPlaceCursorAtEnd(SAMPLE_BODY)
            titleState.setTextAndPlaceCursorAtEnd("Downtown listing walkthrough")
        }
        TitleField(
            state = titleState, onFocusChanged = {},
            modifier = Modifier.fillMaxWidth().padding(horizontal = WgSpacing.s4, vertical = WgSpacing.s2),
        )
        WritingSurface(state = bodyState, focusRequester = focusRequester, modifier = Modifier.weight(1f))
        CountLine(ScriptEditorUiState(wordCount = 43, readTimeSeconds = 18))
        BottomActionRow(onExit = onBack)
    }

@Composable
private fun State09EditorKbdDismissed(onBack: () -> Unit) =
    GalleryFrame(label = "09 · ScriptEditor · keyboard dismissed", onBack = onBack) {
        val bodyState = rememberTextFieldState()
        val titleState = rememberTextFieldState()
        val focusRequester = remember { FocusRequester() }
        LaunchedEffect(Unit) {
            bodyState.setTextAndPlaceCursorAtEnd(SAMPLE_BODY)
            titleState.setTextAndPlaceCursorAtEnd("Downtown listing walkthrough")
        }
        TitleField(
            state = titleState, onFocusChanged = {},
            modifier = Modifier.fillMaxWidth().padding(horizontal = WgSpacing.s4, vertical = WgSpacing.s2),
        )
        WritingSurface(state = bodyState, focusRequester = focusRequester, modifier = Modifier.weight(1f))
        CountLine(ScriptEditorUiState(wordCount = 43, readTimeSeconds = 18))
        // No imePadding() — simulates the keyboard-dismissed state where the action row sits at
        // space.6 + navigationBars rather than floating above the IME.
        BottomActionRow(onExit = onBack)
    }

@Composable
private fun GalleryFrame(
    label: String,
    onBack: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(
                top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
            ),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = WgSpacing.s4, vertical = WgSpacing.s2),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            OutlinedButton(onClick = onBack) { Text("← Gallery") }
        }
        HorizontalDivider()
        content()
    }
}
