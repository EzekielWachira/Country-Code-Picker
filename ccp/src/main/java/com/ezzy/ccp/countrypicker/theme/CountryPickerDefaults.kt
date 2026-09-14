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

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Text styles for the picker, all derived from the host's [MaterialTheme.typography].
 *
 * Nothing here invents a font size from scratch except where the design calls for a treatment
 * Material has no role for — the uppercase, wide-tracked section header. Everything else is a
 * Material role, so the picker inherits the host's type scale, its font family, and correct scaling
 * with the user's font-size preference.
 *
 * @property screenTitle Host screen headline (sample screen only).
 * @property sheetTitle Sheet title — "Select country".
 * @property sheetSubtitle Sheet subtitle.
 * @property fieldLabel Selector label above the value.
 * @property selectorValue Selected country name in the selector.
 * @property selectorSecondary ISO/dial line under the country name.
 * @property compactValue Country name in the compact pill.
 * @property dialCodeValue Dial code in the phone prefix selector.
 * @property countryName Country name in a list row.
 * @property countryMetadata ISO/dial metadata in a list row.
 * @property sectionHeader Uppercase section header.
 * @property resultCount Search result count.
 * @property buttonLabel Footer button labels.
 * @property helperText Helper and error text.
 * @property badgeLabel "Detected" chip label.
 * @property regionChipLabel Region filter chip label.
 * @property searchInput Search query text.
 */
@Immutable
data class CountryPickerTypography(
    val screenTitle: TextStyle,
    val sheetTitle: TextStyle,
    val sheetSubtitle: TextStyle,
    val fieldLabel: TextStyle,
    val selectorValue: TextStyle,
    val selectorSecondary: TextStyle,
    val compactValue: TextStyle,
    val dialCodeValue: TextStyle,
    val countryName: TextStyle,
    val countryMetadata: TextStyle,
    val sectionHeader: TextStyle,
    val resultCount: TextStyle,
    val buttonLabel: TextStyle,
    val helperText: TextStyle,
    val badgeLabel: TextStyle,
    val regionChipLabel: TextStyle,
    val searchInput: TextStyle,
)

/**
 * Component defaults for the country picker.
 *
 * Every UI entry point takes its colors, shapes, dimensions, typography and motion from here, so a
 * host can retheme the whole picker by passing one modified value rather than threading parameters
 * through nested composables:
 *
 * ```kotlin
 * CountrySelector(
 *     selectedCountry = country,
 *     onCountrySelected = { country = it },
 *     colors = CountryPickerDefaults.colors(selectorContainer = brand.fieldBackground),
 * )
 * ```
 */
object CountryPickerDefaults {

    /**
     * The default single-selection configuration: search, region filters, grouped sections, flags,
     * and dismiss-on-select.
     *
     * Metadata (ISO and dial code) is off by default — in a residence picker it is noise. The phone
     * prefix selector turns the dial code on explicitly.
     */
    fun config(
        allowedCountryCodes: Set<String>? = null,
        excludedCountryCodes: Set<String> = emptySet(),
        showIsoCode: Boolean = false,
        showDialCode: Boolean = false,
        showRegionFilters: Boolean = true,
        showSearch: Boolean = true,
        suggestedCountryCodes: List<String> = emptyList(),
    ): com.ezzy.ccp.countrypicker.state.CountryPickerConfig =
        com.ezzy.ccp.countrypicker.state.CountryPickerConfig(
            selectionMode = com.ezzy.ccp.countrypicker.model.CountrySelectionMode.Single,
            allowedCountryCodes = allowedCountryCodes,
            excludedCountryCodes = excludedCountryCodes,
            showIsoCode = showIsoCode,
            showDialCode = showDialCode,
            showRegionFilters = showRegionFilters,
            showSearch = showSearch,
            suggestedCountryCodes = suggestedCountryCodes,
        )

    /**
     * The default multi-selection configuration.
     *
     * Differs from [config] in more than the mode: metadata is shown, because a multi-select list is
     * usually about markets or coverage where the ISO code is the thing the user is actually
     * reconciling against; and the sheet stays open, because closing after each tick would make
     * multi-select unusable.
     */
    fun multiSelectConfig(
        allowedCountryCodes: Set<String>? = null,
        excludedCountryCodes: Set<String> = emptySet(),
        minimumSelectionCount: Int = 1,
        maximumSelectionCount: Int? = null,
        showIsoCode: Boolean = true,
        showDialCode: Boolean = true,
        suggestedCountryCodes: List<String> = emptyList(),
    ): com.ezzy.ccp.countrypicker.state.CountryPickerConfig =
        com.ezzy.ccp.countrypicker.state.CountryPickerConfig(
            selectionMode = com.ezzy.ccp.countrypicker.model.CountrySelectionMode.Multiple,
            allowedCountryCodes = allowedCountryCodes,
            excludedCountryCodes = excludedCountryCodes,
            showIsoCode = showIsoCode,
            showDialCode = showDialCode,
            closeOnSingleSelection = false,
            minimumSelectionCount = minimumSelectionCount,
            maximumSelectionCount = maximumSelectionCount,
            suggestedCountryCodes = suggestedCountryCodes,
        )

