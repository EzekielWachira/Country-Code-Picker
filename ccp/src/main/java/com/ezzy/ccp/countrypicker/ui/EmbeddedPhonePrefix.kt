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
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import com.ezzy.ccp.R
import com.ezzy.ccp.countrypicker.data.CountryRepository
import com.ezzy.ccp.countrypicker.model.Country
import com.ezzy.ccp.countrypicker.model.PhonePrefixContentMode
import com.ezzy.ccp.countrypicker.model.UiText
import com.ezzy.ccp.countrypicker.persistence.NoOpRecentCountryStore
import com.ezzy.ccp.countrypicker.persistence.RecentCountryStore
import com.ezzy.ccp.countrypicker.state.CountryPickerConfig
import com.ezzy.ccp.countrypicker.state.rememberCountryPickerState
import com.ezzy.ccp.countrypicker.theme.CountryFlagConfig
import com.ezzy.ccp.countrypicker.theme.CountryFlagStyle
import com.ezzy.ccp.countrypicker.theme.CountryPickerColors
import com.ezzy.ccp.countrypicker.theme.CountryPickerDefaults
import com.ezzy.ccp.countrypicker.theme.CountryPickerDimensions
import com.ezzy.ccp.countrypicker.theme.CountryPickerMotion
import com.ezzy.ccp.countrypicker.theme.CountryPickerShapes
import com.ezzy.ccp.countrypicker.theme.CountryPickerTypography
import com.ezzy.ccp.icons.ChevronDown
import com.ezzy.ccp.icons.EzzyIcons

/**
 * The phone field's country prefix, rendered as a bare clickable segment *inside* the unified field's
 * own outline — not as its own bordered pill.
 *
 * This is the piece that fixes the "two separate rounded boxes" bug: [PhoneCountryCodeSelector] (and
 * every other [CountrySelectorVariant]) intentionally draws its own [CountrySelectorSurface] with a
 * background and border, because a *standalone* dial-code pill is a legitimate, correct look on its
 * own. Embedded inside [PhoneNumberField], though, that same independent Surface is the bug — it reads
 * as a second field glued to the first. This composable renders exactly the same flag/dial-code/chevron
 * content (via the shared [PhonePrefixInnerContent]) with no Surface of its own: no background, no
 * border, no independent shape — just a plain `Modifier.clickable` region that shares the outer field's
 * single outline.
 *
 * It owns its own [rememberCountryPickerState] and opens the same [CountryPickerSheet] every other
 * selector uses — "the same reusable country picker" the acceptance criteria asks for, not a
 * parallel one.
 *
 * @param onCountrySelected Called with the newly chosen country. The caller re-formats/re-validates the
 *   number for the new region — [PhoneNumberField] already does this via [com.ezzy.ccp.countrypicker.state.PhoneNumberFieldState.selectCountry].
 */
