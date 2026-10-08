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

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ezzy.ccp.countrypicker.data.CountryRepository
import com.ezzy.ccp.countrypicker.model.Country
import com.ezzy.ccp.countrypicker.model.UiText
import com.ezzy.ccp.countrypicker.persistence.NoOpRecentCountryStore
import com.ezzy.ccp.countrypicker.persistence.RecentCountryStore
import com.ezzy.ccp.countrypicker.state.CountryPickerConfig
import com.ezzy.ccp.countrypicker.state.rememberCountryPickerState
import com.ezzy.ccp.countrypicker.theme.CountryPickerDefaults
import com.ezzy.ccp.countrypicker.theme.CountryPickerStyle
import com.ezzy.ccp.countrypicker.theme.CountryPickerTheme
import com.ezzy.ccp.resources.Res
import com.ezzy.ccp.resources.ccp_country_code_subtitle
import com.ezzy.ccp.resources.ccp_country_code_title

/**
 * A standalone dial-code pill — `🇰🇪 +254 ˅` — that opens the country-code picker.
 *
 * [PhoneNumberField] already has a prefix built in; this is for a host composing its own phone row
 * that wants the identical picker rather than reimplementing one. It opens the same sheet as every
 * other selector, with dial codes on the rows and dial-code search enabled.
 *
 * @param selectedCountry The country whose dial code is shown.
 * @param onCountrySelected Called with the chosen country. The caller re-formats and re-validates
 *   its number — [PhoneNumberField] does that itself.
 */
@Composable
public fun PhoneCountryCodeSelector(
    selectedCountry: Country,
    onCountrySelected: (Country) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    config: CountryPickerConfig = CountryPickerDefaults.phoneConfig(),
    sheetTitle: UiText = UiText.resource(Res.string.ccp_country_code_title),
    sheetSubtitle: UiText? = UiText.resource(Res.string.ccp_country_code_subtitle),
    recentCountryStore: RecentCountryStore = NoOpRecentCountryStore,
    repository: CountryRepository = CountryRepository.Default,
    style: CountryPickerStyle = CountryPickerTheme.style,
    flagContent: (@Composable (Country) -> Unit)? = null,
) {
    val pickerState = rememberCountryPickerState(
        config = config,
        selectedCountries = setOf(selectedCountry),
        repository = repository,
        recentCountryStore = recentCountryStore,
    )

    CountryPickerTheme(style) {
        CountrySelectorField(
            country = selectedCountry,
            onClick = pickerState::open,
            modifier = modifier,
            isOpen = pickerState.isSheetOpen,
            state = if (enabled) CountrySelectorState.Default else CountrySelectorState.Disabled,
            variant = CountrySelectorVariant.DialCode,
            // No label: the surrounding phone row owns "Phone number", and a second label on the
            // prefix would have a screen reader announce it twice for one logical control.
            label = null,
            style = style,
            flagContent = flagContent,
        )

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
}
