package com.ezzy.ccp.screenshot

import com.ezzy.ccp.countrypicker.data.DefaultCountryDataSource
import com.ezzy.ccp.countrypicker.data.findByIso2
import com.ezzy.ccp.countrypicker.state.rememberPhoneNumberFieldState
import com.ezzy.ccp.countrypicker.ui.CountrySelector
import com.ezzy.ccp.countrypicker.ui.PhoneNumberField
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import java.util.Locale
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * The library under a real non-English, right-to-left locale.
 *
 * The RTL cases in the other suites force `LayoutDirection.Rtl` around English strings, which
 * exercises the bidi handling but not the translations. This runs the whole thing the way an Arabic
 * device does: Arabic resources, Arabic country names from the platform, RTL layout — the
 * combination that actually ships.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
// Locale and layout-direction qualifiers have to lead: Android requires qualifiers in a fixed
// order, and Robolectric rejects the string outright if they trail the device ones.
@Config(qualifiers = "ar-rEG-ldrtl-" + RobolectricDeviceQualifiers.Pixel5)
class LocalizedScreenshotTest : ScreenshotTestBase() {

    private val arabic = Locale.forLanguageTag("ar")

    @Test
    fun `selector in arabic`() = snapshot("locale_ar_selector") {
        CountrySelector(
            // Localized lookup, so the field agrees with what the sheet would list.
            selectedCountry = DefaultCountryDataSource.findByIso2("DE", arabic),
            onCountrySelected = {},
        )
    }

    @Test
    fun `phone field in arabic`() = snapshot("locale_ar_phone") {
        // The dial code must still read "+254" and not "254+" beside Arabic text.
        PhoneNumberField(
            onValueChange = {},
            state = rememberPhoneNumberFieldState(
                initialCountry = DefaultCountryDataSource.findByIso2("KE", arabic)!!,
                initialNumber = "712345678",
            ),
        )
    }

    @Test
    fun `phone field error in arabic`() = snapshot("locale_ar_phone_error") {
        // The incomplete-number message embeds the dial code inside an Arabic sentence, which is
        // the case the Unicode isolates in CountryPickerBidi exist for.
        val state = rememberPhoneNumberFieldState(
            initialCountry = DefaultCountryDataSource.findByIso2("KE", arabic)!!,
            initialNumber = "71",
        )
        state.markTouched()
        PhoneNumberField(onValueChange = {}, state = state)
    }
}
