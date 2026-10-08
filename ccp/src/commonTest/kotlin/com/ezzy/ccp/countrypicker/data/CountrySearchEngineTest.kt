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

package com.ezzy.ccp.countrypicker.data

import com.ezzy.ccp.countrypicker.model.CountrySearchField
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Search behaviour and result ranking.
 *
 * The examples in the design brief are asserted verbatim (`unit`, `254`, `+254`, `ke`, `GB`, `uae`),
 * because those are the queries a user will actually type and the ones a regression would be noticed
 * on first.
 */
class CountrySearchEngineTest {

    private val all = DefaultCountryDataSource.countries

    private fun search(
        query: String,
        fields: Set<CountrySearchField> = CountrySearchField.All,
    ) = CountrySearchEngine.search(all, query, fields).map { it.country.iso2Code }

    // ── The brief's worked examples ──────────────────────────────────────────────────────────────

    @Test
    fun `partial name matches every country starting with it — alphabetically`() {
        val results = search("unit")
        // United Arab Emirates, United Kingdom, United States — all rank as name-prefix matches, so the
        // alphabetical tie-break decides the order.
        assertEquals(listOf("AE", "GB", "US"), results.take(3))
    }

    @Test
    fun `bare dial code finds the country`() {
        assertEquals("KE", search("254").first())
    }

    @Test
    fun `dial code with a plus prefix behaves identically`() {
        assertEquals(search("254"), search("+254"))
    }

    @Test
    fun `two-letter query finds the country by iso and by name prefix`() {
        // "ke" is both Kenya's ISO alpha-2 and the prefix of "Kenya", so Kenya must come first.
        assertEquals("KE", search("ke").first())
    }

    @Test
    fun `uppercase iso code matches case-insensitively`() {
        assertEquals("GB", search("GB").first())
    }

    @Test
    fun `alias matches a country whose name does not contain the query`() {
        assertEquals("AE", search("uae").first())
    }

    // ── Field coverage ──────────────────────────────────────────────────────────────────────────

    @Test
    fun `iso alpha-3 is searchable`() {
        assertEquals("KEN", all.first { it.iso2Code == "KE" }.iso3Code)
        assertTrue("KE" in search("ken"))
    }

    @Test
    fun `search is accent-insensitive in both directions`() {
        // "Côte d'Ivoire" must be reachable by typing plain ASCII...
        assertTrue("CI" in search("cote"))
        // ...and by typing the accented form.
        assertTrue("CI" in search("côte"))
        // Réunion likewise.
        assertTrue("RE" in search("reunion"))
    }

    @Test
    fun `search trims whitespace`() {
        assertEquals(search("kenya"), search("   kenya   "))
    }

    @Test
    fun `search is case-insensitive on names`() {
        assertEquals(search("kenya"), search("KENYA"))
    }

    @Test
    fun `disabling dial code search makes a numeric query match nothing`() {
        val fields = CountrySearchField.All - CountrySearchField.DialCode
        assertTrue(search("254", fields).isEmpty())
    }

    @Test
    fun `disabling iso search still matches by name`() {
        val fields = setOf(CountrySearchField.Name)
        assertTrue("GB" !in search("gbr", fields))
        assertTrue("GB" in search("united king", fields))
    }

    // ── Ranking ─────────────────────────────────────────────────────────────────────────────────

    @Test
    fun `exact name match outranks a prefix match`() {
        // "Niger" is an exact name and also a prefix of "Nigeria"; the exact match must win.
        val results = search("niger")
        assertEquals("NE", results.first())
        assertTrue(results.indexOf("NG") > results.indexOf("NE"))
    }

    @Test
    fun `name prefix outranks an infix match`() {
        val results = search("guinea")
        // Guinea itself is an exact match; Guinea-Bissau a prefix; the rest contain it mid-name.
        assertEquals("GN", results.first())
        assertTrue(results.indexOf("GW") < results.indexOf("PG"))
    }

    @Test
    fun `exact iso outranks a name infix match`() {
        // "in" is India's ISO alpha-2 and appears inside many other country names.
        val results = search("in")
        assertEquals("IN", results.first())
    }

    @Test
    fun `results are deterministic across repeated calls`() {
        repeat(3) { assertEquals(search("ma"), search("ma")) }
    }

    @Test
    fun `a blank query returns every candidate unranked`() {
        val results = CountrySearchEngine.search(all, "")
        assertEquals(all.size, results.size)
        assertTrue(results.all { it.matchedField == null })
    }

