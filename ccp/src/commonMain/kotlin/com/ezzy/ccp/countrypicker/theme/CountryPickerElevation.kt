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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * One layer of a soft shadow.
 *
 * Real depth is rarely one shadow. A tight, dark contact layer under a wide, faint ambient layer is
 * what makes a card read as resting *on* the canvas rather than glowing above it, which is why
 * [CountryPickerShadow] stacks layers.
 *
 * @property blur Blur radius.
 * @property offsetY Vertical offset — light comes from above.
 * @property spread Grows (or, negative, shrinks) the shadow before blurring.
 * @property alpha Opacity, applied to [CountryPickerColors.shadow].
 */
@Immutable
public data class ShadowLayer(
    val blur: Dp,
    val offsetY: Dp = 0.dp,
    val spread: Dp = 0.dp,
    val alpha: Float,
)

/** A stack of [ShadowLayer]s, drawn bottom-up. */
@Immutable
public data class CountryPickerShadow(val layers: List<ShadowLayer>) {
    public companion object {
        /** No shadow. */
        public val None: CountryPickerShadow = CountryPickerShadow(emptyList())
    }
}

/**
 * Shadows for each level of the picker's surface hierarchy.
 *
 * Grouped lists deliberately have none: they are drawn row by row, and a shadow per row would show a
 * seam at every boundary. They separate from the canvas through contrast and a hairline instead.
 *
 * @property field Selector and phone fields at rest.
 * @property fieldFocused Fields while focused or open.
 * @property searchField The search field.
 * @property card Banners and standalone cards.
 * @property tile Quick-pick tiles.
 * @property floatingBar The multi-select confirmation bar.
 * @property dialog The wide-screen dialog.
 */
@Immutable
public data class CountryPickerElevation(
    val field: CountryPickerShadow,
    val fieldFocused: CountryPickerShadow,
    val searchField: CountryPickerShadow,
    val card: CountryPickerShadow,
    val tile: CountryPickerShadow,
    val floatingBar: CountryPickerShadow,
    val dialog: CountryPickerShadow,
) {
    public companion object {
        /** Layered soft shadows, tuned per mode: dark surfaces need far denser shadows to read at all. */
        public fun signature(dark: Boolean = false): CountryPickerElevation {
            val k = if (dark) DARK_SHADOW_MULTIPLIER else 1f
            fun shadow(vararg layers: ShadowLayer) =
                CountryPickerShadow(layers.map { it.copy(alpha = (it.alpha * k).coerceAtMost(1f)) })
            return CountryPickerElevation(
                field = shadow(
                    ShadowLayer(blur = 1.dp, offsetY = 0.5.dp, alpha = 0.06f),
                    ShadowLayer(blur = 10.dp, offsetY = 3.dp, alpha = 0.04f),
                ),
                fieldFocused = shadow(
                    ShadowLayer(blur = 1.dp, offsetY = 0.5.dp, alpha = 0.06f),
                    ShadowLayer(blur = 18.dp, offsetY = 6.dp, alpha = 0.07f),
                ),
                searchField = shadow(
                    ShadowLayer(blur = 1.dp, offsetY = 0.5.dp, alpha = 0.05f),
                    ShadowLayer(blur = 8.dp, offsetY = 2.dp, alpha = 0.035f),
                ),
                card = shadow(
                    ShadowLayer(blur = 1.dp, offsetY = 0.5.dp, alpha = 0.06f),
                    ShadowLayer(blur = 14.dp, offsetY = 4.dp, alpha = 0.05f),
                ),
                tile = shadow(
                    ShadowLayer(blur = 1.dp, offsetY = 0.5.dp, alpha = 0.07f),
                    ShadowLayer(blur = 12.dp, offsetY = 4.dp, alpha = 0.05f),
                ),
                floatingBar = shadow(
                    ShadowLayer(blur = 2.dp, offsetY = 1.dp, alpha = 0.08f),
                    ShadowLayer(blur = 28.dp, offsetY = 10.dp, alpha = 0.14f),
                ),
                dialog = shadow(
                    ShadowLayer(blur = 2.dp, offsetY = 1.dp, alpha = 0.1f),
                    ShadowLayer(blur = 48.dp, offsetY = 18.dp, alpha = 0.22f),
                ),
            )
        }

        /** No shadows anywhere — for flat designs that rely on hairlines alone. */
        public val Flat: CountryPickerElevation = CountryPickerElevation(
            field = CountryPickerShadow.None,
            fieldFocused = CountryPickerShadow.None,
            searchField = CountryPickerShadow.None,
            card = CountryPickerShadow.None,
            tile = CountryPickerShadow.None,
            floatingBar = CountryPickerShadow.None,
            dialog = CountryPickerShadow.None,
        )
    }
}

private const val DARK_SHADOW_MULTIPLIER = 4f