@Composable
internal fun EmbeddedPhonePrefix(
    selectedCountry: Country,
    onCountrySelected: (Country) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentMode: PhonePrefixContentMode = PhonePrefixContentMode.FlagAndDialCode,
    showDropdownIcon: Boolean = true,
    flagConfig: CountryFlagConfig = CountryFlagConfig(style = CountryFlagStyle.Plain, size = 24.dp),
    config: CountryPickerConfig = CountryPickerDefaults.phoneConfig(),
    sheetTitle: UiText = UiText.resource(R.string.ccp_country_code_title),
    sheetSubtitle: UiText? = UiText.resource(R.string.ccp_country_code_subtitle),
    recentCountryStore: RecentCountryStore = NoOpRecentCountryStore,
    repository: CountryRepository = CountryRepository.Default,
    colors: CountryPickerColors = CountryPickerDefaults.colors(),
    shapes: CountryPickerShapes = CountryPickerDefaults.shapes(),
    dimensions: CountryPickerDimensions = CountryPickerDefaults.dimensions(),
    typography: CountryPickerTypography = CountryPickerDefaults.typography(),
    motion: CountryPickerMotion = CountryPickerDefaults.motion(),
    flagContent: (@Composable (Country) -> Unit)? = null,
    /**
     * Reports whenever the embedded sheet opens or closes, so an enclosing field (see
     * [PhoneNumberField]) can treat "the country picker is open" as an active state for its own
     * outline — the prefix is part of the same field, so its interaction should read as the field
     * being interacted with, not as an unrelated control opening a dialog elsewhere.
     */
    onOpenChanged: (Boolean) -> Unit = {},
) {
    val pickerState = rememberCountryPickerState(
        config = config,
        selectedCountries = setOf(selectedCountry),
        repository = repository,
        recentCountryStore = recentCountryStore,
    )

    LaunchedEffect(pickerState.isSheetOpen) { onOpenChanged(pickerState.isSheetOpen) }

    val description = phonePrefixContentDescription(selectedCountry)

    Row(
        modifier = modifier
            // Clipped to a shape close to the field's own rounding rather than left rectangular, so the
            // ripple does not visually spill past the field's rounded start corners.
            .clip(PrefixRippleShape)
            .clickable(enabled = enabled, role = Role.Button, onClick = pickerState::open)
            .defaultMinSize(minHeight = dimensions.minimumTouchTarget)
            .padding(horizontal = PREFIX_HORIZONTAL_PADDING)
            .semantics {
                contentDescription = description
                role = Role.Button
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PREFIX_CONTENT_SPACING),
    ) {
        PhonePrefixInnerContent(
            country = selectedCountry,
            contentMode = contentMode,
            showDropdownIcon = showDropdownIcon,
            isOpen = pickerState.isSheetOpen,
            flagConfig = flagConfig,
            colors = colors,
            dimensions = dimensions,
            typography = typography,
            motion = motion,
            flagContent = flagContent,
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
        )
    }
}

/**
 * The flag/dial-code/chevron row content shared between [EmbeddedPhonePrefix] (bare, no Surface) and
 * anything else that wants the identical visual — the one place this rendering exists, so a change to
 * how the prefix looks never needs to be made twice.
 */
@Composable
internal fun PhonePrefixInnerContent(
    country: Country,
    contentMode: PhonePrefixContentMode,
    showDropdownIcon: Boolean,
    isOpen: Boolean,
    flagConfig: CountryFlagConfig,
    colors: CountryPickerColors,
    dimensions: CountryPickerDimensions,
    typography: CountryPickerTypography,
    motion: CountryPickerMotion,
    flagContent: (@Composable (Country) -> Unit)?,
) {
    if (contentMode.showsFlag) {
        CountryFlag(
            country = country,
            config = flagConfig,
            colors = colors,
            dimensions = dimensions,
            motion = motion,
            flagContent = flagContent,
        )
    }

    when {
        contentMode.showsCountryCode -> Text(
            text = country.iso2Code,
            style = typography.dialCodeValue,
            color = colors.selectorContent,
            maxLines = 1,
            modifier = Modifier.clearAndSetSemantics {},
        )

        contentMode.showsDialCode -> Text(
            text = country.dialCode,
            style = typography.dialCodeValue,
            color = colors.selectorContent,
            maxLines = 1,
            modifier = Modifier.clearAndSetSemantics {},
        )
    }

    if (showDropdownIcon) {
        val rotation by animateFloatAsState(
            targetValue = if (isOpen) PREFIX_CHEVRON_OPEN_ROTATION else 0f,
            animationSpec = motion.floatSpec,
            label = "phonePrefixChevron",
        )
        Icon(
            imageVector = EzzyIcons.ChevronDown,
            contentDescription = null,
            tint = colors.chevron,
            modifier = Modifier
                .size(dimensions.chevronSize * PREFIX_CHEVRON_SCALE)
                .rotate(rotation),
        )
    }
}

