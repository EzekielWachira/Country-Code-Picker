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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
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
import com.ezzy.ccp.countrypicker.state.CountryPickerState
import com.ezzy.ccp.countrypicker.state.rememberCountryPickerState
import com.ezzy.ccp.countrypicker.theme.CountryFlagStyle
import com.ezzy.ccp.countrypicker.theme.CountryPickerColors
import com.ezzy.ccp.countrypicker.theme.CountryPickerDefaults
import com.ezzy.ccp.countrypicker.theme.CountryPickerDimensions
import com.ezzy.ccp.countrypicker.theme.CountryPickerMotion
import com.ezzy.ccp.countrypicker.theme.CountryPickerShapes
import com.ezzy.ccp.countrypicker.theme.CountryPickerTypography
import com.ezzy.ccp.countrypicker.theme.CountrySelectorContentConfig
import com.ezzy.ccp.icons.ChevronDown
import com.ezzy.ccp.icons.EzzyIcons
import com.ezzy.ccp.resources.Res
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
 * A country selector: a tappable surface showing the current selection that opens the country sheet.
 *
 * This is the library's primary entry point and works for country of residence, nationality,
 * supported markets, travel destinations, billing country — any single-country field. The
 * phone-specific behaviour lives in [PhoneCountryCodeSelector] and [PhoneNumberField], both of which
 * open this same sheet.
 *
 * ### Controlled component
 * [selectedCountry] is the source of truth and the selector never mutates it — selection arrives via
 * [onCountrySelected]. Hoist it in the caller:
 *
 * ```kotlin
 * var country by rememberSaveable { mutableStateOf<Country?>(null) }
 * CountrySelector(
 *     selectedCountry = country,
 *     onCountrySelected = { country = it },
 * )
 * ```
 *
 * ### Variants
 * [variant] switches between the full-width field, the compact `🇩🇪 Germany ˅` pill, the flag-only
 * `🇩🇪 ˅` pill, and the phone `🇰🇪 +254 ˅` pill. Every variant opens the same sheet with the same
 * config — they differ in footprint, never in behaviour.
 *
 * @param selectedCountry Current selection, or `null` for none.
 * @param onCountrySelected Invoked with the chosen country. Always the full [Country], never just a
 *   name or dial code, so the caller never has to look one up.
 * @param config Behaviour. See [CountryPickerConfig].
 * @param enabled Convenience for `state = Disabled`. When false, [state] is ignored.
 * @param state Interaction/validation state.
 * @param variant Visual form.
 * @param label Field label. Ignored by the pill variants, which have no room for one.
 * @param placeholder Shown when [selectedCountry] is `null`.
 * @param supportingText Helper text below the field. Replaced by [errorText]/[successText] in those
 *   states.
 * @param errorText Shown when [state] is [CountrySelectorState.Error].
 * @param successText Shown when [state] is [CountrySelectorState.Success].
 * @param required Marks the field required in its label and to accessibility services.
 * @param sheetTitle Sheet title. Defaults to "Select country".
 * @param sheetSubtitle Sheet subtitle.
 * @param recentCountryStore Recents persistence. The default persists nothing.
 * @param detector Country detection, or `null` to disable.
 * @param detectionBehavior What to do with a detection result.
 * @param repository Country data source.
 * @param flagContent Replaces the flag renderer, e.g. to use vector assets.
 * @param trailingContent Replaces the trailing chevron/spinner/error icon.
 * @param selectorContent Replaces the selector's entire inner layout, keeping its click and
 *   accessibility behaviour. The escape hatch for a bespoke design; prefer [variant] first.
 * @param listItemContent Replaces the sheet's row renderer.
 * @param emptyContent Replaces the sheet's no-results state.
 * @param contentConfig Independently controls what the *field itself* displays — label visibility,
 *   metadata line, flag, dropdown icon — separately from what the sheet's own rows show (which
 *   [config] controls). `null` (the default) preserves this function's own [label]/[config]-derived
 *   display exactly as before this parameter existed; pass
 *   [com.ezzy.ccp.countrypicker.theme.CountrySelectorDefaults.rowOnlyContentConfig] for the row-only
 *   layout (no label, no metadata).
 */
