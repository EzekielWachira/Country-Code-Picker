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
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.size.Dimension
import coil3.size.Scale
import coil3.size.Size as CoilSize
import com.ezzy.ccp.countrypicker.model.Country
import com.ezzy.ccp.countrypicker.theme.CountryFlagSource
import com.ezzy.ccp.countrypicker.theme.CountryFlagStyle
import com.ezzy.ccp.countrypicker.theme.CountryPickerColors
import com.ezzy.ccp.countrypicker.theme.CountryPickerLayout
import com.ezzy.ccp.countrypicker.theme.CountryPickerTheme
import com.ezzy.ccp.countrypicker.theme.FlagImageShape
import com.ezzy.ccp.countrypicker.theme.blendTowards
import com.ezzy.ccp.countrypicker.theme.flagCdnUrl

/**
 * A country's flag.
 *
 * Two independent choices shape it. [source] decides the artwork — flagcdn.com images in one of three
 * shapes, or the platform's emoji (see [CountryFlagSource]). [style] decides the frame — a tile, a
 * circle, rounded corners, or nothing (see [CountryFlagStyle]).
 *
 * Images arrive asynchronously; until then, and whenever one cannot be fetched, the emoji flag stands
 * in, so a flag slot is never empty. Where the emoji is missing too — a font stripped of flags, a
 * territory with no emoji — the ISO code is drawn in the same frame instead of a row of boxes.
 *
 * A change of country cross-fades, so a selection reads as a swap rather than a flicker. The flag is
 * decorative and hidden from accessibility: the surrounding component always names the country.
 *
 * @param size The flag's height. [CountryFlagStyle.Plain] and [CountryFlagStyle.Rounded] reserve
 *   a 4:3 slot of this height; the original shapes may draw slightly past it, as true proportions do.
 * @param style How the flag is framed. Defaults to the theme's [CountryPickerLayout.flagStyle].
 * @param source Where the artwork comes from. Defaults to the theme's [CountryPickerLayout.flagSource].
 * @param flagContent Replaces the artwork entirely — for your own vectors or images. It is still
 *   framed by [style].
 */
@Composable
public fun CountryFlag(
    country: Country?,
    modifier: Modifier = Modifier,
    size: Dp = CountryPickerTheme.style.dimensions.flagSizeRow,
    style: CountryFlagStyle = CountryPickerTheme.style.layout.flagStyle,
    source: CountryFlagSource = CountryPickerTheme.style.layout.flagSource,
    flagContent: (@Composable (Country) -> Unit)? = null,
) {
    if (style == CountryFlagStyle.Hidden || country == null) return
    val theme = CountryPickerTheme.style
    val motion = theme.motion
    val remote = (source as? CountryFlagSource.FlagCdn).takeIf { flagContent == null }

    AnimatedContent(
        targetState = country,
        transitionSpec = {
            (fadeIn(motion.fadeIn) + scaleIn(motion.selection, initialScale = SWAP_INITIAL_SCALE)) togetherWith
                fadeOut(motion.fadeOut)
        },
        contentKey = { it.iso2Code },
        modifier = modifier.clearAndSetSemantics {},
        label = "CountryFlag",
    ) { target ->
        when (style) {
            CountryFlagStyle.Tile -> FlagTile(target, size, theme.shapes.flagTile, theme.colors, remote, flagContent)
            CountryFlagStyle.Circle -> CircleFlag(target, size, theme.colors, remote, flagContent)
            CountryFlagStyle.Rounded -> RoundedFlag(target, size, theme.shapes.flagRounded, theme.colors, remote, flagContent)
            CountryFlagStyle.Plain, CountryFlagStyle.Hidden -> PlainFlag(target, size, theme.colors, remote, flagContent)
        }
    }
}

/** The Signature tile: the flag centered on a softly shaded rounded square with a hairline edge. */
@Composable
private fun FlagTile(
    country: Country,
    size: Dp,
    shape: Shape,
    colors: CountryPickerColors,
    remote: CountryFlagSource.FlagCdn?,
    flagContent: (@Composable (Country) -> Unit)?,
) {
    val top = if (colors.isDark) colors.surfaceSunken.blendTowards(Color.White, 0.06f) else colors.surface
    Box(
        modifier = Modifier
            .size(size)
            .clip(shape)
            .background(Brush.verticalGradient(listOf(top, colors.surfaceSunken)))
            .border(TILE_BORDER, colors.hairline, shape),
        contentAlignment = Alignment.Center,
    ) {
        val emoji: @Composable () -> Unit = {
            if (country.hasDrawableFlag) EmojiFlag(country.flag!!, fontSizeOf(size * TILE_EMOJI_RATIO)) else IsoCode(country, size, colors)
        }
        when {
            flagContent != null -> Box(Modifier.size(size * TILE_CONTENT_RATIO)) { flagContent(country) }
            remote != null -> {
                // Flat artwork gets a wider slot than waving: a same-height flag is only half the
                // slot's width tall, and would look small on the tile otherwise.
                val width = size * if (remote.shape.isOriginal) TILE_FLAT_ART_RATIO else TILE_ART_RATIO
                RemoteFlag(country, remote, width, width * 3f / 4f, crop = null, flatShape = FLAT_CORNERS, colors = colors, fallback = emoji)
            }
            else -> emoji()
        }
    }
}

