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

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ezzy.ccp.countrypicker.data.CountryRepository
import com.ezzy.ccp.countrypicker.detection.CountryDetectionBehavior
import com.ezzy.ccp.countrypicker.detection.CountryDetector
import com.ezzy.ccp.countrypicker.model.Country
import com.ezzy.ccp.countrypicker.model.CountryPickerBidi
import com.ezzy.ccp.countrypicker.model.CountrySupportingContent
import com.ezzy.ccp.countrypicker.model.InputLabelMode
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
import com.ezzy.ccp.resources.ccp_change
import com.ezzy.ccp.resources.ccp_country_of_residence
import com.ezzy.ccp.resources.ccp_dial_code_a11y
import com.ezzy.ccp.resources.ccp_flag_only_a11y
import com.ezzy.ccp.resources.ccp_flag_only_a11y_empty
import com.ezzy.ccp.resources.ccp_required_label
import com.ezzy.ccp.resources.ccp_select_country
import com.ezzy.ccp.resources.ccp_select_country_placeholder
import com.ezzy.ccp.resources.ccp_select_country_subtitle
import com.ezzy.ccp.resources.ccp_selector_a11y_empty
import com.ezzy.ccp.resources.ccp_selector_a11y_selected
import org.jetbrains.compose.resources.stringResource

/**
 * A country field that opens the picker.
 *
 * ### Controlled component
 * [selectedCountry] is the source of truth and the selector never changes it on its own — selection
 * arrives through [onCountrySelected]. Hoist it in the caller:
 *
 * ```kotlin
 * var country by rememberSaveable { mutableStateOf<Country?>(null) }
 * CountrySelector(selectedCountry = country, onCountrySelected = { country = it })
 * ```
 *
 * ### Appearance
 * [variant] chooses the footprint — a full field, a rich card, or a compact pill. Colors, flags,
 * shapes and the picker's own presentation come from [style] (by default the nearest
 * [CountryPickerTheme]).
 *
 * @param config Which countries are offered and how selection behaves.
 * @param enabled Convenience for `state = Disabled`. When false, [state] is ignored.
 * @param state Interaction and validation state.
 * @param label Field label. The pill variants have no room for one; it is still announced.
 * @param placeholder Shown while no country is selected.
 * @param labelMode Show the label, or keep it for accessibility only.
 * @param supportingContent The ISO and/or dial code under the country name in the field.
 * @param supportingText Helper text below the field, replaced by [errorText] or [successText] in
 *   those states.
 * @param required Marks the label and the accessibility description as required.
 * @param recentCountryStore Recents persistence. The default persists nothing.
 * @param detector Country detection, or `null` to disable it.
 * @param detectionBehavior What to do with a detection result.
 * @param flagContent Replaces the flag renderer everywhere this selector draws one.
 * @param trailingContent Replaces the chevron, spinner and state icons.
 * @param selectorContent Replaces the field's inner layout, keeping its click and accessibility
 *   behaviour — the escape hatch for a bespoke design.
 * @param listItemContent Replaces the picker's row renderer.
 * @param emptyContent Replaces the picker's no-results state.
 */
