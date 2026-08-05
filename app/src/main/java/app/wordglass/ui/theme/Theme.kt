package app.wordglass.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

/**
 * The Wordglass theme. **Dark only, single set** — `isSystemInDarkTheme()` is never consulted and
 * there is no dynamic (Material You) colour: this is a dark *product*, not a dark *mode* (§2). A
 * light UI beside a live camera preview would blow out the user's read of their exposure.
 *
 * On top of Material 3's `colorScheme` and `typography` it provides two extension sets — the
 * colours (`record`, `primaryDim`, …) and the type styles (`numericMedium`, prompter) that M3 has
 * no slot for — reachable as `MaterialTheme.wg` and `MaterialTheme.wgType`.
 */
@Composable
fun WordglassTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalWordglassExtraColors provides WordglassExtraColors(),
        LocalWordglassExtraTypography provides WordglassExtraTypography(),
    ) {
        MaterialTheme(
            colorScheme = WordglassColorScheme,
            typography = WordglassTypography,
            content = content,
        )
    }
}

/** Colours with no Material 3 slot — `MaterialTheme.wg.record`, etc. */
val MaterialTheme.wg: WordglassExtraColors
    @Composable @ReadOnlyComposable get() = LocalWordglassExtraColors.current

/** Type styles with no Material 3 slot — `MaterialTheme.wgType.numericMedium`, etc. */
val MaterialTheme.wgType: WordglassExtraTypography
    @Composable @ReadOnlyComposable get() = LocalWordglassExtraTypography.current
