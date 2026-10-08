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
import com.ezzy.ccp.countrypicker.model.CountrySupportingContent
import com.ezzy.ccp.countrypicker.model.InputLabelMode

/**
 * What a [com.ezzy.ccp.countrypicker.ui.CountrySelector] field itself displays — independently of what
 * the country sheet's own rows show.
 *
 * This is what makes the field's label/metadata *independently configurable* from the sheet: a host can
 * show ISO+dial code under the sheet's rows while showing nothing under the field's own value, or vice
 * versa, because these are two different concerns — [com.ezzy.ccp.countrypicker.state.CountryPickerConfig]
 * governs the sheet, this governs the field.
 *
 * Passing `null` for a selector's `contentConfig` parameter (the default everywhere) preserves each
 * component's pre-existing derivation from its own `label` parameter and picker config — this type is
 * purely additive and never required.
 *
 * @property labelMode Whether the field shows a visible label above its value. Hiding it never removes
 *   the label from accessibility — see [InputLabelMode].
 * @property showCountryName Whether the country's name is shown at all. Off is unusual (a flag-only
 *   look without the FlagOnly variant's compact footprint) but valid — e.g. a purely iconographic field.
 * @property supportingContent The metadata line under the country name — see [CountrySupportingContent].
 * @property showFlag Whether the leading flag renders.
 * @property showDropdownIcon Whether the trailing chevron renders. Off is for a host embedding the
 *   selector inside a control that already conveys "tap to open" some other way.
 * @property flagConfig How the flag itself is presented — see [CountryFlagConfig].
 */
@Immutable
public data class CountrySelectorContentConfig(
    val labelMode: InputLabelMode = InputLabelMode.Floating,
    val showCountryName: Boolean = true,
    val supportingContent: CountrySupportingContent = CountrySupportingContent.IsoAndDialCode,
    val showFlag: Boolean = true,
    val showDropdownIcon: Boolean = true,
    val flagConfig: CountryFlagConfig = CountryFlagConfig(style = CountryFlagStyle.TonalContainer),
) {
    /** [labelMode] as a plain boolean, for call sites that don't need the enum's other meaning. */
    val hasVisibleLabel: Boolean get() = labelMode == InputLabelMode.Floating
}

/**
 * Component defaults for [CountrySelectorContentConfig], mirroring
 * [com.ezzy.ccp.countrypicker.state.CountryPickerConfig]'s and
 * [com.ezzy.ccp.countrypicker.theme.CountryPickerDefaults]'s factory-function convention.
 */
public object CountrySelectorDefaults {

    /** The library's default full-selector content: floating label, ISO + dial code metadata. */
    public fun contentConfig(
        labelMode: InputLabelMode = InputLabelMode.Floating,
        showCountryName: Boolean = true,
        supportingContent: CountrySupportingContent = CountrySupportingContent.IsoAndDialCode,
        showFlag: Boolean = true,
        showDropdownIcon: Boolean = true,
        flagConfig: CountryFlagConfig = CountryFlagConfig(style = CountryFlagStyle.TonalContainer),
    ): CountrySelectorContentConfig = CountrySelectorContentConfig(
        labelMode = labelMode,
        showCountryName = showCountryName,
        supportingContent = supportingContent,
        showFlag = showFlag,
        showDropdownIcon = showDropdownIcon,
        flagConfig = flagConfig,
    )

    /**
     * A row-only content config: no field label, no metadata line — just flag, name and chevron. Use
     * for a selector embedded inside a form row that provides its own labeling context.
     */
    public fun rowOnlyContentConfig(
        flagConfig: CountryFlagConfig = CountryFlagConfig(style = CountryFlagStyle.TonalContainer),
    ): CountrySelectorContentConfig = CountrySelectorContentConfig(
        labelMode = InputLabelMode.Hidden,
        supportingContent = CountrySupportingContent.None,
        flagConfig = flagConfig,
    )
}
