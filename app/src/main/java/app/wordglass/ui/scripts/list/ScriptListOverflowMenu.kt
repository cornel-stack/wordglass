package app.wordglass.ui.scripts.list

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import app.wordglass.ui.theme.WgShape

/**
 * The `ScriptList` overflow menu (handoff §4.6, B1) — an M3 `DropdownMenu` anchored to a row's
 * `···`. One item in slice 01: **Delete**.
 *
 * Container `surfaceContainerHigh`, `shape.sm`, a **1 dp `outline`** edge (load-bearing — the sole
 * thing identifying the menu's bounds, §4 sole-identifier rule). **No scrim** — the anchor keeps
 * context. Item label `bodyLarge` on `onSurface`, **not** `error`: a one-item menu two taps from the
 * act carries no red; the weight lands in the dialog (§4.6).
 *
 * **Anchoring (§4.6, fixed D2):** the menu's trailing edge aligns to the `···`'s trailing edge.
 * `DropdownMenu` anchors to its parent `Box`'s top-left by default, so a negative x-offset of
 * `MENU_MIN_WIDTH − ANCHOR_SIZE` shifts the menu leftward until its right edge coincides with the
 * `···`'s right edge. The anchor `Box` is the device's required 48 dp tap target.
 */
@Composable
fun ScriptListOverflowMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        shape = WgShape.sm,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        offset = DpOffset(x = -(MENU_MIN_WIDTH - ANCHOR_SIZE), y = 0.dp),
        modifier = modifier
            .width(MENU_MIN_WIDTH)
            .border(1.dp, MaterialTheme.colorScheme.outline, WgShape.sm),
    ) {
        DropdownMenuItem(
            text = {
                Text(
                    "Delete",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface, // NOT error (§4.6)
                )
            },
            onClick = onDelete,
        )
    }
}

// ANCHOR_SIZE must match the Box in ScriptRow — both are the §4.7 48 dp carved-out target.
private val MENU_MIN_WIDTH = 112.dp
private val ANCHOR_SIZE = 48.dp
