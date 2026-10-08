package com.ezzy.ccp.countrypicker.data

import com.ezzy.ccp.countrypicker.model.Country
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Locale

/**
 * Country names in the user's language.
 *
 * The bundled dataset is English, and the platform supplies translations via
 * `Locale.getDisplayCountry`. Two things have to hold at once and they pull in opposite directions:
 * what the user *reads* must follow the device language, while the identity and lookup surface
 * ([DefaultCountryDataSource.countries], [DefaultCountryDataSource.byIso2]) must not move, because
 * things are keyed off it.
 */
class LocalizedCountryNamesTest {

    private val originalLocale: Locale = Locale.getDefault()

    @After
    fun restoreLocale() {
        Locale.setDefault(originalLocale)
    }

    private fun named(countries: List<Country>, iso2: String): String =
        countries.first { it.iso2Code == iso2 }.displayName

    @Test
    fun `names follow the requested locale`() {
        val french = DefaultCountryDataSource.localizedNames(Locale.FRENCH)
        assertEquals("Allemagne", named(french, "DE"))
        assertEquals("États-Unis", named(french, "US"))

        val german = DefaultCountryDataSource.localizedNames(Locale.GERMAN)
        assertEquals("Deutschland", named(german, "DE"))
    }

    @Test
    fun `English names come from the dataset, not from the platform`() {
        // The platform's English is behind the dataset on exactly the names the dataset was curated
        // to fix, and by an amount that varies with the device's ICU version. Routing English
        // through ICU would undo that curation differently on different Android releases.
        val english = DefaultCountryDataSource.localizedNames(Locale.ENGLISH)
        assertEquals("Cabo Verde", named(english, "CV"))
        assertEquals("Türkiye", named(english, "TR"))

        // What ICU would have said instead, so this test explains itself when it fails.
        val icu = Locale.Builder().setRegion("CV").build().getDisplayCountry(Locale.ENGLISH)
        assertNotEquals(icu, named(english, "CV"))
    }

    @Test
    fun `English is still collated, so accented names file correctly`() {
        val english = DefaultCountryDataSource.localizedNames(Locale.ENGLISH)
        // A plain string sort compares UTF-16 code units and files "Åland Islands" after "Zimbabwe",
        // because Å is above Z.
        val aland = english.indexOfFirst { it.iso2Code == "AX" }
        val zimbabwe = english.indexOfFirst { it.iso2Code == "ZW" }
        assertTrue("Åland Islands should sort under A, not past Z", aland < zimbabwe)
        assertTrue(aland < 5)
    }

    @Test
    fun `regional English variants are treated as English`() {
        // en-GB and en-CA are the same authored content; only the language matters.
        assertEquals(
            named(DefaultCountryDataSource.localizedNames(Locale.ENGLISH), "CV"),
            named(DefaultCountryDataSource.localizedNames(Locale.UK), "CV"),
        )
    }

    @Test
    fun `the canonical dataset stays English whatever the locale`() {
        Locale.setDefault(Locale.FRENCH)
        // This is what byIso2, findByIso2 and the legacy countryList are built from. If it moved
        // with the device language, every one of them would change meaning mid-process.
        assertEquals("Germany", named(DefaultCountryDataSource.countries, "DE"))
        assertEquals("Germany", DefaultCountryDataSource.findByIso2("DE")?.displayName)
    }

    @Test
    fun `the data source serves localized names to the picker`() = runTest {
        Locale.setDefault(Locale.FRENCH)
        val loaded = DefaultCountryDataSource.load()
        assertEquals("Allemagne", named(loaded, "DE"))
    }

    @Test
    fun `the English name survives as a search alias`() {
        val french = DefaultCountryDataSource.localizedNames(Locale.FRENCH)
        val germany = french.first { it.iso2Code == "DE" }
        // A user who knows the English name must still find the country in a localized list.
        assertTrue("Germany" in germany.alternativeNames)
    }

    @Test
    fun `search finds a country by its localized name`() {
        val french = DefaultCountryDataSource.localizedNames(Locale.FRENCH)

        assertEquals(
            "DE",
            CountrySearchEngine.search(french, "Allemagne").firstOrNull()?.country?.iso2Code,
        )
        // ...and by the English one, via the alias.
        assertEquals(
            "DE",
            CountrySearchEngine.search(french, "Germany").firstOrNull()?.country?.iso2Code,
        )
    }

    @Test
    fun `the list is sorted for the locale, not by the English name`() {
        val french = DefaultCountryDataSource.localizedNames(Locale.FRENCH)
        val names = french.map { it.displayName }
        val collator = java.text.Collator.getInstance(Locale.FRENCH)
            .apply { strength = java.text.Collator.PRIMARY }
        assertEquals(names.sortedWith { a, b -> collator.compare(a, b) }, names)
        // Germany sits under A in French and G in English — proof the order really did change.
        assertNotEquals(
            DefaultCountryDataSource.countries.indexOfFirst { it.iso2Code == "DE" },
            french.indexOfFirst { it.iso2Code == "DE" },
        )
    }

    @Test
    fun `identity is unaffected by localization`() {
        val french = DefaultCountryDataSource.localizedNames(Locale.FRENCH).first { it.iso2Code == "DE" }
        val english = DefaultCountryDataSource.findByIso2("DE")!!
        // Country compares on iso2Code alone, so the two instances are interchangeable — a selection
        // made in one language still matches a country held from the other.
        assertEquals(english, french)
        assertEquals(english.hashCode(), french.hashCode())
        assertTrue(setOf(english).contains(french))
    }

    @Test
    fun `localized lists are cached per locale`() {
        // load() runs on every repository load; rebuilding 236 countries and re-sorting each time
        // would be a real cost on a cold sheet open.
        assertSame(
            DefaultCountryDataSource.localizedNames(Locale.ITALIAN),
            DefaultCountryDataSource.localizedNames(Locale.ITALIAN),
        )
        assertNotSame(
            DefaultCountryDataSource.localizedNames(Locale.ITALIAN),
            DefaultCountryDataSource.localizedNames(Locale.JAPANESE),
        )
    }

    @Test
    fun `findByIso2 with a locale returns the localized name`() {
        assertEquals("Allemagne", DefaultCountryDataSource.findByIso2("DE", Locale.FRENCH)?.displayName)
        // Still case-insensitive and still normalizing UK to GB, like the English overload.
        assertEquals("GB", DefaultCountryDataSource.findByIso2("uk", Locale.FRENCH)?.iso2Code)
        assertNotNull(DefaultCountryDataSource.findByIso2("de", Locale.FRENCH))
    }

    @Test
    fun `the phone field default country is localized but is always the same country`() {
        val english = DefaultCountryDataSource.defaultInitialCountry(Locale.ENGLISH)
        val french = DefaultCountryDataSource.defaultInitialCountry(Locale.FRENCH)
        val japanese = DefaultCountryDataSource.defaultInitialCountry(Locale.JAPANESE)
        // Same country in every language: a default that changed identity with the device language
        // would be a surprising thing for a default to do.
        assertEquals(english.iso2Code, french.iso2Code)
        assertEquals(english.iso2Code, japanese.iso2Code)
        assertNotEquals(english.displayName, japanese.displayName)
    }

    private fun assertNotSame(a: Any?, b: Any?) =
        org.junit.Assert.assertNotSame(a, b)
}
