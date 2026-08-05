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

@file:Suppress("DEPRECATION")

package com.ezzy.ccp.countrypicker

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.ezzy.ccp.countrypicker.data.DefaultCountryDataSource
import com.ezzy.ccp.countrypicker.model.resolveCountryByCodeOrName
import com.ezzy.ccp.countrypicker.model.toCanonical
import com.ezzy.ccp.countrypicker.model.toLegacy
import com.ezzy.ccp.data.countryList
import com.ezzy.ccp.state.PhoneState
import com.ezzy.ccp.utils.countryToFlagEmoji
import com.ezzy.ccp.utils.formatAndValidatePhone
import com.ezzy.ccp.utils.getMaxPhoneLength
import com.ezzy.ccp.utils.isPhoneNumberValid
import com.ezzy.ccp.utils.parsePhoneNumber
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Regression coverage for the pre-existing public API, so the refactor cannot break current callers.
 *
 * Everything asserted here was reachable before this change: [countryList], [PhoneState]'s properties
 * and methods, and the `com.ezzy.ccp.utils` helpers. The behaviours that *did* change are asserted here
 * too, deliberately, so the change is documented by a test rather than only by a comment — see
 * [countryList] for the list of intentional differences.
 */
class LegacyRegressionTest {

    // ── countryList still works and is now derived ──────────────────────────────────────────────

    @Test
    fun `countryList is populated and has unique iso codes`() {
        assertTrue(countryList.isNotEmpty())
        assertEquals(countryList.size, countryList.map { it.code }.distinct().size)
    }

    @Test
    fun `countryList is derived from the canonical dataset, not maintained separately`() {
        assertEquals(DefaultCountryDataSource.countries.size, countryList.size)
        assertEquals(
            DefaultCountryDataSource.countries.map { it.iso2Code },
            countryList.map { it.code },
        )
    }

    @Test
    fun `the countries existing callers look up are all still present`() {
        val expected = listOf("US", "GB", "KE", "IN", "JP", "DE", "FR", "NG", "ZA", "BR", "CN")
        expected.forEach { code ->
            assertNotNull("Missing $code", countryList.firstOrNull { it.code == code })
        }
    }

    @Test
    fun `dial codes are now hyphen-free, which is the documented behaviour change`() {
        // Was "+1-809"; the hyphenated form was not a valid dial code and broke E.164 for NANP regions.
        assertEquals("+1809", countryList.first { it.code == "DO" }.dialCode)
        assertTrue(countryList.none { "-" in it.dialCode })
    }

    @Test
    fun `renamed countries remain findable by their former names`() {
        // "Czech Republic" → "Czechia" etc. The old names survive as search aliases.
        assertEquals("CZ", resolveCountryByCodeOrName("Czech Republic")?.iso2Code)
        assertEquals("TR", resolveCountryByCodeOrName("Turkey")?.iso2Code)
        assertEquals("SZ", resolveCountryByCodeOrName("Swaziland")?.iso2Code)
    }

    @Test
    fun `flag vectors are still attached where the project has an asset`() {
        assertNotNull(countryList.first { it.code == "KE" }.flag)
        assertNotNull(countryList.first { it.code == "US" }.flag)
    }

    // ── The utils helpers still behave ──────────────────────────────────────────────────────────

    @Test
    fun `isPhoneNumberValid still validates`() {
        assertTrue("712345678".isPhoneNumberValid("KE"))
        assertFalse("1".isPhoneNumberValid("KE"))
    }

    @Test
    fun `formatAndValidatePhone still returns all three representations`() {
        val result = formatAndValidatePhone("712345678", "KE")
        assertTrue(result.isValid)
        assertEquals("+254712345678", result.unformattedNumber)
        assertTrue(result.formattedNumber.startsWith("+254"))
        assertTrue(result.formattedWithoutCountryCode.isNotEmpty())
    }

    @Test
    fun `parsePhoneNumber still resolves a country from an international number`() {
        val (country, local) = parsePhoneNumber("+254712345678")
        assertEquals("KE", country?.code)
        assertTrue(local.isNotEmpty())
    }

    @Test
    fun `getMaxPhoneLength still returns a sane per-region length`() {
        // Unchanged legacy behaviour: this counts digits in the *national* form, which for Kenya
        // includes the trunk 0 ("0712 123456" → 10). The new
        // PhoneNumberFormatter.expectedNationalDigits reports 9, the significant-digit count, which is
        // the more useful notion — but this helper is left exactly as it was.
        assertEquals(10, getMaxPhoneLength("KE"))
        assertEquals(10, getMaxPhoneLength("US"))
        // An unknown region falls back rather than throwing.
        assertEquals(15, getMaxPhoneLength("ZZ"))
    }

