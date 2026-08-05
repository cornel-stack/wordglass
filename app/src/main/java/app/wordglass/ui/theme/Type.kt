package app.wordglass.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import app.wordglass.R

/**
 * Typography — the screen type scale from `design-system.md` §3. **IBM Plex Sans** for text,
 * **IBM Plex Mono** for tabular numerals. Both are **bundled in the APK** (`res/font`), never
 * downloadable: a prompter that renders in a fallback face because the device is offline is a
 * broken prompter (§3, local-first).
 *
 * Line height is set per role group as a multiple of the size: body roles 1.5×, label roles
 * 1.33×, numeric roles a tight 1.2× (single-line counters). Weights: Regular 400, Medium 500,
 * SemiBold 600 — the three bundled Sans weights.
 *
 * Two tiers Material 3 has no slot for live in [WordglassExtraTypography], read via
 * `MaterialTheme.wgType` (Theme.kt):
 * - the **numeric** styles (Plex Mono, tabular) — the count line's figures;
 * - the **prompter** tier (§3), which sits above M3's whole scale. Reserved here; its default is
 *   ⚠ pending on-device validation at slice 02.
 */

val PlexSans = FontFamily(
    Font(R.font.ibm_plex_sans_regular, FontWeight.Normal),
    Font(R.font.ibm_plex_sans_medium, FontWeight.Medium),
    Font(R.font.ibm_plex_sans_semibold, FontWeight.SemiBold),
)

val PlexMono = FontFamily(
    Font(R.font.ibm_plex_mono_regular, FontWeight.Normal),
    Font(R.font.ibm_plex_mono_medium, FontWeight.Medium),
)

/** Screen type scale (§3). Sizes and line heights verbatim; every family is Plex Sans. */
val WordglassTypography = Typography(
    displayLarge = TextStyle(fontFamily = PlexSans, fontWeight = FontWeight.SemiBold, fontSize = 44.sp, lineHeight = 51.sp),
    headlineLarge = TextStyle(fontFamily = PlexSans, fontWeight = FontWeight.SemiBold, fontSize = 30.sp, lineHeight = 36.sp),
    headlineMedium = TextStyle(fontFamily = PlexSans, fontWeight = FontWeight.Medium, fontSize = 24.sp, lineHeight = 30.sp),
    titleLarge = TextStyle(fontFamily = PlexSans, fontWeight = FontWeight.Medium, fontSize = 20.sp, lineHeight = 26.sp),
    titleMedium = TextStyle(fontFamily = PlexSans, fontWeight = FontWeight.Medium, fontSize = 17.sp, lineHeight = 23.sp),
    bodyLarge = TextStyle(fontFamily = PlexSans, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = PlexSans, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 21.sp),
    labelLarge = TextStyle(fontFamily = PlexSans, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 19.sp),
    labelMedium = TextStyle(fontFamily = PlexSans, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp),
    labelSmall = TextStyle(fontFamily = PlexSans, fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 15.sp),
)

/**
 * The tiers M3 has no slot for. `numeric*` are Plex Mono with `tnum` tabular figures so counters
 * do not jitter as they settle. The prompter values are sizes for the slice-02 reading surface.
 */
@Immutable
data class WordglassExtraTypography(
    val numericMedium: TextStyle = TextStyle(
        fontFamily = PlexMono, fontWeight = FontWeight.Medium,
        fontSize = 14.sp, lineHeight = 17.sp, fontFeatureSettings = "tnum",
    ),
    val numericLarge: TextStyle = TextStyle(
        fontFamily = PlexMono, fontWeight = FontWeight.Medium,
        fontSize = 20.sp, lineHeight = 24.sp, fontFeatureSettings = "tnum",
    ),
    // Prompter tier (§3) — reserved for slice 02. default is ⚠ pending on-device validation.
    val prompterMinSp: Int = 28,
    val prompterDefaultSp: Int = 44,
    val prompterMaxSp: Int = 72,
    val prompter: TextStyle = TextStyle(
        fontFamily = PlexSans, fontWeight = FontWeight.Medium,
        fontSize = 44.sp, lineHeight = 66.sp, letterSpacing = 0.44.sp,
    ),
)

val LocalWordglassExtraTypography = staticCompositionLocalOf { WordglassExtraTypography() }
