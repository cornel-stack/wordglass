package app.wordglass.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Colour tokens — the surface set from `design-system.md` §4, verbatim hex. **Dark only** — there
 * is no light set (§2). Nothing outside this file may hardcode a colour.
 *
 * Two groups:
 * - Tokens with a Material 3 `ColorScheme` slot go into [WordglassColorScheme] and are read as
 *   `MaterialTheme.colorScheme.*`.
 * - Tokens M3 has no slot for — `record`, `primaryDim`, `onSurfaceDisabled`, `warning`, `success`
 *   — live in [WordglassExtraColors] and are read as `MaterialTheme.wg.record` (see Theme.kt).
 *
 * The over-camera set (§5) is deliberately absent: it is validated against a live preview at
 * slice 02 and defined then, not guessed here.
 */

// Surfaces (§4)
private val Surface = Color(0xFF0B0C0E)
private val SurfaceContainerLowest = Color(0xFF08090A)
private val SurfaceContainerLow = Color(0xFF101215)
private val SurfaceContainer = Color(0xFF16181C)
private val SurfaceContainerHigh = Color(0xFF1D2024)
private val SurfaceContainerHighest = Color(0xFF25282D)

// Content (§4)
private val OnSurface = Color(0xFFE8EAED)
private val OnSurfaceVariant = Color(0xFF9BA1A8)
private val Outline = Color(0xFF767C85)
private val OutlineVariant = Color(0xFF3A3F45)

// Accent (§4)
private val Primary = Color(0xFF5EC8DC)
private val OnPrimary = Color(0xFF00323C)
private val PrimaryContainer = Color(0xFF004A57)
private val OnPrimaryContainer = Color(0xFFA8E8F5)

// Semantic (§4)
private val ErrorColor = Color(0xFFFF6B60)
private val OnError = Color(0xFF3D0906)
private val ErrorContainer = Color(0xFF4A130E)

// Scrim (§4)
private val ScrimColor = Color(0xFF000000)

/**
 * The Material 3 scheme, dark only. Slots M3 does not carry are handled by [WordglassExtraColors].
 * `background` mirrors `surface`; there is no separate app background token.
 */
val WordglassColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    background = Surface,
    onBackground = OnSurface,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceContainer,
    onSurfaceVariant = OnSurfaceVariant,
    surfaceContainerLowest = SurfaceContainerLowest,
    surfaceContainerLow = SurfaceContainerLow,
    surfaceContainer = SurfaceContainer,
    surfaceContainerHigh = SurfaceContainerHigh,
    surfaceContainerHighest = SurfaceContainerHighest,
    outline = Outline,
    outlineVariant = OutlineVariant,
    error = ErrorColor,
    onError = OnError,
    errorContainer = ErrorContainer,
    scrim = ScrimColor,
)

/**
 * Tokens Material 3 has no colour-scheme slot for. `record` / `recordDim` are their own semantic
 * and are **never** reused for anything else (§4). Read via `MaterialTheme.wg` (Theme.kt).
 */
@Immutable
data class WordglassExtraColors(
    val onSurfaceDisabled: Color = Color(0xFF5A6068),
    val primaryDim: Color = Color(0xFF3A8494),
    val record: Color = Color(0xFFFF4438),
    val recordDim: Color = Color(0xFFB32E26),
    val warning: Color = Color(0xFFFFB84D),
    val success: Color = Color(0xFF5FD98C),
)

val LocalWordglassExtraColors = staticCompositionLocalOf { WordglassExtraColors() }
