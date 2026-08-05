/**
 * Copyright (c) 2025 Ezekiel Wachira
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.ezzy.ccp.countrypicker.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ezzy.ccp.countrypicker.model.Country
import com.ezzy.ccp.countrypicker.theme.CountryFlagConfig
import com.ezzy.ccp.countrypicker.theme.CountryFlagShape
import com.ezzy.ccp.countrypicker.theme.CountryFlagStyle
import com.ezzy.ccp.countrypicker.theme.CountryPickerColors
import com.ezzy.ccp.countrypicker.theme.CountryPickerDefaults
import com.ezzy.ccp.countrypicker.theme.CountryPickerDimensions
import com.ezzy.ccp.countrypicker.theme.CountryPickerMotion

/**
 * Renders a country's flag.
 *
 * ### Why emoji and not bitmaps
 * The flag is the platform's own emoji glyph — a font lookup, not an asset. The alternative, ~250
 * flag PNGs at four densities, is roughly a megabyte of APK for something the device can already
 * draw, and it goes stale whenever a flag changes. The existing `EzzyIcons` vectors remain available
 * for hosts who want them via [flagContent].
 *
 * When a country has no emoji sequence ([Country.flag] is `null` — Kosovo is the real-world case), an
 * ISO-code badge is drawn instead of an empty box, so the row never looks broken.
 *
 * ### Accessibility
 * The flag is decorative and carries `clearAndSetSemantics {}`, contributing nothing to the
 * accessibility tree. TalkBack must read "Kenya", not "flag of Kenya, Kenya" — the country name is
 * announced by the row or selector that owns this flag.
 */
@Composable
fun CountryFlag(
    country: Country?,
    modifier: Modifier = Modifier,
    size: Dp = CountryPickerDefaults.dimensions().flagSize,
    shape: CountryFlagShape = CountryFlagShape.Circle,
    /**
     * Whether a background is drawn behind the flag, and if so, what kind — see [CountryFlagStyle].
     * Defaults to [CountryFlagStyle.TonalContainer], the library's original look, so every existing
     * call site that predates this parameter renders exactly as before.
     */
    style: CountryFlagStyle = CountryFlagStyle.TonalContainer,
    colors: CountryPickerColors = CountryPickerDefaults.colors(),
    dimensions: CountryPickerDimensions = CountryPickerDefaults.dimensions(),
    motion: CountryPickerMotion = CountryPickerDefaults.motion(),
    /** Replaces the default renderer entirely, e.g. to draw a vector or a remote image. */
    flagContent: (@Composable (Country) -> Unit)? = null,
) {
    if (shape == CountryFlagShape.Hidden || country == null) return

    // Plain never clips or masks — "plain" means the bare glyph at its natural proportions, same as
    // the Original shape always did. A masked shape only actually masks when a container is being
    // drawn at all (FilledContainer or TonalContainer); Plain overrides shape entirely.
    val isUnmasked = style == CountryFlagStyle.Plain || shape == CountryFlagShape.Original
    val hasBackground = style == CountryFlagStyle.TonalContainer && !isUnmasked

    // `Original`/`Plain` keep a flag's true 4:3 proportions; masked containers are square.
    val width = if (isUnmasked) size * dimensions.flagAspectRatioWidthMultiplier else size

    // Crossfading on the ISO code makes a country change read as a swap rather than a flicker.
    AnimatedContent(
        targetState = country,
        transitionSpec = { fadeIn(motion.fadeIn) togetherWith fadeOut(motion.fadeOut) },
        contentKey = { it.iso2Code },
        modifier = modifier.clearAndSetSemantics {},
        label = "CountryFlag",
    ) { target ->
        Box(
            modifier = Modifier
                .width(width)
                .height(size)
                .then(if (isUnmasked) Modifier else Modifier.clip(shape.shape(CountryPickerDefaults.shapes())))
                .then(
                    // A flag with white edges (many do) needs a backing to read as a distinct object
                    // against the surface — but only when the caller actually asked for a tonal
                    // container. Plain and FilledContainer paint nothing behind the glyph.
                    if (hasBackground) Modifier.background(colors.flagPlaceholderContainer) else Modifier,
                ),
            contentAlignment = Alignment.Center,
        ) {
            when {
                flagContent != null -> flagContent(target)
                target.flag != null -> Text(
                    text = target.flag,
                    style = TextStyle(
                        fontSize = with(androidx.compose.ui.platform.LocalDensity.current) {
                            (size * EMOJI_SIZE_RATIO).toSp()
                        },
                        textAlign = TextAlign.Center,
                    ),
                    // Emoji flags are wider than tall; scaling up fills a masked container edge-to-edge
                    // instead of leaving crescents of background either side. Unmasked glyphs render at
                    // their natural size — there is no container edge to fill.
                    modifier = if (isUnmasked) {
                        Modifier
                    } else {
                        Modifier.size(width * MASK_FILL_SCALE, size * MASK_FILL_SCALE)
                    },
                )

                else -> IsoCodeBadge(target, size, colors)
            }
        }
    }
}

/**
 * [CountryFlag] driven by a bundled [CountryFlagConfig] instead of separate `size`/`shape`/`style`
 * parameters — the entry point shared by the phone prefix, every selector variant, and country rows so
 * none of them re-implements flag presentation logic locally.
 */
@Composable
fun CountryFlag(
    country: Country?,
    config: CountryFlagConfig,
    modifier: Modifier = Modifier,
    colors: CountryPickerColors = CountryPickerDefaults.colors(),
    dimensions: CountryPickerDimensions = CountryPickerDefaults.dimensions(),
    motion: CountryPickerMotion = CountryPickerDefaults.motion(),
    flagContent: (@Composable (Country) -> Unit)? = null,
) {
    CountryFlag(
        country = country,
        modifier = modifier.padding(config.contentPadding),
        size = config.size,
        shape = config.shape,
        style = config.style,
        colors = colors,
        dimensions = dimensions,
        motion = motion,
        flagContent = flagContent,
    )
}

/** Fallback for countries with no emoji flag: the ISO alpha-2 code in a tinted badge. */
@Composable
private fun IsoCodeBadge(
    country: Country,
    size: Dp,
    colors: CountryPickerColors,
) {
    Text(
        text = country.iso2Code,
        color = colors.flagPlaceholderContent,
        style = TextStyle(
            // Scaled from the flag size rather than fixed, so the badge tracks the flag it replaces
            // at every call site and under font scaling.
            fontSize = (size.value * ISO_BADGE_SIZE_RATIO).sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp,
            textAlign = TextAlign.Center,
        ),
    )
}

/** Emoji glyph size relative to the flag box. */
private const val EMOJI_SIZE_RATIO = 0.82f

/** Extra scale applied so a 4:3 glyph covers a 1:1 mask. */
private const val MASK_FILL_SCALE = 1.5f

/** ISO badge text size relative to the flag box. */
private const val ISO_BADGE_SIZE_RATIO = 0.38f
