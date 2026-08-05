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
 * Sizes and spacing for the picker, taken from the design's CSS measurements.
 *
 * Every touch target here is at least 48dp, which is a hard floor rather than a default: values
 * below it fail Material's accessibility guidance and are unusable for anyone with reduced motor
 * precision. The compact variants get there through padding around smaller visuals rather than by
 * shrinking the target.
 *
 * @property selectorMinHeight Filled/minimal selector height (design: 56px).
 * @property selectorOutlinedMinHeight Outlined selector height — slightly taller for the stroke
 *   (design: 60px).
 * @property selectorCompactMinHeight Pill-shaped compact selector (design: 40px visual) padded out
 *   to a 48dp target.
 * @property selectorFlagOnlyMinHeight Flag-only selector (design: 48px).
 * @property selectorDialMinHeight Phone dial-code selector (design: 48px).
 * @property selectorHorizontalPadding Inner horizontal padding of the full-width selectors.
 * @property selectorVerticalPadding Inner vertical padding of the full-width selectors.
 * @property selectorContentSpacing Gap between flag, text block and chevron (design: 16px).
 * @property selectorBorderWidth Outlined selector stroke at rest.
 * @property selectorFocusedBorderWidth Outlined selector stroke when focused or errored — the
 *   thickening is a second, non-color cue so focus is not conveyed by hue alone.
 * @property flagSize Default flag diameter in selectors and rows.
 * @property flagSizeCompact Flag diameter in the compact pill.
 * @property flagSizeRow Flag diameter in list rows (design: 30px).
 * @property flagAspectRatioWidthMultiplier Width multiplier for the `orig` flag shape, which keeps a
 *   flag's real 4:3 proportions instead of cropping it into a circle.
 * @property chevronSize Chevron icon size.
 * @property rowMinHeight List row height (design: 56px).
 * @property rowHorizontalPadding Row horizontal padding (design: 24px).
 * @property rowVerticalPadding Row vertical padding.
 * @property rowContentSpacing Gap between row flag, text and trailing control.
 * @property checkIconSize Single-select check mark.
 * @property checkboxSize Multi-select checkbox.
 * @property searchFieldHeight Search field height (design: 56px).
 * @property searchHorizontalMargin Search field margin inside the sheet.
 * @property searchContentSpacing Gap between search icon, input and trailing button.
 * @property searchBorderWidth Search field stroke when focused.
 * @property sectionHeaderTopPadding Space above a section header.
 * @property sectionHeaderBottomPadding Space below a section header.
 * @property sheetHorizontalPadding Sheet content inset.
 * @property sheetHeaderStartPadding Sheet title inset (design: 24px).
 * @property currentSelectionPadding Inner padding of the "Current selection" card.
 * @property regionChipHeight Region filter chip height (design: 32px) — its 48dp target comes from
 *   the surrounding row padding.
 * @property regionChipHorizontalPadding Region chip inner padding.
 * @property regionChipSpacing Gap between region chips.
 * @property minimumTouchTarget Accessibility floor applied to every clickable element.
 * @property iconButtonSize Close/clear icon button size.
 * @property phoneFieldMinHeightWithLabel Unified phone field height with its floating label visible.
 * @property phoneFieldMinHeightNoLabel Unified phone field height with the label hidden — shorter,
 *   since no space needs to be reserved for it.
 */
@Immutable
data class CountryPickerDimensions(
    val selectorMinHeight: Dp = 56.dp,
    val selectorOutlinedMinHeight: Dp = 60.dp,
    val selectorCompactMinHeight: Dp = 48.dp,
    val selectorFlagOnlyMinHeight: Dp = 48.dp,
    val selectorDialMinHeight: Dp = 48.dp,
    val selectorHorizontalPadding: Dp = 16.dp,
    val selectorVerticalPadding: Dp = 8.dp,
    val selectorContentSpacing: Dp = 16.dp,
    val selectorBorderWidth: Dp = 1.dp,
    val selectorFocusedBorderWidth: Dp = 2.dp,
    val flagSize: Dp = 28.dp,
    val flagSizeCompact: Dp = 22.dp,
    val flagSizeRow: Dp = 30.dp,
    val flagAspectRatioWidthMultiplier: Float = 4f / 3f,
    val chevronSize: Dp = 24.dp,
    val rowMinHeight: Dp = 56.dp,
    val rowHorizontalPadding: Dp = 24.dp,
    val rowVerticalPadding: Dp = 8.dp,
    val rowContentSpacing: Dp = 16.dp,
    val checkIconSize: Dp = 24.dp,
    val checkboxSize: Dp = 20.dp,
    val searchFieldHeight: Dp = 56.dp,
    val searchHorizontalMargin: Dp = 16.dp,
    val searchContentSpacing: Dp = 12.dp,
    val searchBorderWidth: Dp = 2.dp,
    val sectionHeaderTopPadding: Dp = 12.dp,
    val sectionHeaderBottomPadding: Dp = 6.dp,
    val sheetHorizontalPadding: Dp = 16.dp,
    val sheetHeaderStartPadding: Dp = 24.dp,
    val currentSelectionPadding: Dp = 14.dp,
    val regionChipHeight: Dp = 32.dp,
    val regionChipHorizontalPadding: Dp = 14.dp,
    val regionChipSpacing: Dp = 8.dp,
    val minimumTouchTarget: Dp = 48.dp,
    val iconButtonSize: Dp = 48.dp,
    /** Unified phone field height when its floating label is visible (target range 64–72dp). */
    val phoneFieldMinHeightWithLabel: Dp = 68.dp,
    /** Unified phone field height with the label hidden (target range 56–64dp). */
    val phoneFieldMinHeightNoLabel: Dp = 60.dp,
)
