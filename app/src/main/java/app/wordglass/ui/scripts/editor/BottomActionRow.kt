package app.wordglass.ui.scripts.editor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import app.wordglass.ui.theme.WgIcon
import app.wordglass.ui.theme.WgIcons
import app.wordglass.ui.theme.WgSpacing

/**
 * The editor's bottom action row (§6.3): exit on the left, Record (RESERVED) on the right.
 *
 * **Exit** — the "Scripts" label beneath an **outlined document mark** (Material Symbols Outlined
 * `description`, `icon.size`, `onSurfaceVariant`), in a 96 × 72 dp target with **no container**.
 * The visible label names the destination; the whole target's content description names the
 * direction ("Back to scripts"), read as one node — the deliberate split from §6.6.
 *
 * Geometry from §6.3: the row is 72 dp tall; Record takes the remaining width. The dimensions come
 * from the handoff's per-screen geometry (design-system §11 places button geometry in the screen
 * handoff, not the token file).
 */
@Composable
fun BottomActionRow(onExit: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(ACTION_ROW_HEIGHT)
            .padding(horizontal = WgSpacing.s4),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .width(EXIT_WIDTH)
                .fillMaxHeight()
                .clickable(onClick = onExit)
                .clearAndSetSemantics { contentDescription = "Back to scripts" },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            WgIcon(
                glyph = WgIcons.Description,
                contentDescription = null, // decorative — the target names the direction
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(WgSpacing.s1))
            Text(
                text = "Scripts",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelLarge,
            )
        }
        ReservedAction(modifier = Modifier.weight(1f).fillMaxHeight())
    }
}

// §6.3 per-screen geometry (handoff, not a design-system token).
private val ACTION_ROW_HEIGHT = 72.dp
private val EXIT_WIDTH = 96.dp
