package app.wordglass.ui.scripts.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
 * the row `···` opens the overflow menu → Delete → `DeleteConfirm`. Navigation targets are lambdas
 * — real wiring lands in Phase D.
 *
 * Insets (§4.1, edge-to-edge): header top `statusBars` + `space.6`; list bottom `space.24` +
 * `navigationBars`; the FAB floats above `navigationBars`.
 *
 * FLAGGED (never verified against the canvas, surface at the §11 sweep): (1) **rows divided by a
 * line vs a container** — built as a line per §4.4; (2) **FAB clearance when fully scrolled** — the
 * list's `space.24 + navigationBars` bottom padding is meant to clear the FAB's top edge by 16 dp
 * (§4.1 derivation), verify scrolled to the very end; (3) the overflow menu's **edge margin**
 * (§4.6, see `ScriptListOverflowMenu`).
 */
@Composable
fun ScriptListScreen(
    onNewScript: () -> Unit,
    onOpenScript: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ScriptListViewModel = hiltViewModel(),
) {
    val scripts by viewModel.scripts.collectAsStateWithLifecycle()
    val now = remember { System.currentTimeMillis() }
    var expandedRowId by remember { mutableStateOf<String?>(null) }
    var deleting by remember { mutableStateOf<ScriptListItem?>(null) }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNewScript,
                shape = WgShape.sm,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    // Float above the system nav bar (§4.1 FAB offset = space.6 + navigationBars;
                    // Scaffold supplies the space.6-class edge padding, this adds navigationBars).
                    .padding(bottom = navigationBarsBottom())
                    .semantics { contentDescription = "New script." },
            ) {
                WgIcon(glyph = WgIcons.Add, contentDescription = null)
            }
        },
    ) { _ ->
        Column(Modifier.fillMaxSize()) {
            Text(
                text = "Scripts",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(
                    start = WgSpacing.s4,
                    end = WgSpacing.s4,
                    top = statusBarsTop() + WgSpacing.s6,
                    bottom = WgSpacing.s3,
                ),
            )

            if (scripts.isEmpty()) {
                app.wordglass.ui.components.EmptyState(
                    glyph = WgIcons.Description,
                    message = "No scripts yet. Write your first one.",
                ) {
                    androidx.compose.material3.OutlinedButton(onClick = onNewScript) {
                        Text("Write your own")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = WgSpacing.s16 + WgSpacing.s8 + navigationBarsBottom()),
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
                            onMenuDismiss = { expandedRowId = null },
                            onDelete = {
                                expandedRowId = null
                                deleting = item
                            },
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
    }

    deleting?.let { item ->
        DeleteConfirm(
            title = item.titleRaw,
            onConfirm = {
                viewModel.delete(item.id)
                deleting = null
            },
            onDismiss = { deleting = null },
        )
    }
}

@Composable
private fun statusBarsTop() =
    WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

@Composable
private fun navigationBarsBottom() =
    WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