@Composable
public fun CountrySelector(
    selectedCountry: Country?,
    onCountrySelected: (Country) -> Unit,
    modifier: Modifier = Modifier,
    config: CountryPickerConfig = CountryPickerDefaults.config(),
    enabled: Boolean = true,
    state: CountrySelectorState = CountrySelectorState.Default,
    variant: CountrySelectorVariant = CountrySelectorVariant.Outlined,
    label: UiText? = UiText.resource(Res.string.ccp_country_of_residence),
    placeholder: UiText? = UiText.resource(Res.string.ccp_select_country_placeholder),
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
    colors: CountryPickerColors = CountryPickerDefaults.colors(),
    shapes: CountryPickerShapes = CountryPickerDefaults.shapes(),
    dimensions: CountryPickerDimensions = CountryPickerDefaults.dimensions(),
    typography: CountryPickerTypography = CountryPickerDefaults.typography(),
    motion: CountryPickerMotion = CountryPickerDefaults.motion(),
    flagContent: (@Composable (Country) -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    selectorContent: (@Composable RowScope.(Country?) -> Unit)? = null,
    listItemContent: (@Composable (CountryListItemScope) -> Unit)? = null,
    emptyContent: (@Composable (String) -> Unit)? = null,
    contentConfig: CountrySelectorContentConfig? = null,
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

    Column(modifier = modifier) {
        CountrySelectorField(
            country = selectedCountry,
            onClick = pickerState::open,
            isOpen = pickerState.isSheetOpen,
            state = effectiveState,
            variant = variant,
            label = label,
            placeholder = placeholder,
            required = required,
            showIsoCode = config.showIsoCode,
            showDialCode = config.showDialCode,
            flagShape = if (config.flagsVisible) config.flagShape else
                com.ezzy.ccp.countrypicker.theme.CountryFlagShape.Hidden,
            colors = colors,
            shapes = shapes,
            dimensions = dimensions,
            typography = typography,
            motion = motion,
            flagContent = flagContent,
            trailingContent = trailingContent,
            selectorContent = selectorContent,
            contentConfig = contentConfig,
        )

        SelectorHelperText(
            state = effectiveState,
            supportingText = supportingText,
            errorText = errorText,
            successText = successText,
            colors = colors,
            dimensions = dimensions,
            typography = typography,
            motion = motion,
        )
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
            colors = colors,
            shapes = shapes,
            dimensions = dimensions,
            typography = typography,
            motion = motion,
            flagContent = flagContent,
            listItemContent = listItemContent,
            emptyContent = emptyContent,
        )
    }
}

/**
 * The selector surface alone, without the sheet.
 *
 * Public because a host that already manages sheet visibility — inside its own navigation graph, say —
 * needs the field without the picker attaching a second one.
 */
