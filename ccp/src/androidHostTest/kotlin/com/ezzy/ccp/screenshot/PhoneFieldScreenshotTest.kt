package com.ezzy.ccp.screenshot

import androidx.compose.runtime.Composable
import com.ezzy.ccp.countrypicker.data.DefaultCountryDataSource
import com.ezzy.ccp.countrypicker.model.PhoneNumberType
import com.ezzy.ccp.countrypicker.state.rememberPhoneNumberFieldState
import com.ezzy.ccp.countrypicker.theme.PhoneFieldSize
import com.ezzy.ccp.countrypicker.theme.PhoneNumberInputDefaults
import com.ezzy.ccp.countrypicker.ui.PhoneNumberField
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel5)
class PhoneFieldScreenshotTest : ScreenshotTestBase() {

    private val kenya = DefaultCountryDataSource.findByIso2("KE")!!
    private val uk = DefaultCountryDataSource.findByIso2("GB")!!

    @Composable
    private fun Field(
        number: String = "",
        touched: Boolean = false,
        allowedTypes: Set<PhoneNumberType> = emptySet(),
        size: PhoneFieldSize = PhoneFieldSize.Regular,
        country: com.ezzy.ccp.countrypicker.model.Country = kenya,
    ) {
        val state = rememberPhoneNumberFieldState(
            initialCountry = country,
            initialNumber = number,
            allowedNumberTypes = allowedTypes,
        )
        if (touched) state.markTouched()
        PhoneNumberField(
            onValueChange = {},
            state = state,
            inputStyle = PhoneNumberInputDefaults.style(size = size),
        )
    }

    @Test
    fun `empty field shows the region's example number`() = snapshot("phone_empty") { Field() }

    @Test
    fun `filled field groups the number`() = snapshot("phone_filled") { Field("712345678") }

    @Test
    fun `a number that is too short shows an error once touched`() =
        snapshot("phone_error") { Field("71", touched = true) }

    @Test
    fun `a landline is rejected when the field requires a mobile`() =
        snapshot("phone_wrong_type") {
            // The message must name what is wanted ("Enter a mobile number"), not just say invalid.
            Field("2079460000", touched = true, allowedTypes = PhoneNumberType.SmsCapable, country = uk)
        }

    @Test
    fun `all three field sizes`() = snapshot("phone_sizes") {
        // Together in one golden: the scaling is meant to keep flag, divider, text and chevron in
        // proportion, and that is only visible by comparing them side by side.
        PhoneFieldSize.entries.forEach { Field("712345678", size = it) }
    }

    @Test
    fun `field in dark theme`() = snapshot("phone_dark", dark = true) { Field("712345678") }

    @Test
    fun `field right to left`() = snapshot("phone_rtl", rtl = true) {
        // The regression this guards: "+254" rendering as "254+", and the grouped number reordering.
        Field("712345678")
    }

    @Test
    fun `empty field right to left`() = snapshot("phone_rtl_empty", rtl = true) { Field() }
}
