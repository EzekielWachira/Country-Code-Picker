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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.ezzy.ccp.R
import com.ezzy.ccp.countrypicker.model.Country
import com.ezzy.ccp.countrypicker.model.CountryMatch
import com.ezzy.ccp.countrypicker.model.CountrySearchField
import com.ezzy.ccp.countrypicker.model.CountrySelectionMode
import com.ezzy.ccp.countrypicker.theme.CountryFlagShape
import com.ezzy.ccp.countrypicker.theme.CountryFlagStyle
import com.ezzy.ccp.countrypicker.theme.CountryPickerColors
import com.ezzy.ccp.countrypicker.theme.CountryPickerDefaults
import com.ezzy.ccp.countrypicker.theme.CountryPickerDimensions
import com.ezzy.ccp.countrypicker.theme.CountryPickerMotion
import com.ezzy.ccp.countrypicker.theme.CountryPickerShapes
import com.ezzy.ccp.countrypicker.theme.CountryPickerTypography

/**
 * Everything a custom row renderer needs, so `listItemContent` does not need a dozen parameters.
 *
 * @property match The country plus its search-highlight range.
 * @property selected Whether the row is selected (single) or ticked (multi).
 * @property enabled Whether the row can be activated.
 * @property selectionMode Drives whether the trailing control is a check or a checkbox.
 * @property onClick Activates the row. Call this rather than reimplementing toggle logic.
 */
@Immutable
data class CountryListItemScope(
    val match: CountryMatch,
    val selected: Boolean,
    val enabled: Boolean,
    val selectionMode: CountrySelectionMode,
    val onClick: () -> Unit,
) {
    val country: Country get() = match.country
}

/**
 * One country row.
 *
 * ### Single click target
 * The row is the only clickable element — the checkbox is drawn but is **not** independently
 * clickable, and carries `clearAndSetSemantics {}`. A `Checkbox` nested inside a clickable row is the
 * classic source of double-toggling (tap the box, both handlers fire, net change zero) and of TalkBack
 * announcing two overlapping targets. Selection state reaches accessibility through the row's own
 * `selected` and `stateDescription` semantics instead.
 *
 * ### Ripple
 * `Modifier.clip(shape)` precedes `Modifier.clickable`, so the ripple is bounded by the row's shape
 * rather than spilling past it.
 *
 * ### Selection is not conveyed by color alone
 * A selected row gets the tinted background *and* a check mark or ticked box, so the state survives
 * greyscale, high-contrast modes and color-vision deficiency.
 */
