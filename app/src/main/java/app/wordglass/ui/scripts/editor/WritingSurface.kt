package app.wordglass.ui.scripts.editor

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import app.wordglass.ui.theme.WgSpacing

/**
 * The bespoke body editing surface (§6.1). A `BasicTextField` — the unstyled editable-text
 * primitive — deliberately NOT the Material `TextField`: no border, no label, no fill, body text
 * straight on the surface. Kept as its own component so it is never accidentally wrapped.
 *
 * The [scrollState] is an explicit `rememberScrollState()` so the scroll offset lands in the
 * saved-state graph and restores across process death (§6.4). A self-scrolling field would not.
 *
 * `bodyLarge` (IBM Plex Sans) on `surface`, `space.4` horizontal margin, 1.5× leading — the
 * leading rides in the `bodyLarge` token (24sp / 16sp), not a local override.
 */
@Composable
fun WritingSurface(
    state: TextFieldState,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    BasicTextField(
        state = state,
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .padding(horizontal = WgSpacing.s4),
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.onSurface,
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        // Default line limits are already multi-line; the explicit scrollState is what makes the
        // scroll offset saveable/restorable.
        scrollState = scrollState,
    )
}
