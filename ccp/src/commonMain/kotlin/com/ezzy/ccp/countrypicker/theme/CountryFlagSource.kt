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

package com.ezzy.ccp.countrypicker.theme

import androidx.compose.runtime.Immutable

/**
 * Where flag artwork comes from.
 *
 * The source decides what a flag *is*; [CountryFlagStyle] decides how it is framed — on a tile, in a
 * circle, with rounded corners, or bare. Any source works with any style.
 */
@Immutable
public sealed interface CountryFlagSource {

    /**
     * The platform's emoji flags.
     *
     * Entirely offline and dependency-free, but drawn differently on Android and iOS, absent from
     * some Android builds (the ISO code is drawn instead), and missing for territories Unicode has
     * no flag for, such as Kosovo.
     */
    public data object Emoji : CountryFlagSource

    /**
     * Flag images from [flagcdn.com](https://flagcdn.com) — the same artwork on every platform, for
     * every country in the dataset, Kosovo included.
     *
     * Images are fetched at the smallest size that is sharp at the screen's density and cached in
     * memory and on disk, so each flag downloads once. While a flag loads — and whenever it cannot,
     * offline for instance — the emoji flag stands in, so the picker never shows an empty slot.
     *
     * Flags are fetched from a third-party CDN, which sees the device's IP address. For apps that
     * must not make that request, use [Emoji], or point [baseUrl] at a mirror you host with the
     * same layout.
     *
     * @property shape The three flagcdn shapes: waving at 4:3, or the flag's original proportions at
     *   a common width or a common height. See [FlagImageShape].
     * @property format The image format. See [FlagImageFormat] for which shapes offer which formats.
     * @property fallbackToEmoji Show the emoji flag while an image loads and when it fails. Off shows
     *   a quiet placeholder instead — for a design that must never mix the two kinds of artwork.
     * @property baseUrl The CDN root, without a trailing slash.
     */
    @Immutable
    public data class FlagCdn(
        val shape: FlagImageShape = FlagImageShape.Waving,
        val format: FlagImageFormat = FlagImageFormat.Png,
        val fallbackToEmoji: Boolean = true,
        val baseUrl: String = DEFAULT_BASE_URL,
    ) : CountryFlagSource {

        /**
         * The address of [iso2Code]'s flag, at the smallest size that covers [widthPx] by
         * [heightPx] pixels.
         *
         * Exposed for drawing the same artwork outside the picker — a notification icon, a share
         * card. Combinations the CDN does not serve are resolved as [FlagImageFormat] describes.
         */
        public fun urlFor(iso2Code: String, widthPx: Int, heightPx: Int): String =
            flagCdnUrl(baseUrl, iso2Code, shape, format, widthPx, heightPx)

        public companion object {
            /** flagcdn.com, served from Cloudflare's CDN. */
            public const val DEFAULT_BASE_URL: String = "https://flagcdn.com"
        }
    }
}

/**
 * The geometry of flag artwork — flagcdn's three shapes.
 *
 * Flags disagree about proportions: Switzerland is square, Qatar is 11:28, Nepal is taller than it is
 * wide. A shape decides how that disagreement is resolved.
 */
public enum class FlagImageShape {
    /**
     * Every flag waving on the same 4:3 canvas, with a soft fold of light across it — uniform, and
     * the closest to the emoji look.
     */
    Waving,

    /**
     * Each flag at its true proportions, all the same width; heights vary from flag to flag.
     */
    OriginalSameWidth,

    /**
     * Each flag at its true proportions, all the same height; widths vary from flag to flag. In a
     * list they line up on their leading edge, and none is ever wider than its slot.
     */
    OriginalSameHeight,
    ;

    /** True for the flat, true-to-proportion shapes. */
    public val isOriginal: Boolean get() = this != Waving
}

/**
 * The image format of flag artwork.
 *
 * Not every shape comes in every format. Waving flags are PNG or WebP only — they need transparency
 * around the fold — so [Jpeg] and [Svg] fall back to [Png] for them. The original shapes come in all
 * four; [Svg] is a single scalable file, laid out at the shape's width or height like the rest.
 *
 * [CountryFlagStyle.Circle] and [CountryFlagStyle.Rounded] crop the flag to one size, and a crop of a
 * waving flag would show its fold and transparent corners — so those styles always draw the original
 * artwork, whichever shape is set.
 */
public enum class FlagImageFormat {
    /** Lossless with transparency. The safe default. */
    Png,

    /** Smaller than PNG at the same quality, with transparency. */
    WebP,

    /** Smallest, without transparency. Original shapes only. */
    Jpeg,

    /** Vector, sharp at any size. Original shapes only. */
    Svg,
}

// ── URL scheme ────────────────────────────────────────────────────────────────────────────────────

/** Waving sizes flagcdn serves, as width×height, smallest first. */
private val WAVING_SIZES: List<Pair<Int, Int>> = listOf(
    16 to 12, 20 to 15, 24 to 18, 28 to 21, 32 to 24, 36 to 27, 40 to 30, 48 to 36, 56 to 42,
    60 to 45, 64 to 48, 72 to 54, 80 to 60, 84 to 63, 96 to 72, 108 to 81, 112 to 84, 120 to 90,
    128 to 96, 144 to 108, 160 to 120, 192 to 144, 224 to 168, 256 to 192,
)

/** Widths flagcdn serves the original shapes at. */
private val ORIGINAL_WIDTHS: List<Int> = listOf(20, 40, 80, 160, 320, 640, 1280, 2560)

/** Heights flagcdn serves the original shapes at. */
private val ORIGINAL_HEIGHTS: List<Int> = listOf(20, 24, 40, 60, 80, 120, 240)

/**
 * Builds a flagcdn address. The size is the smallest the CDN serves that covers the request, so a
 * flag is never upscaled, and the largest when nothing does.
 *
 * - Waving: `/{w}x{h}/{code}.{png|webp}`
 * - Same width: `/w{w}/{code}.{png|webp|jpg}`
 * - Same height: `/h{h}/{code}.{png|webp|jpg}`
 * - SVG: `/{code}.svg`
 */
internal fun flagCdnUrl(
    baseUrl: String,
    iso2Code: String,
    shape: FlagImageShape,
    format: FlagImageFormat,
    widthPx: Int,
    heightPx: Int,
): String {
    val code = iso2Code.lowercase()
    val root = baseUrl.trimEnd('/')
    if (shape.isOriginal && format == FlagImageFormat.Svg) return "$root/$code.svg"
    val extension = when (format) {
        FlagImageFormat.WebP -> "webp"
        FlagImageFormat.Jpeg -> if (shape.isOriginal) "jpg" else "png"
        FlagImageFormat.Png, FlagImageFormat.Svg -> "png"
    }
    return when (shape) {
        FlagImageShape.Waving -> {
            val (w, h) = WAVING_SIZES.firstOrNull { (w, h) -> w >= widthPx && h >= heightPx } ?: WAVING_SIZES.last()
            "$root/${w}x$h/$code.$extension"
        }
        FlagImageShape.OriginalSameWidth -> {
            val w = ORIGINAL_WIDTHS.firstOrNull { it >= widthPx } ?: ORIGINAL_WIDTHS.last()
            "$root/w$w/$code.$extension"
        }
        FlagImageShape.OriginalSameHeight -> {
            val h = ORIGINAL_HEIGHTS.firstOrNull { it >= heightPx } ?: ORIGINAL_HEIGHTS.last()
            "$root/h$h/$code.$extension"
        }
    }
}
