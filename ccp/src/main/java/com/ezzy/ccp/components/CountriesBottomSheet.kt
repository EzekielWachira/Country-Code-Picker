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

import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ezzy.ccp.data.countryList
import com.ezzy.ccp.icons.EzzyIcons
import com.ezzy.ccp.icons.Grid
import com.ezzy.ccp.icons.List
import com.ezzy.ccp.model.CCPColors
import com.ezzy.ccp.model.CCPConfig
import com.ezzy.ccp.model.Country
import com.ezzy.ccp.model.CountryListStyle
import com.ezzy.ccp.state.CountrySearchState
import com.ezzy.ccp.state.SearchState
import com.ezzy.ccp.state.rememberCountrySearchState
import com.ezzy.ccp.utils.CCPDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountriesBottomSheet(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    onSelectCountries: (Country) -> Unit,
    sheetState: SheetState,
    countriesToShow: List<String> = emptyList(),
    countriesExclude: List<String> = emptyList(),
    pinnedCountries: List<String> = emptyList(),
    ccpColors: CCPColors = CCPDefaults.colors(),
    ccpConfig: CCPConfig = CCPDefaults.defaultConfig()
) {
    val searchState = rememberCountrySearchState(countriesToShow, countriesExclude, pinnedCountries)
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    ModalBottomSheet(
        sheetState = sheetState,
        containerColor = ccpColors.ccpSheetColor.containerColor,
        onDismissRequest = onDismiss,
        modifier = modifier.padding(top = statusBarHeight),
        shape = ccpConfig.countriesSheetShape,
    ) {
        SheetContent(
            searchState = searchState,
            onSelectCountries = onSelectCountries,
            ccpColors = ccpColors,
            ccpConfig = ccpConfig
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SheetContent(
    modifier: Modifier = Modifier,
    searchState: CountrySearchState = rememberCountrySearchState(),
    onSelectCountries: (Country) -> Unit = {},
    ccpColors: CCPColors = CCPDefaults.colors(),
    ccpConfig: CCPConfig = CCPDefaults.defaultConfig()
) {
    var listStyle by remember { mutableStateOf(ccpConfig.defaultCountryListStyle) }

    Surface(modifier = modifier, color = ccpColors.ccpSheetColor.containerColor) {
        Column {
            // Search bar + sort toggle
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SearchComponent(
                    searchState = SearchState(query = searchState.query),
                    onValueChange = searchState::updateQuery,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    ccpColors = ccpColors,
                    ccpConfig = ccpConfig,
                    searchHint = "Search country by name, code or dial code"
                )
                SortComponent(
                    listStyle = listStyle,
                    onToggle = { listStyle = it },
                    ccpColors = ccpColors
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (listStyle == CountryListStyle.List) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    // Pinned / suggested section
                    if (searchState.pinnedList.isNotEmpty()) {
                        stickyHeader {
                            CountryHeader(
                                header = "Suggested",
                                headerColor = ccpColors.ccpSheetColor.countryHeaderColor,
                                headerStyle = ccpConfig.headerStyle,
                                modifier = Modifier.padding(horizontal = 16.dp),
                                headerDividerColor = ccpColors.ccpSheetColor.headerDividerColor,
                                showDivider = ccpConfig.showCountriesHeaderDivider
                            )
                        }
                        items(searchState.pinnedList, key = { "pinned_${it.code}" }) { country ->
                            CountryItem(
                                country = country,
                                onClick = onSelectCountries,
                                modifier = Modifier.animateItem(
                                    fadeInSpec = null,
                                    fadeOutSpec = null,
                                    placementSpec = tween(durationMillis = 300)
                                ),
                                ccpColors = ccpColors,
                                ccpConfig = ccpConfig
                            )
                        }
                        item {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = ccpColors.ccpSheetColor.headerDividerColor
                            )
                        }
                    }

                    // Main alphabetical list
                    searchState.filteredCountries.forEach { (initial, countries) ->
                        if (ccpConfig.showHeader) {
                            stickyHeader {
                                CountryHeader(
                                    header = initial.toString(),
                                    headerColor = ccpColors.ccpSheetColor.countryHeaderColor,
                                    headerStyle = ccpConfig.headerStyle,
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    headerDividerColor = ccpColors.ccpSheetColor.headerDividerColor,
                                    showDivider = ccpConfig.showCountriesHeaderDivider
                                )
                            }
                        }
                        items(countries, key = { it.code }) { country ->
                            CountryItem(
                                country = country,
                                onClick = onSelectCountries,
                                modifier = Modifier.animateItem(
                                    fadeInSpec = null,
                                    fadeOutSpec = null,
                                    placementSpec = tween(durationMillis = 300)
                                ),
                                ccpColors = ccpColors,
                                ccpConfig = ccpConfig
                            )
                        }
                    }
                    item { Spacer(modifier = Modifier.height(20.dp)) }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    searchState.filteredCountries.forEach { (initial, countries) ->
                        if (ccpConfig.showHeader) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                CountryHeader(
                                    header = initial.toString(),
                                    headerColor = ccpColors.ccpSheetColor.countryHeaderColor,
                                    headerStyle = ccpConfig.headerStyle,
                                    headerDividerColor = ccpColors.ccpSheetColor.headerDividerColor,
                                    showDivider = ccpConfig.showCountriesHeaderDivider
                                )
                            }
                        }
                        items(countries, key = { it.code }) { country ->
                            CountryGridItem(
                                country = country,
                                onClick = onSelectCountries,
                                ccpColors = ccpColors,
                                ccpConfig = ccpConfig
                            )
                        }
                    }
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SortComponent(
    modifier: Modifier = Modifier,
    listStyle: CountryListStyle = CountryListStyle.List,
    onToggle: (CountryListStyle) -> Unit = {},
    ccpColors: CCPColors = CCPDefaults.colors()
) {
    val activeColor = MaterialTheme.colorScheme.primary
    val inactiveColor = ccpColors.ccpSheetColor.searchIconTint
    val containerColor = ccpColors.ccpSheetColor.searchContainerColor

    Surface(
        modifier = modifier,
        color = containerColor,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(width = 1.dp, color = ccpColors.ccpSheetColor.searchBorderColor)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                color = Color.Transparent,
                onClick = { onToggle(CountryListStyle.List) },
                shape = RoundedCornerShape(bottomStart = 10.dp, topStart = 10.dp),
                modifier = Modifier.semantics {
                    contentDescription = "List view"
                    role = Role.Button
                }
            ) {
                Box(modifier = Modifier.padding(10.dp), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = EzzyIcons.List,
                        contentDescription = null,
                        tint = if (listStyle == CountryListStyle.List) activeColor else inactiveColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(
                modifier = Modifier
                    .background(color = ccpColors.ccpSheetColor.searchBorderColor)
                    .width(1.dp)
                    .height(40.dp)
            )
            Surface(
                color = Color.Transparent,
                onClick = { onToggle(CountryListStyle.Grid) },
                shape = RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp),
                modifier = Modifier.semantics {
                    contentDescription = "Grid view"
                    role = Role.Button
                }
            ) {
                Box(modifier = Modifier.padding(10.dp), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = EzzyIcons.Grid,
                        contentDescription = null,
                        tint = if (listStyle == CountryListStyle.Grid) activeColor else inactiveColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CountryHeader(
    modifier: Modifier = Modifier,
    header: String,
    headerColor: Color = Color.Black,
    headerStyle: TextStyle = MaterialTheme.typography.titleMedium,
    headerDividerColor: Color = Color.Black.copy(alpha = .1f),
    showDivider: Boolean = true
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Text(text = header, color = headerColor, style = headerStyle)
        if (showDivider) {
            HorizontalDivider(color = headerDividerColor)
        }
    }
}

@Preview
@Composable
private fun SortComponentPreview() {
    SortComponent()
}

@Preview
@Composable
private fun SheetContentPreview() {
    SheetContent()
}