@Composable
fun CountryListItem(
    match: CountryMatch,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selectionMode: CountrySelectionMode = CountrySelectionMode.Single,
    showIsoCode: Boolean = false,
    showDialCode: Boolean = false,
    highlightMatches: Boolean = true,
    unavailable: Boolean = false,
    flagShape: CountryFlagShape = CountryFlagShape.Circle,
    flagStyle: CountryFlagStyle = CountryFlagStyle.Plain,
    colors: CountryPickerColors = CountryPickerDefaults.colors(),
    shapes: CountryPickerShapes = CountryPickerDefaults.shapes(),
    dimensions: CountryPickerDimensions = CountryPickerDefaults.dimensions(),
    typography: CountryPickerTypography = CountryPickerDefaults.typography(),
    motion: CountryPickerMotion = CountryPickerDefaults.motion(),
    flagContent: (@Composable (Country) -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
) {
    val country = match.country
    val metadata = country.metadataLine(showIsoCode, showDialCode)

    val containerColor by animateColorAsState(
        targetValue = colors.rowContainer(selected),
        animationSpec = motion.colorSpec,
        label = "rowContainer",
    )
    val contentColor by animateColorAsState(
        targetValue = colors.rowContent(selected, enabled),
        animationSpec = motion.colorSpec,
        label = "rowContent",
    )

    val rowDescription = rowContentDescription(country, metadata, unavailable)
    val stateText = stringResource(
        if (selected) R.string.ccp_a11y_selected else R.string.ccp_a11y_not_selected,
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shapes.row)
            .background(containerColor)
            .clickable(
                enabled = enabled,
                role = if (selectionMode == CountrySelectionMode.Multiple) {
                    Role.Checkbox
                } else {
                    Role.RadioButton
                },
                onClick = onClick,
            )
            .defaultMinSize(minHeight = maxOf(dimensions.rowMinHeight, dimensions.minimumTouchTarget))
            .padding(
                horizontal = dimensions.rowHorizontalPadding,
                vertical = dimensions.rowVerticalPadding,
            )
            .semantics {
                contentDescription = rowDescription
                this.selected = selected
                stateDescription = stateText
                role = if (selectionMode == CountrySelectionMode.Multiple) {
                    Role.Checkbox
                } else {
                    Role.RadioButton
                }
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimensions.rowContentSpacing),
    ) {
        CountryFlag(
            country = country,
            size = dimensions.flagSizeRow,
            shape = flagShape,
            style = flagStyle,
            colors = colors,
            dimensions = dimensions,
            motion = motion,
            flagContent = flagContent,
            modifier = Modifier.alpha(if (enabled) 1f else DISABLED_ALPHA),
        )

        // Children are hidden from accessibility; the row announces itself as one phrase.
        Column(
            modifier = Modifier
                .weight(1f)
                .clearAndSetSemantics {},
        ) {
            Text(
                text = highlightedName(match, highlightMatches, colors),
                style = typography.countryName,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            if (!metadata.isNullOrEmpty() || unavailable) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(METADATA_SPACING),
                ) {
                    if (!metadata.isNullOrEmpty()) {
                        Text(
                            text = metadata,
                            style = typography.countryMetadata,
                            color = if (selected) {
                                contentColor.copy(alpha = SELECTED_METADATA_ALPHA)
                            } else {
                                colors.rowSecondaryContent
                            },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    if (unavailable) UnavailableTag(colors, typography)
                }
            }
        }

        // A fixed-size trailing slot regardless of state: reserving the space means the check mark
        // appearing cannot shift the country name sideways.
        Box(
            modifier = Modifier
                .size(dimensions.checkIconSize)
                .clearAndSetSemantics {},
            contentAlignment = Alignment.Center,
        ) {
            when {
                trailingContent != null -> trailingContent()
                selectionMode == CountrySelectionMode.Multiple -> SelectionCheckbox(
                    checked = selected,
                    enabled = enabled,
                    colors = colors,
                    dimensions = dimensions,
                    motion = motion,
                )

                // Animated scale/alpha rather than AnimatedVisibility: the check mark is one icon in a
                // fixed-size slot, so a whole subcomposition per row would cost more than it buys —
                // and this list can hold 236 rows.
                else -> {
                    val checkScale by animateFloatAsState(
                        targetValue = if (selected) 1f else 0f,
                        animationSpec = motion.selectionSpring,
                        label = "rowCheckScale",
                    )
                    if (checkScale > 0f) {
                        Icon(
                            imageVector = PickerIcons.Check,
                            contentDescription = null,
                            tint = colors.checkIcon,
                            modifier = Modifier
                                .size(dimensions.checkIconSize)
                                .scale(checkScale)
                                .alpha(checkScale.coerceIn(0f, 1f)),
                        )
                    }
                }
            }
        }
    }
}

/**
 * The multi-select tick box.
 *
 * Hand-drawn rather than Material's `Checkbox` on purpose: `Checkbox` brings its own 48dp touch target
 * and its own ripple, which inside an already-clickable row produces two overlapping indications and
 * two accessibility nodes. This is a pure visual — all interaction belongs to the row.
 */
@Composable
private fun SelectionCheckbox(
    checked: Boolean,
    enabled: Boolean,
    colors: CountryPickerColors,
    dimensions: CountryPickerDimensions,
    motion: CountryPickerMotion,
) {
    val fill by animateColorAsState(
        targetValue = if (checked) colors.checkboxChecked else Color.Transparent,
        animationSpec = motion.colorSpec,
        label = "checkboxFill",
    )
    val outline by animateColorAsState(
        targetValue = when {
            !enabled -> colors.rowDisabledContent
            checked -> colors.checkboxChecked
            else -> colors.checkboxUnchecked
        },
        animationSpec = motion.colorSpec,
        label = "checkboxOutline",
    )
    val tickScale by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = motion.selectionSpring,
        label = "checkboxTick",
    )

    Box(
        modifier = Modifier
            .size(dimensions.checkboxSize)
            .clip(CheckboxShape)
            .background(fill)
            .border(CHECKBOX_BORDER_WIDTH, outline, CheckboxShape),
        contentAlignment = Alignment.Center,
    ) {
        if (tickScale > 0f) {
            Icon(
                imageVector = PickerIcons.Check,
                contentDescription = null,
                tint = colors.selectedRowContainer,
                modifier = Modifier
                    .size(dimensions.checkboxSize * TICK_SIZE_RATIO)
                    .scale(tickScale),
            )
        }
    }
}

