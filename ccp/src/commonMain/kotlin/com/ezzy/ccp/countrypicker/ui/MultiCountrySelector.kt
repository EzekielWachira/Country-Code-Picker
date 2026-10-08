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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import com.ezzy.ccp.countrypicker.data.CountryRepository
import com.ezzy.ccp.countrypicker.model.Country
import com.ezzy.ccp.countrypicker.model.UiText
import com.ezzy.ccp.countrypicker.model.resolve
import com.ezzy.ccp.countrypicker.persistence.NoOpRecentCountryStore
import com.ezzy.ccp.countrypicker.persistence.RecentCountryStore
import com.ezzy.ccp.countrypicker.state.CountryPickerConfig
import com.ezzy.ccp.countrypicker.state.rememberCountryPickerState
import com.ezzy.ccp.countrypicker.theme.CountryFlagShape
import com.ezzy.ccp.countrypicker.theme.CountryPickerColors
import com.ezzy.ccp.countrypicker.theme.CountryPickerDefaults
import com.ezzy.ccp.countrypicker.theme.CountryPickerDimensions
import com.ezzy.ccp.countrypicker.theme.CountryPickerMotion
import com.ezzy.ccp.countrypicker.theme.CountryPickerShapes
import com.ezzy.ccp.countrypicker.theme.CountryPickerTypography
import com.ezzy.ccp.icons.ChevronDown
import com.ezzy.ccp.icons.EzzyIcons
import com.ezzy.ccp.resources.Res
import com.ezzy.ccp.resources.ccp_countries_selected
import com.ezzy.ccp.resources.ccp_country_of_residence
import com.ezzy.ccp.resources.ccp_multi_selector_a11y
import com.ezzy.ccp.resources.ccp_select_country
import com.ezzy.ccp.resources.ccp_select_country_placeholder
import com.ezzy.ccp.resources.ccp_select_country_subtitle
import org.jetbrains.compose.resources.stringResource

/**
 * A selector for choosing several countries — supported markets, service coverage, travel
 * destinations, countries a payment method works in.
 *
 * ### The confirmed selection is never mutated in place
 * [selectedCountries] changes only when [onSelectionConfirmed] fires, which happens only when the user
 * taps Confirm. Ticking rows edits a *pending* set inside the picker state; Cancel and dismiss discard
 * it. That is the difference between a Cancel button that works and one that merely looks like it does.
 *
 * ```kotlin
 * var markets by rememberSaveable { mutableStateOf(emptySet<Country>()) }
 * MultiCountrySelector(
 *     selectedCountries = markets,
 *     onSelectionConfirmed = { markets = it },   // called on Confirm only
 *     config = CountryPickerDefaults.multiSelectConfig(maximumSelectionCount = 5),
 * )
 * ```
 *
 * @param selectedCountries The confirmed selection.
 * @param onSelectionConfirmed Invoked with the new selection when the user confirms.
 * @param label Selector label.
 * @param placeholder Shown when nothing is selected.
 * @param summaryText Overrides the selector's value line. Defaults to "3 countries selected".
 */
