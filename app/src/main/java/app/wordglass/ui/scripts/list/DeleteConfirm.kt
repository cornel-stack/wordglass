package app.wordglass.ui.scripts.list

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import app.wordglass.data.model.ScriptDisplay
import app.wordglass.ui.theme.WgShape

/**
 * DeleteConfirm (handoff §7, design DeleteConfirm.md). The destructive confirmation for a script —
 * a dialog, not the take-style undo: a script is text that syncs, so the cost lands *before* the
 * action (§7.1).
 *
 * Copy is all `[NEW · APPROVED 2026-08-04]`. The echoed title is the only variable — and the only
 * overflow case — truncated at ~40 chars with the **ellipsis inside the closing quote** so it always
 * reads as a quotation (§7.3). Both actions are **outlined** at 88 × 48: Cancel (`outline` border,
 * `onSurface` label) left, Delete (`error` border, `error` label) right (§7.4). Focus lands on
 * **Cancel** — one stray confirm should keep the script (§7.5).
 *
 * Buttons use the native `dismissButton` (Cancel) + `confirmButton` (Delete) slots so M3's
 * `AlertDialogFlowRow` handles the 200% stacking: Cancel is placed first in the FlowRow → it
 * appears left at normal scale, above Delete when the row wraps at large text scale (§7.5).
 *
 * FLAGGED (§10.4): the scrim here is Material3's default (~0.32), **not** the `scrim.modal` 0.60
 * that must match ScriptStart's across both system bars. ScriptStart is slice 06, so there is
 * nothing to match against yet — the exact-0.60 full-window scrim is deferred until then. TalkBack
 * reads the untruncated title without quote marks (§7.5).
 */
@Composable
fun DeleteConfirm(
    title: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val display = ScriptDisplay.title(title)
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        shape = WgShape.lg,
        title = { Text("Delete this script?", style = MaterialTheme.typography.titleLarge) },
        text = {
            Text(
                text = "\"${echoTitle(display)}\" will be removed from all your devices. " +
                    "This can't be undone.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.semantics {
                    // §7.5: TalkBack reads the full untruncated title WITHOUT the quote marks.
                    contentDescription =
                        "$display will be removed from all your devices. This can't be undone."
                },
            )
        },
        // §7.4: Cancel left/above, Delete right/below. §7.5: focus lands on Cancel (dismissButton
        // is first in M3's AlertDialogFlowRow, so it receives initial accessibility focus).
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = WgShape.sm,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ),
                modifier = Modifier.semantics { contentDescription = "Cancel. Keep this script." },
            ) { Text("Cancel") }
        },
        confirmButton = {
            OutlinedButton(
                onClick = onConfirm,
                shape = WgShape.sm,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error,
                ),
                modifier = Modifier.semantics {
                    contentDescription = "Delete this script permanently."
                },
            ) { Text("Delete") }
        },
    )
}

/** ~40-char cap with the ellipsis inside the quote (the quote marks live in the sentence). */
private fun echoTitle(title: String): String =
    if (title.length <= ECHO_MAX) title else title.take(ECHO_MAX).trimEnd() + "…"

private const val ECHO_MAX = 40
