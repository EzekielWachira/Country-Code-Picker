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
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ezzy.ccp.countrypicker.data.CountryRepository
import com.ezzy.ccp.countrypicker.model.Country
import com.ezzy.ccp.countrypicker.model.CountryPickerBidi
import com.ezzy.ccp.countrypicker.model.PhonePrefixContentMode
import com.ezzy.ccp.countrypicker.model.UiText
import com.ezzy.ccp.countrypicker.persistence.NoOpRecentCountryStore
import com.ezzy.ccp.countrypicker.persistence.RecentCountryStore
import com.ezzy.ccp.countrypicker.state.CountryPickerConfig
import com.ezzy.ccp.countrypicker.state.rememberCountryPickerState
import com.ezzy.ccp.countrypicker.theme.CountryFlagStyle
import com.ezzy.ccp.countrypicker.theme.CountryPickerDefaults
import com.ezzy.ccp.countrypicker.theme.CountryPickerStyle
import com.ezzy.ccp.countrypicker.theme.CountryPickerTheme
import com.ezzy.ccp.resources.Res
import com.ezzy.ccp.resources.ccp_country_code_subtitle
import com.ezzy.ccp.resources.ccp_country_code_title
import com.ezzy.ccp.resources.ccp_digit_words
import com.ezzy.ccp.resources.ccp_phone_prefix_a11y
import com.ezzy.ccp.resources.ccp_phone_prefix_plus
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource

/**
 * The country prefix inside a phone field — flag, dial code and a small chevron — that opens the
 * country-code picker.
 *
 * When the country changes on its own (a pasted or autofilled international number), the dial code
 * rolls to its new value and the flag swaps, so the change is noticed rather than missed.
 *
 * @param flagStyle How the flag is drawn here, independently of the picker's rows.
 * @param textStyle The dial code's style. A color set on it overrides the style's text color.
 * @param minHeight Touch-target height. Shrinks with the compact field sizes so the prefix is never
 *   taller than the field it sits in.
 * @param onOpenChanged Reports whether the picker is open, so the field can draw itself active while
 *   either of its two regions has the user's attention.
 */
@Composable
internal fun EmbeddedPhonePrefix(
    selectedCountry: Country,
    onCountrySelected: (Country) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentMode: PhonePrefixContentMode = PhonePrefixContentMode.FlagAndDialCode,
    showDropdownIcon: Boolean = true,
    flagSize: Dp = CountryPickerTheme.style.dimensions.flagSizeCompact,
    flagStyle: CountryFlagStyle = CountryPickerTheme.style.layout.flagStyle,
    textStyle: TextStyle = CountryPickerTheme.style.typography.dialCode,
    chevronColor: Color = CountryPickerTheme.style.colors.textTertiary,
    config: CountryPickerConfig = CountryPickerDefaults.phoneConfig(),
    sheetTitle: UiText = UiText.resource(Res.string.ccp_country_code_title),
    sheetSubtitle: UiText? = UiText.resource(Res.string.ccp_country_code_subtitle),
    recentCountryStore: RecentCountryStore = NoOpRecentCountryStore,
    repository: CountryRepository = CountryRepository.Default,
    style: CountryPickerStyle = CountryPickerTheme.style,
    minHeight: Dp = style.dimensions.minimumTouchTarget,
    flagContent: (@Composable (Country) -> Unit)? = null,
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
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val highlight by animateColorAsState(
        targetValue = if (pressed || pickerState.isSheetOpen) style.colors.surfaceSunken else Color.Transparent,
        animationSpec = style.motion.color,
        label = "prefixHighlight",
    )

    Row(
        modifier = modifier
            .clip(PrefixShape)
            .background(highlight)
            .clickable(interactionSource = interaction, indication = null, enabled = enabled, role = Role.Button, onClick = pickerState::open)
            .defaultMinSize(minHeight = minHeight)
            .padding(horizontal = 6.dp)
            .semantics {
                contentDescription = description
                role = Role.Button
            },
        verticalAlignment = Alignment.CenterVertically,
        // Image flags fill their box edge to edge, unlike an emoji glyph with its side bearings, so
        // the gap is set for them.
        horizontalArrangement = Arrangement.spacedBy(9.dp),
    ) {
        PhonePrefixInnerContent(
            country = selectedCountry,
            contentMode = contentMode,
            showDropdownIcon = showDropdownIcon,
            isOpen = pickerState.isSheetOpen,
            flagSize = flagSize,
            flagStyle = flagStyle,
            textStyle = textStyle,
            chevronColor = chevronColor,
            style = style,
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
            style = style.withDialCodes(),
            flagContent = flagContent,
        )
    }
}

