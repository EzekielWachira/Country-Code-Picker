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

package com.ezzy.ccp.countrypicker.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.ezzy.ccp.R
import com.ezzy.ccp.countrypicker.model.Country
import com.ezzy.ccp.countrypicker.model.CountrySelectionMode
import com.ezzy.ccp.countrypicker.state.CountryPickerConfig
import com.ezzy.ccp.countrypicker.state.CountrySection
import com.ezzy.ccp.countrypicker.state.CountrySectionKind
import com.ezzy.ccp.countrypicker.theme.CountryFlagShape
import com.ezzy.ccp.countrypicker.theme.CountryPickerColors
import com.ezzy.ccp.countrypicker.theme.CountryPickerDefaults
import com.ezzy.ccp.countrypicker.theme.CountryPickerDimensions
import com.ezzy.ccp.countrypicker.theme.CountryPickerMotion
import com.ezzy.ccp.countrypicker.theme.CountryPickerShapes
import com.ezzy.ccp.countrypicker.theme.CountryPickerTypography

/**
 * The grouped, scrollable country list.
 *
 * ### Performance
 * The whole world is ~236 rows, and this list has to stay smooth on low-end hardware:
 *
 * - Item keys are **ISO alpha-2 codes**, never list indices. An index key makes Compose reuse the
 *   wrong slot when the list is filtered, which shows up as check marks appearing on the wrong rows.
 *   The dial code would be worse still — `+1` is not unique.
 * - `contentType` is set per row so Compose reuses row slots across sections instead of recreating them.
 * - `animateItem` is applied only to *placement*, not to fades. A fade on every item makes each frame
 *   of a fast scroll composite ~10 semi-transparent layers.
 * - The reveal stagger is capped at [CountryPickerMotion.maxStaggeredItems] rows and is skipped
 *   entirely while searching — animating rows the user is filtering through fights their input.
 *
 * @param sections Already grouped and deduplicated by
 *   [com.ezzy.ccp.countrypicker.state.CountryPickerState].
 * @param selectedCountries Countries to show as selected.
 * @param onCountryClick Row activation.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CountryList(
    sections: List<CountrySection>,
    selectedCountries: Set<Country>,
    onCountryClick: (Country) -> Unit,
    modifier: Modifier = Modifier,
    config: CountryPickerConfig = CountryPickerConfig(),
    listState: LazyListState = rememberLazyListState(),
    disabledCountries: Set<String> = emptySet(),
    isSearching: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(bottom = LIST_BOTTOM_PADDING),
    colors: CountryPickerColors = CountryPickerDefaults.colors(),
    shapes: CountryPickerShapes = CountryPickerDefaults.shapes(),
    dimensions: CountryPickerDimensions = CountryPickerDefaults.dimensions(),
    typography: CountryPickerTypography = CountryPickerDefaults.typography(),
    motion: CountryPickerMotion = CountryPickerDefaults.motion(),
    flagContent: (@Composable (Country) -> Unit)? = null,
    listItemContent: (@Composable (CountryListItemScope) -> Unit)? = null,
) {
    val flagShape = if (config.flagsVisible) config.flagShape else CountryFlagShape.Hidden

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxWidth(),
        contentPadding = contentPadding,
    ) {
        sections.forEach { section ->
            if (section.kind.titleRes != null) {
                // Sticky headers: with four sections and a long tail, a header that scrolls away
                // leaves the user unable to tell which group a row belongs to.
                stickyHeader(key = "header_${section.kind.name}", contentType = HEADER_CONTENT_TYPE) {
                    CountrySectionHeader(
                        section = section,
                        showCount = config.isMultiSelect && section.kind == CountrySectionKind.Selected,
                        colors = colors,
                        dimensions = dimensions,
                        typography = typography,
                    )
                }
            }

            countrySectionItems(
                section = section,
                selectedCountries = selectedCountries,
                disabledCountries = disabledCountries,
                config = config,
                isSearching = isSearching,
                flagShape = flagShape,
                colors = colors,
                shapes = shapes,
                dimensions = dimensions,
                typography = typography,
                motion = motion,
                flagContent = flagContent,
                listItemContent = listItemContent,
                onCountryClick = onCountryClick,
            )
        }
    }
}

/**
 * Emits one section's rows.
 *
 * Extracted so the key/contentType/animation decisions live in one place rather than being repeated
 * per section, and so the item lambda stays small enough for Compose to skip cheaply.
 */
