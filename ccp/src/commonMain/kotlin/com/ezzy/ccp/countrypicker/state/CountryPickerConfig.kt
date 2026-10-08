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

package com.ezzy.ccp.countrypicker.state

import androidx.compose.runtime.Immutable
import com.ezzy.ccp.countrypicker.model.Country
import com.ezzy.ccp.countrypicker.model.CountryRegion
import com.ezzy.ccp.countrypicker.model.CountrySearchField
import com.ezzy.ccp.countrypicker.model.CountrySelectionMode
import com.ezzy.ccp.countrypicker.theme.CountryFlagShape
import com.ezzy.ccp.countrypicker.theme.CountryFlagStyle

/**
 * Everything about *how* a picker behaves, in one value.
 *
 * This exists instead of two dozen boolean parameters on [
 * com.ezzy.ccp.countrypicker.ui.CountrySelector]. Booleans on a composable signature cannot be named
 * at a call site in a readable order, cannot be reused across selectors that should behave
 * identically, and cannot be built up by a host's own defaults layer. A config value can:
 *
 * ```kotlin
 * val marketPicker = CountryPickerDefaults.config(
 *     allowedCountryCodes = supportedMarkets,
 *     showDialCode = false,
 *     showRegionFilters = false,
 * )
 * // reused by every market selector in the app
 * ```
 *
 * Instances are [Immutable], so passing one to a composable does not defeat skipping.
 *
 * @property selectionMode Single or multi selection. Drives the row's trailing control (check vs.
 *   checkbox) and whether the sheet gets a confirm/cancel footer.
 * @property allowedCountryCodes When non-null, the only ISO alpha-2 codes offered. An **empty set is
 *   meaningful**: it allows nothing, and the sheet shows its "no countries available" state rather
 *   than silently falling back to everything.
 * @property excludedCountryCodes Codes removed from the list. Applied after [allowedCountryCodes],
 *   so exclusion always wins.
 * @property disabledCountryCodes Codes shown but not selectable — the design's "Not available" rows.
 *   Prefer this over exclusion when the user needs to understand *why* their country is missing.
 * @property enabledRegions Regions offered as filter chips. Narrow it for a regional product.
 * @property initialRegion Region pre-selected when the sheet opens, or `null` for "All".
 * @property showCurrentSelection Show the "Current selection" card at the top of the sheet.
 * @property showRecentlySelected Show the "Recently selected" section. Requires a
 *   [com.ezzy.ccp.countrypicker.persistence.RecentCountryStore]; with the default no-op store the
 *   section simply never appears.
 * @property showSuggestedCountries Show the "Suggested" section from [suggestedCountryCodes].
 * @property showRegionFilters Show the region chip row.
 * @property showSearch Show the search field.
 * @property showIsoCode Show the ISO alpha-2 code as row/selector metadata.
 * @property showDialCode Show the dial code as row/selector metadata.
 * @property showFlag Show flags. Superseded by `flagShape = Hidden`, which is the more expressive way
 *   to say the same thing.
 * @property flagShape How flags are masked.
 * @property rowFlagStyle Whether a background is drawn behind each row's flag — see
 *   [CountryFlagStyle]. Defaults to [CountryFlagStyle.Plain] (no background), matching the flag-only
 *   look used elsewhere in the picker; pass [CountryFlagStyle.TonalContainer] to restore the sheet's
 *   original tonal-circle look.
 * @property closeOnSingleSelection Dismiss the sheet after a single selection. Disable for a picker
 *   the user is expected to browse.
 * @property minimumSelectionCount Multi-select floor. Confirm stays disabled below it.
 * @property maximumSelectionCount Multi-select ceiling, or `null` for unlimited. Further taps are
 *   refused with feedback rather than silently ignored.
 * @property autoConfirmOnMaximum Confirm automatically once [maximumSelectionCount] is reached.
 *   Reasonable for `max = 1`; surprising above that, so it defaults to off.
 * @property searchFields Which fields search matches against.
 * @property minimumSearchQueryLength Characters before search filters. `1` — local search is instant,
 *   so there is no reason to make the user type more.
 * @property highlightSearchMatches Highlight the matched substring in country names.
 * @property showResultCount Show "12 results" while searching.
 * @property suggestedCountryCodes Codes for the "Suggested" section. **Deliberately empty by
 *   default**: which countries deserve promotion is a product decision, and a library that ships
 *   `["US","GB"]` as a universal default is asserting something false about most apps.
 * @property recentCountryLimit How many recents to display.
 * @property countryComparator Custom ordering within the "All countries" section, or `null` for
 *   alphabetical by display name.
 */
@Immutable
public data class CountryPickerConfig(
    val selectionMode: CountrySelectionMode = CountrySelectionMode.Single,
    val allowedCountryCodes: Set<String>? = null,
    val excludedCountryCodes: Set<String> = emptySet(),
    val disabledCountryCodes: Set<String> = emptySet(),
    val enabledRegions: Set<CountryRegion> = CountryRegion.entries.toSet(),
    val initialRegion: CountryRegion? = null,
    val showCurrentSelection: Boolean = true,
    val showRecentlySelected: Boolean = true,
    val showSuggestedCountries: Boolean = true,
    val showRegionFilters: Boolean = true,
    val showSearch: Boolean = true,
    val showIsoCode: Boolean = false,
    val showDialCode: Boolean = false,
    val showFlag: Boolean = true,
    val flagShape: CountryFlagShape = CountryFlagShape.Circle,
    val rowFlagStyle: CountryFlagStyle = CountryFlagStyle.Plain,
    val closeOnSingleSelection: Boolean = true,
    val minimumSelectionCount: Int = 0,
    val maximumSelectionCount: Int? = null,
    val autoConfirmOnMaximum: Boolean = false,
    val searchFields: Set<CountrySearchField> = CountrySearchField.All,
    val minimumSearchQueryLength: Int = 1,
    val highlightSearchMatches: Boolean = true,
    val showResultCount: Boolean = true,
    val showAlphabetIndex: Boolean = false,
    val suggestedCountryCodes: List<String> = emptyList(),
    val recentCountryLimit: Int = 5,
    val countryComparator: Comparator<Country>? = null,
) {
    init {
        require(minimumSelectionCount >= 0) { "minimumSelectionCount must not be negative" }
        require(maximumSelectionCount == null || maximumSelectionCount >= 1) {
            "maximumSelectionCount must be at least 1 when set"
        }
        require(maximumSelectionCount == null || maximumSelectionCount >= minimumSelectionCount) {
            "maximumSelectionCount ($maximumSelectionCount) must be >= " +
                "minimumSelectionCount ($minimumSelectionCount)"
        }
        require(minimumSearchQueryLength >= 1) { "minimumSearchQueryLength must be at least 1" }
        require(recentCountryLimit >= 0) { "recentCountryLimit must not be negative" }
    }

    /** True when this config drives a multi-selection picker. */
    val isMultiSelect: Boolean get() = selectionMode == CountrySelectionMode.Multiple

    /** True when flags should be drawn at all. */
    val flagsVisible: Boolean get() = showFlag && flagShape != CountryFlagShape.Hidden

    /** True when [count] satisfies the min/max bounds, i.e. Confirm should be enabled. */
    public fun isSelectionCountValid(count: Int): Boolean =
        count >= minimumSelectionCount && (maximumSelectionCount == null || count <= maximumSelectionCount)

    /** True when [count] has hit the ceiling and further additions must be refused. */
    public fun isAtMaximum(count: Int): Boolean =
        maximumSelectionCount != null && count >= maximumSelectionCount
}
