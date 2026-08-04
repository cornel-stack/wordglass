package app.wordglass.ui.scripts.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/**
 * The Record button in `STATE · RESERVED` (§6.3, design-system §14): rendered in its permanent
 * position before slice 02 wires it. Container `surfaceContainerLow`, **no outline** (the absence
 * is what says "not a button yet"), label + dot in `onSurfaceVariant` — never the record red, and
 * never `onSurfaceDisabled`. Announced as unavailable, never skipped in the focus order.
 * Slice 02 changes only the fill / colours / description — not the layout.
 */
@Composable
fun ReservedAction(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .semantics {
                disabled()
                contentDescription =
                    "Record. Not available yet — recording arrives in a later version."
            }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(
            Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.onSurfaceVariant), // NOT record red
        )
        Spacer(Modifier.width(8.dp))
        Text(text = "Record", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