/**
 * The vertical divider between the prefix and the number editor.
 *
 * Deliberately lighter than the field's own outline (a fixed low alpha over the border color, never
 * the border color at full strength) — the acceptance criteria calls for a divider that reads as
 * "subtle", and a divider as dark as the outline it sits inside competes with it rather than reading
 * as a lesser internal separator.
 *
 * Takes an explicit [fieldHeight] and computes its own height from it directly, rather than using
 * `Modifier.fillMaxHeight(fraction)` against whatever the ambient constraint happens to be. A `Row`
 * sitting inside a `Surface` with only `defaultMinSize(minHeight = ...)` does not necessarily carry a
 * *bounded* incoming max height — `defaultMinSize` sets a floor, not a ceiling — so `fillMaxHeight` can
 * end up computing a fraction of an effectively unbounded constraint, which is exactly what made an
 * earlier version of this divider (and the row containing it) balloon to fill the rest of the screen
 * instead of sitting at a normal text-field height. Deriving the height directly from a value the
 * caller already knows removes that dependency on constraint propagation entirely.
 *
 * @param visible When `false`, the divider is not just made transparent but removed from the layout
 *   entirely, so hiding it never leaves a dead gap.
 * @param fieldHeight The unified field's own target height, from which [heightFraction] is taken.
 */
@Composable
internal fun PhonePrefixDivider(
    visible: Boolean,
    fieldHeight: Dp,
    modifier: Modifier = Modifier,
    heightFraction: Float = DIVIDER_HEIGHT_FRACTION,
    colors: CountryPickerColors = CountryPickerDefaults.colors(),
    motion: CountryPickerMotion = CountryPickerDefaults.motion(),
) {
    val width by animateDpAsState(
        targetValue = if (visible) DIVIDER_WIDTH else 0.dp,
        animationSpec = motion.dpSpec,
        label = "dividerWidth",
    )
    val color by animateColorAsState(
        targetValue = if (visible) colors.selectorBorder.copy(alpha = DIVIDER_ALPHA) else
            colors.selectorBorder.copy(alpha = 0f),
        animationSpec = motion.colorSpec,
        label = "dividerColor",
    )
    if (width <= 0.dp) return

    Box(
        modifier = modifier
            .padding(vertical = DIVIDER_VERTICAL_INSET)
            .height((fieldHeight * heightFraction).coerceAtLeast(0.dp))
            .width(width)
            .clip(RoundedCornerShape(percent = 50))
            .background(color),
    )
}

/**
 * Builds "Kenya, calling code plus two five four. Double tap to change country." — the dial code is
 * spelled out digit-by-digit via `R.array.ccp_digit_words` rather than left as "+254", since a screen
 * reader given the numeral form tends to read it as a cardinal number ("two hundred fifty-four")
 * instead of a sequence of individual digits.
 */
@Composable
private fun phonePrefixContentDescription(country: Country): String {
    val plusWord = stringResource(R.string.ccp_phone_prefix_plus)
    val digitWords = androidx.compose.ui.res.stringArrayResource(R.array.ccp_digit_words)
    val spokenDialCode = country.dialCodeDigits
        .mapNotNull { digit -> digit.digitToIntOrNull()?.let { digitWords.getOrNull(it) } }
        .joinToString(separator = " ", prefix = "$plusWord ")
    return stringResource(R.string.ccp_phone_prefix_a11y, country.displayName, spokenDialCode)
}

private const val PREFIX_CHEVRON_OPEN_ROTATION = 180f
private const val PREFIX_CHEVRON_SCALE = 0.85f
private const val DIVIDER_ALPHA = 0.6f
private const val DIVIDER_HEIGHT_FRACTION = 0.48f
private val DIVIDER_WIDTH = 1.dp
private val DIVIDER_VERTICAL_INSET = 4.dp
private val PREFIX_HORIZONTAL_PADDING = 12.dp
private val PREFIX_CONTENT_SPACING = 8.dp
private val PrefixRippleShape = RoundedCornerShape(12.dp)