    /**
     * Configuration for the phone country-code picker: dial codes shown, search matching dial codes,
     * no "current selection" card (the field beside it already shows the choice).
     */
    fun phoneConfig(
        allowedCountryCodes: Set<String>? = null,
        excludedCountryCodes: Set<String> = emptySet(),
        suggestedCountryCodes: List<String> = emptyList(),
    ): com.ezzy.ccp.countrypicker.state.CountryPickerConfig =
        com.ezzy.ccp.countrypicker.state.CountryPickerConfig(
            selectionMode = com.ezzy.ccp.countrypicker.model.CountrySelectionMode.Single,
            allowedCountryCodes = allowedCountryCodes,
            excludedCountryCodes = excludedCountryCodes,
            showIsoCode = false,
            showDialCode = true,
            showCurrentSelection = false,
            suggestedCountryCodes = suggestedCountryCodes,
        )

    /**
     * Semantic colors resolved from the host's [MaterialTheme.colorScheme].
     *
     * The mapping mirrors the design's CSS custom properties exactly, so light and dark both work
     * out of the box on any M3 color scheme — including a dynamic-color one, which is why nothing
     * here is a hardcoded hue. The single exception is [success]: Material has no success role, so it
     * falls back to [CountryPickerTokens], picked by [isSystemInDarkTheme].
     */
    @Composable
    @ReadOnlyComposable
    fun colors(
        selectorContainer: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
        selectorContent: Color = MaterialTheme.colorScheme.onSurface,
        selectorLabel: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        selectorSecondaryContent: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        selectorBorder: Color = MaterialTheme.colorScheme.outline,
        selectorFocusedBorder: Color = MaterialTheme.colorScheme.primary,
        selectorDisabledContainer: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f),
        selectorDisabledContent: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        chevron: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        error: Color = MaterialTheme.colorScheme.error,
        success: Color = if (isSystemInDarkTheme()) {
            CountryPickerTokens.SuccessDark
        } else {
            CountryPickerTokens.SuccessLight
        },
        sheetContainer: Color = MaterialTheme.colorScheme.surfaceContainerLow,
        sheetContent: Color = MaterialTheme.colorScheme.onSurface,
        sheetSecondaryContent: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        dragHandle: Color = MaterialTheme.colorScheme.outlineVariant,
        scrim: Color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.32f),
        searchContainer: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
        searchFocusedBorder: Color = MaterialTheme.colorScheme.primary,
        searchContent: Color = MaterialTheme.colorScheme.onSurface,
        searchPlaceholder: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        searchHighlight: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.28f),
        sectionLabel: Color = MaterialTheme.colorScheme.primary,
        rowContainer: Color = Color.Transparent,
        rowContent: Color = MaterialTheme.colorScheme.onSurface,
        rowSecondaryContent: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        rowDisabledContent: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        selectedRowContainer: Color = MaterialTheme.colorScheme.secondaryContainer,
        selectedRowContent: Color = MaterialTheme.colorScheme.onSecondaryContainer,
        checkIcon: Color = MaterialTheme.colorScheme.primary,
        checkboxChecked: Color = MaterialTheme.colorScheme.primary,
        checkboxUnchecked: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        currentSelectionContainer: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
        detectedBadgeContainer: Color = MaterialTheme.colorScheme.secondaryContainer,
        detectedBadgeContent: Color = MaterialTheme.colorScheme.onSecondaryContainer,
        regionChipContainer: Color = Color.Transparent,
        regionChipSelectedContainer: Color = MaterialTheme.colorScheme.secondaryContainer,
        regionChipContent: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        regionChipSelectedContent: Color = MaterialTheme.colorScheme.onSecondaryContainer,
        regionChipBorder: Color = MaterialTheme.colorScheme.outlineVariant,
        flagPlaceholderContainer: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
        flagPlaceholderContent: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    ): CountryPickerColors = CountryPickerColors(
        selectorContainer = selectorContainer,
        selectorContent = selectorContent,
        selectorLabel = selectorLabel,
        selectorSecondaryContent = selectorSecondaryContent,
        selectorBorder = selectorBorder,
        selectorFocusedBorder = selectorFocusedBorder,
        selectorDisabledContainer = selectorDisabledContainer,
        selectorDisabledContent = selectorDisabledContent,
        chevron = chevron,
        error = error,
        success = success,
        sheetContainer = sheetContainer,
        sheetContent = sheetContent,
        sheetSecondaryContent = sheetSecondaryContent,
        dragHandle = dragHandle,
        scrim = scrim,
        searchContainer = searchContainer,
        searchFocusedBorder = searchFocusedBorder,
        searchContent = searchContent,
        searchPlaceholder = searchPlaceholder,
        searchHighlight = searchHighlight,
        sectionLabel = sectionLabel,
        rowContainer = rowContainer,
        rowContent = rowContent,
        rowSecondaryContent = rowSecondaryContent,
        rowDisabledContent = rowDisabledContent,
        selectedRowContainer = selectedRowContainer,
        selectedRowContent = selectedRowContent,
        checkIcon = checkIcon,
        checkboxChecked = checkboxChecked,
        checkboxUnchecked = checkboxUnchecked,
        currentSelectionContainer = currentSelectionContainer,
        detectedBadgeContainer = detectedBadgeContainer,
        detectedBadgeContent = detectedBadgeContent,
        regionChipContainer = regionChipContainer,
        regionChipSelectedContainer = regionChipSelectedContainer,
        regionChipContent = regionChipContent,
        regionChipSelectedContent = regionChipSelectedContent,
        regionChipBorder = regionChipBorder,
        flagPlaceholderContainer = flagPlaceholderContainer,
        flagPlaceholderContent = flagPlaceholderContent,
    )

    /** Shapes. Override individual corners without restating the rest. */
    fun shapes(
        selectorFilled: androidx.compose.ui.graphics.Shape? = null,
        selectorOutlined: androidx.compose.ui.graphics.Shape? = null,
        sheet: androidx.compose.ui.graphics.Shape? = null,
        searchField: androidx.compose.ui.graphics.Shape? = null,
        row: androidx.compose.ui.graphics.Shape? = null,
    ): CountryPickerShapes {
        val base = CountryPickerShapes()
        return base.copy(
            selectorFilled = selectorFilled ?: base.selectorFilled,
            selectorOutlined = selectorOutlined ?: base.selectorOutlined,
            sheet = sheet ?: base.sheet,
            searchField = searchField ?: base.searchField,
            row = row ?: base.row,
        )
    }

    /** Sizes and spacing. */
    fun dimensions(): CountryPickerDimensions = CountryPickerDimensions()

    /**
     * Motion specs, already respecting the system animator duration scale.
     *
     * Pass `respectSystemAnimationScale = false` only when the host has its own reduced-motion
     * handling and would otherwise apply the reduction twice.
     */
    @Composable
    @ReadOnlyComposable
    fun motion(respectSystemAnimationScale: Boolean = true): CountryPickerMotion {
        val base = CountryPickerMotion()
        return if (respectSystemAnimationScale) base.respectingSystemAnimationScale() else base
    }

    /**
     * Text styles derived from the host's type scale.
     *
     * [CountryPickerTypography.sectionHeader] is the one style built by hand: the design's uppercase, 0.8px-tracked accent
     * header has no Material equivalent, so it is `labelMedium` with the tracking and weight the
     * design specifies rather than an arbitrary invented style.
     */
    @Composable
    @ReadOnlyComposable
    fun typography(): CountryPickerTypography {
        val type = MaterialTheme.typography
        return CountryPickerTypography(
            screenTitle = type.headlineSmall,
            sheetTitle = type.titleLarge,
            sheetSubtitle = type.bodyMedium,
            fieldLabel = type.bodySmall,
            selectorValue = type.bodyLarge,
            selectorSecondary = type.bodySmall,
            compactValue = type.labelLarge,
            dialCodeValue = type.bodyLarge.copy(fontWeight = FontWeight.Medium),
            countryName = type.bodyLarge,
            countryMetadata = type.bodySmall,
            sectionHeader = type.labelMedium.copy(
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.8.sp,
            ),
            resultCount = type.bodySmall,
            buttonLabel = type.labelLarge,
            helperText = type.bodySmall,
            badgeLabel = type.labelSmall.copy(fontWeight = FontWeight.Medium),
            regionChipLabel = type.labelLarge,
            searchInput = type.bodyLarge,
        )
    }
}