@Composable
public fun CountrySelector(
    selectedCountry: Country?,
    onCountrySelected: (Country) -> Unit,
    modifier: Modifier = Modifier,
    config: CountryPickerConfig = CountryPickerDefaults.config(),
    variant: CountrySelectorVariant = CountrySelectorVariant.Elevated,
    enabled: Boolean = true,
    state: CountrySelectorState = CountrySelectorState.Default,
    label: UiText? = UiText.resource(Res.string.ccp_country_of_residence),
    placeholder: UiText? = UiText.resource(Res.string.ccp_select_country_placeholder),
    labelMode: InputLabelMode = InputLabelMode.Floating,
    supportingContent: CountrySupportingContent = CountrySupportingContent.None,
    supportingText: UiText? = null,
    errorText: UiText? = null,
    successText: UiText? = null,
    required: Boolean = false,
    sheetTitle: UiText = UiText.resource(Res.string.ccp_select_country),
    sheetSubtitle: UiText? = UiText.resource(Res.string.ccp_select_country_subtitle),
    recentCountryStore: RecentCountryStore = NoOpRecentCountryStore,
    detector: CountryDetector? = null,
    detectionBehavior: CountryDetectionBehavior = CountryDetectionBehavior.ShowBadge,
    repository: CountryRepository = CountryRepository.Default,
    style: CountryPickerStyle = CountryPickerTheme.style,
    flagContent: (@Composable (Country) -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    selectorContent: (@Composable RowScope.(Country?) -> Unit)? = null,
    listItemContent: (@Composable (CountryListItemScope) -> Unit)? = null,
    emptyContent: (@Composable (String) -> Unit)? = null,
) {
    val effectiveState = if (!enabled) CountrySelectorState.Disabled else state
    val pickerState = rememberCountryPickerState(
        config = config,
        selectedCountries = selectedCountry?.let(::setOf).orEmpty(),
        repository = repository,
        recentCountryStore = recentCountryStore,
        detector = detector,
        detectionBehavior = detectionBehavior,
        onDetectedCountry = onCountrySelected,
    )

    CountryPickerTheme(style) {
        Column(modifier = modifier) {
            CountrySelectorField(
                country = selectedCountry,
                onClick = pickerState::open,
                isOpen = pickerState.isSheetOpen,
                state = effectiveState,
                variant = variant,
                label = label,
                placeholder = placeholder,
                labelMode = labelMode,
                supportingContent = supportingContent,
                required = required,
                style = style,
                flagContent = flagContent,
                trailingContent = trailingContent,
                selectorContent = selectorContent,
            )
            val (helper, helperTone) = when {
                effectiveState == CountrySelectorState.Error && errorText != null -> errorText to FieldTone.Error
                effectiveState == CountrySelectorState.Success && successText != null -> successText to FieldTone.Success
                else -> supportingText to FieldTone.Default
            }
            FieldHelperText(text = helper, tone = helperTone, style = style)
        }

        if (pickerState.isSheetOpen) {
            CountryPickerSheet(
                state = pickerState,
                onCountrySelected = { country ->
                    onCountrySelected(country)
                    pickerState.markExplicitSelection()
                },
                onDismiss = pickerState::dismiss,
                recentCountryStore = recentCountryStore,
                title = sheetTitle,
                subtitle = sheetSubtitle,
                style = style,
                flagContent = flagContent,
                listItemContent = listItemContent,
                emptyContent = emptyContent,
            )
        }
    }
}

/**
 * The selector field alone, without the picker — for a host that already manages when its picker
 * is shown.
 *
 * @param isOpen Draws the field as active (accent border, focus ring, chevron turned) while its
 *   picker is open.
 * @param accessibilityDescription Overrides the spoken description. The default names the label and
 *   the selected country; a field showing a summary over several countries must pass its own.
 */
@Composable
public fun CountrySelectorField(
    country: Country?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isOpen: Boolean = false,
    state: CountrySelectorState = CountrySelectorState.Default,
    variant: CountrySelectorVariant = CountrySelectorVariant.Elevated,
    label: UiText? = null,
    placeholder: UiText? = null,
    labelMode: InputLabelMode = InputLabelMode.Floating,
    supportingContent: CountrySupportingContent = CountrySupportingContent.None,
    required: Boolean = false,
    style: CountryPickerStyle = CountryPickerTheme.style,
    flagContent: (@Composable (Country) -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    selectorContent: (@Composable RowScope.(Country?) -> Unit)? = null,
    accessibilityDescription: String? = null,
) {
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val description = accessibilityDescription ?: selectorContentDescription(country, label, placeholder, variant)
    val dimensions = style.dimensions
    val isCard = variant == CountrySelectorVariant.Card

    PickerFieldContainer(
        variant = variant,
        active = isOpen || focused,
        tone = state.fieldTone,
        enabled = state != CountrySelectorState.Disabled,
        style = style,
        minHeight = when {
            variant.isPill -> dimensions.fieldCompactHeight
            isCard -> dimensions.fieldMinHeight + 20.dp
            else -> dimensions.fieldMinHeight
        },
        contentPadding = when {
            variant == CountrySelectorVariant.FlagOnly -> PaddingValues(start = 10.dp, end = 8.dp)
            variant.isPill -> PaddingValues(start = 8.dp, end = 10.dp)
            isCard -> PaddingValues(horizontal = 16.dp, vertical = 14.dp)
            else -> PaddingValues(horizontal = dimensions.fieldHorizontalPadding, vertical = 8.dp)
        },
        horizontalArrangement = Arrangement.spacedBy(if (variant.isPill) 8.dp else dimensions.fieldContentSpacing),
        interactionSource = interaction,
        onClick = if (state.isInteractive) onClick else null,
        modifier = modifier.semantics { contentDescription = description },
    ) {
        if (selectorContent != null) {
            selectorContent(country)
            return@PickerFieldContainer
        }
        val content = if (state == CountrySelectorState.Disabled) style.colors.textDisabled else style.colors.textPrimary

        LeadingFlag(country, variant, style, flagContent)

        when (variant) {
            CountrySelectorVariant.FlagOnly -> Unit
            CountrySelectorVariant.Compact -> SingleLineText(
                text = country?.displayName ?: placeholder?.resolve().orEmpty(),
                style = style.typography.chip,
                color = if (country == null) style.colors.textTertiary else content,
                modifier = Modifier.weight(1f, fill = false).clearAndSetSemantics {},
            )
            CountrySelectorVariant.DialCode -> Text(
                text = country?.dialCode ?: "+—",
                // "+254" is neutral and weak-direction characters; pinned LTR it never reads "254+".
                style = style.typography.dialCode.copy(textDirection = CountryPickerBidi.LTR),
                color = content,
                maxLines = 1,
                modifier = Modifier.clearAndSetSemantics {},
            )
            CountrySelectorVariant.Card -> CardValue(country, label, placeholder, required, state, style, Modifier.weight(1f))
            else -> FieldValue(
                country = country,
                label = label.takeIf { labelMode == InputLabelMode.Floating },
                placeholder = placeholder,
                required = required,
                state = state,
                active = isOpen || focused,
                supportingContent = supportingContent,
                style = style,
                modifier = Modifier.weight(1f),
            )
        }

        if (trailingContent != null) {
            Box(Modifier.clearAndSetSemantics {}) { trailingContent() }
        } else {
            FieldTrailing(state, isOpen, variant, style)
        }
    }
}

/** The flag, or for an empty full-width field a globe on the same tile so the layout never jumps. */
@Composable
private fun LeadingFlag(
    country: Country?,
    variant: CountrySelectorVariant,
    style: CountryPickerStyle,
    flagContent: (@Composable (Country) -> Unit)?,
) {
    val size: Dp = when {
        variant.isPill -> style.dimensions.flagSizeCompact
        variant == CountrySelectorVariant.Card -> style.dimensions.flagSizeField + 12.dp
        else -> style.dimensions.flagSizeField
    }
    val flagStyle = when {
        style.layout.flagStyle == CountryFlagStyle.Hidden -> CountryFlagStyle.Hidden
        variant.isPill && style.layout.flagStyle == CountryFlagStyle.Tile -> CountryFlagStyle.Circle
        else -> style.layout.flagStyle
    }
    if (flagStyle == CountryFlagStyle.Hidden) return
    if (country != null) {
        CountryFlag(country = country, size = size, style = flagStyle, flagContent = flagContent)
    } else if (variant.isFullWidth || variant == CountrySelectorVariant.FlagOnly) {
        Box(
            modifier = Modifier
                .size(size)
                .background(style.colors.surfaceSunken, if (flagStyle == CountryFlagStyle.Tile) style.shapes.flagTile else CircleShape)
                .clearAndSetSemantics {},
            contentAlignment = Alignment.Center,
        ) {
            Icon(PickerIcons.Globe, contentDescription = null, tint = style.colors.textTertiary, modifier = Modifier.size(size * 0.55f))
        }
    }
}

/** Label, value and supporting line of a full-width field. */
@Composable
private fun FieldValue(
    country: Country?,
    label: UiText?,
    placeholder: UiText?,
    required: Boolean,
    state: CountrySelectorState,
    active: Boolean,
    supportingContent: CountrySupportingContent,
    style: CountryPickerStyle,
    modifier: Modifier,
) {
    val colors = style.colors
    val labelColor by animateColorAsState(
        targetValue = when {
            state == CountrySelectorState.Error -> colors.error
            state == CountrySelectorState.Disabled -> colors.textDisabled
            active -> colors.accent
            else -> colors.textSecondary
        },
        animationSpec = style.motion.color,
        label = "fieldLabel",
    )
    Column(modifier = modifier.clearAndSetSemantics {}) {
        if (label != null) {
            val text = label.resolve()
            SingleLineText(
                text = if (required) stringResource(Res.string.ccp_required_label, text) else text,
                style = style.typography.fieldLabel,
                color = labelColor,
            )
        }
        // The new value rises into place, so a change reads as a replacement rather than a flicker.
        AnimatedContent(
            targetState = country,
            transitionSpec = {
                (fadeIn(style.motion.fadeIn) + slideInVertically(style.motion.offset) { it / 3 }) togetherWith
                    fadeOut(style.motion.fadeOut)
            },
            contentKey = { it?.iso2Code },
            label = "fieldValue",
        ) { target ->
            Text(
                text = target?.displayName ?: placeholder?.resolve().orEmpty(),
                style = style.typography.fieldValue,
                color = when {
                    state == CountrySelectorState.Disabled -> colors.textDisabled
                    target == null -> colors.textTertiary
                    else -> colors.textPrimary
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        val metadata = country?.supportingLine(supportingContent)
        if (!metadata.isNullOrEmpty()) {
            SingleLineText(
                text = metadata,
                style = style.typography.caption.copy(textDirection = CountryPickerBidi.LTR),
                color = colors.textSecondary,
            )
        }
    }
}

/** The Card variant's body: label, name in large type, and region · dial code · ISO. */
@Composable
private fun CardValue(
    country: Country?,
    label: UiText?,
    placeholder: UiText?,
    required: Boolean,
    state: CountrySelectorState,
    style: CountryPickerStyle,
    modifier: Modifier,
) {
    val colors = style.colors
    Column(modifier = modifier.clearAndSetSemantics {}) {
        if (label != null) {
            val text = label.resolve()
            SingleLineText(
                text = (if (required) stringResource(Res.string.ccp_required_label, text) else text).uppercase(),
                style = style.typography.sectionLabel,
                color = if (state == CountrySelectorState.Error) colors.error else colors.textSecondary,
            )
        }
        AnimatedContent(
            targetState = country,
            transitionSpec = { (fadeIn(style.motion.fadeIn) + scaleIn(style.motion.layout, 0.96f)) togetherWith fadeOut(style.motion.fadeOut) },
            contentKey = { it?.iso2Code },
            label = "cardValue",
        ) { target ->
            Column {
                Text(
                    text = target?.displayName ?: placeholder?.resolve().orEmpty(),
                    style = style.typography.emptyTitle,
                    color = if (target == null) colors.textTertiary else colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (target != null) {
                    SingleLineText(
                        text = listOf(stringResource(target.region.labelRes), target.dialCode, target.iso2Code).joinToString(" · "),
                        style = style.typography.caption,
                        color = colors.textSecondary,
                    )
                }
            }
        }
    }
}

/** Chevron in a small disc, turning while the picker is open — or a spinner or state mark. */
@Composable
private fun FieldTrailing(state: CountrySelectorState, isOpen: Boolean, variant: CountrySelectorVariant, style: CountryPickerStyle) {
    val colors = style.colors
    val rotation by animateFloatAsState(if (isOpen) 180f else 0f, style.motion.layout, label = "chevron")
    val modifier = Modifier.clearAndSetSemantics {}
    when (state) {
        CountrySelectorState.Loading -> CircularProgressIndicator(
            color = colors.accent,
            strokeWidth = 2.dp,
            trackColor = colors.surfaceSunken,
            modifier = modifier.size(18.dp),
        )
        CountrySelectorState.Error -> Icon(PickerIcons.Alert, null, tint = colors.error, modifier = modifier.size(20.dp))
        CountrySelectorState.Success -> Box(
            modifier = modifier.size(22.dp).background(colors.success, CircleShape),
            contentAlignment = Alignment.Center,
        ) { AnimatedCheckMark(visible = true, color = colors.surface, modifier = Modifier.size(16.dp)) }
        CountrySelectorState.ReadOnly -> Unit
        else -> if (variant == CountrySelectorVariant.Card) {
            Text(
                text = stringResource(Res.string.ccp_change),
                style = style.typography.chip,
                color = colors.accent,
                modifier = modifier,
            )
        } else if (variant.isPill) {
            Icon(
                PickerIcons.ChevronDown,
                contentDescription = null,
                tint = if (state == CountrySelectorState.Disabled) colors.textDisabled else colors.textSecondary,
                modifier = modifier.size(16.dp).rotate(rotation),
            )
        } else {
            Box(
                modifier = modifier
                    .size(26.dp)
                    .background(colors.surfaceSunken, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    PickerIcons.ChevronDown,
                    contentDescription = null,
                    tint = if (state == CountrySelectorState.Disabled) colors.textDisabled else colors.textSecondary,
                    modifier = Modifier.size(16.dp).rotate(rotation),
                )
            }
        }
    }
}

/** The tone a selector state draws its field in. */
internal val CountrySelectorState.fieldTone: FieldTone
    get() = when (this) {
        CountrySelectorState.Error -> FieldTone.Error
        CountrySelectorState.Success -> FieldTone.Success
        else -> FieldTone.Default
    }

/** "KE · +254", from whichever parts [content] asks for. */
private fun Country.supportingLine(content: CountrySupportingContent): String? {
    val parts = buildList {
        if (content.showsIsoCode) add(iso2Code)
        if (content.showsDialCode) add(dialCode)
    }
    return parts.takeIf { it.isNotEmpty() }?.joinToString(" · ")
}

/**
 * The field's spoken description, as one complete sentence — "Country of residence. Kenya selected.
 * Double tap to change." — rather than fragments read as separate nodes. The flag-only variant has no
 * visible text at all, so its description is the only thing that identifies it.
 */
@Composable
private fun selectorContentDescription(
    country: Country?,
    label: UiText?,
    placeholder: UiText?,
    variant: CountrySelectorVariant,
): String {
    val labelText = label?.resolve() ?: stringResource(Res.string.ccp_country_of_residence)
    val placeholderText = placeholder?.resolve() ?: stringResource(Res.string.ccp_select_country_placeholder)
    return when {
        variant == CountrySelectorVariant.FlagOnly && country != null ->
            stringResource(Res.string.ccp_flag_only_a11y, country.displayName)
        variant == CountrySelectorVariant.FlagOnly -> stringResource(Res.string.ccp_flag_only_a11y_empty)
        variant == CountrySelectorVariant.DialCode && country != null ->
            stringResource(Res.string.ccp_dial_code_a11y, country.dialCode, country.displayName)
        country != null -> stringResource(Res.string.ccp_selector_a11y_selected, labelText, country.displayName)
        else -> stringResource(Res.string.ccp_selector_a11y_empty, labelText, placeholderText)
    }
}
