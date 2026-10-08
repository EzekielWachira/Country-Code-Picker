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

import com.ezzy.ccp.countrypicker.model.CountrySelectionMode
import com.ezzy.ccp.countrypicker.state.CountryPickerConfig

/**
 * Behaviour presets for the three kinds of picker.
 *
 * These build a [CountryPickerConfig]; for appearance see [CountryPickerStyles] and
 * [CountryPickerTheme].
 */
public object CountryPickerDefaults {

    /** A single-country picker, such as country of residence. */
    public fun config(
        allowedCountryCodes: Set<String>? = null,
        excludedCountryCodes: Set<String> = emptySet(),
        disabledCountryCodes: Set<String> = emptySet(),
        suggestedCountryCodes: List<String> = emptyList(),
        showSearch: Boolean = true,
    ): CountryPickerConfig = CountryPickerConfig(
        selectionMode = CountrySelectionMode.Single,
        allowedCountryCodes = allowedCountryCodes,
        excludedCountryCodes = excludedCountryCodes,
        disabledCountryCodes = disabledCountryCodes,
        suggestedCountryCodes = suggestedCountryCodes,
        showSearch = showSearch,
    )

    /**
     * A multiple-selection picker: the sheet stays open, rows tick, and nothing reaches the caller
     * until Confirm.
     */
    public fun multiSelectConfig(
        allowedCountryCodes: Set<String>? = null,
        excludedCountryCodes: Set<String> = emptySet(),
        disabledCountryCodes: Set<String> = emptySet(),
        minimumSelectionCount: Int = 1,
        maximumSelectionCount: Int? = null,
        suggestedCountryCodes: List<String> = emptyList(),
    ): CountryPickerConfig = CountryPickerConfig(
        selectionMode = CountrySelectionMode.Multiple,
        allowedCountryCodes = allowedCountryCodes,
        excludedCountryCodes = excludedCountryCodes,
        disabledCountryCodes = disabledCountryCodes,
        closeOnSingleSelection = false,
        minimumSelectionCount = minimumSelectionCount,
        maximumSelectionCount = maximumSelectionCount,
        suggestedCountryCodes = suggestedCountryCodes,
    )

    /** The country-code picker behind a phone field. */
    public fun phoneConfig(
        allowedCountryCodes: Set<String>? = null,
        excludedCountryCodes: Set<String> = emptySet(),
        suggestedCountryCodes: List<String> = emptyList(),
    ): CountryPickerConfig = CountryPickerConfig(
        selectionMode = CountrySelectionMode.Single,
        allowedCountryCodes = allowedCountryCodes,
        excludedCountryCodes = excludedCountryCodes,
        suggestedCountryCodes = suggestedCountryCodes,
    )
}
