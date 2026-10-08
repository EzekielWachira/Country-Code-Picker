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

package com.ezzy.ccp.countrypicker.phone

import com.ezzy.ccp.countrypicker.data.DefaultCountryDataSource
import com.ezzy.ccp.countrypicker.model.Country
import com.ezzy.ccp.countrypicker.model.PhoneNumberValidity
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Formatting, E.164 normalization and validation.
 *
 * These run on the JVM against real libphonenumber metadata, so they assert the actual contract
 * callers depend on rather than a stubbed approximation.
 */
class PhoneNumberTest {

    private fun country(iso: String): Country = requireNotNull(DefaultCountryDataSource.findByIso2(iso))

    private val kenya = country("KE")
    private val germany = country("DE")
    private val unitedKingdom = country("GB")
    private val unitedStates = country("US")

    // ── Formatting ──────────────────────────────────────────────────────────────────────────────

    @Test
    fun `digits are grouped using the region's own metadata`() {
        // libphonenumber's national rule for Kenya is (3)(6), so "712 345678" — not the (3)(3)(3)
        // grouping the design mock used. The metadata is the authority here.
        assertEquals("712 345678", PhoneNumberFormatter.formatAsYouType("712345678", kenya))
    }

    @Test
    fun `grouping is applied even when the user omits the national trunk prefix`() {
        // The regression this guards: libphonenumber's rules are written against the trunk-prefixed
        // national form, so a bare "712345678" matches no rule and would come back ungrouped.
        val withoutPrefix = PhoneNumberFormatter.formatAsYouType("712345678", kenya)
        val withPrefix = PhoneNumberFormatter.formatAsYouType("0712345678", kenya)
        assertTrue(" " in withoutPrefix, message = "Ungrouped without trunk prefix: '$withoutPrefix'")
        assertEquals("0712 345678", withPrefix)
    }

    @Test
    fun `the same holds for a region with a different rule shape`() {
        // UK mobiles are (4)(6); Germany's example is (5)(7).
        assertTrue(" " in PhoneNumberFormatter.formatAsYouType("7400123456", unitedKingdom))
        assertTrue(" " in PhoneNumberFormatter.formatAsYouType("15123456789", germany))
    }

    @Test
    fun `formatting is progressive while typing — not only when complete`() {
        val progressive = listOf("7", "712", "712345678")
            .map { PhoneNumberFormatter.formatAsYouType(it, kenya) }
        assertEquals("7", progressive[0])
        assertEquals("712", progressive[1])
        // Grouping has appeared by the time the number is complete.
        assertTrue(" " in progressive[2], message = "Expected grouping, got '${progressive[2]}'")
    }

    @Test
    fun `separators in the input are ignored`() {
        assertEquals(
            PhoneNumberFormatter.formatAsYouType("0712345678", kenya),
            PhoneNumberFormatter.formatAsYouType("(0712) 345-678".filter(Char::isDigit), kenya),
        )
    }

    @Test
    fun `the helper example is formatted the same way the field formats input`() {
        // The example shown in helper text must be exactly what the user's own typing produces,
        // not a differently-punctuated near-miss.
        val example = requireNotNull(PhoneNumberFormatter.exampleNationalNumber(kenya))
        assertEquals(
            example,
            PhoneNumberFormatter.formatAsYouType(example.filter(Char::isDigit), kenya),
        )
    }

    @Test
    fun `empty input formats to empty rather than throwing`() {
        assertEquals("", PhoneNumberFormatter.formatAsYouType("", kenya))
    }

    @Test
    fun `each region groups differently`() {
        val ke = PhoneNumberFormatter.formatAsYouType("712345678", kenya)
        val de = PhoneNumberFormatter.formatAsYouType("15123456789", germany)
        assertTrue(ke.isNotEmpty() && de.isNotEmpty())
        assertTrue(ke != de, message = "Regions should group differently")
    }

    // ── E.164 ───────────────────────────────────────────────────────────────────────────────────

