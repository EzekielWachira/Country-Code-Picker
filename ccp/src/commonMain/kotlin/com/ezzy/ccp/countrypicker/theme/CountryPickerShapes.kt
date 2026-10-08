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

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Corner shapes for every surface the picker draws.
 *
 * Every clickable surface clips its press feedback to the shape declared here, so a shape that does
 * not match its container shows up immediately as a highlight bleeding past a corner.
 *
 * @property sheet Bottom-sheet top corners.
 * @property dialog The dialog used on wide screens.
 * @property field Selector and phone fields.
 * @property searchField The search field.
 * @property groupCornerRadius Corner radius of a grouped (inset) list section. A radius rather than a
 *   [Shape] because the list is drawn row by row, and each row needs the top, bottom or no corners of
 *   it depending on its position in the group.
 * @property row Row highlight in the plain and card list styles.
 * @property chip Region filters and the segment pill that slides between them.
 * @property flagTile The rounded tile behind a flag in [CountryFlagStyle.Tile].
 * @property flagRounded The rounded crop of [CountryFlagStyle.Rounded].
 * @property badge Small labels: "Detected", "Mobile", "Not available".
 * @property button Primary and secondary buttons.
 * @property tile Quick-pick tiles.
 * @property floatingBar The multi-select confirmation bar.
 * @property pill Pill-shaped selectors (compact, flag-only, dial code).
 */
@Immutable
public data class CountryPickerShapes(
    val sheet: Shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
    val dialog: Shape = RoundedCornerShape(32.dp),
    val field: Shape = RoundedCornerShape(16.dp),
    val searchField: Shape = RoundedCornerShape(14.dp),
    val groupCornerRadius: Dp = 20.dp,
    val row: Shape = RoundedCornerShape(14.dp),
    val chip: Shape = CircleShape,
    val flagTile: Shape = RoundedCornerShape(percent = 28),
    val flagRounded: Shape = RoundedCornerShape(percent = 22),
    val badge: Shape = RoundedCornerShape(6.dp),
    val button: Shape = RoundedCornerShape(14.dp),
    val tile: Shape = RoundedCornerShape(18.dp),
    val floatingBar: Shape = RoundedCornerShape(22.dp),
    val pill: Shape = CircleShape,
)
