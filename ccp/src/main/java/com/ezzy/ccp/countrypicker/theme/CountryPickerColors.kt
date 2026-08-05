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
import androidx.compose.ui.graphics.Color

/**
 * Semantic colors for every surface the country picker draws.
 *
 * These are *roles*, not raw values — `selectedRowContainer` rather than "light purple" — so a host
 * retheming the picker changes meaning rather than guessing which of twelve purples to override. The
 * names mirror the design's own CSS custom properties (`--selector-container`, `--row-selected-container`,
 * `--sheet-container`, …) one-for-one, which is what keeps the Compose implementation and the design
 * source verifiably in sync.
 *
 * Defaults come from [androidx.compose.material3.MaterialTheme.colorScheme] via
 * [CountryPickerDefaults.colors], so the picker inherits the host's theme — including dark mode —
 * without the host configuring anything. No composable in this library reads `colorScheme` directly;
 * they all go through an instance of this class.
 *
 * @property selectorContainer Filled selector background (`--selector-container`).
 * @property selectorContent Selected country name and primary selector text.
 * @property selectorLabel Floating/inline field label above the value.
 * @property selectorSecondaryContent ISO code and dial code shown under the country name.
 * @property selectorBorder Outlined selector stroke in its resting state.
 * @property selectorFocusedBorder Outlined selector stroke while focused. Drawn thicker too — see
 *   [CountryPickerDimensions.selectorFocusedBorderWidth].
 * @property selectorDisabledContainer Disabled selector background.
 * @property selectorDisabledContent Disabled text and icons. Must stay legible: a disabled field the
 *   user cannot read is worse than one they cannot edit.
 * @property chevron Trailing dropdown chevron.
 * @property error Error stroke, error text and error icon (`--selector-error`).
 * @property success Success stroke and confirmation text. Material 3 has no success role, so this is
 *   the library's own token — see [CountryPickerTokens].
 * @property sheetContainer Bottom-sheet background (`--sheet-container`).
 * @property sheetContent Sheet title and body text.
 * @property sheetSecondaryContent Sheet subtitle and metadata.
 * @property dragHandle Sheet drag handle.
 * @property scrim Behind-sheet scrim.
 * @property searchContainer Search field background in its resting, filled state.
 * @property searchFocusedBorder Search field stroke once focused — the morph from filled to outlined.
 * @property searchContent Query text.
 * @property searchPlaceholder Placeholder and leading search icon.
 * @property sectionLabel Uppercase section headers (SELECTED, SUGGESTED, …). Accent-colored.
 * @property rowContainer Unselected row background.
 * @property selectedRowContainer Selected row tint (`--row-selected-container`).
 * @property selectedRowContent Text on a selected row (`--row-selected-content`).
 * @property rowContent Country name on an unselected row.
 * @property rowSecondaryContent ISO/dial metadata under the country name.
 * @property rowDisabledContent Rows for countries the configuration disallows.
 * @property checkIcon Single-select check mark.
 * @property checkboxChecked Multi-select checkbox fill when checked.
 * @property checkboxUnchecked Multi-select checkbox outline when unchecked.
 * @property searchHighlight Background behind the matched substring in a country name.
 * @property currentSelectionContainer "Current selection" card background.
 * @property detectedBadgeContainer "✓ Detected" chip background.
 * @property detectedBadgeContent "✓ Detected" chip text and tick.
 * @property regionChipContainer Unselected region chip background.
 * @property regionChipSelectedContainer Selected region chip fill.
 * @property regionChipContent Unselected region chip text.
 * @property regionChipSelectedContent Selected region chip text.
 * @property regionChipBorder Unselected region chip outline.
 * @property flagPlaceholderContainer Backing for the ISO-code badge shown when a country has no flag.
 * @property flagPlaceholderContent The ISO code drawn in that badge.
 */
@Immutable
data class CountryPickerColors(
    // Selector
    val selectorContainer: Color,
    val selectorContent: Color,
    val selectorLabel: Color,
    val selectorSecondaryContent: Color,
    val selectorBorder: Color,
    val selectorFocusedBorder: Color,
    val selectorDisabledContainer: Color,
    val selectorDisabledContent: Color,
    val chevron: Color,
    val error: Color,
    val success: Color,
    // Sheet
    val sheetContainer: Color,
    val sheetContent: Color,
    val sheetSecondaryContent: Color,
    val dragHandle: Color,
    val scrim: Color,
    // Search
    val searchContainer: Color,
    val searchFocusedBorder: Color,
    val searchContent: Color,
    val searchPlaceholder: Color,
    val searchHighlight: Color,
    // List
    val sectionLabel: Color,
    val rowContainer: Color,
    val rowContent: Color,
    val rowSecondaryContent: Color,
    val rowDisabledContent: Color,
    val selectedRowContainer: Color,
    val selectedRowContent: Color,
    val checkIcon: Color,
    val checkboxChecked: Color,
    val checkboxUnchecked: Color,
    // Accents
    val currentSelectionContainer: Color,
    val detectedBadgeContainer: Color,
    val detectedBadgeContent: Color,
    val regionChipContainer: Color,
    val regionChipSelectedContainer: Color,
    val regionChipContent: Color,
    val regionChipSelectedContent: Color,
    val regionChipBorder: Color,
    val flagPlaceholderContainer: Color,
    val flagPlaceholderContent: Color,
) {
    /** The container color for a row, given whether it is selected. */
    fun rowContainer(selected: Boolean): Color =
        if (selected) selectedRowContainer else rowContainer

    /** The content color for a row, given selection and enablement. */
    fun rowContent(selected: Boolean, enabled: Boolean): Color = when {
        !enabled -> rowDisabledContent
        selected -> selectedRowContent
        else -> rowContent
    }
}

/**
 * The library's own color values — the *only* raw colors it defines.
 *
 * Everything else in [CountryPickerColors] maps to a Material 3 role. Success is the exception:
 * Material 3 has no success/positive role, and reusing `tertiary` for "residency confirmed" would be
 * semantically wrong and would break for any host whose tertiary is red-ish. So the picker defines
 * one, tuned to sit alongside the M3 baseline palette at the same tonal steps the design used
 * (`--ok` / `--ok-container`).
 *
 * Hosts with a real success color in their own design system should pass it to
 * [CountryPickerDefaults.colors] rather than accepting these.
 */
object CountryPickerTokens {
    /** Success content on a light background. Contrast ≥ 4.5:1 on M3 light surfaces. */
    val SuccessLight: Color = Color(0xFF2E6B4F)

    /** Success content on a dark background. Contrast ≥ 4.5:1 on M3 dark surfaces. */
    val SuccessDark: Color = Color(0xFF8FD8AE)
}
