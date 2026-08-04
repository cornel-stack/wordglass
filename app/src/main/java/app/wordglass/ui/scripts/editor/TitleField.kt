package app.wordglass.ui.scripts.editor

import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import app.wordglass.ui.theme.WgShape

/**
 * The script title — an editable single-line field (§11 `TextField` in its title role). It
 * auto-fills from the body's first line until the user edits it here; the coordination and the
 * takeover flag live in the ViewModel and [ScriptEditorScreen].
 *
 * §11 title-role styling: fill `surfaceContainerLowest`, a load-bearing `outline` stroke that is
 * the sole thing identifying it as editable (no label, no placeholder), `shape.sm`; focus swaps
 * the stroke to `primary`. Single line — overflow scrolls while editing; the list row ellipsizes.
 *
 * Text token `titleLarge` — decided for **consistency**: the same derived string is the list row's
 * primary line (`ScriptRow`) and DeleteConfirm's echo, and matching weight across all three is what
 * makes the derivation legible as a rule (Addendum 2026-08-05).
 *
 * TODO (tracked in design/DEFERRED.md): when the shared §11 `TextField` component lands, this
 * should use it rather than styling an `OutlinedTextField` inline — two implementations will drift.
 */
@Composable
fun TitleField(
    state: TextFieldState,
    onFocusChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        state = state,
        lineLimits = TextFieldLineLimits.SingleLine,
        textStyle = MaterialTheme.typography.titleLarge,
        shape = WgShape.sm,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            cursorColor = MaterialTheme.colorScheme.primary,
        ),
        modifier = modifier.onFocusChanged { onFocusChanged(it.isFocused) },
    )
}
