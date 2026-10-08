package com.ezzy.ccp.screenshot

import com.ezzy.ccp.countrypicker.data.DefaultCountryDataSource
import com.ezzy.ccp.countrypicker.ui.CountrySelector
import com.ezzy.ccp.countrypicker.model.UiText
import com.ezzy.ccp.countrypicker.ui.CountrySelectorState
import com.ezzy.ccp.countrypicker.ui.CountrySelectorVariant
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel5)
class SelectorScreenshotTest : ScreenshotTestBase() {

    private val kenya = DefaultCountryDataSource.findByIso2("KE")

    @Test
    fun `selector with a selection`() = snapshot("selector_selected") {
        CountrySelector(selectedCountry = kenya, onCountrySelected = {})
    }

    @Test
    fun `selector with no selection`() = snapshot("selector_empty") {
        CountrySelector(selectedCountry = null, onCountrySelected = {})
    }

    @Test
    fun `selector in the error state`() = snapshot("selector_error") {
        CountrySelector(
            selectedCountry = kenya,
            onCountrySelected = {},
            state = CountrySelectorState.Error,
        )
    }

    @Test
    fun `selector disabled`() = snapshot("selector_disabled") {
        CountrySelector(
            selectedCountry = kenya,
            onCountrySelected = {},
            state = CountrySelectorState.Disabled,
        )
    }

    @Test
    fun `selector in dark theme`() = snapshot("selector_dark", dark = true) {
        CountrySelector(selectedCountry = kenya, onCountrySelected = {})
    }

    @Test
    fun `selector right to left`() = snapshot("selector_rtl", rtl = true) {
        CountrySelector(selectedCountry = kenya, onCountrySelected = {})
    }

    @Test
    fun `every selector variant`() = snapshot("selector_variants") {
        // One golden covering all six rather than six goldens: the thing worth catching is a change
        // in how they relate — a padding or height tweak that makes Compact taller than Outlined.
        CountrySelectorVariant.entries.forEach { variant ->
            CountrySelector(
                selectedCountry = kenya,
                onCountrySelected = {},
                variant = variant,
            )
        }
    }

    @Test
    fun `every selector variant in dark theme`() = snapshot("selector_variants_dark", dark = true) {
        CountrySelectorVariant.entries.forEach { variant ->
            CountrySelector(
                selectedCountry = kenya,
                onCountrySelected = {},
                variant = variant,
            )
        }
    }

    @Test
    fun `selector with supporting and error text`() = snapshot("selector_supporting_text") {
        CountrySelector(
            selectedCountry = kenya,
            onCountrySelected = {},
            supportingText = UiText.of("Where you pay tax"),
            required = true,
        )
        CountrySelector(
            selectedCountry = null,
            onCountrySelected = {},
            state = CountrySelectorState.Error,
            errorText = UiText.of("Choose a country to continue"),
        )
    }
}
