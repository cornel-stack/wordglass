package app.wordglass.ui.scripts.editor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/**
 * The editor's bottom action row (§6.3): exit on the left, Record (RESERVED) on the right.
 * The visible exit label names the destination ("Scripts"); its content description names the
 * direction ("Back to scripts") — the deliberate split from §6.6. Phase B adds the outlined
 * document mark and the exact 96×72 / remaining-width geometry.
 */
@Composable
fun BottomActionRow(onExit: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Scripts",
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .clickable(onClick = onExit)
                .semantics { contentDescription = "Back to scripts" }
                .padding(horizontal = 16.dp, vertical = 12.dp),
        )
        Spacer(Modifier.weight(1f))
        ReservedAction()
    }
}
