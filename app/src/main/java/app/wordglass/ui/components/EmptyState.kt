package app.wordglass.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.wordglass.ui.theme.WgDimen
import app.wordglass.ui.theme.WgIcon
import app.wordglass.ui.theme.WgSpacing

/**
 * The shared empty-state block (design-system §11, handoff §2 `EmptyState`). A vertically-centred
 * column: glyph, `space.6`, body copy, `space.6`, then the action slot.
 *
 * The glyph is **decorative** — hidden from TalkBack, because the body copy beneath states the same
 * thing, so announcing the glyph would be redundant (design-system §11, item 0.2). One action in
 * slice 01; slice 06 adds a filled primary above it (the caller supplies the slot content, so this
 * component does not change between slices).
 */
@Composable
fun EmptyState(
    glyph: String,
    message: String,
    modifier: Modifier = Modifier,
    action: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = WgSpacing.s4),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        WgIcon(
            glyph = glyph,
            contentDescription = null, // decorative — the body copy carries the meaning
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            size = WgDimen.iconSizeLarge,
        )
        Spacer(Modifier.height(WgSpacing.s6))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = EMPTY_COPY_MAX_WIDTH),
        )
        Spacer(Modifier.height(WgSpacing.s6))
        action()
    }
}

private val EMPTY_COPY_MAX_WIDTH = 288.dp
