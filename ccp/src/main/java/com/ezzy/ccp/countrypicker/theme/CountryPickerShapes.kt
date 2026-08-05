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

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * Corner shapes for the picker's surfaces.
 *
 * These are not decorative — every clickable component clips its ripple to the shape declared here,
 * so a wrong shape shows up immediately as a ripple bleeding past a rounded corner. The
 * `Modifier.clip(shape)` and the `indication` always take the same value from this class.
 *
 * @property selectorFilled Filled selector: rounded top, near-square bottom, matching an M3 filled
 *   text field so a country selector sits correctly in a form beside real text fields.
 * @property selectorOutlined Outlined selector — uniformly rounded.
 * @property selectorMinimal Minimal (underlined) selector.
 * @property selectorPill Compact, flag-only and dial selectors.
 * @property sheet Bottom sheet top corners.
 * @property searchField Search field — fully rounded, M3 search-bar style.
 * @property row List row. Square by default: rows are edge-to-edge and rounding them would break the
 *   continuous tinted band the design uses for the selected section.
 * @property currentSelectionCard "Current selection" card.
 * @property regionChip Region filter chip.
 * @property detectedBadge "✓ Detected" chip.
 * @property flagCircle Circular flag mask.
 * @property flagRounded Rounded-rectangle flag mask.
 * @property flagSquare Square flag mask.
 * @property button Footer buttons.
 */
@Immutable
data class CountryPickerShapes(
    val selectorFilled: Shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 4.dp, bottomEnd = 4.dp),
    val selectorOutlined: Shape = RoundedCornerShape(12.dp),
    val selectorMinimal: Shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp),
    val selectorPill: Shape = RoundedCornerShape(percent = 50),
    val sheet: Shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
    val searchField: Shape = RoundedCornerShape(28.dp),
    val row: Shape = RoundedCornerShape(0.dp),
    val currentSelectionCard: Shape = RoundedCornerShape(16.dp),
    val regionChip: Shape = RoundedCornerShape(8.dp),
    val detectedBadge: Shape = RoundedCornerShape(6.dp),
    val flagCircle: Shape = RoundedCornerShape(percent = 50),
    val flagRounded: Shape = RoundedCornerShape(6.dp),
    val flagSquare: Shape = RoundedCornerShape(0.dp),
    val button: Shape = RoundedCornerShape(20.dp),
)

/**
 * How a flag is masked.
 *
 * [Original] is the only shape that preserves a flag's real proportions; the others crop to a mask,
 * which looks tidier in a dense list but distorts flags with distinctive geometry. Both are legitimate
 * choices, so the library exposes them rather than picking for the host.
 */
enum class CountryFlagShape {
    /** Circular mask. The design's default. */
    Circle,

    /** Rounded-rectangle mask. */
    Rounded,

    /** Square mask. */
    Square,

    /** No mask — the flag keeps its natural 4:3 aspect ratio. */
    Original,

    /** No flag at all. Selectors fall back to text; rows drop the leading visual. */
    Hidden,
    ;

    /** Resolves the matching [Shape] from [shapes]. */
    fun shape(shapes: CountryPickerShapes): Shape = when (this) {
        Circle -> shapes.flagCircle
        Rounded -> shapes.flagRounded
        Square, Original, Hidden -> shapes.flagSquare
    }
}
