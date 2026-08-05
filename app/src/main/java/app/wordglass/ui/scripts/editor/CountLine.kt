package app.wordglass.ui.scripts.editor

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import app.wordglass.data.model.ReadTime
import app.wordglass.ui.theme.WgSpacing
import app.wordglass.ui.theme.wgType

/**
 * "1,247 words · ≈ 8 min 54 sec" (§6.2). Both figures settle on the debounce, not per keystroke.
 * Empty reads "0 words · ≈ 0 sec".
 *
 * The words settle in `labelMedium`; the **numerals run in `numericMedium`** (Plex Mono, tabular)
 * so they do not jitter as they land on the debounce (§6.2, design doc). The two are mixed in one
 * `AnnotatedString` — every maximal run of digits and thousands-commas takes the mono style.
 */
@Composable
fun CountLine(uiState: ScriptEditorUiState, modifier: Modifier = Modifier) {
    val words = "%,d".format(uiState.wordCount)
    val line = "$words words · ${ReadTime.format(uiState.readTimeSeconds)}"
    val spoken = "${uiState.wordCount} words, ${ReadTime.spoken(uiState.readTimeSeconds)} to read aloud"
    Text(
        text = withMonoNumerals(line),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.labelMedium,
        // §6.6: TalkBack hears the natural-language form — no "≈" symbol or "·" separator.
        modifier = modifier
            .padding(horizontal = WgSpacing.s4, vertical = WgSpacing.s1)
            .semantics { contentDescription = spoken },
    )
}

/** Wrap each maximal run of digits (and the thousands separators inside them) in numericMedium. */
@Composable
private fun withMonoNumerals(text: String): AnnotatedString {
    val mono = MaterialTheme.wgType.numericMedium
    fun isNumeric(c: Char) = c.isDigit() || c == ','
    return buildAnnotatedString {
        var i = 0
        while (i < text.length) {
            if (isNumeric(text[i])) {
                var j = i
                while (j < text.length && isNumeric(text[j])) j++
                // A trailing comma is punctuation, not part of the number — don't style it mono.
                val end = if (text[j - 1] == ',') j - 1 else j
                withStyle(mono.toSpanStyle()) { append(text, i, end) }
                i = end
            } else {
                append(text[i])
                i++
            }
        }
    }
}
