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

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ezzy.ccp.countrypicker.model.Country
import com.ezzy.ccp.countrypicker.theme.CountryFlagStyle
import com.ezzy.ccp.countrypicker.theme.CountryPickerStyle
import com.ezzy.ccp.countrypicker.theme.CountryPickerTheme
import com.ezzy.ccp.resources.Res
import com.ezzy.ccp.resources.ccp_clear_search
import com.ezzy.ccp.resources.ccp_did_you_mean
import com.ezzy.ccp.resources.ccp_empty_body
import com.ezzy.ccp.resources.ccp_empty_title
import com.ezzy.ccp.resources.ccp_empty_title_query
import com.ezzy.ccp.resources.ccp_error_body
import com.ezzy.ccp.resources.ccp_error_title
import com.ezzy.ccp.resources.ccp_loading_countries
import com.ezzy.ccp.resources.ccp_none_available_body
import com.ezzy.ccp.resources.ccp_none_available_title
import com.ezzy.ccp.resources.ccp_offline_body
import com.ezzy.ccp.resources.ccp_offline_title
import com.ezzy.ccp.resources.ccp_retry
import org.jetbrains.compose.resources.stringResource

/**
 * Shown when a search matches nothing.
 *
 * A dead end is the worst outcome of a search, so this offers two ways forward: the countries the
 * query nearly matched ("Did you mean Kenya?", for "Kenia"), each one tap away, and a button that
 * clears the search.
 *
 * @param suggestions Near matches, closest first — see
 *   [com.ezzy.ccp.countrypicker.state.CountryPickerState.searchSuggestions].
 * @param onSuggestionClick Invoked with the suggestion the user tapped.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
public fun CountrySearchEmptyState(
    query: String,
    onClearSearch: () -> Unit,
    modifier: Modifier = Modifier,
    suggestions: List<Country> = emptyList(),
    onSuggestionClick: (Country) -> Unit = {},
    style: CountryPickerStyle = CountryPickerTheme.style,
) {
    StateLayout(
        icon = PickerIcons.Search,
        tint = style.colors.accent,
        title = if (query.isBlank()) {
            stringResource(Res.string.ccp_empty_title)
        } else {
            stringResource(Res.string.ccp_empty_title_query, query.trim())
        },
        body = stringResource(Res.string.ccp_empty_body),
        actionLabel = stringResource(Res.string.ccp_clear_search),
        onAction = onClearSearch,
        modifier = modifier,
        style = style,
    ) {
        if (suggestions.isNotEmpty() && style.layout.showSearchSuggestions) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(top = 4.dp, bottom = 10.dp),
            ) {
                Icon(PickerIcons.Sparkle, contentDescription = null, tint = style.colors.accent, modifier = Modifier.size(13.dp))
                Text(
                    text = stringResource(Res.string.ccp_did_you_mean),
                    style = style.typography.sectionLabel,
                    color = style.colors.textSecondary,
                )
            }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 20.dp),
            ) {
                suggestions.forEach { country ->
                    SuggestionChip(country, onClick = { onSuggestionClick(country) }, style = style)
                }
            }
        }
    }
}

/** A suggested country: its flag and name on a raised capsule. */
@Composable
private fun SuggestionChip(country: Country, onClick: () -> Unit, style: CountryPickerStyle) {
    val colors = style.colors
    val interaction = remember { MutableInteractionSource() }
    val scale by pressScale(interaction)
    Row(
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .pickerShadow(style.elevation.tile, style.shapes.chip, colors.shadow)
            .clip(style.shapes.chip)
            .background(colors.surfaceRaised)
            .border(0.75.dp, colors.hairline, style.shapes.chip)
            .clickable(interactionSource = interaction, indication = null, role = Role.Button, onClick = onClick)
            .padding(start = 6.dp, end = 14.dp, top = 6.dp, bottom = 6.dp)
            .semantics { contentDescription = country.displayName },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        CountryFlag(country = country, size = 26.dp, style = CountryFlagStyle.Circle)
        SingleLineText(text = country.displayName, style = style.typography.chip, color = colors.textPrimary)
    }
}

/**
 * Shown when loading the country list fails.
 *
 * @param offline Shows the offline wording and icon instead of the generic error.
 */