/** The flag cropped to a circle, with a hairline so white-edged flags keep their outline. */
@Composable
private fun CircleFlag(
    country: Country,
    size: Dp,
    colors: CountryPickerColors,
    remote: CountryFlagSource.FlagCdn?,
    flagContent: (@Composable (Country) -> Unit)?,
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(colors.surfaceSunken)
            .border(TILE_BORDER, colors.hairline, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        val emoji: @Composable () -> Unit = { CroppedEmoji(country, size, colors) }
        when {
            flagContent != null -> flagContent(country)
            remote != null -> RemoteFlag(country, remote, size, size, crop = CircleShape, flatShape = CircleShape, colors = colors, fallback = emoji)
            else -> emoji()
        }
    }
}

/**
 * A flat flag cropped to a 4:3 rounded rectangle — every flag the same size, whatever its own
 * proportions or the source's shape.
 */
@Composable
private fun RoundedFlag(
    country: Country,
    size: Dp,
    shape: Shape,
    colors: CountryPickerColors,
    remote: CountryFlagSource.FlagCdn?,
    flagContent: (@Composable (Country) -> Unit)?,
) {
    val width = size * FLAG_ASPECT_RATIO
    Box(
        modifier = Modifier
            .size(width, size)
            .clip(shape)
            .background(colors.surfaceSunken)
            .border(TILE_BORDER, colors.hairline, shape),
        contentAlignment = Alignment.Center,
    ) {
        val emoji: @Composable () -> Unit = { CroppedEmoji(country, size, colors) }
        when {
            flagContent != null -> flagContent(country)
            remote != null -> RemoteFlag(country, remote, width, size, crop = shape, flatShape = shape, colors = colors, fallback = emoji)
            else -> emoji()
        }
    }
}

/** The bare flag in a 4:3 slot. */
@Composable
private fun PlainFlag(
    country: Country,
    size: Dp,
    colors: CountryPickerColors,
    remote: CountryFlagSource.FlagCdn?,
    flagContent: (@Composable (Country) -> Unit)?,
) {
    val width = size * FLAG_ASPECT_RATIO
    Box(
        modifier = Modifier.width(width).height(size),
        contentAlignment = Alignment.Center,
    ) {
        val emoji: @Composable () -> Unit = {
            if (country.hasDrawableFlag) {
                EmojiFlag(country.flag!!, fontSizeOf(size * PLAIN_EMOJI_RATIO))
            } else {
                Box(
                    modifier = Modifier.size(size).clip(CircleShape).background(colors.surfaceSunken),
                    contentAlignment = Alignment.Center,
                ) { IsoCode(country, size, colors) }
            }
        }
        when {
            flagContent != null -> flagContent(country)
            // Leading-aligned, so a list of same-height flags of differing widths keeps one clean
            // column of left edges.
            remote != null -> RemoteFlag(
                country, remote, width, size,
                crop = null, flatShape = FLAT_CORNERS, colors = colors, fallback = emoji,
                alignment = Alignment.CenterStart,
            )
            else -> emoji()
        }
    }
}

/**
 * A flagcdn image laid out by its shape inside a 4:3 reference slot of [slotWidth] × [slotHeight]:
 *
 * - **Waving** fills the slot.
 * - **Same width** and **same height** are sized by [originalArtSize] and placed by [alignment].
 * - With a [crop], flat artwork covers the slot and is clipped to it, so every flag is the same size.
 *
 * The slot keeps its size whatever the image does, so a list never shifts as flags arrive.
 * [fallback] shows until the image is ready, and stays if it never is.
 */
