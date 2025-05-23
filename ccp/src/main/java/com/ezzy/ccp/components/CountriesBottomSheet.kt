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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ezzy.ccp.CountryViewModel
import com.ezzy.ccp.data.countryList
import com.ezzy.ccp.icons.EzzyIcons
import com.ezzy.ccp.icons.Grid
import com.ezzy.ccp.icons.List
import com.ezzy.ccp.model.Country
import com.ezzy.ccp.state.SearchState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountriesBottomSheet(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    onSelectCountries: (Country) -> Unit,
    containerColor: Color = Color.White,
    cornerRadius: Dp = 10.dp,
    viewModel: CountryViewModel = viewModel(),
    searchTextColor: Color = Color.Black,
    sheetState: SheetState,
    countriesToShow: List<String> = emptyList(), // listOf(US, UK, FR, KE ...etc)'
    showHeader: Boolean = true,
) {

    val countries by viewModel.countries.collectAsStateWithLifecycle(emptyMap())
    val searchState by viewModel.searchState.collectAsStateWithLifecycle()

    LaunchedEffect(countriesToShow) {
        if (countriesToShow.isNotEmpty()) {
            viewModel.setCountriesToShow(countriesToShow)
        }
    }

    ModalBottomSheet(
        sheetState = sheetState,
        containerColor = containerColor,
        onDismissRequest = onDismiss,
        modifier = modifier,
        shape = RoundedCornerShape(topEnd = cornerRadius, topStart = cornerRadius),
        dragHandle = {}
    ) {
        SheetContent(
            countriesState = countries,
            onSelectCountries = onSelectCountries,
            containerColor = containerColor,
            searchState = searchState,
            onValueChange = viewModel::updateSearchQuery,
            searchTextColor = searchTextColor,
            showHeader = showHeader
        )
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SheetContent(
    modifier: Modifier = Modifier,
    countriesState: Map<Char, List<Country>> = countryList.sortedBy {
        it.name[0]
    }.groupBy { country -> country.name[0] },
    onSelectCountries: (Country) -> Unit = {},
    containerColor: Color = Color.White,
    headerColor: Color = Color.Black,
    headerStyle: TextStyle = MaterialTheme.typography.titleMedium,
    searchState: SearchState = SearchState(),
    onValueChange: (String) -> Unit = {},
    searchTextColor: Color = Color.Black,
    showHeader: Boolean = true,
) {


    Surface(
        modifier = modifier,
        color = containerColor,
    ) {


        Column {
            Spacer(modifier = Modifier.height(60.dp))

            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                SearchComponent(
                    searchState = searchState,
                    onValueChange = onValueChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    searchTextColor = searchTextColor
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                countriesState.forEach { (initial, countries) ->
                    if (showHeader) {
                        stickyHeader {
                            CountryHeader(
                                header = initial.toString(),
                                headerColor = headerColor,
                                headerStyle = headerStyle,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }

                    items(countries) {
                        CountryItem(
                            country = it,
                            onClick = onSelectCountries,
                            modifier = Modifier.animateItem(
                                fadeInSpec = null,
                                fadeOutSpec = null,
                                placementSpec = tween(durationMillis = 500)
                            ),
                            borderColor = Color.White
                        )
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun SortComponent(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = Color.White,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(width = 1.dp, color = Color.Black.copy(alpha = .1f))
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                color = Color.White,
                onClick = {},
                shape = RoundedCornerShape(bottomStart = 10.dp, topStart = 10.dp)
            ) {
                Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = EzzyIcons.List,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(
                modifier = modifier
                    .background(color = Color.Black.copy(alpha = .1f))
                    .width(1.dp)
                    .height(50.dp)
            )
            Surface(
                color = Color.White,
                onClick = {},
                shape = RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp)
            ) {
                Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = EzzyIcons.Grid,
                        contentDescription = null,
                        tint = Color.Black,
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
    headerDividerColor: Color = Color.Black.copy(alpha = .1f)
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = header,
            color = headerColor,
            style = headerStyle,
        )

        HorizontalDivider(color = headerDividerColor)
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