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
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.ezzy.ccp.countrypicker.data.CountryRepository
import com.ezzy.ccp.countrypicker.model.Country
import com.ezzy.ccp.countrypicker.model.UiText
import com.ezzy.ccp.countrypicker.model.resolve
import com.ezzy.ccp.countrypicker.persistence.NoOpRecentCountryStore
import com.ezzy.ccp.countrypicker.persistence.RecentCountryStore
import com.ezzy.ccp.countrypicker.state.CountryPickerConfig
import com.ezzy.ccp.countrypicker.state.rememberCountryPickerState
import com.ezzy.ccp.countrypicker.theme.CountryFlagStyle
import com.ezzy.ccp.countrypicker.theme.CountryPickerDefaults
import com.ezzy.ccp.countrypicker.theme.CountryPickerStyle
import com.ezzy.ccp.countrypicker.theme.CountryPickerTheme
import com.ezzy.ccp.resources.Res
import com.ezzy.ccp.resources.ccp_countries_selected
import com.ezzy.ccp.resources.ccp_country_of_residence
import com.ezzy.ccp.resources.ccp_multi_selector_a11y
import com.ezzy.ccp.resources.ccp_remove_country_a11y
import com.ezzy.ccp.resources.ccp_required_label
import com.ezzy.ccp.resources.ccp_select_country
import com.ezzy.ccp.resources.ccp_select_country_placeholder
import com.ezzy.ccp.resources.ccp_select_country_subtitle
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

/**
 * A field for choosing several countries.
 *
 * With one country it reads like a [CountrySelector]. With more, the field shows their flags stacked
 * like avatars beside a summary — "Kenya, Germany +3" — and, with [showChips], each country as a
 * removable chip underneath, so the selection can be trimmed without reopening the picker.
 *
 * Selection is controlled: [selectedCountries] is the source of truth, and changes — from the
 * picker's Confirm or from removing a chip — arrive through [onSelectionConfirmed].
 *
 * @param showChips A chip per selected country under the field. A chip cannot be removed when that
 *   would take the selection below the config's minimum.
 * @param summaryText Overrides the field's summary.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
public fun MultiCountrySelector(
    selectedCountries: Set<Country>,
    onSelectionConfirmed: (Set<Country>) -> Unit,
    modifier: Modifier = Modifier,
    config: CountryPickerConfig = CountryPickerDefaults.multiSelectConfig(),
    variant: CountrySelectorVariant = CountrySelectorVariant.Elevated,
    enabled: Boolean = true,
    state: CountrySelectorState = CountrySelectorState.Default,
    label: UiText? = UiText.resource(Res.string.ccp_country_of_residence),
    placeholder: UiText? = UiText.resource(Res.string.ccp_select_country_placeholder),
    summaryText: UiText? = null,
    showChips: Boolean = true,
    supportingText: UiText? = null,
    errorText: UiText? = null,
    required: Boolean = false,
    sheetTitle: UiText = UiText.resource(Res.string.ccp_select_country),
    sheetSubtitle: UiText? = UiText.resource(Res.string.ccp_select_country_subtitle),
    recentCountryStore: RecentCountryStore = NoOpRecentCountryStore,
    repository: CountryRepository = CountryRepository.Default,
    style: CountryPickerStyle = CountryPickerTheme.style,
    flagContent: (@Composable (Country) -> Unit)? = null,
    listItemContent: (@Composable (CountryListItemScope) -> Unit)? = null,
) {
    val effectiveState = if (!enabled) CountrySelectorState.Disabled else state
    val pickerState = rememberCountryPickerState(
        config = config,
        selectedCountries = selectedCountries,
        repository = repository,
        recentCountryStore = recentCountryStore,
    )
    val ordered = selectedCountries.toList()
    val summary = summaryText?.resolve() ?: summaryOf(ordered)

    // Above one selection the field shows a summary rather than one country, so its spoken
    // description must say what is actually on screen rather than name only the first.
    val description = if (ordered.size > 1) {
        val labelText = label?.resolve() ?: stringResource(Res.string.ccp_country_of_residence)
        stringResource(
            Res.string.ccp_multi_selector_a11y,
            labelText,
            pluralStringResource(Res.plurals.ccp_countries_selected, ordered.size, ordered.size) + ". " + summary,
        )
    } else {
        null
    }

    CountryPickerTheme(style) {
        Column(modifier = modifier) {
            CountrySelectorField(
                country = ordered.firstOrNull(),
                onClick = pickerState::open,
                isOpen = pickerState.isSheetOpen,
                state = effectiveState,
                variant = variant,
                label = label,
                placeholder = placeholder,
                required = required,
                style = style,
                flagContent = flagContent,
                accessibilityDescription = description,
                selectorContent = if (ordered.size > 1) {
                    { MultiSummary(ordered, label, summary, required, pickerState.isSheetOpen, style) }
                } else {
                    null
                },
            )

            val removable = enabled && ordered.size > config.minimumSelectionCount
            AnimatedVisibility(
                visible = showChips && ordered.size > 1,
                enter = fadeIn(style.motion.fadeIn),
                exit = fadeOut(style.motion.fadeOut),
            ) {
                // No vertical spacing: each chip's 48dp touch target already spaces the rows.
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(top = 2.dp),
                ) {
                    ordered.forEach { country ->
                        SelectionChip(
                            country = country,
                            removable = removable,
                            onRemove = { onSelectionConfirmed(selectedCountries - country) },
                            style = style,
                        )
                    }
                }
            }

            FieldHelperText(
                text = if (effectiveState == CountrySelectorState.Error && errorText != null) errorText else supportingText,
                tone = if (effectiveState == CountrySelectorState.Error && errorText != null) FieldTone.Error else FieldTone.Default,
                style = style,
            )
        }

        if (pickerState.isSheetOpen) {
            MultiCountryPickerSheet(
                state = pickerState,
                onSelectionConfirmed = onSelectionConfirmed,
                onDismiss = pickerState::dismiss,
                recentCountryStore = recentCountryStore,
                title = sheetTitle,
                subtitle = sheetSubtitle,
                style = style,
                flagContent = flagContent,
                listItemContent = listItemContent,
            )
        }
    }
}

/** "Kenya, Germany +3" — two names, then the rest as a count. */
private fun summaryOf(countries: List<Country>): String = when {
    countries.isEmpty() -> ""
    countries.size <= 2 -> countries.joinToString(", ") { it.displayName }
    else -> countries.take(2).joinToString(", ") { it.displayName } + " +" + (countries.size - 2)
}