/** Flag, dial code (or ISO code) and chevron, as [contentMode] asks. Hidden from accessibility. */
@Composable
private fun PhonePrefixInnerContent(
    country: Country,
    contentMode: PhonePrefixContentMode,
    showDropdownIcon: Boolean,
    isOpen: Boolean,
    flagSize: Dp,
    flagStyle: CountryFlagStyle,
    textStyle: TextStyle,
    chevronColor: Color,
    style: CountryPickerStyle,
    flagContent: (@Composable (Country) -> Unit)?,
) {
    if (contentMode.showsFlag) {
        CountryFlag(
            country = country,
            size = flagSize,
            style = flagStyle,
            flagContent = flagContent,
        )
    }
    if (contentMode.showsCountryCode) {
        // The ISO code stands where the flag would: a small tag, so "KE +254" reads as two parts.
        Text(
            text = country.iso2Code,
            style = style.typography.badge,
            color = style.colors.textSecondary,
            maxLines = 1,
            modifier = Modifier
                .clip(ISO_TAG_SHAPE)
                .background(style.colors.surfaceSunken)
                .padding(horizontal = 6.dp, vertical = 3.dp)
                .clearAndSetSemantics {},
        )
    }
    if (contentMode.showsDialCode) {
        AnimatedContent(
            targetState = country.dialCode,
            transitionSpec = {
                (slideInVertically(style.motion.offset) { it } + fadeIn(style.motion.fadeIn)) togetherWith
                    (slideOutVertically(style.motion.offset) { -it } + fadeOut(style.motion.fadeOut))
            },
            modifier = Modifier.clearAndSetSemantics {},
            label = "prefixCode",
        ) { value ->
            Text(
                text = value,
                // Pinned LTR so "+254" never renders as "254+" beside RTL text — see CountryPickerBidi.
                style = textStyle.copy(textDirection = CountryPickerBidi.LTR),
                color = textStyle.color.takeOrElse { style.colors.textPrimary },
                maxLines = 1,
            )
        }
    }
    if (showDropdownIcon) {
        val rotation by animateFloatAsState(if (isOpen) 180f else 0f, style.motion.layout, label = "prefixChevron")
        Icon(
            imageVector = PickerIcons.ChevronDown,
            contentDescription = null,
            tint = chevronColor,
            modifier = Modifier.size(14.dp).rotate(rotation),
        )
    }
}

/** The hairline between the prefix and the number, growing in and out as it is shown or hidden. */
@Composable
internal fun PhonePrefixDivider(
    visible: Boolean,
    height: Dp,
    style: CountryPickerStyle,
    modifier: Modifier = Modifier,
) {
    val alpha by animateFloatAsState(if (visible) 1f else 0f, style.motion.layout, label = "dividerAlpha")
    if (alpha <= 0f) return
    Box(
        modifier = modifier
            .width(1.dp)
            .height(height * alpha)
            .background(style.colors.hairline.copy(alpha = style.colors.hairline.alpha * 1.6f * alpha)),
    )
}

/**
 * "Kenya, calling code plus two five four. Double tap to change country." — the dial code spelled
 * digit by digit, because a screen reader given "+254" tends to read a cardinal number ("two hundred
 * fifty-four") instead of a sequence of digits.
 */
@Composable
private fun phonePrefixContentDescription(country: Country): String {
    val plusWord = stringResource(Res.string.ccp_phone_prefix_plus)
    val digitWords = stringArrayResource(Res.array.ccp_digit_words)
    val spokenDialCode = country.dialCodeDigits
        .mapNotNull { digit -> digit.digitToIntOrNull()?.let { digitWords.getOrNull(it) } }
        .joinToString(separator = " ", prefix = "$plusWord ")
    return stringResource(Res.string.ccp_phone_prefix_a11y, country.displayName, spokenDialCode)
}

/**
 * This style with dial codes on the picker's rows. A country-code picker shows them whatever the
 * theme says — in that picker they are the very thing being chosen.
 */
internal fun CountryPickerStyle.withDialCodes(): CountryPickerStyle =
    if (layout.showDialCode) this else copy(layout = layout.copy(showDialCode = true))

private val PrefixShape = RoundedCornerShape(10.dp)
private val ISO_TAG_SHAPE = RoundedCornerShape(6.dp)
