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

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.ezzy.ccp.R
import com.ezzy.ccp.countrypicker.model.CountryRegion
import com.ezzy.ccp.countrypicker.theme.CountryPickerColors
import com.ezzy.ccp.countrypicker.theme.CountryPickerDefaults
import com.ezzy.ccp.countrypicker.theme.CountryPickerDimensions
import com.ezzy.ccp.countrypicker.theme.CountryPickerMotion
import com.ezzy.ccp.countrypicker.theme.CountryPickerShapes
import com.ezzy.ccp.countrypicker.theme.CountryPickerTypography

/**
 * The horizontally scrollable region filter chips: All, Africa, Americas, Asia, Europe, Oceania.
 *
 * Uses [LazyRow] rather than a `Row` in a `horizontalScroll`, so the row makes no assumption about how
 * much width it has — on a narrow phone, at large font scale, or in a localization where "Americas" is
 * three times longer, the chips scroll instead of clipping.
 *
 * The selected chip is scrolled into view when it changes, which matters when a region is restored from
 * saved state and would otherwise be selected somewhere off-screen.
 *
 * @param selectedRegion The active filter, or `null` for "All".
 * @param onRegionSelected Called with the new filter. `null` means "All".
 * @param regions Regions to offer, from [com.ezzy.ccp.countrypicker.state.CountryPickerConfig.enabledRegions].
 */
@Composable
fun CountryRegionFilters(
    selectedRegion: CountryRegion?,
    onRegionSelected: (CountryRegion?) -> Unit,
    modifier: Modifier = Modifier,
    regions: List<CountryRegion> = CountryRegion.entries,
    colors: CountryPickerColors = CountryPickerDefaults.colors(),
    shapes: CountryPickerShapes = CountryPickerDefaults.shapes(),
    dimensions: CountryPickerDimensions = CountryPickerDefaults.dimensions(),
    typography: CountryPickerTypography = CountryPickerDefaults.typography(),
    motion: CountryPickerMotion = CountryPickerDefaults.motion(),
) {
    val listState = rememberLazyListState()
    val filterLabel = stringResource(R.string.ccp_region_filter_label)

    // Index 0 is the "All" chip, so a region's chip index is its position in `regions` plus one.
    val selectedIndex = selectedRegion?.let { regions.indexOf(it).takeIf { i -> i >= 0 }?.plus(1) } ?: 0
    LaunchedEffect(selectedIndex) {
        listState.animateScrollToItem(selectedIndex)
    }

    LazyRow(
        state = listState,
        modifier = modifier.semantics { contentDescription = filterLabel },
        contentPadding = PaddingValues(horizontal = dimensions.searchHorizontalMargin),
        horizontalArrangement = Arrangement.spacedBy(dimensions.regionChipSpacing),
    ) {
        item(key = ALL_CHIP_KEY) {
            RegionChip(
                label = stringResource(R.string.ccp_region_all),
                selected = selectedRegion == null,
                onClick = { onRegionSelected(null) },
                colors = colors,
                shapes = shapes,
                dimensions = dimensions,
                typography = typography,
                motion = motion,
            )
        }
        items(count = regions.size, key = { regions[it].key }) { index ->
            val region = regions[index]
            RegionChip(
                label = stringResource(region.labelRes),
                selected = selectedRegion == region,
                onClick = { onRegionSelected(region) },
                colors = colors,
                shapes = shapes,
                dimensions = dimensions,
                typography = typography,
                motion = motion,
            )
        }
    }
}

/**
 * One filter chip.
 *
 * `Modifier.clip` before `Modifier.clickable` gives the ripple the chip's rounded bounds. The chip
 * reports `Role.Tab` and its `selected` state, so a screen reader announces "Africa, selected" rather
 * than leaving the user to infer it from the fill.
 */
@Composable
private fun RegionChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    colors: CountryPickerColors,
    shapes: CountryPickerShapes,
    dimensions: CountryPickerDimensions,
    typography: CountryPickerTypography,
    motion: CountryPickerMotion,
) {
    val container by animateColorAsState(
        targetValue = if (selected) {
            colors.regionChipSelectedContainer
        } else {
            colors.regionChipContainer
        },
        animationSpec = motion.colorSpec,
        label = "regionChipContainer",
    )
    val content by animateColorAsState(
        targetValue = if (selected) {
            colors.regionChipSelectedContent
        } else {
            colors.regionChipContent
        },
        animationSpec = motion.colorSpec,
        label = "regionChipContent",
    )
    val border by animateColorAsState(
        // The selected chip drops its outline: fill and outline together read as a heavier,
        // pressed-looking chip rather than a selected one.
        targetValue = if (selected) {
            androidx.compose.ui.graphics.Color.Transparent
        } else {
            colors.regionChipBorder
        },
        animationSpec = motion.colorSpec,
        label = "regionChipBorder",
    )

    Box(
        modifier = Modifier
            // The visual chip is 32dp; the vertical padding lifts the touch target to 48dp without
            // making the chip itself look oversized.
            .padding(vertical = CHIP_TOUCH_PADDING)
            .clip(shapes.regionChip)
            .background(container)
            .border(CHIP_BORDER_WIDTH, border, shapes.regionChip)
            .clickable(role = Role.Tab, onClick = onClick)
            .defaultMinSize(minHeight = dimensions.regionChipHeight)
            .padding(horizontal = dimensions.regionChipHorizontalPadding)
            .semantics {
                this.selected = selected
                role = Role.Tab
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(text = label, style = typography.regionChipLabel, color = content)
    }
}

private const val ALL_CHIP_KEY = "__all__"
private val CHIP_BORDER_WIDTH = 1.dp

/** Half the gap between the 32dp chip and the 48dp target, applied above and below. */
private val CHIP_TOUCH_PADDING = 8.dp