@Composable
public fun MultiCountrySelector(
    selectedCountries: Set<Country>,
    onSelectionConfirmed: (Set<Country>) -> Unit,
    modifier: Modifier = Modifier,
    config: CountryPickerConfig = CountryPickerDefaults.multiSelectConfig(),
    enabled: Boolean = true,
    state: CountrySelectorState = CountrySelectorState.Default,
    variant: CountrySelectorVariant = CountrySelectorVariant.Outlined,
    label: UiText? = UiText.resource(Res.string.ccp_country_of_residence),
    placeholder: UiText? = UiText.resource(Res.string.ccp_select_country_placeholder),
    summaryText: UiText? = null,
    supportingText: UiText? = null,
    errorText: UiText? = null,
    required: Boolean = false,
    sheetTitle: UiText = UiText.resource(Res.string.ccp_select_country),
    sheetSubtitle: UiText? = UiText.resource(Res.string.ccp_select_country_subtitle),
    recentCountryStore: RecentCountryStore = NoOpRecentCountryStore,
    repository: CountryRepository = CountryRepository.Default,
    colors: CountryPickerColors = CountryPickerDefaults.colors(),
    shapes: CountryPickerShapes = CountryPickerDefaults.shapes(),
    dimensions: CountryPickerDimensions = CountryPickerDefaults.dimensions(),
    typography: CountryPickerTypography = CountryPickerDefaults.typography(),
    motion: CountryPickerMotion = CountryPickerDefaults.motion(),
    flagContent: (@Composable (Country) -> Unit)? = null,
    listItemContent: (@Composable (CountryListItemScope) -> Unit)? = null,
) {
    require(config.isMultiSelect) {
        "MultiCountrySelector requires a multi-select config; " +
            "use CountryPickerDefaults.multiSelectConfig()"
    }

    val effectiveState = if (!enabled) CountrySelectorState.Disabled else state
    val pickerState = rememberCountryPickerState(
        config = config,
        selectedCountries = selectedCountries,
        repository = repository,
        recentCountryStore = recentCountryStore,
    )

    val flagShape = if (config.flagsVisible) config.flagShape else CountryFlagShape.Hidden

    // Above one selection the selector shows a count rather than names: listing five country names in a
    // 56dp field truncates all of them and says less than "5 countries selected".
    val summary = summaryText ?: when (selectedCountries.size) {
        0 -> null
        1 -> UiText.of(selectedCountries.first().displayName)
        else -> UiText.plural(Res.plurals.ccp_countries_selected, selectedCountries.size)
    }

    // The default merged description is built from a single country and would announce only the
    // first selection while the visible field says "N countries selected" — so above one selection
    // the description is overridden to match what is actually on screen.
    val accessibilityDescription = if (selectedCountries.size > 1) {
        val labelText = label?.resolve() ?: stringResource(Res.string.ccp_country_of_residence)
        stringResource(Res.string.ccp_multi_selector_a11y, labelText, summary?.resolve().orEmpty())
    } else {
        null
    }

    Column(modifier = modifier) {
        CountrySelectorField(
            country = selectedCountries.firstOrNull(),
            onClick = pickerState::open,
            isOpen = pickerState.isSheetOpen,
            state = effectiveState,
            variant = variant,
            label = label,
            placeholder = placeholder,
            required = required,
            flagShape = flagShape,
            colors = colors,
            shapes = shapes,
            dimensions = dimensions,
            typography = typography,
            motion = motion,
            flagContent = flagContent,
            accessibilityDescription = accessibilityDescription,
            selectorContent = if (summary != null && selectedCountries.size > 1) {
                {
                    MultiSelectionSelectorContent(
                        countries = selectedCountries,
                        label = label,
                        summary = summary,
                        state = effectiveState,
                        flagShape = flagShape,
                        isOpen = pickerState.isSheetOpen,
                        colors = colors,
                        dimensions = dimensions,
                        typography = typography,
                        motion = motion,
                        flagContent = flagContent,
                    )
                }
            } else {
                null
            },
        )

        val helperText = if (effectiveState == CountrySelectorState.Error && errorText != null) {
            errorText
        } else {
            supportingText
        }
        AnimatedVisibility(
            visible = helperText != null,
            enter = fadeIn(motion.fadeIn),
            exit = fadeOut(motion.fadeOut),
        ) {
            Text(
                text = helperText?.resolve().orEmpty(),
                style = typography.helperText,
                color = if (effectiveState == CountrySelectorState.Error) {
                    colors.error
                } else {
                    colors.selectorSecondaryContent
                },
                modifier = Modifier.padding(
                    start = dimensions.selectorHorizontalPadding,
                    end = dimensions.selectorHorizontalPadding,
                    top = HELPER_TOP_PADDING,
                ),
            )
        }
    }

    if (pickerState.isSheetOpen) {
        MultiCountryPickerSheet(
            state = pickerState,
            onSelectionConfirmed = onSelectionConfirmed,
            onDismiss = pickerState::dismiss,
            recentCountryStore = recentCountryStore,
            title = sheetTitle,
            subtitle = sheetSubtitle,
            colors = colors,
            shapes = shapes,
            dimensions = dimensions,
            typography = typography,
            motion = motion,
            flagContent = flagContent,
            listItemContent = listItemContent,
        )
    }
}

/**
 * Label + "N countries selected" + chevron.
 *
 * A `RowScope` extension because it is passed as `selectorContent` into
 * [CountrySelectorSurface]'s `Row`, and needs `weight` to claim the space between flag and chevron.
 */
@Composable
private fun RowScope.MultiSelectionSelectorContent(
    countries: Set<Country>,
    label: UiText?,
    summary: UiText,
    state: CountrySelectorState,
    flagShape: CountryFlagShape,
    isOpen: Boolean,
    colors: CountryPickerColors,
    dimensions: CountryPickerDimensions,
    typography: CountryPickerTypography,
    motion: CountryPickerMotion,
    flagContent: (@Composable (Country) -> Unit)?,
) {
    CountryFlag(
        country = countries.firstOrNull(),
        size = dimensions.flagSize,
        shape = flagShape,
        colors = colors,
        dimensions = dimensions,
        motion = motion,
        flagContent = flagContent,
    )

    // The surface already announces the whole field; hiding the children stops TalkBack reading the
    // label and the count as two separate nodes.
    Column(
        modifier = Modifier
            .weight(1f)
            .clearAndSetSemantics {},
    ) {
        if (label != null) {
            Text(
                text = label.resolve(),
                style = typography.fieldLabel,
                color = colors.selectorLabel,
                maxLines = 1,
            )
        }
        Text(
            text = summary.resolve(),
            style = typography.selectorValue,
            color = if (state == CountrySelectorState.Disabled) {
                colors.selectorDisabledContent
            } else {
                colors.selectorContent
            },
            maxLines = 1,
        )
    }

    val rotation by animateFloatAsState(
        targetValue = if (isOpen) CHEVRON_OPEN_DEGREES else 0f,
        animationSpec = motion.floatSpec,
        label = "multiSelectChevron",
    )
    Icon(
        imageVector = EzzyIcons.ChevronDown,
        contentDescription = null,
        tint = colors.chevron,
        modifier = Modifier
            .size(dimensions.chevronSize)
            .rotate(rotation),
    )
}

private const val CHEVRON_OPEN_DEGREES = 180f
private val HELPER_TOP_PADDING = 4.dp