private fun androidx.compose.foundation.lazy.LazyListScope.countrySectionItems(
    section: CountrySection,
    selectedCountries: Set<Country>,
    disabledCountries: Set<String>,
    config: CountryPickerConfig,
    isSearching: Boolean,
    flagShape: CountryFlagShape,
    colors: CountryPickerColors,
    shapes: CountryPickerShapes,
    dimensions: CountryPickerDimensions,
    typography: CountryPickerTypography,
    motion: CountryPickerMotion,
    flagContent: (@Composable (Country) -> Unit)?,
    listItemContent: (@Composable (CountryListItemScope) -> Unit)?,
    onCountryClick: (Country) -> Unit,
) {
    items(
        count = section.items.size,
        // ISO alpha-2 scoped by section: a country appears in exactly one section, but scoping keeps
        // keys unique even if a future config allowed overlap.
        key = { index -> "${section.kind.name}_${section.items[index].country.iso2Code}" },
        contentType = { ROW_CONTENT_TYPE },
    ) { index ->
        val match = section.items[index]
        val country = match.country
        val selected = country in selectedCountries
        val enabled = country.iso2Code !in disabledCountries

        val scope = CountryListItemScope(
            match = match,
            selected = selected,
            enabled = enabled,
            selectionMode = config.selectionMode,
            onClick = { onCountryClick(country) },
        )

        // Placement-only animation, so a country moving into the Selected section slides rather than
        // teleporting — without paying for a fade on every row during a scroll.
        val itemModifier = if (isSearching || motion.staggerPerItemMillis == 0) {
            Modifier
        } else {
            Modifier.animateItem(
                fadeInSpec = null,
                fadeOutSpec = null,
                placementSpec = motion.listItemPlacement,
            )
        }

        if (listItemContent != null) {
            androidx.compose.foundation.layout.Box(modifier = itemModifier) { listItemContent(scope) }
        } else {
            CountryListItem(
                match = match,
                selected = selected,
                onClick = scope.onClick,
                modifier = itemModifier,
                enabled = enabled,
                selectionMode = config.selectionMode,
                showIsoCode = config.showIsoCode,
                showDialCode = config.showDialCode,
                highlightMatches = config.highlightSearchMatches,
                unavailable = !enabled,
                flagShape = flagShape,
                colors = colors,
                shapes = shapes,
                dimensions = dimensions,
                typography = typography,
                motion = motion,
                flagContent = flagContent,
            )
        }
    }
}

/**
 * An uppercase, accent-colored section header.
 *
 * Carries `heading()` semantics so screen-reader users can jump between sections with heading
 * navigation instead of scrolling through every country.
 *
 * The header is opaque: as a sticky header it scrolls over rows, and a transparent one would show them
 * through the text.
 */
@Composable
fun CountrySectionHeader(
    section: CountrySection,
    modifier: Modifier = Modifier,
    showCount: Boolean = false,
    colors: CountryPickerColors = CountryPickerDefaults.colors(),
    dimensions: CountryPickerDimensions = CountryPickerDefaults.dimensions(),
    typography: CountryPickerTypography = CountryPickerDefaults.typography(),
) {
    val titleRes = section.kind.titleRes ?: return
    val title = if (showCount) {
        stringResource(R.string.ccp_section_selected_count, section.count)
    } else {
        stringResource(titleRes)
    }

    Text(
        text = title.uppercase(),
        style = typography.sectionHeader,
        color = colors.sectionLabel,
        modifier = modifier
            .fillMaxWidth()
            .background(colors.sheetContainer)
            .padding(
                start = dimensions.rowHorizontalPadding,
                end = dimensions.rowHorizontalPadding,
                top = dimensions.sectionHeaderTopPadding,
                bottom = dimensions.sectionHeaderBottomPadding,
            )
            .semantics { heading() },
    )
}

/** Marks header slots so Compose does not reuse a header composition for a row. */
private const val HEADER_CONTENT_TYPE = "ccp_section_header"

/** Marks row slots so Compose reuses row compositions across sections. */
private const val ROW_CONTENT_TYPE = "ccp_country_row"

private val LIST_BOTTOM_PADDING = 8.dp

/** Convenience for a fully-configured list driven straight off picker state. */
@Composable
fun CountryList(
    state: com.ezzy.ccp.countrypicker.state.CountryPickerState,
    onCountryClick: (Country) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    colors: CountryPickerColors = CountryPickerDefaults.colors(),
    shapes: CountryPickerShapes = CountryPickerDefaults.shapes(),
    dimensions: CountryPickerDimensions = CountryPickerDefaults.dimensions(),
    typography: CountryPickerTypography = CountryPickerDefaults.typography(),
    motion: CountryPickerMotion = CountryPickerDefaults.motion(),
    flagContent: (@Composable (Country) -> Unit)? = null,
    listItemContent: (@Composable (CountryListItemScope) -> Unit)? = null,
) {
    val sections by state.sections
    val pending by state.pendingSelection
    val isSearching by state.isSearching

    CountryList(
        sections = sections,
        // Multi-select renders the pending set so ticking a row is visible immediately. Single-select
        // falls back to the confirmed value, which is what the sheet shows before the user taps
        // anything.
        selectedCountries = when {
            state.config.selectionMode == CountrySelectionMode.Multiple -> pending
            pending.isNotEmpty() -> pending
            else -> state.confirmedSelection
        },
        onCountryClick = onCountryClick,
        modifier = modifier,
        config = state.config,
        listState = listState,
        disabledCountries = state.config.disabledCountryCodes,
        isSearching = isSearching,
        colors = colors,
        shapes = shapes,
        dimensions = dimensions,
        typography = typography,
        motion = motion,
        flagContent = flagContent,
        listItemContent = listItemContent,
    )
}
