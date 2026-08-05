package app.wordglass.ui.components

import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import app.wordglass.ui.theme.WgIcons
import app.wordglass.ui.theme.WordglassTheme

/** `@Preview` for the shared `EmptyState` — the ScriptList empty state (slice 01, one action). */
@Preview(name = "EmptyState · ScriptList (slice 01)", showBackground = true, backgroundColor = 0xFF0B0C0E)
@Composable
private fun PreviewEmptyState() = WordglassTheme {
    EmptyState(
        glyph = WgIcons.Description,
        message = "No scripts yet. Write your first one.",
    ) {
        OutlinedButton(onClick = {}) { Text("Write your own") }
    }
}