    @Test
    fun `a query shorter than the minimum length is ignored`() {
        val results = CountrySearchEngine.search(all, "k", minQueryLength = 2)
        assertEquals(all.size, results.size)
    }

    @Test
    fun `no match returns an empty list rather than everything`() {
        assertTrue(CountrySearchEngine.search(all, "zzzzznotacountry").isEmpty())
    }

    // ── Highlighting ────────────────────────────────────────────────────────────────────────────

    @Test
    fun `a name match carries a highlight range into the display name`() {
        val match = CountrySearchEngine.search(all, "kenya").first()
        assertTrue(match.hasHighlight)
        assertEquals(0, match.highlightStart)
        assertEquals(5, match.highlightLength)
        assertEquals(
            "kenya",
            match.country.displayName
                .substring(match.highlightStart, match.highlightStart + match.highlightLength)
                .lowercase(),
        )
    }

    @Test
    fun `an infix match highlights the correct offset`() {
        val match = CountrySearchEngine.search(all, "bissau").first { it.country.iso2Code == "GW" }
        assertTrue(match.hasHighlight)
        assertEquals(
            "bissau",
            match.country.displayName
                .substring(match.highlightStart, match.highlightStart + match.highlightLength)
                .lowercase(),
        )
    }

    @Test
    fun `a dial code match carries no highlight — because there is nothing in the name to highlight`() {
        val match = CountrySearchEngine.search(all, "254").first()
        assertEquals(CountrySearchField.DialCode, match.matchedField)
        assertFalse(match.hasHighlight)
    }

    // ── Non-unique dial codes ───────────────────────────────────────────────────────────────────

    @Test
    fun `a shared dial code returns every country that uses it`() {
        val plus44 = DefaultCountryDataSource.findByDialCode("44").map { it.iso2Code }.toSet()
        // The UK plus its three Crown Dependencies all use +44.
        assertTrue(plus44.containsAll(setOf("GB", "JE", "GG", "IM")))
    }

    @Test
    fun `a shared dial code resolves to a conventional primary country`() {
        assertEquals("US", DefaultCountryDataSource.primaryForDialCode("1")?.iso2Code)
        assertEquals("GB", DefaultCountryDataSource.primaryForDialCode("+44")?.iso2Code)
    }

    // ── Dataset integrity ───────────────────────────────────────────────────────────────────────

    @Test
    fun `iso alpha-2 codes are unique — since identity depends on it`() {
        assertEquals(all.size, all.map { it.iso2Code }.distinct().size)
    }

    @Test
    fun `iso alpha-3 codes are unique`() {
        assertEquals(all.size, all.map { it.iso3Code }.distinct().size)
    }

    @Test
    fun `dial codes are deliberately not unique`() {
        // Asserted rather than merely noted: any change that made these unique would mean territories
        // had been dropped from the dataset.
        assertTrue(all.map { it.dialCode }.distinct().size < all.size)
    }

    @Test
    fun `every dial code is a plus followed by digits only`() {
        val malformed = all.filterNot { it.dialCode.matches(Regex("""\+\d{1,4}""")) }
        assertTrue(malformed.isEmpty(), message = "Malformed dial codes: $malformed")
    }

    @Test
    fun `the dataset covers every region and is substantially complete`() {
        // Every ISO 3166-1 entity that has an international dial code. Pinned exactly so an accidental
        // deletion from the dataset string fails here rather than quietly shrinking the country list.
        assertEquals(EXPECTED_COUNTRY_COUNT, all.size)
        assertEquals(
            com.ezzy.ccp.countrypicker.model.CountryRegion.entries.toSet(),
            all.map { it.region }.toSet(),
        )
    }

    @Test
    fun `countries without an emoji flag fall back to null rather than a broken sequence`() {
        // Kosovo has no Unicode regional-indicator sequence; the UI renders its ISO code instead.
        assertEquals(null, DefaultCountryDataSource.findByIso2("XK")?.flag)
        assertNotNull(DefaultCountryDataSource.findByIso2("KE")?.flag)
    }

    @Test
    fun `well-known non-iso codes normalise to their canonical form`() {
        assertEquals("GB", DefaultCountryDataSource.normalizeIsoCode("uk"))
        assertEquals("KE", DefaultCountryDataSource.findByIso2("ke")?.iso2Code)
    }

    private companion object {
        const val EXPECTED_COUNTRY_COUNT = 236
    }
}
