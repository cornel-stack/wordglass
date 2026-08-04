package app.wordglass.ui.scripts.editor

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.wordglass.data.model.ReadTime

/**
 * "1,247 words · ≈ 8 min 54 sec" (§6.2). Both figures settle on the debounce, not per keystroke.
 * Empty reads "0 words · ≈ 0 sec". Phase B applies the labelMedium / numericMedium token split.
 */
@Composable
fun CountLine(uiState: ScriptEditorUiState, modifier: Modifier = Modifier) {
    val words = "%,d".format(uiState.wordCount)
    Text(
        text = "$words words · ${ReadTime.format(uiState.readTimeSeconds)}",
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.labelMedium,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 4.dp),
    )
}