    @Test
    fun `e164 is produced by libphonenumber — not by string concatenation`() {
        assertEquals("+254712345678", PhoneNumberFormatter.toE164("712345678", kenya))
    }

    @Test
    fun `a national trunk prefix is stripped rather than embedded`() {
        // This is the case naive dialCode + digits concatenation gets wrong: a UK mobile typed with its
        // leading 0 must become +447400123456, never +4407400123456.
        val e164 = PhoneNumberFormatter.toE164("07400123456", unitedKingdom)
        assertEquals("+447400123456", e164)
        assertFalse(e164!!.startsWith("+440"), message = "Trunk prefix leaked into E.164: $e164")
    }

    @Test
    fun `e164 is null rather than a partial echo when the number cannot be parsed`() {
        assertNull(PhoneNumberFormatter.toE164("", kenya))
        assertNull(com.ezzy.ccp.countrypicker.phone.PhoneNumberValidator.evaluate("1", kenya).e164Number)
    }

    @Test
    fun `international display form is human readable`() {
        assertEquals("+254 712 345678", PhoneNumberFormatter.toInternational("712345678", kenya))
    }

    // ── Parsing pasted numbers ──────────────────────────────────────────────────────────────────

    @Test
    fun `a pasted international number identifies its country`() {
        val parsed = PhoneNumberFormatter.parseInternational("+4915123456789", kenya)
        assertTrue(parsed.countryWasDetected)
        assertEquals("DE", parsed.country.iso2Code)
        assertEquals("15123456789", parsed.nationalDigits)
    }

    @Test
    fun `a pasted international number with separators still parses`() {
        val parsed = PhoneNumberFormatter.parseInternational("+254 712 345 678", germany)
        assertTrue(parsed.countryWasDetected)
        assertEquals("KE", parsed.country.iso2Code)
    }

    @Test
    fun `a double-zero international prefix is normalised to plus`() {
        val parsed = PhoneNumberFormatter.parseInternational("00254712345678", germany)
        assertTrue(parsed.countryWasDetected)
        assertEquals("KE", parsed.country.iso2Code)
    }

    @Test
    fun `a bare national number does not claim to have detected a country`() {
        // Critical: this flag is what stops a pasted local number from silently changing the user's
        // explicitly chosen country.
        val parsed = PhoneNumberFormatter.parseInternational("0712345678", germany)
        assertFalse(parsed.countryWasDetected)
        assertEquals("DE", parsed.country.iso2Code)
    }

    @Test
    fun `a shared calling code is disambiguated from the number itself`() {
        // +1 604 is Canada, +1 415 is the US — the country comes from libphonenumber's region
        // inference, not from a dial-code prefix table.
        val canadian = PhoneNumberFormatter.parseInternational("+16045550132", unitedStates)
        assertEquals("CA", canadian.country.iso2Code)
        val american = PhoneNumberFormatter.parseInternational("+14155550132", unitedStates)
        assertEquals("US", american.country.iso2Code)
    }

    @Test
    fun `unparseable input falls back to the given country without throwing`() {
        val parsed = PhoneNumberFormatter.parseInternational("+", kenya)
        assertEquals("KE", parsed.country.iso2Code)
        assertFalse(parsed.countryWasDetected)
    }

    // ── Validation ──────────────────────────────────────────────────────────────────────────────

    @Test
    fun `a real number is valid`() {
        val value = PhoneNumberValidator.evaluate("712345678", kenya)
        assertTrue(value.isValid)
        assertEquals(PhoneNumberValidity.Valid, value.validity)
        assertEquals("+254712345678", value.e164Number)
    }

    @Test
    fun `empty is Empty — not invalid`() {
        val value = PhoneNumberValidator.evaluate("", kenya)
        assertEquals(PhoneNumberValidity.Empty, value.validity)
        assertFalse(value.validity.isError)
        assertTrue(value.isEmpty)
    }

    @Test
    fun `a partially typed number reports Incomplete and shows no error`() {
        // The behaviour that keeps the field from nagging mid-typing.
        val value = PhoneNumberValidator.evaluate("712", kenya)
        assertEquals(PhoneNumberValidity.Incomplete, value.validity)
        assertFalse(value.validity.isError)
        assertNull(PhoneNumberValidator.errorMessage(value))
    }

