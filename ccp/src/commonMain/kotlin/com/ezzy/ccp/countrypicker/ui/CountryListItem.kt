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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.ezzy.ccp.countrypicker.model.Country
import com.ezzy.ccp.countrypicker.model.CountryMatch
import com.ezzy.ccp.countrypicker.model.CountryPickerBidi
import com.ezzy.ccp.countrypicker.model.CountrySearchField
import com.ezzy.ccp.countrypicker.model.CountrySelectionMode
import com.ezzy.ccp.countrypicker.theme.CountryPickerColors
import com.ezzy.ccp.countrypicker.theme.CountryPickerStyle
import com.ezzy.ccp.countrypicker.theme.CountryPickerTheme
import com.ezzy.ccp.countrypicker.theme.SelectionIndicator
import com.ezzy.ccp.resources.Res
import com.ezzy.ccp.resources.ccp_a11y_not_selected
import com.ezzy.ccp.resources.ccp_a11y_row
import com.ezzy.ccp.resources.ccp_a11y_selected
import com.ezzy.ccp.resources.ccp_row_unavailable
import org.jetbrains.compose.resources.stringResource

/**
 * Everything a custom row renderer needs, so `listItemContent` does not need a dozen parameters.
 *
 * @property match The country plus its search-highlight range.
 * @property selected Whether the row is selected (single) or ticked (multiple).
 * @property enabled Whether the row can be activated.
 * @property selectionMode Drives whether the trailing control is a check or a checkbox.
 * @property onClick Activates the row. Call this rather than reimplementing toggle logic.
 */
@Immutable
public data class CountryListItemScope(
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
 * ### One click target
 * The row is the only clickable element. Its check mark, radio or checkbox is drawn but is not
 * itself clickable and is hidden from accessibility — a control nested inside a clickable row is the
 * classic source of double toggling and of a screen reader announcing two overlapping targets.
 * Selection reaches accessibility through the row's own `selected` and `stateDescription`.
 *
 * ### Selection is never color alone
 * A selected row gets a tint *and* a check mark (or ticked box), so the state survives greyscale,
 * high-contrast modes and color-vision deficiency — which is why
 * [SelectionIndicator.None] is discouraged.
 *
 * @param shape Clips the row's highlight and press feedback. The list passes a rectangle for grouped
 *   rows (the group clips the corners) and a rounded shape for plain and card rows.
 * @param unavailable Marks the row "Not available" — shown, but not selectable.
 * @param flagContent Replaces the flag renderer.
 * @param trailingContent Replaces the selection indicator.
 */
@Composable
public fun CountryListItem(
    match: CountryMatch,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selectionMode: CountrySelectionMode = CountrySelectionMode.Single,
    unavailable: Boolean = false,
    shape: Shape = CountryPickerTheme.style.shapes.row,
    style: CountryPickerStyle = CountryPickerTheme.style,
    flagContent: (@Composable (Country) -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
) {
    val colors = style.colors
    val dimensions = style.dimensions
    val layout = style.layout
    val country = match.country
    val secondary = country.secondaryLine(layout.showIsoCode, layout.showRegionName)
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by pressScale(interaction, enabled)

    val container by animateColorAsState(
        targetValue = when {
            selected -> colors.accentSoft
            pressed -> colors.textPrimary.copy(alpha = PRESSED_OVERLAY_ALPHA)
            else -> Color.Transparent
        },
        animationSpec = style.motion.color,
        label = "rowContainer",
    )

    val rowRole = if (selectionMode == CountrySelectionMode.Multiple) Role.Checkbox else Role.RadioButton
    val rowDescription = rowContentDescription(country, secondary, layout.showDialCode, unavailable)
    val stateText = stringResource(if (selected) Res.string.ccp_a11y_selected else Res.string.ccp_a11y_not_selected)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(shape)
            .background(container)
            .clickable(interactionSource = interaction, indication = null, enabled = enabled, role = rowRole, onClick = onClick)
            .defaultMinSize(minHeight = maxOf(dimensions.rowMinHeight, dimensions.minimumTouchTarget))
            .padding(horizontal = dimensions.rowHorizontalPadding, vertical = dimensions.rowVerticalPadding)
            .semantics {
                contentDescription = rowDescription
                this.selected = selected
                stateDescription = stateText
                role = rowRole
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimensions.rowContentSpacing),
    ) {
        CountryFlag(
            country = country,
            size = dimensions.flagSizeRow,
            style = layout.flagStyle,
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
                text = highlightedName(match, layout.highlightSearchMatches, colors),
                style = if (selected) style.typography.countryNameSelected else style.typography.countryName,
                color = if (enabled) colors.textPrimary else colors.textDisabled,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (!secondary.isNullOrEmpty() || unavailable) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    if (!secondary.isNullOrEmpty()) {
                        SingleLineText(
                            text = secondary,
                            style = style.typography.caption.copy(textDirection = CountryPickerBidi.LTR),
                            color = colors.textSecondary,
                        )
                    }
                    if (unavailable) {
                        PickerBadge(
                            text = stringResource(Res.string.ccp_row_unavailable),
                            container = colors.errorSoft,
                            content = colors.error,
                        )
                    }
                }
            }
        }

        if (layout.showDialCode) {
            Text(
                text = country.dialCode,
                // "+254" is neutral and weak-direction characters; pinned LTR it never reads "254+".
                style = style.typography.dialCode.copy(textDirection = CountryPickerBidi.LTR),
                color = if (selected) colors.textPrimary else colors.textSecondary,
                maxLines = 1,
                modifier = Modifier.clearAndSetSemantics {},
            )
        }

        if (trailingContent != null) {
            Box(Modifier.clearAndSetSemantics {}) { trailingContent() }
        } else {
            SelectionMark(selected, enabled, selectionMode, layout.selectionIndicator, style)
        }
    }
}

