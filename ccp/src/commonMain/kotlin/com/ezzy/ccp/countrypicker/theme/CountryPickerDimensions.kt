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

/** How much room the picker gives its rows and fields. */
public enum class CountryPickerDensity {
    /** Tighter rows for dense, data-heavy screens. Touch targets stay at 48dp. */
    Compact,

    /** The default. */
    Comfortable,

    /** Generous spacing for onboarding and other low-density screens. */
    Spacious,
}

/**
 * Sizes and spacing.
 *
 * Use [forDensity] rather than constructing one, then `copy` the values you want to change. Every
 * interactive element keeps a [minimumTouchTarget] of at least 48dp at every density: the compact
 * density gets its tighter look from smaller visuals and padding, never from smaller targets.
 *
 * @property fieldMinHeight Selector and phone field height.
 * @property fieldCompactHeight Pill selectors (compact, flag-only, dial code).
 * @property fieldHorizontalPadding Inner horizontal padding of fields.
 * @property fieldContentSpacing Gap between a field's flag, text and trailing icon.
 * @property fieldBorderWidth Field border at rest.
 * @property fieldFocusedBorderWidth Field border when focused, open or in error.
 * @property focusRingWidth Spread of the soft focus glow around a focused field.
 * @property flagSizeField Flag in a field.
 * @property flagSizeRow Flag in a list row.
 * @property flagSizeCompact Flag in a pill selector or the phone prefix.
 * @property flagSizeTile Flag in a quick-pick tile.
 * @property rowMinHeight List row height.
 * @property rowHorizontalPadding Row inner padding.
 * @property rowVerticalPadding Row inner padding.
 * @property rowContentSpacing Gap between a row's flag, text and trailing content.
 * @property groupHorizontalMargin Inset of grouped list sections from the sheet edge.
 * @property groupSpacing Space between list sections.
 * @property sheetHorizontalPadding Header, search and filter inset.
 * @property searchFieldHeight Search field height.
 * @property chipHeight Region filter height.
 * @property chipHorizontalPadding Region filter inner padding.
 * @property chipSpacing Gap between region filters.
 * @property tileWidth Quick-pick tile width.
 * @property tileHeight Quick-pick tile height.
 * @property indicatorSize Selection check and radio size.
 * @property checkboxSize Multi-select checkbox size.
 * @property iconButtonSize Visual size of the close and clear buttons; their touch target is
 *   [minimumTouchTarget].
 * @property indexRailWidth Width of the A–Z rail.
 * @property indexBubbleSize Diameter of the letter bubble shown while scrubbing the rail.
 * @property minimumTouchTarget Accessibility floor for every clickable element.
 */
@Immutable
public data class CountryPickerDimensions(
    val fieldMinHeight: Dp = 60.dp,
    val fieldCompactHeight: Dp = 44.dp,
    val fieldHorizontalPadding: Dp = 14.dp,
    val fieldContentSpacing: Dp = 12.dp,
    val fieldBorderWidth: Dp = 1.dp,
    val fieldFocusedBorderWidth: Dp = 1.5.dp,
    val focusRingWidth: Dp = 4.dp,
    val flagSizeField: Dp = 34.dp,
    val flagSizeRow: Dp = 36.dp,
    val flagSizeCompact: Dp = 24.dp,
    val flagSizeTile: Dp = 40.dp,
    val rowMinHeight: Dp = 58.dp,
    val rowHorizontalPadding: Dp = 14.dp,
    val rowVerticalPadding: Dp = 10.dp,
    val rowContentSpacing: Dp = 14.dp,
    val groupHorizontalMargin: Dp = 16.dp,
    val groupSpacing: Dp = 22.dp,
    val sheetHorizontalPadding: Dp = 20.dp,
    val searchFieldHeight: Dp = 48.dp,
    val chipHeight: Dp = 34.dp,
    val chipHorizontalPadding: Dp = 14.dp,
    val chipSpacing: Dp = 4.dp,
    val tileWidth: Dp = 84.dp,
    val tileHeight: Dp = 104.dp,
    val indicatorSize: Dp = 22.dp,
    val checkboxSize: Dp = 22.dp,
    val iconButtonSize: Dp = 32.dp,
    val indexRailWidth: Dp = 22.dp,
    val indexBubbleSize: Dp = 60.dp,
    val minimumTouchTarget: Dp = 48.dp,
) {
    public companion object {
        /** The dimensions for [density]. */
        public fun forDensity(density: CountryPickerDensity): CountryPickerDimensions = when (density) {
            CountryPickerDensity.Comfortable -> CountryPickerDimensions()
            CountryPickerDensity.Compact -> CountryPickerDimensions(
                fieldMinHeight = 52.dp,
                fieldCompactHeight = 40.dp,
                fieldHorizontalPadding = 12.dp,
                fieldContentSpacing = 10.dp,
                flagSizeField = 28.dp,
                flagSizeRow = 30.dp,
                flagSizeCompact = 22.dp,
                flagSizeTile = 34.dp,
                rowMinHeight = 48.dp,
                rowVerticalPadding = 6.dp,
                rowContentSpacing = 12.dp,
                groupSpacing = 16.dp,
                searchFieldHeight = 44.dp,
                chipHeight = 32.dp,
                tileWidth = 76.dp,
                tileHeight = 96.dp,
            )
            CountryPickerDensity.Spacious -> CountryPickerDimensions(
                fieldMinHeight = 68.dp,
                fieldHorizontalPadding = 16.dp,
                fieldContentSpacing = 14.dp,
                flagSizeField = 38.dp,
                flagSizeRow = 40.dp,
                flagSizeTile = 44.dp,
                rowMinHeight = 66.dp,
                rowHorizontalPadding = 16.dp,
                rowVerticalPadding = 12.dp,
                rowContentSpacing = 16.dp,
                groupSpacing = 28.dp,
                searchFieldHeight = 52.dp,
                chipHeight = 38.dp,
                tileWidth = 92.dp,
                tileHeight = 112.dp,
            )
        }
    }
}