/** The field's content above one selection: stacked flags, the label, the summary and the chevron. */
@Composable
private fun androidx.compose.foundation.layout.RowScope.MultiSummary(
    countries: List<Country>,
    label: UiText?,
    summary: String,
    required: Boolean,
    isOpen: Boolean,
    style: CountryPickerStyle,
) {
    val rotation by animateFloatAsState(if (isOpen) 180f else 0f, style.motion.layout, label = "multiChevron")
    StackedFlags(countries = countries, size = 30.dp, max = 3)
    Column(modifier = Modifier.weight(1f).clearAndSetSemantics {}) {
        if (label != null) {
            val text = label.resolve()
            SingleLineText(
                text = if (required) stringResource(Res.string.ccp_required_label, text) else text,
                style = style.typography.fieldLabel,
                color = if (isOpen) style.colors.accent else style.colors.textSecondary,
            )
        }
        SingleLineText(text = summary, style = style.typography.fieldValue, color = style.colors.textPrimary)
    }
    Box(
        modifier = Modifier.size(26.dp).background(style.colors.surfaceSunken, CircleShape).clearAndSetSemantics {},
        contentAlignment = Alignment.Center,
    ) {
        Icon(PickerIcons.ChevronDown, null, tint = style.colors.textSecondary, modifier = Modifier.size(16.dp).rotate(rotation))
    }
}

/**
 * One selected country as a chip. When the selection can shrink, the whole chip is the remove
 * button — announced as "Remove Kenya" — with the × as its visual cue, inside a 48dp-tall target.
 */
@Composable
private fun SelectionChip(
    country: Country,
    removable: Boolean,
    onRemove: () -> Unit,
    style: CountryPickerStyle,
) {
    val colors = style.colors
    val interaction = remember { MutableInteractionSource() }
    val scale by pressScale(interaction, removable)
    val removeLabel = stringResource(Res.string.ccp_remove_country_a11y, country.displayName)
    Box(
        modifier = Modifier
            .heightIn(min = style.dimensions.minimumTouchTarget)
            .then(
                if (removable) {
                    Modifier
                        .clickable(interactionSource = interaction, indication = null, role = Role.Button, onClick = onRemove)
                        .semantics { contentDescription = removeLabel }
                } else {
                    Modifier.semantics { contentDescription = country.displayName }
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .background(colors.surface, style.shapes.chip)
                .border(0.75.dp, colors.hairline, style.shapes.chip)
                .padding(start = 4.dp, end = 10.dp, top = 4.dp, bottom = 4.dp)
                .clearAndSetSemantics {},
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            CountryFlag(country = country, size = 22.dp, style = CountryFlagStyle.Circle)
            SingleLineText(text = country.displayName, style = style.typography.chip, color = colors.textPrimary)
            if (removable) {
                Box(
                    modifier = Modifier.size(16.dp).background(colors.surfaceSunken, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(PickerIcons.Close, contentDescription = null, tint = colors.textSecondary, modifier = Modifier.size(9.dp))
                }
            }
        }
    }
}