    @Test
    fun `countryToFlagEmoji still produces a flag`() {
        assertEquals("🇰🇪", "KE".countryToFlagEmoji())
    }

    // ── PhoneState's public surface is unchanged ─────────────────────────────────────────────────

    @Test
    fun `PhoneState defaults to the United States as before`() {
        val state = PhoneState()
        assertEquals("US", state.activeCountry?.code)
        assertEquals("+1", state.activeCountry?.dialCode)
    }

    @Test
    fun `updatePhoneNumber drives formatted, unformatted and validity`() {
        val state = PhoneState()
        state.setCountryByCode("KE")
        state.updatePhoneNumber(TextFieldValue("712345678"))

        assertEquals("712345678", state.phoneNumber)
        assertTrue(state.isValid)
        assertEquals("+254712345678", state.unformattedPhone)
        assertTrue(state.formattedPhone.startsWith("+254"))
        assertTrue(state.phoneField.text.isNotEmpty())
    }

    @Test
    fun `the field text is now grouped as the user types`() {
        // Behaviour improvement: this used to stay ungrouped until the number parsed cleanly.
        val state = PhoneState()
        state.setCountryByCode("KE")
        state.updatePhoneNumber(TextFieldValue("712345678"))
        assertTrue("Expected grouping, got '${state.phoneField.text}'", " " in state.phoneField.text)
    }

    @Test
    fun `the cursor lands at the end for an append-style edit`() {
        val state = PhoneState()
        state.setCountryByCode("KE")
        val digits = "712345"
        state.updatePhoneNumber(TextFieldValue(digits, selection = TextRange(digits.length)))
        assertEquals(state.phoneField.text.length, state.phoneField.selection.start)
    }

    @Test
    fun `the cursor is re-anchored by digit count, not forced to the end, for a mid-string edit`() {
        // Regression: applyDigits used to always reset the selection to the end of the reformatted
        // text, which made it impossible to place the cursor anywhere but the tail — every edit,
        // including one made with the cursor in the middle of the number, snapped it back to the end.
        val state = PhoneState()
        state.setCountryByCode("KE")
        // Four digits precede the cursor here ("7129"), simulating a "9" inserted after "712" while
        // editing mid-string.
        state.updatePhoneNumber(TextFieldValue("7129345678", selection = TextRange(4)))

        val digitsBeforeCursor = state.phoneField.text
            .take(state.phoneField.selection.start)
            .count(Char::isDigit)
        assertEquals(4, digitsBeforeCursor)
        assertTrue(
            "Cursor should not be pinned to the end",
            state.phoneField.selection.start < state.phoneField.text.length,
        )
    }

    @Test
    fun `the field displays international-style grouping, not the national form`() {
        // A US number's national form uses parentheses ("(712) 345-6789"); its international form
        // does not ("712-345-6789") — the dial code is already shown separately by the field's own
        // prefix, so the number itself should read the way it would in international notation.
        val state = PhoneState()
        state.setCountryByCode("US")
        state.updatePhoneNumber(TextFieldValue("7123456789", selection = TextRange(10)))

        assertFalse(
            "Expected no parentheses, got '${state.phoneField.text}'",
            "(" in state.phoneField.text,
        )
    }

    @Test
    fun `setCountryByCode is case-insensitive and falls back on an unknown code`() {
        val state = PhoneState()
        state.setCountryByCode("ke")
        assertEquals("KE", state.activeCountry?.code)

        state.setCountryByCode("ZZZ")
        assertEquals("US", state.activeCountry?.code)
    }

    @Test
    fun `selectCountry keeps the typed digits and re-formats them`() {
        val state = PhoneState()
        state.setCountryByCode("KE")
        state.updatePhoneNumber(TextFieldValue("712345678"))
        val digits = state.phoneNumber

        state.selectCountry(requireNotNull(countryList.firstOrNull { it.code == "DE" }))

        assertEquals("DE", state.activeCountry?.code)
        assertEquals("Digits must survive a country change", digits, state.phoneNumber)
        assertEquals("+49712345678", state.unformattedPhone)
    }

    @Test
    fun `parseAndSet detects the country from an E164 number`() {
        val state = PhoneState()
        state.parseAndSet("+4915123456789")
        assertEquals("DE", state.activeCountry?.code)
        assertEquals("15123456789", state.phoneNumber)
    }

