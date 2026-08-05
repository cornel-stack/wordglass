package app.wordglass.ui.scripts.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import app.wordglass.ui.theme.WordglassTheme

/**
 * `@Preview`s for the `ScriptList` components — one per state (build-plan: every component with a
 * `@Preview` per state). Each is wrapped in `WordglassTheme` so it renders against the real dark
 * token set. Debug-only; no effect on the release build.
 *
 * `nowMillis` is fixed (`Date.now()` is unavailable in this environment) so the relative dates are
 * stable across preview renders.
 */
private const val NOW = 1_785_890_000_000L
private const val HOUR = 3_600_000L
private const val DAY = 86_400_000L

@Preview(name = "Row · normal", showBackground = true, backgroundColor = 0xFF0B0C0E)
@Composable
private fun PreviewRowNormal() = WordglassTheme {
    RowSurface {
        ScriptRow(
            title = "Downtown listing walkthrough",
            updatedAtMillis = NOW - 2 * HOUR,
            readTimeSeconds = 54,
            nowMillis = NOW,
            onOpen = {}, onOverflow = {}, menuExpanded = false, onMenuDismiss = {}, onDelete = {},
        )
    }
}

@Preview(name = "Row · untitled (empty body)", showBackground = true, backgroundColor = 0xFF0B0C0E)
@Composable
private fun PreviewRowUntitled() = WordglassTheme {
    RowSurface {
        ScriptRow(
            title = "", // empty body -> "Untitled" at render
            updatedAtMillis = NOW - DAY,
            readTimeSeconds = 0,
            nowMillis = NOW,
            onOpen = {}, onOverflow = {}, menuExpanded = false, onMenuDismiss = {}, onDelete = {},
        )
    }
}

@Preview(name = "Row · overflowing 74-char title", showBackground = true, backgroundColor = 0xFF0B0C0E)
@Composable
private fun PreviewRowOverflowing() = WordglassTheme {
    RowSurface {
        ScriptRow(
            title = "A".repeat(74),
            updatedAtMillis = NOW - 40 * DAY,
            readTimeSeconds = 1719, // 4,012 words -> ≈ 28 min 39 sec
            nowMillis = NOW,
            onOpen = {}, onOverflow = {}, menuExpanded = false, onMenuDismiss = {}, onDelete = {},
        )
    }
}

@Preview(name = "DeleteConfirm", showBackground = true, backgroundColor = 0xFF0B0C0E)
@Composable
private fun PreviewDeleteConfirm() = WordglassTheme {
    DeleteConfirm(title = "Downtown listing walkthrough", onConfirm = {}, onDismiss = {})
}

@Composable
private fun RowSurface(content: @Composable () -> Unit) =
    Column(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)) { content() }
