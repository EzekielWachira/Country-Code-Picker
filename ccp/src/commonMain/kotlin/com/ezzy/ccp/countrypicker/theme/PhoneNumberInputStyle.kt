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
import com.ezzy.ccp.countrypicker.model.InputLabelMode
import com.ezzy.ccp.countrypicker.model.PhonePrefixContentMode

/**
 * Options specific to [com.ezzy.ccp.countrypicker.ui.PhoneNumberField] — one typed value instead of a
 * row of unrelated booleans on the composable.
 *
 * Colors, shapes, flags and motion come from the [CountryPickerStyle] like every other component;
 * these are the choices only a phone field has.
 *
 * @property labelMode A floating label, or none. Hiding it never removes the label from
 *   accessibility.
 * @property prefixContentMode What the country prefix shows — see [PhonePrefixContentMode].
 * @property showDropdownIcon The small chevron in the prefix.
 * @property showPrefixDivider The hairline between the prefix and the number.
 * @property size Overall size — see [PhoneFieldSize].
 * @property showGhostDigits Show the country's example number as faint digits that the user's typing
 *   fills in, so the expected length and grouping are visible before a digit is typed.
 * @property showProgress A thin bar along the bottom of the field that fills as digits are entered and
 *   turns to the success color once the number is valid.
 * @property showNumberType A badge naming the kind of number — Mobile, Landline — once it is valid.
 * @property showValidIndicator A check mark once the number is valid.
 */
@Immutable
public data class PhoneNumberInputStyle(
    val labelMode: InputLabelMode = InputLabelMode.Floating,
    val prefixContentMode: PhonePrefixContentMode = PhonePrefixContentMode.FlagAndDialCode,
    val showDropdownIcon: Boolean = true,
    val showPrefixDivider: Boolean = true,
    val size: PhoneFieldSize = PhoneFieldSize.Regular,
    val showGhostDigits: Boolean = true,
    val showProgress: Boolean = true,
    val showNumberType: Boolean = true,
    val showValidIndicator: Boolean = true,
) {
    /** [labelMode] as a plain boolean. */
    val hasVisibleLabel: Boolean get() = labelMode == InputLabelMode.Floating
}

/** Builders for [PhoneNumberInputStyle]. */
public object PhoneNumberInputDefaults {

    /** The default phone field: floating label, flag and dial code, ghost digits and progress. */
    public fun style(
        labelMode: InputLabelMode = InputLabelMode.Floating,
        prefixContentMode: PhonePrefixContentMode = PhonePrefixContentMode.FlagAndDialCode,
        showDropdownIcon: Boolean = true,
        showPrefixDivider: Boolean = true,
        size: PhoneFieldSize = PhoneFieldSize.Regular,
        showGhostDigits: Boolean = true,
        showProgress: Boolean = true,
        showNumberType: Boolean = true,
        showValidIndicator: Boolean = true,
    ): PhoneNumberInputStyle = PhoneNumberInputStyle(
        labelMode = labelMode,
        prefixContentMode = prefixContentMode,
        showDropdownIcon = showDropdownIcon,
        showPrefixDivider = showPrefixDivider,
        size = size,
        showGhostDigits = showGhostDigits,
        showProgress = showProgress,
        showNumberType = showNumberType,
        showValidIndicator = showValidIndicator,
    )

    /** No visible label, for a field the surrounding form already labels. */
    public fun hiddenLabelStyle(size: PhoneFieldSize = PhoneFieldSize.Regular): PhoneNumberInputStyle =
        PhoneNumberInputStyle(labelMode = InputLabelMode.Hidden, size = size)
}