    @Test
    fun `an over-long number reports TooLong and does show an error`() {
        val value = PhoneNumberValidator.evaluate("7123456789012345", kenya)
        assertTrue(value.validity.isError)
        assertNotNull(PhoneNumberValidator.errorMessage(value))
    }

    @Test
    fun `validation is not a length comparison`() {
        // Right length for Kenya, but not an assigned range — a length check would wrongly pass this.
        val value = PhoneNumberValidator.evaluate("000000000", kenya)
        assertEquals(9, value.nationalNumber.length)
        assertFalse(value.isValid, message = "A correctly-sized but unassigned number must not be valid")
    }

    @Test
    fun `possible and valid are distinct signals`() {
        val incomplete = PhoneNumberValidator.evaluate("71234", kenya)
        assertFalse(incomplete.isValid)
        val valid = PhoneNumberValidator.evaluate("712345678", kenya)
        assertTrue(valid.isPossible && valid.isValid)
    }

    @Test
    fun `an incomplete number produces a message only when the field asks for one`() {
        val value = PhoneNumberValidator.evaluate("712", kenya)
        assertNull(PhoneNumberValidator.errorMessage(value, treatIncompleteAsError = false))
        assertNotNull(PhoneNumberValidator.errorMessage(value, treatIncompleteAsError = true))
    }

    @Test
    fun `the same digits are re-evaluated per region — not carried over`() {
        val digits = "712345678"
        val asKenyan = PhoneNumberValidator.evaluate(digits, kenya)
        val asGerman = PhoneNumberValidator.evaluate(digits, germany)

        // Same digits, different countries: the E.164 output must differ, proving validation and
        // normalization are recomputed for the new region rather than reused.
        assertEquals("+254712345678", asKenyan.e164Number)
        assertEquals("+49712345678", asGerman.e164Number)
        assertTrue(asKenyan.country != asGerman.country)
    }

    @Test
    fun `a number valid in one country can become invalid in another`() {
        // A UK mobile's significant number is not a valid Kenyan number.
        val digits = "7400123456"
        assertTrue(PhoneNumberValidator.evaluate(digits, unitedKingdom).isValid)
        assertFalse(PhoneNumberValidator.evaluate(digits, kenya).isValid)
    }

    // ── Metadata-driven helper text ─────────────────────────────────────────────────────────────

    @Test
    fun `an example number is available per region and differs between regions`() {
        val ke = PhoneNumberFormatter.exampleNationalNumber(kenya)
        val de = PhoneNumberFormatter.exampleNationalNumber(germany)
        assertNotNull(ke)
        assertNotNull(de)
        assertTrue(ke != de, message = "Examples should be region specific")
    }

    @Test
    fun `expected digit counts come from metadata rather than a hardcoded table`() {
        assertEquals(9, PhoneNumberFormatter.expectedNationalDigits(kenya))
        assertEquals(10, PhoneNumberFormatter.expectedNationalDigits(unitedStates))
    }

    @Test
    fun `the max accepted length is at least the expected length`() {
        DefaultCountryDataSource.countries.take(40).forEach { country ->
            assertTrue(
                PhoneNumberFormatter.maxNationalDigits(country) >=
                    PhoneNumberFormatter.expectedNationalDigits(country).coerceAtMost(
                        PhoneNumberFormatter.E164_MAX_DIGITS,
                    ),
                message = "max < expected for ${country.iso2Code}",
            )
        }
    }

    @Test
    fun `no region blows up on formatting or validation or example lookup`() {
        // A dataset entry libphonenumber has no metadata for must degrade, not crash.
        DefaultCountryDataSource.countries.forEach { country ->
            PhoneNumberFormatter.formatAsYouType("712345678", country)
            PhoneNumberFormatter.exampleNationalNumber(country)
            PhoneNumberValidator.evaluate("712345678", country)
        }
    }
}
