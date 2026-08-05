package app.wordglass.ui.scripts.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.wordglass.ui.components.EmptyState
import app.wordglass.ui.theme.WgDimen
import app.wordglass.ui.theme.WgIcon
import app.wordglass.ui.theme.WgIcons
import app.wordglass.ui.theme.WgShape
import app.wordglass.ui.theme.WgSpacing

/**
 * `ScriptList` (handoff §4) — the app's home for a solo user: find, open, create, delete scripts.
 *
 * Empty → the shared `EmptyState` (one button in slice 01). Populated → the rows newest-first, a
 * 1 dp `outlineVariant` divider between them, inset to `space.4`. The FAB creates; row tap opens;
 * the row `···` opens the overflow menu → Delete → `DeleteConfirm`.
 *
 * **Insets (§4.1, edge-to-edge), counted once each:**
 * - header top = `statusBars` + `space.6`;
 * - the list is `weight(1f)` — it fills the space *below the header*, its bottom at the window edge,
 *   so its bottom content padding is measured from there;
 * - list bottom content padding = `space.24` + `navigationBars` (56 + 24 + 16 + navBars);
 * - FAB bottom margin = `space.6` + `navigationBars`.
 *
 * With those, the last row scrolled to the very end clears the FAB's top edge by exactly `space.4`
 * (16 dp) — the §4.1 derivation. The FAB is placed **explicitly** (not via Scaffold's FAB slot, whose
 * private `FabSpacing` is not `space.6`), so the offset is exactly what §4.1 specifies.
 *
 * **Focus returns (§8 rows 8, 9, 10):**
 * - Row 8 (menu dismissed): [focusOverflowFor] is set to the row's id → that row's `···` requests
 *   focus via [ScriptRow.shouldFocusOverflow].
 * - Row 9 (delete confirmed): the row that takes the deleted row's position gets the focus request;
 *   if the list is now empty, the empty-state button gets it via [emptyActionFocusRequester].
 * - Row 10 (DeleteConfirm cancelled/dismissed): same as row 8 — focus returns to the `···` of the
 *   item that initiated the delete chain.
 *
 * FLAGGED (visual, cannot be settled from a11y bounds — needs the §11 screenshot pass): rows divided
 * by a **line vs a container** (built as a line per §4.4).
 */