@Composable
private fun RemoteFlag(
    country: Country,
    source: CountryFlagSource.FlagCdn,
    slotWidth: Dp,
    slotHeight: Dp,
    crop: Shape?,
    flatShape: Shape,
    colors: CountryPickerColors,
    fallback: @Composable () -> Unit,
    alignment: Alignment = Alignment.Center,
) {
    // A crop of a waving flag would show its fold and transparent corners, so crops take the flat
    // artwork, fetched by height.
    val shape = if (crop != null) FlagImageShape.OriginalSameHeight else source.shape
    val density = LocalDensity.current
    val widthPx = with(density) { slotWidth.roundToPx() }
    val heightPx = with(density) { slotHeight.roundToPx() }
    // A crop covers the slot, so a flag wider than the slot is scaled by height and a narrower one
    // by width; fetching a size up keeps the narrow ones (Switzerland, Nepal) sharp too.
    val fetchHeightPx = if (crop != null) (heightPx * CROP_FETCH_SCALE).toInt() else heightPx
    val url = flagCdnUrl(source.baseUrl, country.iso2Code, shape, source.format, widthPx, fetchHeightPx)
    val context = LocalPlatformContext.current
    val request = remember(url, widthPx, heightPx, shape, crop != null) {
        ImageRequest.Builder(context)
            .data(url)
            .size(
                when {
                    crop != null || shape == FlagImageShape.Waving -> CoilSize(widthPx, heightPx)
                    shape == FlagImageShape.OriginalSameWidth -> CoilSize(Dimension(widthPx), Dimension.Undefined)
                    else -> CoilSize(Dimension.Undefined, Dimension(heightPx))
                },
            )
            .scale(if (crop != null) Scale.FILL else Scale.FIT)
            .build()
    }
    val painter = rememberAsyncImagePainter(model = request, imageLoader = rememberFlagImageLoader())
    val state by painter.state.collectAsState()
    val loaded = state is AsyncImagePainter.State.Success
    // Starts at its target, so a flag already in the memory cache appears without a fade.
    val reveal by animateFloatAsState(if (loaded) 1f else 0f, CountryPickerTheme.style.motion.fadeIn, label = "flagReveal")

    Box(Modifier.size(slotWidth, slotHeight), contentAlignment = Alignment.Center) {
        if (reveal < 1f) {
            Box(Modifier.graphicsLayer { alpha = 1f - reveal }, contentAlignment = Alignment.Center) {
                if (source.fallbackToEmoji) {
                    fallback()
                } else {
                    Box(
                        Modifier
                            .size(slotWidth, slotHeight)
                            .clip(crop ?: flatShape)
                            .background(colors.skeleton),
                    )
                }
            }
        }
        if (loaded) {
            val intrinsic = painter.intrinsicSize
            val aspect = if (intrinsic.isSpecified && intrinsic.height > 0f) intrinsic.width / intrinsic.height else 4f / 3f
            val artModifier = when {
                crop != null -> Modifier.size(slotWidth, slotHeight).clip(crop)
                shape == FlagImageShape.Waving -> Modifier.size(slotWidth, slotHeight)
                else -> {
                    val (width, height) = originalArtSize(shape, aspect, slotWidth, slotHeight)
                    Modifier
                        .align(alignment)
                        // Required, not plain, size: a very tall flag may overhang the slot
                        // vertically without resizing it.
                        .requiredSize(width, height)
                        .clip(flatShape)
                        .border(FLAT_BORDER, colors.hairline, flatShape)
                }
            }
            Image(
                painter = painter,
                contentDescription = null,
                contentScale = if (crop != null) ContentScale.Crop else ContentScale.Fit,
                modifier = artModifier.graphicsLayer { alpha = reveal },
            )
        }
    }
}

/**
 * The drawn size of flat artwork of [aspect] (width ÷ height) for [shape] in a 4:3 slot.
 *
 * - **Same width:** the slot's full width, at the flag's own height. A very tall flag (Nepal) is
 *   capped at [MAX_OVERHANG] × the slot's height, overhanging it vertically, where rows have room.
 * - **Same height:** half the slot's width, so a 2:1 flag — the widest common proportion — exactly
 *   fills it and every 3:2, 5:3 and 2:1 flag shares one height. Anything wider (Qatar, 28:11) is
 *   scaled down whole to fit.
 *
 * Never wider than the slot: a flag spilling sideways would crowd the text beside it, look larger
 * than its neighbours and break the column of left edges.
 */
internal fun originalArtSize(shape: FlagImageShape, aspect: Float, slotWidth: Dp, slotHeight: Dp): Pair<Dp, Dp> {
    var width: Dp
    var height: Dp
    if (shape == FlagImageShape.OriginalSameWidth) {
        width = slotWidth
        height = width / aspect
        val maxHeight = slotHeight * MAX_OVERHANG
        if (height > maxHeight) {
            height = maxHeight
            width = height * aspect
        }
    } else {
        height = slotWidth / 2f
        width = height * aspect
        if (width > slotWidth) {
            width = slotWidth
            height = width / aspect
        }
    }
    return width to height
}

