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

package com.ezzy.ccp.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ezzy.ccp.model.CCPColors
import com.ezzy.ccp.model.CCPConfig
import com.ezzy.ccp.model.Country
import com.ezzy.ccp.state.SearchState
import com.ezzy.ccp.state.rememberCountrySearchState
import com.ezzy.ccp.utils.CCPDefaults
import com.ezzy.ccp.utils.countryToFlagEmoji

/**
 * A compact, searchable dropdown for country selection, anchored below the country
 * selector button. Intended as an alternative to [CountriesBottomSheet] for tablets,
 * landscape layouts, or forms where a full-screen modal feels too heavy.
 *
 * Controlled by the caller via [expanded] / [onDismiss], matching the pattern of
 * Material3's [DropdownMenu].
 *
 * Note: the country list uses [Column] + [verticalScroll] rather than [LazyColumn]
 * because [DropdownMenu] measures its children's intrinsic sizes to size the popup,
 * and SubcomposeLayout-based components (LazyColumn) do not support intrinsic measurement.
 * Search filtering keeps the rendered count small in practice.
 *
 * @param expanded Whether the dropdown is currently open.
 * @param onDismiss Called when the user taps outside or selects a country.
 * @param onSelectCountry Called with the chosen [Country].
 * @param countriesToShow Whitelist of ISO codes to display. Empty = all countries.
 * @param countriesExclude ISO codes to hide from the list.
 * @param pinnedCountries ISO codes shown in a "Suggested" section at the top.
 * @param ccpColors Color configuration.
 * @param ccpConfig Behavior and UI configuration (header style, flag visibility, etc.).
 */
@Composable
fun CountriesDropdown(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onSelectCountry: (Country) -> Unit,
    countriesToShow: List<String> = emptyList(),
    countriesExclude: List<String> = emptyList(),
    pinnedCountries: List<String> = emptyList(),
    ccpColors: CCPColors = CCPDefaults.colors(),
    ccpConfig: CCPConfig = CCPDefaults.defaultConfig(),
    modifier: Modifier = Modifier,
    dropdownOffset: DpOffset = DpOffset.Zero
) {
    val searchState = rememberCountrySearchState(countriesToShow, countriesExclude, pinnedCountries)

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        offset = dropdownOffset,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(ccpColors.ccpSheetColor.containerColor)
    ) {
        // ── Search field (always visible at the top) ──────────────────────────────
        Box(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 6.dp)
                .fillMaxWidth()
        ) {
            SearchComponent(
                searchState = SearchState(query = searchState.query),
                onValueChange = searchState::updateQuery,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                ccpColors = ccpColors,
                ccpConfig = ccpConfig,
                searchHint = "Search for countries"
            )
        }

        HorizontalDivider(color = ccpColors.ccpSheetColor.headerDividerColor)

        // ── Scrollable country list ───────────────────────────────────────────────
        // Column + verticalScroll is used instead of LazyColumn because DropdownMenu
        // queries intrinsic measurements of its children to compute the popup height,
        // and LazyColumn (SubcomposeLayout) does not support intrinsic measurement.
        Column(
            modifier = Modifier
                .heightIn(max = 300.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Pinned / suggested section
            if (searchState.pinnedList.isNotEmpty()) {
                DropdownSectionHeader(
                    title = "Suggested",
                    ccpColors = ccpColors,
                    ccpConfig = ccpConfig
                )
                searchState.pinnedList.forEach { country ->
                    CountryDropdownItem(
                        country = country,
                        ccpColors = ccpColors,
                        ccpConfig = ccpConfig,
                        onSelect = {
                            onSelectCountry(it)
                            onDismiss()
                        }
                    )
                }
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    color = ccpColors.ccpSheetColor.headerDividerColor
                )
            }

            // Alphabetical groups
            searchState.filteredCountries.forEach { (initial, countries) ->
                if (ccpConfig.showHeader) {
                    DropdownSectionHeader(
                        title = initial.toString(),
                        ccpColors = ccpColors,
                        ccpConfig = ccpConfig
                    )
                }
                countries.forEach { country ->
                    CountryDropdownItem(
                        country = country,
                        ccpColors = ccpColors,
                        ccpConfig = ccpConfig,
                        onSelect = {
                            onSelectCountry(it)
                            onDismiss()
                        }
                    )
                }
            }
        }
    }
}

// ── Private helpers ───────────────────────────────────────────────────────────────────

@Composable
private fun DropdownSectionHeader(
    title: String,
    ccpColors: CCPColors,
    ccpConfig: CCPConfig
) {
    DropdownMenuItem(
        text = {
            Text(
                text = title,
                style = ccpConfig.headerStyle,
                color = ccpColors.ccpSheetColor.countryHeaderColor
            )
        },
        onClick = {},
        enabled = false,
        modifier = Modifier.padding(horizontal = 4.dp)
    )
}

@Composable
private fun CountryDropdownItem(
    country: Country,
    ccpColors: CCPColors,
    ccpConfig: CCPConfig,
    onSelect: (Country) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    DropdownMenuItem(
        text = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (ccpConfig.showFlagCountryItem) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = country.code.countryToFlagEmoji() ?: "",
                            fontSize = 24.sp
                        )
                    }
                }
                Text(
                    text = country.name,
                    style = ccpConfig.countryItemNameTextStyle,
                    color = ccpColors.ccpSheetColor.countryItemTextColor,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (ccpConfig.showDialCodeCountryItem) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(
                            width = 1.dp,
                            color = ccpColors.ccpSheetColor.headerDividerColor
                        ),
                        color = ccpColors.ccpSheetColor.containerColor
                    ) {
                        Text(
                            text = country.dialCode,
                            style = ccpConfig.countryItemDialCodeTextStyle,
                            color = ccpColors.ccpSheetColor.countryItemDialCodeTextColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        },
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onSelect(country)
        },
        modifier = Modifier.semantics {
            contentDescription = "${country.name}, ${country.dialCode}"
        }
    )
}