/**
 * The trailing check, radio or checkbox. A fixed-size slot whatever the state, so the mark appearing
 * cannot shift the name sideways.
 */
@Composable
private fun SelectionMark(
    selected: Boolean,
    enabled: Boolean,
    mode: CountrySelectionMode,
    indicator: SelectionIndicator,
    style: CountryPickerStyle,
) {
    val size = style.dimensions.indicatorSize
    val modifier = Modifier.size(size).clearAndSetSemantics {}
    when {
        mode == CountrySelectionMode.Multiple ->
            CheckboxIndicator(selected, enabled, style.colors, style.dimensions.checkboxSize, modifier)
        indicator == SelectionIndicator.Radio -> RadioIndicator(selected, enabled, style.colors, size, modifier)
        indicator == SelectionIndicator.Check -> AnimatedCheckMark(selected, style.colors.accent, modifier)
        else -> Unit
    }
}

/**
 * The country name with the matched part of a search emphasised by a tint and a weight bump — the
 * weight keeps the emphasis visible where the tint is not (greyscale, high contrast). The text itself
 * is unchanged, so a screen reader reads the plain name.
 */
private fun highlightedName(
    match: CountryMatch,
    highlightMatches: Boolean,
    colors: CountryPickerColors,
): AnnotatedString {
    val name = match.country.displayName
    val shouldHighlight = highlightMatches &&
        match.hasHighlight &&
        match.matchedField == CountrySearchField.Name &&
        match.highlightStart + match.highlightLength <= name.length
    if (!shouldHighlight) return AnnotatedString(name)

    val end = match.highlightStart + match.highlightLength
    return buildAnnotatedString {
        append(name.substring(0, match.highlightStart))
        withStyle(SpanStyle(background = colors.highlight, fontWeight = FontWeight.SemiBold)) {
            append(name.substring(match.highlightStart, end))
        }
        append(name.substring(end))
    }
}

/** "KE · Africa", from whichever parts are enabled, or `null` when neither is. */
@Composable
private fun Country.secondaryLine(showIso: Boolean, showRegion: Boolean): String? {
    val parts = buildList {
        if (showIso) add(iso2Code)
        if (showRegion) add(stringResource(region.labelRes))
    }
    return parts.takeIf { it.isNotEmpty() }?.joinToString(SECONDARY_SEPARATOR)
}

/**
 * The row's spoken description: name, then the secondary line and dial code, as one phrase.
 * Selection state is announced separately through `selected` / `stateDescription`, in the user's own
 * language and where their screen reader expects it.
 */
@Composable
private fun rowContentDescription(
    country: Country,
    secondary: String?,
    showDialCode: Boolean,
    unavailable: Boolean,
): String {
    val details = listOfNotNull(secondary, country.dialCode.takeIf { showDialCode })
        .joinToString(SECONDARY_SEPARATOR)
    val base = if (details.isEmpty()) {
        country.displayName
    } else {
        stringResource(Res.string.ccp_a11y_row, country.displayName, details)
    }
    return if (unavailable) {
        stringResource(Res.string.ccp_a11y_row, base, stringResource(Res.string.ccp_row_unavailable))
    } else {
        base
    }
}

private const val SECONDARY_SEPARATOR = " · "
private const val DISABLED_ALPHA = 0.45f
private const val PRESSED_OVERLAY_ALPHA = 0.05f