/** The emoji scaled up past its transparent margins so it covers a crop instead of floating in it. */
@Composable
private fun CroppedEmoji(country: Country, height: Dp, colors: CountryPickerColors) {
    if (country.hasDrawableFlag) EmojiFlag(country.flag!!, fontSizeOf(height * CROP_EMOJI_RATIO)) else IsoCode(country, height, colors)
}

/**
 * The emoji glyph, centered on its container even when drawn larger than it. Measured unbounded:
 * capped to the container's width, an oversized glyph would be laid out from the start edge and sit
 * off-center in a crop.
 */
@Composable
private fun EmojiFlag(flag: String, fontSize: androidx.compose.ui.unit.TextUnit) {
    Text(
        text = flag,
        style = TextStyle(
            fontSize = fontSize,
            lineHeight = fontSize,
            textAlign = TextAlign.Center,
            lineHeightStyle = LineHeightStyle(alignment = LineHeightStyle.Alignment.Center, trim = LineHeightStyle.Trim.Both),
        ),
        maxLines = 1,
        softWrap = false,
        modifier = Modifier.wrapContentSize(unbounded = true),
    )
}

/** Fallback for countries with no drawable flag: the ISO alpha-2 code, scaled to the flag. */
@Composable
private fun IsoCode(country: Country, size: Dp, colors: CountryPickerColors) {
    Text(
        text = country.iso2Code,
        color = colors.textSecondary,
        style = TextStyle(
            fontSize = (size.value * ISO_SIZE_RATIO).sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            textAlign = TextAlign.Center,
        ),
        maxLines = 1,
    )
}

@Composable
private fun fontSizeOf(size: Dp) = with(LocalDensity.current) { size.toSp() }

/**
 * Whether the platform can actually draw this country's emoji flag — `flag != null` alone is not
 * enough on devices whose font has had flag glyphs stripped.
 */
private val Country.hasDrawableFlag: Boolean
    get() = flag != null && CountryFlagSupport.emojiFlagsSupported

/** Flags are, on average, 4:3. */
private const val FLAG_ASPECT_RATIO = 4f / 3f
private const val TILE_EMOJI_RATIO = 0.6f
private const val TILE_CONTENT_RATIO = 0.62f
private const val TILE_ART_RATIO = 0.68f
private const val TILE_FLAT_ART_RATIO = 0.8f
private const val CROP_EMOJI_RATIO = 1.18f
private const val PLAIN_EMOJI_RATIO = 0.86f
private const val ISO_SIZE_RATIO = 0.34f
private const val SWAP_INITIAL_SCALE = 0.85f

/** How far a same-width flag may overhang its slot vertically before it is scaled down whole. */
private const val MAX_OVERHANG = 1.3f

/** How much larger than its slot a cropped flag is fetched, so narrow flags stay sharp. */
private const val CROP_FETCH_SCALE = 1.4f
private val TILE_BORDER = 0.75.dp
private val FLAT_BORDER = 0.5.dp
private val FLAT_CORNERS = RoundedCornerShape(2.dp)

/**
 * Up to [max] flags overlapping like a row of avatars, with a "+N" disc for the rest — how the
 * multiple-selection summary shows several countries in the space of one.
 */
@Composable
internal fun StackedFlags(
    countries: List<Country>,
    modifier: Modifier = Modifier,
    size: Dp = 28.dp,
    max: Int = 4,
    ringColor: Color = CountryPickerTheme.style.colors.surface,
) {
    if (countries.isEmpty()) return
    val theme = CountryPickerTheme.style
    val shown = countries.take(max)
    val overflow = countries.size - shown.size
    val step = size * STACK_STEP_RATIO
    val discs = shown.size + if (overflow > 0) 1 else 0
    Box(modifier = modifier.width(size + step * (discs - 1)).height(size).clearAndSetSemantics {}) {
        shown.forEachIndexed { index, country ->
            Box(
                modifier = Modifier
                    .padding(start = step * index)
                    .size(size)
                    .background(ringColor, CircleShape)
                    .padding(STACK_RING),
            ) {
                CountryFlag(country = country, size = size - STACK_RING * 2, style = CountryFlagStyle.Circle)
            }
        }
        if (overflow > 0) {
            Box(
                modifier = Modifier
                    .padding(start = step * shown.size)
                    .size(size)
                    .background(ringColor, CircleShape)
                    .padding(STACK_RING)
                    .background(theme.colors.surfaceSunken, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "+$overflow",
                    style = theme.typography.badge,
                    color = theme.colors.textSecondary,
                    maxLines = 1,
                )
            }
        }
    }
}

private const val STACK_STEP_RATIO = 0.62f
private val STACK_RING = 2.dp
