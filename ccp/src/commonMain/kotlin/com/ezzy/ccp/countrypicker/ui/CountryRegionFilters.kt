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
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.ezzy.ccp.countrypicker.model.CountryRegion
import com.ezzy.ccp.countrypicker.theme.CountryPickerStyle
import com.ezzy.ccp.countrypicker.theme.CountryPickerTheme
import com.ezzy.ccp.resources.Res
import com.ezzy.ccp.resources.ccp_region_all
import com.ezzy.ccp.resources.ccp_region_filter_label
import org.jetbrains.compose.resources.stringResource

/**
 * The region filter: a recessed track holding "All" and each region, with a raised pill that slides
 * to the selected one.
 *
 * One moving highlight, rather than chips that each light up, makes the change of filter something
 * the eye can follow. The track scrolls horizontally on narrow screens and keeps the selected region
 * in view. Each option reports `Role.Tab` and its `selected` state, so a screen reader announces
 * "Africa, selected" rather than leaving the user to infer it from the highlight.
 *
 * @param counts Countries per region, shown beside each label when
 *   [com.ezzy.ccp.countrypicker.theme.CountryPickerLayout.showRegionCounts] is on. Regions missing
 *   from the map show no count.
 */
@Composable
public fun CountryRegionFilters(
    selectedRegion: CountryRegion?,
    onRegionSelected: (CountryRegion?) -> Unit,
    modifier: Modifier = Modifier,
    regions: List<CountryRegion> = CountryRegion.entries,
    counts: Map<CountryRegion, Int> = emptyMap(),
    contentPadding: PaddingValues = PaddingValues(horizontal = CountryPickerTheme.style.dimensions.sheetHorizontalPadding),
    style: CountryPickerStyle = CountryPickerTheme.style,
) {
    val colors = style.colors
    val dimensions = style.dimensions
    val shapes = style.shapes
    val motion = style.motion
    val haptics = rememberPickerHaptics()
    val density = LocalDensity.current
    val scrollState = rememberScrollState()
    val filterLabel = stringResource(Res.string.ccp_region_filter_label)
    val showCounts = style.layout.showRegionCounts && counts.isNotEmpty()

    // Measured positions of each option inside the track, keyed by region (null = "All").
    val bounds = remember { mutableStateMapOf<CountryRegion?, Pair<Dp, Dp>>() }
    val target = bounds[selectedRegion]
    // Kept as State and read in the offset lambda, so the slide re-places the pill each frame
    // rather than recomposing the row.
    val pillX = animateDpAsState(target?.first ?: 0.dp, motion.dp, label = "pillX")
    val pillWidth by animateDpAsState(target?.second ?: 0.dp, motion.dp, label = "pillWidth")

    LaunchedEffect(selectedRegion, target) {
        val (x, width) = target ?: return@LaunchedEffect
        // Keep the selected option comfortably inside the viewport rather than flush with its edge.
        val viewport = scrollState.viewportSize
        val left = with(density) { x.roundToPx() } - with(density) { 24.dp.roundToPx() }
        val right = with(density) { (x + width).roundToPx() } + with(density) { 24.dp.roundToPx() }
        when {
            left < scrollState.value -> scrollState.animateScrollTo(left.coerceAtLeast(0))
            right > scrollState.value + viewport -> scrollState.animateScrollTo(right - viewport)
        }
    }

    val trackFill = colors.surfaceSunken
    val raised = colors.background != colors.surface
    val pillFill = if (raised) colors.surface else colors.accentSoft

    Box(
        modifier = modifier
            .horizontalScrollFade(scrollState, EDGE_FADE)
            .horizontalScroll(scrollState)
            .padding(contentPadding)
            .semantics { contentDescription = filterLabel },
    ) {
        Box(
            modifier = Modifier
                .height(dimensions.chipHeight + TRACK_PADDING * 2)
                .background(trackFill, shapes.chip)
                .padding(TRACK_PADDING),
        ) {
            // The pill sits behind the labels and only appears once the selected option is measured.
            if (target != null) {
                Box(
                    modifier = Modifier
                        .offset { IntOffset(pillX.value.roundToPx(), 0) }
                        .width(pillWidth)
                        .fillMaxHeight()
                        .then(if (raised) Modifier.pickerShadow(style.elevation.tile, shapes.chip, colors.shadow) else Modifier)
                        .background(pillFill, shapes.chip),
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(dimensions.chipSpacing)) {
                RegionOption(
                    label = stringResource(Res.string.ccp_region_all),
                    count = if (showCounts) counts.values.sum() else null,
                    selected = selectedRegion == null,
                    onClick = {
                        if (selectedRegion != null) haptics.filter()
                        onRegionSelected(null)
                    },
                    onMeasured = { x, w -> bounds[null] = x to w },
                    style = style,
                    pillOnSurface = raised,
                )
                regions.forEach { region ->
                    RegionOption(
                        label = stringResource(region.labelRes),
                        count = if (showCounts) counts[region] else null,
                        selected = selectedRegion == region,
                        onClick = {
                            if (selectedRegion != region) haptics.filter()
                            onRegionSelected(region)
                        },
                        onMeasured = { x, w -> bounds[region] = x to w },
                        style = style,
                        pillOnSurface = raised,
                    )
                }
            }
        }
    }
}

@Composable
private fun RegionOption(
    label: String,
    count: Int?,
    selected: Boolean,
    onClick: () -> Unit,
    onMeasured: (x: Dp, width: Dp) -> Unit,
    style: CountryPickerStyle,
    pillOnSurface: Boolean,
) {
    val colors = style.colors
    val density = LocalDensity.current
    val interaction = remember { MutableInteractionSource() }
    val scale by pressScale(interaction)
    val selectedContent = if (pillOnSurface) colors.textPrimary else colors.onAccentSoft
    val content by animateColorAsState(
        targetValue = if (selected) selectedContent else colors.textSecondary,
        animationSpec = style.motion.color,
        label = "regionContent",
    )
    Row(
        modifier = Modifier
            .fillMaxHeight()
            .onPlaced { coordinates ->
                with(density) {
                    onMeasured(coordinates.positionInParent().x.toDp(), coordinates.size.width.toDp())
                }
            }
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(interactionSource = interaction, indication = null, role = Role.Tab, onClick = onClick)
            .padding(horizontal = style.dimensions.chipHorizontalPadding)
            .semantics {
                this.selected = selected
                role = Role.Tab
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = label,
            style = style.typography.chip.copy(fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium),
            color = content,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (count != null) {
            Text(
                text = count.toString(),
                style = style.typography.chip.copy(fontWeight = FontWeight.Medium),
                color = if (selected) content.copy(alpha = 0.55f) else colors.textTertiary,
                maxLines = 1,
            )
        }
    }
}

private val TRACK_PADDING = 3.dp
private val EDGE_FADE = 28.dp
