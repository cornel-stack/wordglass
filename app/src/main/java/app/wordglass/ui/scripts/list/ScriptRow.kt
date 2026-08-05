package app.wordglass.ui.scripts.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import app.wordglass.data.model.ReadTime
import app.wordglass.data.model.RelativeDate
import app.wordglass.data.model.ScriptDisplay
import app.wordglass.ui.theme.WgDimen
import app.wordglass.ui.theme.WgIcon
import app.wordglass.ui.theme.WgIcons
import app.wordglass.ui.theme.WgSpacing
import app.wordglass.ui.theme.wgType

/**
 * A `ScriptList` row (handoff §2.1 `script` config, §4.4). Content-driven height with `row.script`
 * (88 dp) as the minimum:
 *
 * ```
 * space.3 top · title (bodyLarge/onSurface, ≤2 lines, clamp on line 2) · space.1 ·
 * metadata (labelSmall/onSurfaceVariant, one line) · space.3 bottom
 * ```
 *
 * Metadata is `[relative date] · [read time]` with the read-time **numerals in numericMedium**
 * (Plex Mono, tabular) so figures do not jitter between rows. The `···` is a 48 dp square target
 * **carved out of** the row's target — the two are siblings, so the targets never overlap (§4.7).
 *
 * A11y: the title + metadata read as **one node** carrying the full untruncated title (the two-line
 * clamp is sighted-only); the `···` is a separate node naming its script (§4.7). The `action`
 * config (ScriptStart, slice 06) is not built here — see design/DEFERRED.md.
 *
 * Focus return (§8 rows 8, 9, 10): [shouldFocusOverflow] causes the `···` to request focus once,
 * then [onFocusHandled] clears the flag in the parent so it does not repeat on recomposition.
 */
@Composable
fun ScriptRow(
    title: String,
    updatedAtMillis: Long,
    readTimeSeconds: Int,
    nowMillis: Long,
    onOpen: () -> Unit,
    onOverflow: () -> Unit,
    menuExpanded: Boolean,
    onMenuDismiss: () -> Unit,
    onDelete: () -> Unit,
    shouldFocusOverflow: Boolean = false,
    onFocusHandled: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val display = ScriptDisplay.title(title)
    val relativeDate = RelativeDate.format(updatedAtMillis, java.time.Instant.ofEpochMilli(nowMillis))
    val rowDescription = "$display, $relativeDate, ${ReadTime.spoken(readTimeSeconds)} to read aloud."
    val overflowFocusRequester = remember { FocusRequester() }

    LaunchedEffect(shouldFocusOverflow) {
        if (shouldFocusOverflow) {
            overflowFocusRequester.requestFocus()
            onFocusHandled()
        }
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .defaultMinSize(minHeight = WgDimen.rowScript)
                .wrapContentHeight(Alignment.CenterVertically)
                .clickable(onClick = onOpen)
                .padding(start = WgSpacing.s4, top = WgSpacing.s3, bottom = WgSpacing.s3)
                .semantics(mergeDescendants = true) { contentDescription = rowDescription },
        ) {
            Text(
                text = display,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.clearAndSetSemantics { }, // the merged row node carries the title
            )
            Text(
                text = metadata(relativeDate, readTimeSeconds),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                // §4.7 at 200%: the metadata line wraps rather than truncating; no maxLines cap.
                modifier = Modifier
                    .padding(top = WgSpacing.s1)
                    .clearAndSetSemantics { },
            )
        }
        // 48 dp square target, carved out of the row target (sibling, never overlapping). It also
        // anchors the overflow menu — a DropdownMenu positions relative to its parent Box.
        Box(
            modifier = Modifier
                .size(OVERFLOW_TARGET)
                .focusRequester(overflowFocusRequester)
                .clickable(onClick = onOverflow)
                .semantics { contentDescription = "More options for $display." },
            contentAlignment = Alignment.Center,
        ) {
            WgIcon(
                glyph = WgIcons.MoreVert,
                contentDescription = null, // the target already names its object
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            ScriptListOverflowMenu(
                expanded = menuExpanded,
                onDismiss = onMenuDismiss,
                onDelete = onDelete,
            )
        }
    }
}

/** `[relative date] · [read time]` with the read-time numerals in numericMedium (mono tabular). */
@Composable
private fun metadata(relativeDate: String, readTimeSeconds: Int): AnnotatedString {
    val mono = MaterialTheme.wgType.numericMedium
    val readTime = ReadTime.format(readTimeSeconds) // "≈ 47 sec"
    fun isNumeric(c: Char) = c.isDigit() || c == ','
    return buildAnnotatedString {
        append(relativeDate)
        append(" · ")
        var i = 0
        while (i < readTime.length) {
            if (isNumeric(readTime[i])) {
                var j = i
                while (j < readTime.length && isNumeric(readTime[j])) j++
                val end = if (readTime[j - 1] == ',') j - 1 else j
                withStyle(mono.toSpanStyle()) { append(readTime, i, end) }
                i = end
            } else {
                append(readTime[i]); i++
            }
        }
    }
}

private val OVERFLOW_TARGET = 48.dp