@Composable
public fun CountrySelectorField(
    country: Country?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isOpen: Boolean = false,
    state: CountrySelectorState = CountrySelectorState.Default,
    variant: CountrySelectorVariant = CountrySelectorVariant.Outlined,
    label: UiText? = null,
    placeholder: UiText? = null,
    required: Boolean = false,
    showIsoCode: Boolean = false,
    showDialCode: Boolean = false,
    flagShape: com.ezzy.ccp.countrypicker.theme.CountryFlagShape =
        com.ezzy.ccp.countrypicker.theme.CountryFlagShape.Circle,
    colors: CountryPickerColors = CountryPickerDefaults.colors(),
    shapes: CountryPickerShapes = CountryPickerDefaults.shapes(),
    dimensions: CountryPickerDimensions = CountryPickerDefaults.dimensions(),
    typography: CountryPickerTypography = CountryPickerDefaults.typography(),
    motion: CountryPickerMotion = CountryPickerDefaults.motion(),
    flagContent: (@Composable (Country) -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    selectorContent: (@Composable RowScope.(Country?) -> Unit)? = null,
    /**
     * Overrides the merged accessibility description, verbatim.
     *
     * The default description is built from a single [country] and is wrong for a caller (like
     * [MultiCountrySelector]) whose visible value is a summary over several countries — without this,
     * a screen reader would announce only the first selected country while sighted users see
     * "3 countries selected". Multi-select passes its own summary text here instead.
     */
    accessibilityDescription: String? = null,
    /**
     * Independently controls label visibility, metadata, flag and dropdown-icon presence. `null` (the
     * default) preserves this function's existing derivation from [label]/[showIsoCode]/[showDialCode]
     * exactly — this parameter never changes behaviour unless a caller opts in. See
     * [CountrySelectorContentConfig] and [com.ezzy.ccp.countrypicker.theme.CountrySelectorDefaults].
     */
    contentConfig: CountrySelectorContentConfig? = null,
) {
    val contentDescription = accessibilityDescription ?: selectorContentDescription(
        country = country,
        label = label,
        placeholder = placeholder,
        variant = variant,
    )

    // Every one of these falls back to the pre-existing parameter-derived behaviour when no
    // contentConfig is supplied, so this is additive: nothing changes for a caller that never passes it.
    val showVisibleLabel = label != null && (contentConfig?.hasVisibleLabel ?: true)
    val effectiveShowIsoCode = contentConfig?.supportingContent?.showsIsoCode ?: showIsoCode
    val effectiveShowDialCode = contentConfig?.supportingContent?.showsDialCode ?: showDialCode
    val showFlag = contentConfig?.showFlag ?: true
    val showDropdownIcon = contentConfig?.showDropdownIcon ?: true
    val flagStyle = contentConfig?.flagConfig?.style ?: CountryFlagStyle.TonalContainer

    // A hidden label needs less reserved height than a floating one — the outlined variant's extra
    // 4dp over Filled/Minimal exists specifically to make room for the label, so a row-only selector
    // (no label) uses the same compact height as those borderless variants instead of carrying that
    // space unused.
    val effectiveMinHeight = if (variant == CountrySelectorVariant.Outlined && !showVisibleLabel) {
        dimensions.selectorMinHeight
    } else {
        variant.minHeight(dimensions)
    }

    CountrySelectorSurface(
        onClick = onClick,
        enabled = state.isInteractive,
        shape = variant.shape(shapes),
        containerColor = variant.containerColor(
            state = state,
            colors = colors,
            surfaceContainerHigh = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
        borderColor = variant.borderColor(state, colors),
        borderWidth = dimensions.selectorBorderWidth,
        minHeight = effectiveMinHeight,
        contentPadding = variant.contentPadding(dimensions),
        horizontalArrangement = Arrangement.spacedBy(
            if (variant.isFullWidth) dimensions.selectorContentSpacing else PILL_CONTENT_SPACING,
        ),
        fillWidth = variant.isFullWidth,
        colors = colors,
        dimensions = dimensions,
        motion = motion,
        modifier = modifier.semantics {
            this.contentDescription = contentDescription
            this.role = SelectorRole
        },
    ) {
        if (selectorContent != null) {
            selectorContent(country)
            return@CountrySelectorSurface
        }

        val contentColor = if (state == CountrySelectorState.Disabled) {
            colors.selectorDisabledContent
        } else {
            colors.selectorContent
        }

        if (showFlag) {
            SelectorLeadingFlag(
                country = country,
                variant = variant,
                flagShape = flagShape,
                colors = colors,
                dimensions = dimensions,
                motion = motion,
                contentColor = contentColor,
                flagContent = flagContent,
                flagStyle = flagStyle,
            )
        }

        when (variant) {
            CountrySelectorVariant.FlagOnly -> Unit

            CountrySelectorVariant.Compact -> Text(
                text = country?.displayName ?: placeholder?.resolve() ?: "",
                style = typography.compactValue,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.clearAndSetSemantics {},
            )

            CountrySelectorVariant.DialCode -> Text(
                text = country?.dialCode ?: "+—",
                // A dial code is "+" followed by digits — all neutral or weak under the bidi
                // algorithm — so in an RTL locale it renders as "254+" unless pinned to LTR.
                style = typography.dialCodeValue.copy(textDirection = CountryPickerBidi.LTR),
                color = contentColor,
                maxLines = 1,
                modifier = Modifier.clearAndSetSemantics {},
            )

            else -> SelectorValueBlock(
                country = country,
                label = label,
                showVisibleLabel = showVisibleLabel,
                placeholder = placeholder,
                required = required,
                state = state,
                showIsoCode = effectiveShowIsoCode,
                showDialCode = effectiveShowDialCode,
                colors = colors,
                typography = typography,
                motion = motion,
                modifier = Modifier.weight(1f),
            )
        }

        // Loading/error/success indicators communicate field state, not "there's a dropdown here", so
        // they still render even when showDropdownIcon hides the plain chevron.
        if (showDropdownIcon || state != CountrySelectorState.Default) {
            SelectorTrailing(
                state = state,
                isOpen = isOpen,
                colors = colors,
                dimensions = dimensions,
                motion = motion,
                trailingContent = trailingContent,
            )
        }
    }
}

/** Leading flag, or a globe placeholder for the full-width variants when nothing is selected. */
@Composable
private fun SelectorLeadingFlag(
    country: Country?,
    variant: CountrySelectorVariant,
    flagShape: com.ezzy.ccp.countrypicker.theme.CountryFlagShape,
    colors: CountryPickerColors,
    dimensions: CountryPickerDimensions,
    motion: CountryPickerMotion,
    contentColor: androidx.compose.ui.graphics.Color,
    flagContent: (@Composable (Country) -> Unit)?,
    flagStyle: CountryFlagStyle = CountryFlagStyle.TonalContainer,
) {
    val size = variant.flagSize(dimensions)
    when {
        country != null -> CountryFlag(
            country = country,
            size = size,
            shape = flagShape,
            style = flagStyle,
            colors = colors,
            dimensions = dimensions,
            motion = motion,
            flagContent = flagContent,
        )

        // A globe rather than an empty gap: the leading slot keeps its width, so selecting a country
        // does not shift the label and value sideways.
        flagShape != com.ezzy.ccp.countrypicker.theme.CountryFlagShape.Hidden -> Icon(
            imageVector = PickerIcons.Globe,
            contentDescription = null,
            tint = contentColor.copy(alpha = PLACEHOLDER_ICON_ALPHA),
            modifier = Modifier.size(size),
        )
    }
}

/**
 * Label + value + metadata stack for the full-width variants.
 *
 * @param showVisibleLabel Whether the label `Text` renders at all. This is the *visual* switch for
 *   [InputLabelMode.Hidden] — the accessibility description (built separately, in
 *   [selectorContentDescription]) always includes [label] regardless of this flag, so hiding the
 *   visible label never produces an unlabeled control for a screen reader.
 */
@Composable
private fun SelectorValueBlock(
    country: Country?,
    label: UiText?,
    showVisibleLabel: Boolean,
    placeholder: UiText?,
    required: Boolean,
    state: CountrySelectorState,
    showIsoCode: Boolean,
    showDialCode: Boolean,
    colors: CountryPickerColors,
    typography: CountryPickerTypography,
    motion: CountryPickerMotion,
    modifier: Modifier = Modifier,
) {
    // The whole block is announced by the surface's content description, so its children are hidden
    // from accessibility to avoid TalkBack reading disjoint fragments.
    Column(
        modifier = modifier.clearAndSetSemantics {},
        verticalArrangement = if (showVisibleLabel) Arrangement.Top else Arrangement.Center,
    ) {
        if (label != null && showVisibleLabel) {
            val labelText = label.resolve()
            Text(
                text = if (required) {
                    stringResource(Res.string.ccp_required_label, labelText)
                } else {
                    labelText
                },
                style = typography.fieldLabel,
                color = when (state) {
                    CountrySelectorState.Error -> colors.error
                    CountrySelectorState.Disabled -> colors.selectorDisabledContent
                    else -> colors.selectorLabel
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        // Crossfade + slight rise so a country change reads as the value being replaced.
        AnimatedContent(
            targetState = country,
            transitionSpec = {
                (fadeIn(motion.fadeIn) + slideInVertically(motion.listItemPlacement) { it / 4 })
                    .togetherWith(fadeOut(motion.fadeOut))
            },
            contentKey = { it?.iso2Code ?: PLACEHOLDER_KEY },
            label = "selectorValue",
        ) { target ->
            Text(
                text = target?.displayName ?: placeholder?.resolve() ?: "",
                style = typography.selectorValue,
                color = when {
                    state == CountrySelectorState.Disabled -> colors.selectorDisabledContent
                    target == null -> colors.selectorLabel
                    else -> colors.selectorContent
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        val metadata = country?.metadataLine(showIsoCode, showDialCode)
        if (!metadata.isNullOrEmpty()) {
            Text(
                text = metadata,
                // "KE · +254" is entirely codes and digits; see CountryPickerBidi.
                style = typography.selectorSecondary.copy(textDirection = CountryPickerBidi.LTR),
                color = if (state == CountrySelectorState.Disabled) {
                    colors.selectorDisabledContent
                } else {
                    colors.selectorSecondaryContent
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/** Trailing chevron, spinner, or state icon. */
@Composable
private fun SelectorTrailing(
    state: CountrySelectorState,
    isOpen: Boolean,
    colors: CountryPickerColors,
    dimensions: CountryPickerDimensions,
    motion: CountryPickerMotion,
    trailingContent: (@Composable () -> Unit)?,
) {
    if (trailingContent != null) {
        trailingContent()
        return
    }

    // Rotating the chevron ties the sheet's open state to the control that opened it.
    val chevronRotation by animateFloatAsState(
        targetValue = if (isOpen) CHEVRON_OPEN_ROTATION else 0f,
        animationSpec = motion.floatSpec,
        label = "chevronRotation",
    )

    when (state) {
        CountrySelectorState.Loading -> CircularProgressIndicator(
            color = colors.selectorFocusedBorder,
            strokeWidth = SPINNER_STROKE,
            modifier = Modifier.size(SPINNER_SIZE),
        )

        CountrySelectorState.Error -> Icon(
            imageVector = PickerIcons.Alert,
            contentDescription = null,
            tint = colors.error,
            modifier = Modifier.size(dimensions.chevronSize),
        )

        CountrySelectorState.Success -> Icon(
            imageVector = PickerIcons.CheckCircle,
            contentDescription = null,
            tint = colors.success,
            modifier = Modifier.size(dimensions.chevronSize),
        )

        else -> Icon(
            imageVector = EzzyIcons.ChevronDown,
            contentDescription = null,
            tint = if (state == CountrySelectorState.Disabled) {
                colors.selectorDisabledContent
            } else {
                colors.chevron
            },
            modifier = Modifier
                .size(dimensions.chevronSize)
                .rotate(chevronRotation),
        )
    }
}

/** Helper, error or success text beneath the field. */
@Composable
private fun SelectorHelperText(
    state: CountrySelectorState,
    supportingText: UiText?,
    errorText: UiText?,
    successText: UiText?,
    colors: CountryPickerColors,
    dimensions: CountryPickerDimensions,
    typography: CountryPickerTypography,
    motion: CountryPickerMotion,
) {
    val (text, color, icon) = when {
        state == CountrySelectorState.Error && errorText != null ->
            Triple(errorText, colors.error, PickerIcons.Alert)

        state == CountrySelectorState.Success && successText != null ->
            Triple(successText, colors.success, PickerIcons.CheckCircle)

        supportingText != null -> Triple(supportingText, colors.selectorSecondaryContent, null)
        else -> Triple(null, colors.selectorSecondaryContent, null)
    }

    AnimatedVisibility(
        visible = text != null,
        enter = fadeIn(motion.fadeIn),
        exit = fadeOut(motion.fadeOut),
    ) {
        Row(
            modifier = Modifier.padding(
                start = dimensions.selectorHorizontalPadding,
                end = dimensions.selectorHorizontalPadding,
                top = HELPER_TOP_PADDING,
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(HELPER_ICON_SPACING),
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(HELPER_ICON_SIZE),
                )
            }
            Text(
                text = text?.resolve().orEmpty(),
                style = typography.helperText,
                color = color,
            )
        }
    }
}

/**
 * The selector's accessibility label.
 *
 * Built as one complete sentence rather than assembled from fragments, because TalkBack reads the
 * description verbatim and "Country of residence" + "Kenya" + "button" as three nodes is markedly
 * worse than one phrase. The flag-only variant gets special treatment: it has no visible text at all,
 * so its description is the only thing that identifies it.
 */
@Composable
private fun selectorContentDescription(
    country: Country?,
    label: UiText?,
    placeholder: UiText?,
    variant: CountrySelectorVariant,
): String {
    val labelText = label?.resolve()
        ?: stringResource(Res.string.ccp_country_of_residence)
    val placeholderText = placeholder?.resolve()
        ?: stringResource(Res.string.ccp_select_country_placeholder)

    return when {
        variant == CountrySelectorVariant.FlagOnly && country != null ->
            stringResource(Res.string.ccp_flag_only_a11y, country.displayName)

        variant == CountrySelectorVariant.FlagOnly ->
            stringResource(Res.string.ccp_flag_only_a11y_empty)

        variant == CountrySelectorVariant.DialCode && country != null ->
            stringResource(Res.string.ccp_dial_code_a11y, country.dialCode, country.displayName)

        country != null ->
            stringResource(Res.string.ccp_selector_a11y_selected, labelText, country.displayName)

        else -> stringResource(Res.string.ccp_selector_a11y_empty, labelText, placeholderText)
    }
}

/** Inner padding per variant. */
internal fun CountrySelectorVariant.contentPadding(
    dimensions: CountryPickerDimensions,
): PaddingValues = when (this) {
    CountrySelectorVariant.Filled, CountrySelectorVariant.Outlined -> PaddingValues(
        horizontal = dimensions.selectorHorizontalPadding,
        vertical = dimensions.selectorVerticalPadding,
    )

    CountrySelectorVariant.Minimal -> PaddingValues(
        horizontal = MINIMAL_HORIZONTAL_PADDING,
        vertical = dimensions.selectorVerticalPadding,
    )

    CountrySelectorVariant.Compact -> PaddingValues(start = 10.dp, end = 12.dp)
    CountrySelectorVariant.FlagOnly -> PaddingValues(start = 10.dp, end = 8.dp)
    CountrySelectorVariant.DialCode -> PaddingValues(start = 12.dp, end = 10.dp)
}

/**
 * The `FR · +33` metadata line, or `null` when neither piece is enabled.
 *
 * Uses a middle dot with spaces so TalkBack reads a pause rather than running the two values
 * together.
 */
internal fun Country.metadataLine(showIsoCode: Boolean, showDialCode: Boolean): String? {
    val parts = buildList {
        if (showIsoCode) add(iso2Code)
        if (showDialCode) add(dialCode)
    }
    return parts.takeIf { it.isNotEmpty() }?.joinToString(METADATA_SEPARATOR)
}

/** Spacer that keeps a fixed gap without an Arrangement. */
@Composable
internal fun HorizontalGap(width: androidx.compose.ui.unit.Dp) {
    Spacer(Modifier.width(width))
}

/** Box that centres a small trailing control in a consistent slot. */
@Composable
internal fun TrailingSlot(
    size: androidx.compose.ui.unit.Dp,
    content: @Composable () -> Unit,
) {
    Box(modifier = Modifier.size(size), contentAlignment = Alignment.Center) { content() }
}

private const val CHEVRON_OPEN_ROTATION = 180f
private const val PLACEHOLDER_ICON_ALPHA = 0.6f
private const val PLACEHOLDER_KEY = "__placeholder__"
private const val METADATA_SEPARATOR = " · "
private val PILL_CONTENT_SPACING = 8.dp
private val MINIMAL_HORIZONTAL_PADDING = 4.dp
private val HELPER_TOP_PADDING = 4.dp
private val HELPER_ICON_SPACING = 6.dp
private val HELPER_ICON_SIZE = 16.dp
private val SPINNER_SIZE = 20.dp
private val SPINNER_STROKE = 2.dp
