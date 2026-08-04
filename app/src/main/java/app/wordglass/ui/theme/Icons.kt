package app.wordglass.ui.theme

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import app.wordglass.R

/**
 * The icon set — **Material Symbols Outlined** (design-system §13). The bundled font is the
 * variable face **pinned to the tokens** (`wght` 400 = `icon.weight`, `GRAD` 0 = `icon.grade`,
 * `opsz` 24, `FILL` 0) and **subsetted to the glyphs slice 01 uses**, so it is ~1 KB rather than
 * the 10 MB full face. Glyphs are addressed by their Material Symbols codepoint via [WgIcons].
 *
 * When a new glyph is needed, re-subset the font to add its codepoint — do not swap in the full
 * face or a second icon library.
 */
private val MaterialSymbols = FontFamily(Font(R.font.material_symbols_outlined))

/** Material Symbols codepoints in use. Names match the Material Symbols glyph names. */
object WgIcons {
    const val Description = ""
    const val MoreVert = ""
    const val Add = ""
    const val Delete = ""
}

/**
 * Render a Material Symbols [glyph] at [size] (default `icon.size`, 24 dp) in [tint]. Pass a
 * [contentDescription] for a meaningful icon; leave it null for a decorative one (whose meaning is
 * already carried by an adjacent label or its container) — decorative glyphs are hidden from
 * TalkBack so it never reads the raw codepoint.
 */
@Composable
fun WgIcon(
    glyph: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    size: Dp = WgDimen.iconSize,
) {
    val sizeSp = with(LocalDensity.current) { size.toSp() }
    val semantics = if (contentDescription != null) {
        Modifier.semantics { this.contentDescription = contentDescription }
    } else {
        Modifier.clearAndSetSemantics { }
    }
    Text(
        text = glyph,
        fontFamily = MaterialSymbols,
        fontSize = sizeSp,
        color = tint,
        modifier = modifier.then(semantics),
    )
}