@Composable
fun ScriptListScreen(
    onNewScript: () -> Unit,
    onOpenScript: (String) -> Unit,
    modifier: Modifier = Modifier,
    // Null in release builds (GalleryNavBuilder stub returns null); non-null in debug only.
    onOpenGallery: (() -> Unit)? = null,
    viewModel: ScriptListViewModel = hiltViewModel(),
) {
    val scripts by viewModel.scripts.collectAsStateWithLifecycle()
    val now = remember { System.currentTimeMillis() }
    var expandedRowId by remember { mutableStateOf<String?>(null) }
    var deleting by remember { mutableStateOf<ScriptListItem?>(null) }

    // Debug gallery trigger — triple-tap the "Scripts" title. Counter auto-resets after 600 ms of
    // inactivity so accidental taps don't accumulate across time.
    var galleryTaps by remember { mutableIntStateOf(0) }
    LaunchedEffect(galleryTaps) {
        if (galleryTaps in 1..2) {
            delay(600L)
            galleryTaps = 0
        }
    }

    // Focus-return state (§8 rows 8, 9, 10).
    var focusOverflowFor by remember { mutableStateOf<String?>(null) }
    var focusEmptyAction by remember { mutableStateOf(false) }
    val emptyActionFocusRequester = remember { FocusRequester() }

    // §8 row 9, empty-state branch: fire once the list is actually empty and the button is composed.
    LaunchedEffect(scripts.isEmpty(), focusEmptyAction) {
        if (focusEmptyAction && scripts.isEmpty()) {
            emptyActionFocusRequester.requestFocus()
            focusEmptyAction = false
        }
    }

    Box(modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            Text(
                text = "Scripts",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(
                        start = WgSpacing.s4,
                        end = WgSpacing.s4,
                        top = statusBarsTop() + WgSpacing.s6,
                        bottom = WgSpacing.s3,
                    )
                    .then(
                        // Triple-tap the title to open the component gallery. The gesture is only
                        // wired in debug builds — onOpenGallery is null in release (source set stub).
                        if (onOpenGallery != null) {
                            Modifier.clickable(
                                indication = null,
                                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                            ) {
                                galleryTaps++
                                if (galleryTaps >= 3) {
                                    galleryTaps = 0
                                    onOpenGallery()
                                }
                            }
                        } else Modifier
                    ),
            )

            if (scripts.isEmpty()) {
                EmptyState(
                    glyph = WgIcons.Description,
                    message = "No scripts yet. Write your first one.",
                    modifier = Modifier.weight(1f),
                ) {
                    OutlinedButton(
                        onClick = onNewScript,
                        modifier = Modifier.focusRequester(emptyActionFocusRequester),
                    ) { Text("Write your own") }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    // §4.1 derivation, from real tokens: fab.standard + space.6 + space.4 + navBars.
                    // (The handoff calls this sum "space.24", but that is NOT a design-system token —
                    // the §6 scale ends at space.16 — so it is expressed here as the derivation, not
                    // an invented 96 dp token. Flagged for spec reconciliation.)
                    contentPadding = PaddingValues(
                        bottom = WgDimen.fabStandard + WgSpacing.s6 + WgSpacing.s4 + navigationBarsBottom(),
                    ),
                ) {
                    items(scripts, key = { it.id }) { item ->
                        ScriptRow(
                            title = item.titleRaw,
                            updatedAtMillis = item.updatedAt,
                            readTimeSeconds = item.readTimeSeconds,
                            nowMillis = now,
                            onOpen = { onOpenScript(item.id) },
                            onOverflow = { expandedRowId = item.id },
                            menuExpanded = expandedRowId == item.id,
                            onMenuDismiss = {
                                // §8 row 8: menu dismissed without action → focus returns to ···
                                focusOverflowFor = item.id
                                expandedRowId = null
                            },
                            onDelete = {
                                // §8 row 7: menu Delete → DeleteConfirm (no focus change yet)
                                expandedRowId = null
                                deleting = item
                            },
                            shouldFocusOverflow = focusOverflowFor == item.id,
                            onFocusHandled = { focusOverflowFor = null },
                        )
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant,
                            modifier = Modifier.padding(horizontal = WgSpacing.s4),
                        )
                    }
                }
            }
        }

        // FAB placed explicitly (not Scaffold's slot): bottom margin = space.6 + navigationBars,
        // end margin = space.4 (§4.1). Present in both states; in the empty state it is redundant
        // with the button and skipped in the focus order (§4.2).
        FloatingActionButton(
            onClick = onNewScript,
            shape = WgShape.sm,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = WgSpacing.s4, bottom = WgSpacing.s6 + navigationBarsBottom())
                // §4.2: in the empty state the FAB is redundant with the button and is skipped in
                // the focus order — clearAndSetSemantics removes it from TalkBack traversal.
                .then(
                    if (scripts.isEmpty()) Modifier.clearAndSetSemantics { }
                    else Modifier.semantics { contentDescription = "New script." }
                ),
        ) {
            WgIcon(glyph = WgIcons.Add, contentDescription = null)
        }
    }

    deleting?.let { item ->
        DeleteConfirm(
            title = item.titleRaw,
            onConfirm = {
                // §8 row 9: delete confirmed → focus to the row that takes the deleted position.
                val delIndex = scripts.indexOfFirst { it.id == item.id }
                viewModel.delete(item.id)
                if (scripts.size == 1) {
                    // The list will be empty — focus the empty state's action button.
                    focusEmptyAction = true
                } else {
                    // The item at delIndex + 1 moves into this position; if this was the last item,
                    // the item at delIndex - 1 is now the last.
                    val nextId = if (delIndex < scripts.size - 1) {
                        scripts[delIndex + 1].id
                    } else {
                        scripts[delIndex - 1].id
                    }
                    focusOverflowFor = nextId
                }
                deleting = null
            },
            onDismiss = {
                // §8 row 10: cancel / scrim / back → focus returns to the originating ···
                focusOverflowFor = item.id
                deleting = null
            },
        )
    }
}

@Composable
private fun statusBarsTop() =
    WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

@Composable
private fun navigationBarsBottom() =
    WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
