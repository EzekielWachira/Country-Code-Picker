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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ezzy.ccp.R
import com.ezzy.ccp.countrypicker.theme.CountryPickerColors
import com.ezzy.ccp.countrypicker.theme.CountryPickerDefaults
import com.ezzy.ccp.countrypicker.theme.CountryPickerDimensions
import com.ezzy.ccp.countrypicker.theme.CountryPickerShapes
import com.ezzy.ccp.countrypicker.theme.CountryPickerTypography
import com.ezzy.ccp.icons.EzzyIcons
import com.ezzy.ccp.icons.Search

/**
 * The sheet's "no results" state.
 *
 * Echoes the query back so the user can see exactly what was searched — a bare "No results" leaves
 * them unsure whether the typo is theirs or the app's — and names the other things they can search by,
 * since not everyone knows dial codes work. The Clear action is a real button, not just advice.
 *
 * The whole block is an assertive live region so a screen-reader user learns their query returned
 * nothing without having to explore the list to find out.
 */
@Composable
fun CountrySearchEmptyState(
    query: String,
    onClearSearch: () -> Unit,
    modifier: Modifier = Modifier,
    colors: CountryPickerColors = CountryPickerDefaults.colors(),
    shapes: CountryPickerShapes = CountryPickerDefaults.shapes(),
    typography: CountryPickerTypography = CountryPickerDefaults.typography(),
) {
    val title = if (query.isBlank()) {
        stringResource(R.string.ccp_empty_title)
    } else {
        stringResource(R.string.ccp_empty_title_query, query)
    }

    SheetStateBlock(
        icon = { tint ->
            Icon(
                imageVector = EzzyIcons.Search,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(STATE_ICON_SIZE),
            )
        },
        title = title,
        body = stringResource(R.string.ccp_empty_body),
        actionLabel = stringResource(R.string.ccp_clear_search),
        onAction = onClearSearch,
        modifier = modifier.semantics { liveRegion = LiveRegionMode.Assertive },
        colors = colors,
        shapes = shapes,
        typography = typography,
    )
}

/** The sheet's load-failure state, with a Retry action. */
@Composable
fun CountryErrorState(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    offline: Boolean = false,
    colors: CountryPickerColors = CountryPickerDefaults.colors(),
    shapes: CountryPickerShapes = CountryPickerDefaults.shapes(),
    typography: CountryPickerTypography = CountryPickerDefaults.typography(),
) {
    SheetStateBlock(
        icon = { tint ->
            Icon(
                imageVector = PickerIcons.Alert,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(STATE_ICON_SIZE),
            )
        },
        title = stringResource(
            if (offline) R.string.ccp_offline_title else R.string.ccp_error_title,
        ),
        // Both messages state that the user's existing selection is untouched: a failed load must not
        // leave them wondering whether the app also lost what they had already chosen.
        body = stringResource(
            if (offline) R.string.ccp_offline_body else R.string.ccp_error_body,
        ),
        actionLabel = stringResource(R.string.ccp_retry),
        onAction = onRetry,
        modifier = modifier.semantics { liveRegion = LiveRegionMode.Assertive },
        colors = colors,
        shapes = shapes,
        typography = typography,
    )
}

/**
 * The state for a configuration that filters out every country.
 *
 * Distinct from the search-empty state, and deliberately actionless: nothing the *user* can do fixes
 * an allow-list that excludes everything, so offering them a button would be misleading.
 */
@Composable
fun CountryNoneAvailableState(
    modifier: Modifier = Modifier,
    colors: CountryPickerColors = CountryPickerDefaults.colors(),
    shapes: CountryPickerShapes = CountryPickerDefaults.shapes(),
    typography: CountryPickerTypography = CountryPickerDefaults.typography(),
) {
    SheetStateBlock(
        icon = { tint ->
            Icon(
                imageVector = PickerIcons.Globe,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(STATE_ICON_SIZE),
            )
        },
        title = stringResource(R.string.ccp_none_available_title),
        body = stringResource(R.string.ccp_none_available_body),
        actionLabel = null,
        onAction = {},
        modifier = modifier,
        colors = colors,
        shapes = shapes,
        typography = typography,
    )
}

/**
 * Skeleton rows shown while a remote country source loads.
 *
 * Skeletons rather than a spinner because they preview the shape of what is coming, so the list does
 * not appear to jump into existence. Row widths vary so the placeholder reads as text rather than as a
 * progress bar.
 */
