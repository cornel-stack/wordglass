package app.wordglass.ui.scripts.list

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.wordglass.ui.theme.WgShape

/**
 * The `ScriptList` overflow menu (handoff §4.6, B1) — an M3 `DropdownMenu` anchored to a row's
 * `···`. One item in slice 01: **Delete**.
 *
 * Container `surfaceContainerHigh`, `shape.sm`, a **1 dp `outline`** edge (load-bearing — it is the
 * sole thing identifying the menu's bounds, §4 sole-identifier rule). **No scrim** — the anchor
 * keeps context. The item label is `bodyLarge` on `onSurface`, **not** `error`: a one-item menu two
 * taps from the act carries no red; the weight lands in the dialog (§4.6).
 *
 * FLAGGED (§4.6, never verified against the canvas): the **edge margin** — the menu should keep
 * `space.4` minimum from the window's trailing edge, and open upward when the anchor is within
 * 88 dp of the window bottom. M3's `DropdownMenu` applies its own window-edge insets; whether they
 * land exactly on `space.4` is unverified until the §11 sweep on a full list.
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

private val MENU_MIN_WIDTH = 112.dp