    @Test
    fun `parseAndSet leaves the country alone for a bare local number`() {
        val state = PhoneState()
        state.setCountryByCode("KE")
        state.parseAndSet("0712345678")
        assertEquals("KE", state.activeCountry?.code)
    }

    @Test
    fun `every E164 number the sample screen exercises round-trips`() {
        // The exact strings MainActivity cycles through, so a regression here would be visible in the
        // existing sample app. Each must resolve to a country, validate, and re-emit its own E.164 form.
        val samples = mapOf(
            "+254712345678" to "KE",
            "+14155552671" to "US",
            "+919876543210" to "IN",
            "+819012345678" to "JP",
            "+4915123456789" to "DE",
            "+447400123456" to "GB",
        )
        samples.forEach { (input, expectedCode) ->
            val state = PhoneState()
            state.parseAndSet(input)
            assertEquals(input, expectedCode, state.activeCountry?.code)
            assertTrue("$input should be valid", state.isValid)
            assertEquals(input, input, state.unformattedPhone)
        }
    }

    @Test
    fun `a shared calling code resolves by region inference, not by dial-code lookup`() {
        // +44 7911 is a Guernsey mobile range, not a UK one. Resolving the country by matching "+44"
        // against the country list would return whichever +44 entry came first; libphonenumber's
        // region inference gets it right. This matters more now that the dataset includes Guernsey,
        // Jersey and the Isle of Man alongside the UK.
        val state = PhoneState()
        state.parseAndSet("+447911123456")
        assertEquals("GG", state.activeCountry?.code)

        val (country, _) = parsePhoneNumber("+447911123456")
        assertEquals("GG", country?.code)

        // And a genuine UK mobile still resolves to the UK.
        assertEquals("GB", parsePhoneNumber("+447400123456").first?.code)
    }

    @Test
    fun `a NANP number resolves to the right territory rather than the first plus-one entry`() {
        assertEquals("CA", parsePhoneNumber("+16045550132").first?.code)
        assertEquals("US", parsePhoneNumber("+14155552671").first?.code)
    }

    @Test
    fun `clearPhone resets the number but keeps the country`() {
        val state = PhoneState()
        state.setCountryByCode("KE")
        state.updatePhoneNumber(TextFieldValue("712345678"))
        state.clearPhone()

        assertEquals("", state.phoneNumber)
        assertEquals("", state.formattedPhone)
        assertEquals("", state.unformattedPhone)
        assertFalse(state.isValid)
        assertEquals("KE", state.activeCountry?.code)
    }

    @Test
    fun `toPhone still returns the legacy snapshot shape`() {
        val state = PhoneState()
        state.setCountryByCode("KE")
        state.updatePhoneNumber(TextFieldValue("712345678"))

        val phone = state.toPhone()
        assertEquals("+254712345678", phone.phoneNumber)
        assertTrue(phone.isValid)
        assertEquals("KE", phone.country?.code)
        assertEquals("+254", phone.country?.dialCode)
        assertEquals("🇰🇪", phone.country?.flag)
    }

    @Test
    fun `unformattedPhone no longer echoes unparseable input as though it were E164`() {
        // It used to fall back to the raw text, so callers could persist a non-E.164 value.
        val state = PhoneState()
        state.setCountryByCode("KE")
        state.updatePhoneNumber(TextFieldValue("1"))
        assertEquals("", state.unformattedPhone)
    }

    // ── Model interop ───────────────────────────────────────────────────────────────────────────

    @Test
    fun `a legacy country converts to canonical and back`() {
        val legacy = countryList.first { it.code == "KE" }
        val canonical = requireNotNull(legacy.toCanonical())
        assertEquals("KEN", canonical.iso3Code)

        val roundTripped = canonical.toLegacy()
        assertEquals(legacy.code, roundTripped.code)
        assertEquals(legacy.dialCode, roundTripped.dialCode)
        assertEquals(legacy.name, roundTripped.name)
    }

    @Test
    fun `resolveCountryByCodeOrName accepts a code or a name, as setCountry did`() {
        assertEquals("KE", resolveCountryByCodeOrName("KE")?.iso2Code)
        assertEquals("KE", resolveCountryByCodeOrName("ke")?.iso2Code)
        assertEquals("KE", resolveCountryByCodeOrName("Kenya")?.iso2Code)
        assertEquals("KE", resolveCountryByCodeOrName("KEN")?.iso2Code)
        assertEquals("AE", resolveCountryByCodeOrName("uae")?.iso2Code)
        assertNull(resolveCountryByCodeOrName("not a country"))
        assertNull(resolveCountryByCodeOrName(null))
    }
}