@Composable
fun CountryLoadingState(
    modifier: Modifier = Modifier,
    rowCount: Int = SKELETON_ROW_COUNT,
    colors: CountryPickerColors = CountryPickerDefaults.colors(),
    dimensions: CountryPickerDimensions = CountryPickerDefaults.dimensions(),
) {
    val loadingLabel = stringResource(R.string.ccp_loading_countries)
    val transition = rememberInfiniteTransition(label = "skeletonShimmer")
    val shimmerAlpha by transition.animateFloat(
        initialValue = SHIMMER_MIN_ALPHA,
        targetValue = SHIMMER_MAX_ALPHA,
        animationSpec = infiniteRepeatable(
            animation = tween(SHIMMER_DURATION_MILLIS),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "skeletonAlpha",
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = loadingLabel },
    ) {
        repeat(rowCount) { index ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensions.rowMinHeight)
                    .padding(horizontal = dimensions.rowHorizontalPadding),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimensions.rowContentSpacing),
            ) {
                Box(
                    modifier = Modifier
                        .size(dimensions.flagSizeRow)
                        .clip(RoundedCornerShape(percent = 50))
                        .background(colors.flagPlaceholderContainer)
                        .alpha(shimmerAlpha),
                )
                Box(
                    modifier = Modifier
                        // Deterministic pseudo-random widths: varied enough to look like names,
                        // stable enough not to reshuffle on recomposition.
                        .fillMaxWidth(SKELETON_MIN_WIDTH + (index * SKELETON_WIDTH_STEP) % SKELETON_WIDTH_RANGE)
                        .height(SKELETON_LINE_HEIGHT)
                        .clip(RoundedCornerShape(percent = 50))
                        .background(colors.flagPlaceholderContainer)
                        .alpha(shimmerAlpha),
                )
            }
        }
    }
}

/** Shared layout for the icon + title + body + action states. */
@Composable
private fun SheetStateBlock(
    icon: @Composable (androidx.compose.ui.graphics.Color) -> Unit,
    title: String,
    body: String,
    actionLabel: String?,
    onAction: () -> Unit,
    colors: CountryPickerColors,
    shapes: CountryPickerShapes,
    typography: CountryPickerTypography,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = STATE_HORIZONTAL_PADDING, vertical = STATE_VERTICAL_PADDING),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(STATE_ICON_CONTAINER_SIZE)
                .clip(RoundedCornerShape(percent = 50))
                .background(colors.flagPlaceholderContainer),
            contentAlignment = Alignment.Center,
        ) {
            icon(colors.sheetSecondaryContent)
        }

        Spacer(Modifier.height(STATE_ICON_SPACING))

        Text(
            text = title,
            style = typography.countryName,
            color = colors.sheetContent,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(STATE_TITLE_SPACING))

        Text(
            text = body,
            style = typography.sheetSubtitle,
            color = colors.sheetSecondaryContent,
            textAlign = TextAlign.Center,
        )

        if (actionLabel != null) {
            Spacer(Modifier.height(STATE_ACTION_SPACING))
            Button(
                onClick = onAction,
                shape = shapes.button,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.selectedRowContainer,
                    contentColor = colors.selectedRowContent,
                ),
            ) {
                Text(text = actionLabel, style = typography.buttonLabel)
            }
        }
    }
}

private val STATE_ICON_SIZE = 26.dp
private val STATE_ICON_CONTAINER_SIZE = 56.dp
private val STATE_ICON_SPACING = 16.dp
private val STATE_TITLE_SPACING = 6.dp
private val STATE_ACTION_SPACING = 20.dp
private val STATE_HORIZONTAL_PADDING = 32.dp
private val STATE_VERTICAL_PADDING = 40.dp
private val SKELETON_LINE_HEIGHT = 14.dp
private const val SKELETON_ROW_COUNT = 9
private const val SKELETON_MIN_WIDTH = 0.38f
private const val SKELETON_WIDTH_STEP = 0.13f
private const val SKELETON_WIDTH_RANGE = 0.44f
private const val SHIMMER_MIN_ALPHA = 0.4f
private const val SHIMMER_MAX_ALPHA = 1f
private const val SHIMMER_DURATION_MILLIS = 700
