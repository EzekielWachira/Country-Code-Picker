package com.ezzy.ccp.countrypicker.model

import com.ezzy.ccp.countrypicker.data.DefaultCountryDataSource
import com.ezzy.ccp.countrypicker.phone.PhoneNumberValidator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Bidirectional isolation of phone numbers.
 *
 * A dial code is a `+` followed by digits — all neutral or weak under the Unicode bidi algorithm —
 * so inside an RTL sentence it resolves to `254+` unless isolated. This asserts the isolates are
 * actually emitted, which squinting at a rendered Arabic screenshot cannot settle.
 */
class CountryPickerBidiTest {

    private val LRI = '⁦'
    private val PDI = '⁩'

    private val kenya = DefaultCountryDataSource.findByIso2("KE")!!

    @Test
    fun `isolateLtr wraps the value in isolate characters`() {
        val wrapped = CountryPickerBidi.isolateLtr("+254")
        assertEquals("$LRI+254$PDI", wrapped)
    }

    @Test
    fun `isolateLtr leaves blank values alone`() {
        // Wrapping an empty string only adds two invisible characters to every string that embeds it.
        assertEquals("", CountryPickerBidi.isolateLtr(""))
        assertEquals("   ", CountryPickerBidi.isolateLtr("   "))
    }

    @Test
    fun `the helper text isolates both the dial code and the example`() {
        val helper = PhoneNumberValidator.helperText(kenya)
        val args = (helper as UiText.Resource).args.map { it.toString() }

        val numeric = args.filter { it.any(Char::isDigit) }
        assertTrue(numeric.size == 2, message = "expected a dial code and an example, got $args")
        numeric.forEach {
            assertTrue(it.startsWith(LRI) && it.endsWith(PDI), message = "$it is not isolated")
        }
        // The country name is a normal string and must not be wrapped — isolating real words would
        // fight the sentence's own direction.
        val name = args.first { it == kenya.displayName }
        assertFalse(name.contains(LRI))
    }

    @Test
    fun `the incomplete-number error isolates the dial code`() {
        val value = PhoneNumberValidator.evaluate("71", kenya)
        assertEquals(PhoneNumberValidity.Incomplete, value.validity)

        val message = PhoneNumberValidator.errorMessage(value, treatIncompleteAsError = true)
        val args = (message as UiText.Plural).args.map { it.toString() }

        val dialCode = args.first { it.contains(kenya.dialCodeDigits) }
        assertEquals("$LRI${kenya.dialCode}$PDI", dialCode)
    }

    @Test
    fun `an isolated run keeps the plus ahead of the digits`() {
        // The whole point: the isolate must not be stripped or reordered by the wrapping itself.
        val wrapped = CountryPickerBidi.isolateLtr("+254")
        val inner = wrapped.trim(LRI, PDI)
        assertEquals("+254", inner)
        assertEquals(0, inner.indexOf('+'))
    }
}
