package app.wordglass.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

/**
 * Non-colour, non-type tokens from `design-system.md`: spacing (§6), shape (§7), elevation (§8),
 * motion (§9) and the fixed component dimensions (§10). These are compile-time constants — they
 * do not vary by theme — so they are plain objects, not `CompositionLocal`s. Nothing outside a
 * token file may hardcode a dp, a radius or a duration.
 */

/** Spacing scale (§6), 4dp base. Named by step, matching `space.N` in the design system. */
object WgSpacing {
    val s0 = 0.dp
    val s1 = 4.dp
    val s2 = 8.dp
    val s3 = 12.dp
    val s4 = 16.dp
    val s5 = 20.dp
    val s6 = 24.dp
    val s8 = 32.dp
    val s10 = 40.dp
    val s12 = 48.dp
    val s16 = 64.dp
}

/** Shape scale (§7). `full` is the record button only — nothing else. */
object WgShape {
    val none = RoundedCornerShape(0.dp)
    val xs = RoundedCornerShape(2.dp)
    val sm = RoundedCornerShape(4.dp)
    val md = RoundedCornerShape(8.dp)
    val lg = RoundedCornerShape(12.dp)
    val full = RoundedCornerShape(999.dp)
}

/** Fixed component dimensions (§10). Each is also a touch target, not merely a visual height. */
object WgDimen {
    val rowScript = 88.dp
    val rowAction = 72.dp
    val fabStandard = 56.dp
    val iconSize = 24.dp
    val iconSizeLarge = 48.dp
}

/** Motion (§9): two durations, two easings. Durations in milliseconds. */
object WgMotion {
    const val fastMs = 120
    const val standardMs = 240
    val easingStandard = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
    val easingDecelerate = CubicBezierEasing(0.0f, 0.0f, 0.0f, 1.0f)
}