@Composable
public fun CountryErrorState(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    offline: Boolean = false,
    style: CountryPickerStyle = CountryPickerTheme.style,
) {
    StateLayout(
        icon = if (offline) PickerIcons.Offline else PickerIcons.Alert,
        tint = if (offline) style.colors.warning else style.colors.error,
        title = stringResource(if (offline) Res.string.ccp_offline_title else Res.string.ccp_error_title),
        body = stringResource(if (offline) Res.string.ccp_offline_body else Res.string.ccp_error_body),
        actionLabel = stringResource(Res.string.ccp_retry),
        onAction = onRetry,
        actionIsPrimary = true,
        modifier = modifier,
        style = style,
    )
}

/**
 * Shown when the configuration allows no countries at all — almost always an integration mistake,
 * which is why it says so plainly instead of rendering an empty list.
 */
@Composable
public fun CountryNoneAvailableState(
    modifier: Modifier = Modifier,
    style: CountryPickerStyle = CountryPickerTheme.style,
) {
    StateLayout(
        icon = PickerIcons.Globe,
        tint = style.colors.textSecondary,
        title = stringResource(Res.string.ccp_none_available_title),
        body = stringResource(Res.string.ccp_none_available_body),
        modifier = modifier,
        style = style,
    )
}

/**
 * Placeholder rows shaped like the real list, with a highlight sweeping across them, while the
 * country list loads. Announced once to screen readers as "Loading countries".
 */
@Composable
public fun CountryLoadingState(
    modifier: Modifier = Modifier,
    rowCount: Int = SKELETON_ROW_COUNT,
    style: CountryPickerStyle = CountryPickerTheme.style,
) {
    val colors = style.colors
    val dimensions = style.dimensions
    val brush = rememberShimmerBrush(colors)
    val label = stringResource(Res.string.ccp_loading_countries)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = dimensions.groupHorizontalMargin, vertical = 6.dp)
            .semantics {
                contentDescription = label
                liveRegion = LiveRegionMode.Polite
            },
    ) {
        Box(
            Modifier
                .padding(start = 4.dp, bottom = 10.dp)
                .size(width = 96.dp, height = 10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(brush),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(style.shapes.groupCornerRadius))
                .background(colors.surface)
                .border(0.75.dp, colors.hairline, RoundedCornerShape(style.shapes.groupCornerRadius)),
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
                    Box(Modifier.size(dimensions.flagSizeRow).clip(style.shapes.flagTile).background(brush))
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Varying widths read as text; identical bars read as a progress meter.
                        val width = SKELETON_WIDTHS[index % SKELETON_WIDTHS.size]
                        Box(Modifier.fillMaxWidth(width).height(12.dp).clip(RoundedCornerShape(6.dp)).background(brush))
                    }
                    Box(Modifier.size(width = 34.dp, height = 12.dp).clip(RoundedCornerShape(6.dp)).background(brush))
                }
            }
        }
    }
}

/** The shared layout of the empty, error and unavailable states. */
@Composable
private fun StateLayout(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: androidx.compose.ui.graphics.Color,
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: () -> Unit = {},
    actionIsPrimary: Boolean = false,
    style: CountryPickerStyle,
    extra: @Composable () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        StateIllustration(icon = icon, tint = tint)
        Spacer(Modifier.height(18.dp))
        Text(
            text = title,
            style = style.typography.emptyTitle,
            color = style.colors.textPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.semantics {
                heading()
                liveRegion = LiveRegionMode.Polite
            },
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = body,
            style = style.typography.emptyBody,
            color = style.colors.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 320.dp),
        )
        Spacer(Modifier.height(20.dp))
        extra()
        if (actionLabel != null) {
            PickerButton(
                onClick = onAction,
                kind = if (actionIsPrimary) PickerButtonKind.Primary else PickerButtonKind.Secondary,
            ) {
                Text(actionLabel)
            }
        }
    }
}

private const val SKELETON_ROW_COUNT = 8
private val SKELETON_WIDTHS = listOf(0.62f, 0.45f, 0.7f, 0.52f, 0.38f, 0.66f, 0.48f, 0.58f)