/** The "Not available" pill for countries shown but not selectable. */
@Composable
private fun UnavailableTag(
    colors: CountryPickerColors,
    typography: CountryPickerTypography,
) {
    Text(
        text = stringResource(R.string.ccp_row_unavailable),
        style = typography.badgeLabel,
        color = colors.error,
        modifier = Modifier
            .border(TAG_BORDER_WIDTH, colors.error.copy(alpha = TAG_BORDER_ALPHA), TagShape)
            .padding(horizontal = TAG_HORIZONTAL_PADDING, vertical = TAG_VERTICAL_PADDING),
    )
}

/**
 * The country name with the matched substring emphasised.
 *
 * Highlighting uses a background tint plus a weight bump on the same text run, so the emphasis is
 * still visible when the tint is imperceptible (greyscale, high contrast). The text itself is
 * unchanged — no characters inserted, nothing reordered — so TalkBack reads the plain name and text
 * selection still works.
 */
private fun highlightedName(
    match: CountryMatch,
    highlightMatches: Boolean,
    colors: CountryPickerColors,
): androidx.compose.ui.text.AnnotatedString {
    val name = match.country.displayName
    val shouldHighlight = highlightMatches &&
        match.hasHighlight &&
        match.matchedField == CountrySearchField.Name &&
        match.highlightStart + match.highlightLength <= name.length

    if (!shouldHighlight) return buildAnnotatedString { append(name) }

    val end = match.highlightStart + match.highlightLength
    return buildAnnotatedString {
        append(name.substring(0, match.highlightStart))
        withStyle(
            SpanStyle(
                background = colors.searchHighlight,
                fontWeight = FontWeight.SemiBold,
            ),
        ) {
            append(name.substring(match.highlightStart, end))
        }
        append(name.substring(end))
    }
}

/**
 * The row's spoken description: name, then metadata, as one phrase.
 *
 * Selection state is *not* concatenated here — it goes through `selected`/`stateDescription` so the
 * platform announces it in the user's own language and in the position their screen reader expects.
 */
@Composable
private fun rowContentDescription(
    country: Country,
    metadata: String?,
    unavailable: Boolean,
): String {
    val base = if (metadata.isNullOrEmpty()) {
        country.displayName
    } else {
        stringResource(R.string.ccp_a11y_row, country.displayName, metadata)
    }
    return if (unavailable) {
        stringResource(R.string.ccp_a11y_row, base, stringResource(R.string.ccp_row_unavailable))
    } else {
        base
    }
}

private val CheckboxShape = RoundedCornerShape(3.dp)
private val TagShape = RoundedCornerShape(4.dp)
private val CHECKBOX_BORDER_WIDTH = 2.dp
private val TAG_BORDER_WIDTH = 1.dp
private val TAG_HORIZONTAL_PADDING = 5.dp
private val TAG_VERTICAL_PADDING = 1.dp
private val METADATA_SPACING = 6.dp
private const val TAG_BORDER_ALPHA = 0.5f
private const val TICK_SIZE_RATIO = 0.75f
private const val DISABLED_ALPHA = 0.5f
private const val SELECTED_METADATA_ALPHA = 0.8f
