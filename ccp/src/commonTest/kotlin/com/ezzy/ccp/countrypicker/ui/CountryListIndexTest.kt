package com.ezzy.ccp.countrypicker.ui

import com.ezzy.ccp.countrypicker.data.DefaultCountryDataSource
import com.ezzy.ccp.countrypicker.model.CountryMatch
import com.ezzy.ccp.countrypicker.state.CountrySection
import com.ezzy.ccp.countrypicker.state.CountrySectionKind
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * The index's arithmetic against the layout [CountryList] actually emits.
 *
 * Everything here is about one thing: a titled section contributes a header item to the flat lazy
 * list and an untitled one does not. Get that wrong and the rail scrolls one row off per preceding
 * section — which looks almost right, and so is easy to ship.
 */
class CountryListIndexTest {

    private fun country(iso2: String) = DefaultCountryDataSource.findByIso2(iso2)!!

    private fun section(kind: CountrySectionKind, vararg iso2: String) =
        CountrySection(kind, iso2.map { CountryMatch(country(it)) })

    @Test
    fun `an untitled section contributes no header row`() {
        // SearchResults is the only untitled kind.
        val index = buildCountryListIndex(listOf(section(CountrySectionKind.SearchResults, "KE", "DE")))
        assertEquals(0, index.lazyIndexOf("KE"))
        assertEquals(1, index.lazyIndexOf("DE"))
    }

    @Test
    fun `a titled section offsets its rows by its header`() {
        val index = buildCountryListIndex(listOf(section(CountrySectionKind.All, "KE", "DE")))
        assertEquals(1, index.lazyIndexOf("KE"))
        assertEquals(2, index.lazyIndexOf("DE"))
    }

    @Test
    fun `offsets accumulate across several titled sections`() {
        val index = buildCountryListIndex(
            listOf(
                section(CountrySectionKind.Selected, "DE"),   // header 0, row 1
                section(CountrySectionKind.Recent, "FR"),     // header 2, row 3
                section(CountrySectionKind.All, "KE", "US"),  // header 4, rows 5 and 6
            ),
        )
        assertEquals(1, index.lazyIndexOf("DE"))
        assertEquals(3, index.lazyIndexOf("FR"))
        assertEquals(5, index.lazyIndexOf("KE"))
        assertEquals(6, index.lazyIndexOf("US"))
    }

    @Test
    fun `letters come only from the All section`() {
        val index = buildCountryListIndex(
            listOf(
                // France is in Recent; F must not be offered unless it also appears under All.
                section(CountrySectionKind.Recent, "FR"),
                section(CountrySectionKind.All, "KE", "US"),
            ),
        )
        assertEquals(listOf('K', 'U'), index.letters)
        assertNull(index.lazyIndexOf('F'))
    }

    @Test
    fun `a letter points at the first country under it`() {
        val index = buildCountryListIndex(
            listOf(section(CountrySectionKind.All, "KE", "KW", "US")),
        )
        // Header at 0, Kenya at 1, Kuwait at 2 — K must resolve to Kenya, not Kuwait.
        assertEquals(1, index.lazyIndexOf('K'))
    }

    @Test
    fun `letters are accent-folded`() {
        // Åland Islands must index under A rather than sorting past Z.
        val index = buildCountryListIndex(listOf(section(CountrySectionKind.All, "AX")))
        assertEquals(listOf('A'), index.letters)
        assertEquals(1, index.lazyIndexOf('A'))
    }

    @Test
    fun `lookup is case-insensitive and misses return null`() {
        val index = buildCountryListIndex(listOf(section(CountrySectionKind.All, "KE")))
        assertEquals(index.lazyIndexOf("KE"), index.lazyIndexOf("ke"))
        assertEquals(index.lazyIndexOf('K'), index.lazyIndexOf('k'))
        assertNull(index.lazyIndexOf("ZZ"))
    }

    @Test
    fun `the full dataset produces a usable rail`() {
        val all = DefaultCountryDataSource.countries.map { CountryMatch(it) }
        val index = buildCountryListIndex(listOf(CountrySection(CountrySectionKind.All, all)))

        assertTrue(index.letters.size >= 24, message = "expected most of the alphabet, got ${index.letters}")
        // Every offered letter must lead somewhere — the reason the rail is built from real data.
        index.letters.forEach { assertTrue(index.lazyIndexOf(it) != null, message = "$it leads nowhere") }
        // Strictly increasing: a rail that jumps backwards is worse than no rail.
        val positions = index.letters.map { index.lazyIndexOf(it)!! }
        assertEquals(positions.sorted(), positions)
        assertEquals(positions.distinct(), positions)
    }

    @Test
    fun `an empty section list yields an empty index`() {
        val index = buildCountryListIndex(emptyList())
        assertTrue(index.letters.isEmpty())
        assertNull(index.lazyIndexOf("KE"))
    }
}
