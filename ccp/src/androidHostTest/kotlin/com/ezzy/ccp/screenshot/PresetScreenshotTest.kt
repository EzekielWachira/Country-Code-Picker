package com.ezzy.ccp.screenshot

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.ezzy.ccp.countrypicker.data.DefaultCountryDataSource
import com.ezzy.ccp.countrypicker.model.CountrySupportingContent
import com.ezzy.ccp.countrypicker.state.rememberPhoneNumberFieldState
import com.ezzy.ccp.countrypicker.theme.CountryPickerStyle
import com.ezzy.ccp.countrypicker.theme.CountryPickerStyles
import com.ezzy.ccp.countrypicker.theme.CountryPickerTheme
import com.ezzy.ccp.countrypicker.ui.CountrySelector
import com.ezzy.ccp.countrypicker.ui.MultiCountrySelector
import com.ezzy.ccp.countrypicker.ui.PhoneNumberField
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** The same three fields under each preset, so a preset's identity is reviewed as a whole. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel5)
class PresetScreenshotTest : ScreenshotTestBase() {

    private val kenya = requireNotNull(DefaultCountryDataSource.findByIso2("KE"))
    private val germany = requireNotNull(DefaultCountryDataSource.findByIso2("DE"))
    private val japan = requireNotNull(DefaultCountryDataSource.findByIso2("JP"))

    @Composable
    private fun Fields(style: CountryPickerStyle) = CountryPickerTheme(style) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            CountrySelector(
                selectedCountry = germany,
                onCountrySelected = {},
                supportingContent = CountrySupportingContent.DialCode,
            )
            PhoneNumberField(
                onValueChange = {},
                state = rememberPhoneNumberFieldState(initialCountry = kenya, initialNumber = "712345678"),
            )
            MultiCountrySelector(selectedCountries = setOf(kenya, germany, japan), onSelectionConfirmed = {})
        }
    }

    @Test
    fun `signature preset`() = snapshot("preset_signature") { Fields(CountryPickerStyles.signature()) }

    @Test
    fun `signature preset in dark theme`() = snapshot("preset_signature_dark", dark = true) {
        Fields(CountryPickerStyles.signature())
    }

    @Test
    fun `material preset`() = snapshot("preset_material") { Fields(CountryPickerStyles.material()) }

    @Test
    fun `cupertino preset`() = snapshot("preset_cupertino") { Fields(CountryPickerStyles.cupertino()) }

    @Test
    fun `minimal preset`() = snapshot("preset_minimal") { Fields(CountryPickerStyles.minimal()) }
}
