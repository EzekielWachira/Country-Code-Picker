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
import com.ezzy.ccp.countrypicker.theme.CountryFlagShape
import com.ezzy.ccp.countrypicker.theme.CountryPickerColors
import com.ezzy.ccp.countrypicker.theme.CountryPickerDefaults
import com.ezzy.ccp.countrypicker.theme.CountryPickerDimensions
import com.ezzy.ccp.countrypicker.theme.CountryPickerMotion
import com.ezzy.ccp.countrypicker.theme.CountryPickerShapes
import com.ezzy.ccp.countrypicker.theme.CountryPickerTypography
import com.ezzy.ccp.resources.Res
import com.ezzy.ccp.resources.ccp_country_code_subtitle
import com.ezzy.ccp.resources.ccp_country_code_title

/**
 * The phone prefix selector: `🇰🇪 +254 ˅`.
 *
 * A thin, purpose-built wrapper over the same sheet every other selector opens — with the dial code
 * shown on rows, dial-code search enabled, and the "Current selection" card suppressed because the
 * field right next to it already shows the choice.
 *
 * Designed to sit inside [PhoneNumberField] rather than stand alone, but public so a host composing its
 * own phone row gets the identical picker instead of reimplementing one.
 *
 * @param selectedCountry The country whose dial code is shown.
 * @param onCountrySelected Called with the newly chosen country. The caller is responsible for
 *   re-formatting and re-validating the number — [PhoneNumberField] does that for you.
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
    colors: CountryPickerColors = CountryPickerDefaults.colors(),
    shapes: CountryPickerShapes = CountryPickerDefaults.shapes(),
    dimensions: CountryPickerDimensions = CountryPickerDefaults.dimensions(),
    typography: CountryPickerTypography = CountryPickerDefaults.typography(),
    motion: CountryPickerMotion = CountryPickerDefaults.motion(),
    flagContent: (@Composable (Country) -> Unit)? = null,
) {
    val pickerState = rememberCountryPickerState(
        config = config,
        selectedCountries = setOf(selectedCountry),
        repository = repository,
        recentCountryStore = recentCountryStore,
    )

    CountrySelectorField(
        country = selectedCountry,
        onClick = pickerState::open,
        modifier = modifier,
        isOpen = pickerState.isSheetOpen,
        state = if (enabled) CountrySelectorState.Default else CountrySelectorState.Disabled,
        variant = CountrySelectorVariant.DialCode,
        // No label: the parent field owns "Phone number", and repeating a label on the prefix pill
        // would have TalkBack announce it twice for one logical control.
        label = null,
        flagShape = if (config.flagsVisible) config.flagShape else CountryFlagShape.Hidden,
        colors = colors,
        shapes = shapes,
        dimensions = dimensions,
        typography = typography,
        motion = motion,
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
            colors = colors,
            shapes = shapes,
            dimensions = dimensions,
            typography = typography,
            motion = motion,
            flagContent = flagContent,
        )
    }
}
