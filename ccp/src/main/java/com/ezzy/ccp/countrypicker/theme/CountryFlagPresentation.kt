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

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Whether a flag draws a background container behind it, and if so, what kind.
 *
 * This is a separate axis from [CountryFlagShape]: [CountryFlagShape] picks the *mask* (circle,
 * rounded, square, unmasked); [CountryFlagStyle] picks whether that mask is *filled* with anything.
 * A masked flag with [Plain] style has no fill at all — the mask still clips the glyph, but nothing is
 * painted behind it.
 *
 * - [Plain]: no background whatsoever. The flag renders at its natural aspect ratio with nothing
 *   painted behind it — the `🇰🇪` alone, matching the compact phone-prefix reference.
 * - [FilledContainer]: the flag is scaled to fill its clipped container edge-to-edge, with no tonal
 *   color showing through — the flag artwork itself *is* the container's visible content.
 * - [TonalContainer]: the flag sits on a soft, theme-derived background inside its clipped container —
 *   the library's original look for selectors and list rows, now made explicit and overridable rather
 *   than baked in.
 */
enum class CountryFlagStyle {
    Plain,
    FilledContainer,
    TonalContainer,
}

/**
 * Bundles everything about how a flag is drawn, so callers configure flag presentation in one place
 * instead of passing `size`/`shape`/`style` as separate parameters to every component that draws one.
 *
 * Shared by the phone country-code prefix, the full/compact/flag-only country selectors, and country
 * rows — there is exactly one flag-rendering implementation ([com.ezzy.ccp.countrypicker.ui.CountryFlag]),
 * and every one of those call sites can pass a `CountryFlagConfig` to it rather than reimplementing
 * background/clip/scale logic locally.
 *
 * @property style Whether/how a background is drawn — see [CountryFlagStyle].
 * @property shape The mask flags are clipped to. Ignored (treated as unmasked) when [style] is
 *   [CountryFlagStyle.Plain], since "plain" means no clipping at all.
 * @property size The flag glyph's rendered size (its height; width follows from aspect ratio and
 *   [style]/[shape]).
 * @property containerSize Reserved for callers that want the clickable/layout footprint to exceed the
 *   flag's own visual size (e.g. a pill selector with internal padding around the flag). Purely
 *   informational for [com.ezzy.ccp.countrypicker.ui.CountryFlag] itself, which sizes to [size]; a
 *   caller wanting extra footprint wraps it in a `Modifier.size(containerSize)` `Box`.
 * @property contentPadding Padding applied inside the flag's own layout bounds, before the glyph is
 *   drawn — use this rather than wrapping in an external `Modifier.padding` when the padding should be
 *   *part of* the flag's clipped container (e.g. a filled container with a small inset).
 */
@Immutable
data class CountryFlagConfig(
    val style: CountryFlagStyle = CountryFlagStyle.Plain,
    val shape: CountryFlagShape = CountryFlagShape.Circle,
    val size: Dp = 28.dp,
    val containerSize: Dp = 36.dp,
    val contentPadding: PaddingValues = PaddingValues(0.dp),
)
