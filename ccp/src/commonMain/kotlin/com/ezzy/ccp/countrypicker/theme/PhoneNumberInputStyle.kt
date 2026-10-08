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

package com.ezzy.ccp.countrypicker.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.dp
import com.ezzy.ccp.countrypicker.model.InputLabelMode
import com.ezzy.ccp.countrypicker.model.PhonePrefixContentMode

/**
 * Visual configuration for the unified phone number field — one typed model instead of the seven or
 * eight unrelated booleans (`showLabel`, `showFlag`, `showDialCode`, `showDivider`, `showChevron`, …)
 * that configuring a field this way would otherwise require.
 *
 * @property labelMode Whether the field shows a visible floating label. See [InputLabelMode] — hiding
 *   it never removes the label from accessibility.
 * @property flagConfig How the prefix's flag is presented. Defaults to [CountryFlagStyle.Plain] — a
 *   bare flag with no background — matching the compact phone-field reference; the fuller
 *   [CountryFlagStyle.TonalContainer] look most other selectors use would compete visually with the
 *   dial code and chevron packed into the same small prefix.
 * @property showDropdownIcon Whether the trailing chevron renders in the prefix.
 * @property showPrefixDivider Whether the vertical divider between the prefix and the number editor
 *   renders. Off removes it entirely rather than just making it transparent, so no dead space remains.
 * @property prefixContentMode What the prefix shows before the divider — see [PhonePrefixContentMode].
 * @property size Overall field size — see [PhoneFieldSize]. [PhoneFieldSize.Regular] (the default)
 *   changes nothing; [PhoneFieldSize.Compact]/[PhoneFieldSize.ExtraCompact] shrink the field's
 *   padding, flag, chevron, and icon-button sizes together with its font size, so a shorter field
 *   never clips or crowds its own content.
 */
@Immutable
public data class PhoneNumberInputStyle(
    val labelMode: InputLabelMode = InputLabelMode.Floating,
    val flagConfig: CountryFlagConfig = CountryFlagConfig(style = CountryFlagStyle.Plain, size = 24.dp),
    val showDropdownIcon: Boolean = true,
    val showPrefixDivider: Boolean = true,
    val prefixContentMode: PhonePrefixContentMode = PhonePrefixContentMode.FlagAndDialCode,
    val size: PhoneFieldSize = PhoneFieldSize.Regular,
) {
    /** [labelMode] as a plain boolean, for call sites that don't need the enum's other meaning. */
    val hasVisibleLabel: Boolean get() = labelMode == InputLabelMode.Floating
}

/** Component defaults for [PhoneNumberInputStyle], mirroring the rest of the library's convention. */
public object PhoneNumberInputDefaults {

    /** The library's default unified phone field style: floating label, plain flag, flag + dial code. */
    public fun style(
        labelMode: InputLabelMode = InputLabelMode.Floating,
        flagConfig: CountryFlagConfig = CountryFlagConfig(style = CountryFlagStyle.Plain, size = 24.dp),
        showDropdownIcon: Boolean = true,
        showPrefixDivider: Boolean = true,
        prefixContentMode: PhonePrefixContentMode = PhonePrefixContentMode.FlagAndDialCode,
        size: PhoneFieldSize = PhoneFieldSize.Regular,
    ): PhoneNumberInputStyle = PhoneNumberInputStyle(
        labelMode = labelMode,
        flagConfig = flagConfig,
        showDropdownIcon = showDropdownIcon,
        showPrefixDivider = showPrefixDivider,
        prefixContentMode = prefixContentMode,
        size = size,
    )

    /** A style with no visible label, for a field embedded where the surrounding form already labels it. */
    public fun hiddenLabelStyle(
        flagConfig: CountryFlagConfig = CountryFlagConfig(style = CountryFlagStyle.Plain, size = 24.dp),
        size: PhoneFieldSize = PhoneFieldSize.Regular,
    ): PhoneNumberInputStyle =
        PhoneNumberInputStyle(labelMode = InputLabelMode.Hidden, flagConfig = flagConfig, size = size)
}
