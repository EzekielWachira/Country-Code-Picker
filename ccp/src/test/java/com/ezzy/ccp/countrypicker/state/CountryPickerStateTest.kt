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

package com.ezzy.ccp.countrypicker.state

import androidx.compose.runtime.snapshots.Snapshot
import com.ezzy.ccp.countrypicker.data.CountryDataSource
import com.ezzy.ccp.countrypicker.data.CountryRepository
import com.ezzy.ccp.countrypicker.data.DefaultCountryDataSource
import com.ezzy.ccp.countrypicker.detection.CountryDetectionResult
import com.ezzy.ccp.countrypicker.detection.CountryDetectionSource
import com.ezzy.ccp.countrypicker.model.Country
import com.ezzy.ccp.countrypicker.model.CountryRegion
import com.ezzy.ccp.countrypicker.model.CountrySelectionMode
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Selection semantics, section grouping and deduplication, filtering, and detection precedence.
 *
 * `derivedStateOf` needs a snapshot to read outside composition, so every assertion on a derived value
 * goes through [readDerived].
 */
class CountryPickerStateTest {

    private fun country(iso: String): Country = requireNotNull(DefaultCountryDataSource.findByIso2(iso))

    private val germany = country("DE")
    private val france = country("FR")
    private val japan = country("JP")
    private val kenya = country("KE")
    private val unitedStates = country("US")

    /**
     * Builds a loaded state.
     *
     * Loading is driven explicitly rather than through `rememberCountryPickerState`, because these are
     * plain JVM tests with no composition.
     */
    private fun loadedState(
        config: CountryPickerConfig = CountryPickerConfig(),
        repository: CountryRepository = CountryRepository.Default,
    ): CountryPickerState = CountryPickerState(config = config, repository = repository).also { state ->
        runTest { state.loadCountries() }
    }

    /** Reads a `derivedStateOf` value, which requires an active snapshot outside composition. */
    private fun <T> readDerived(block: () -> T): T {
        val snapshot = Snapshot.takeSnapshot()
        return try {
            snapshot.enter(block)
        } finally {
            snapshot.dispose()
        }
    }

    // ── Loading ─────────────────────────────────────────────────────────────────────────────────

    @Test
    fun `the bundled source loads without error`() {
        val state = loadedState()
        assertTrue(state.loadState is CountryLoadState.Loaded)
        assertEquals(DefaultCountryDataSource.countries.size, state.allCountries.size)
    }

    @Test
    fun `a failing source becomes an error state rather than throwing`() = runTest {
        val failing = CountryRepository(CountryDataSource { error("network down") })
        val state = CountryPickerState(repository = failing)
        state.loadCountries()
        assertTrue(state.loadState is CountryLoadState.Error)
    }

    @Test
    fun `retry reloads after a failure`() = runTest {
        var shouldFail = true
        val flaky = CountryRepository(
            CountryDataSource {
                if (shouldFail) error("network down") else DefaultCountryDataSource.countries
            },
        )
        val state = CountryPickerState(repository = flaky)
        state.loadCountries()
        assertTrue(state.loadState is CountryLoadState.Error)

        shouldFail = false
        state.retryLoad()
        assertTrue(state.loadState is CountryLoadState.Loaded)
    }

    // ── Availability filtering ──────────────────────────────────────────────────────────────────

    @Test
    fun `an allow list narrows the offered countries`() {
        val state = loadedState(CountryPickerConfig(allowedCountryCodes = setOf("KE", "UG", "TZ")))
        val codes = readDerived { state.matches.value }.map { it.country.iso2Code }.toSet()
        assertEquals(setOf("KE", "UG", "TZ"), codes)
    }

    @Test
    fun `an exclude list removes countries`() {
        val state = loadedState(CountryPickerConfig(excludedCountryCodes = setOf("KE")))
        val codes = readDerived { state.matches.value }.map { it.country.iso2Code }
        assertFalse("KE" in codes)
    }

    @Test
    fun `exclusion wins over inclusion`() {
        val state = loadedState(
            CountryPickerConfig(
                allowedCountryCodes = setOf("KE", "UG"),
                excludedCountryCodes = setOf("KE"),
            ),
        )
        assertEquals(listOf("UG"), readDerived { state.matches.value }.map { it.country.iso2Code })
    }

    @Test
    fun `an empty allow list allows nothing, and is reported as a configuration problem`() {
        // Deliberately not treated as "no filter": an empty allow-set is a real, if unusual, config.
        val state = loadedState(CountryPickerConfig(allowedCountryCodes = emptySet()))
        assertTrue(readDerived { state.matches.value }.isEmpty())
        assertTrue(readDerived { state.isEmptyByConfiguration.value })
        assertFalse(readDerived { state.isEmptyBySearch.value })
    }

    @Test
    fun `an unmatched query is reported as an empty search, not a configuration problem`() {
        val state = loadedState()
        state.updateSearchQuery("zzzznotacountry")
        assertTrue(readDerived { state.isEmptyBySearch.value })
        assertFalse(readDerived { state.isEmptyByConfiguration.value })
    }

    // ── Region filtering ────────────────────────────────────────────────────────────────────────

    @Test
    fun `a region filter restricts the list to that region`() {
        val state = loadedState()
        state.selectRegion(CountryRegion.Africa)
        val results = readDerived { state.matches.value }.map { it.country }
        assertTrue(results.isNotEmpty())
        assertTrue(results.all { it.region == CountryRegion.Africa })
    }

    @Test
    fun `the Africa filter produces the expected alphabetical opening`() {
        // Asserted because it is the exact list the design shows for the Africa chip.
        val state = loadedState()
        state.selectRegion(CountryRegion.Africa)
        val names = readDerived { state.sections.value }
            .flatMap { it.items }
            .map { it.country.displayName }
        assertEquals(
            listOf(
                "Algeria", "Angola", "Benin", "Botswana", "Burkina Faso",
                "Burundi", "Cabo Verde", "Cameroon", "Central African Republic",
            ),
            names.take(9),
        )
    }

    @Test
    fun `clearing a region restores the full list`() {
        val state = loadedState()
        state.selectRegion(CountryRegion.Europe)
        val filtered = readDerived { state.matches.value }.size
        state.selectRegion(null)
        assertTrue(readDerived { state.matches.value }.size > filtered)
    }

    @Test
    fun `changing region does not clear the pending selection`() {
        val state = loadedState(CountryPickerConfig(selectionMode = CountrySelectionMode.Multiple))
        state.toggleCountry(france)
        state.selectRegion(CountryRegion.Asia)
        assertEquals(setOf(france), state.pendingSelection.value)
    }

    // ── Search interaction with the region filter ────────────────────────────────────────────────

    @Test
    fun `clearing the search restores the region filter that was active before it`() {
        val state = loadedState()
        state.selectRegion(CountryRegion.Africa)
        state.updateSearchQuery("ken")
        state.clearSearch()
        assertEquals(CountryRegion.Africa, state.selectedRegion)
    }

    @Test
    fun `searching collapses the grouped sections into one ranked list`() {
        val state = loadedState(CountryPickerConfig(suggestedCountryCodes = listOf("US", "GB")))
        state.updateSearchQuery("ken")
        val sections = readDerived { state.sections.value }
        assertEquals(1, sections.size)
        assertEquals(CountrySectionKind.SearchResults, sections.first().kind)
    }

    @Test
    fun `clearing the search restores the grouped sections`() {
        val state = loadedState(CountryPickerConfig(suggestedCountryCodes = listOf("US", "GB")))
        state.updateSearchQuery("ken")
        state.clearSearch()
        val kinds = readDerived { state.sections.value }.map { it.kind }
        assertTrue(CountrySectionKind.Suggested in kinds)
        assertTrue(CountrySectionKind.All in kinds)
    }

    // ── Section grouping and deduplication ──────────────────────────────────────────────────────

    @Test
    fun `a selected country appears only in the Selected section`() {
        // The brief's example: with Germany selected it must not also show under Suggested or All.
        val state = loadedState(
            CountryPickerConfig(suggestedCountryCodes = listOf("DE", "US", "GB")),
        )
        state.syncConfirmedSelection(setOf(germany))

        val sections = readDerived { state.sections.value }
        val appearances = sections.count { section ->
            section.items.any { it.country.iso2Code == "DE" }
        }
        assertEquals("Germany appeared in $appearances sections", 1, appearances)
        assertEquals(
            CountrySectionKind.Selected,
            sections.first { s -> s.items.any { it.country.iso2Code == "DE" } }.kind,
        )
    }

    @Test
    fun `a recent country does not also appear under Suggested or All`() {
        val state = loadedState(CountryPickerConfig(suggestedCountryCodes = listOf("FR", "US")))
        state.recentCountryCodes = listOf("FR")

        val sections = readDerived { state.sections.value }
        assertEquals(1, sections.count { s -> s.items.any { it.country.iso2Code == "FR" } })
        assertEquals(
            CountrySectionKind.Recent,
            sections.first { s -> s.items.any { it.country.iso2Code == "FR" } }.kind,
        )
    }

    @Test
    fun `no country appears twice anywhere in the sections`() {
        val state = loadedState(CountryPickerConfig(suggestedCountryCodes = listOf("US", "GB", "DE")))
        state.recentCountryCodes = listOf("FR", "JP")
        state.syncConfirmedSelection(setOf(kenya))

        val all = readDerived { state.sections.value }.flatMap { it.items }.map { it.country.iso2Code }
        assertEquals(all.size, all.distinct().size)
    }

    @Test
    fun `sections appear in priority order`() {
        val state = loadedState(CountryPickerConfig(suggestedCountryCodes = listOf("US")))
        state.recentCountryCodes = listOf("FR")
        state.syncConfirmedSelection(setOf(kenya))

        assertEquals(
            listOf(
                CountrySectionKind.Selected,
                CountrySectionKind.Recent,
                CountrySectionKind.Suggested,
                CountrySectionKind.All,
            ),
            readDerived { state.sections.value }.map { it.kind },
        )
    }

    @Test
    fun `empty sections are omitted rather than rendered as bare headers`() {
        val state = loadedState()
        val kinds = readDerived { state.sections.value }.map { it.kind }
        assertEquals(listOf(CountrySectionKind.All), kinds)
    }

    @Test
    fun `disabling the recents section hides it even when recents exist`() {
        val state = loadedState(CountryPickerConfig(showRecentlySelected = false))
        state.recentCountryCodes = listOf("FR")
        assertFalse(CountrySectionKind.Recent in readDerived { state.sections.value }.map { it.kind })
    }

    @Test
    fun `recents are capped at the configured limit`() {
        val state = loadedState(CountryPickerConfig(recentCountryLimit = 2))
        state.recentCountryCodes = listOf("FR", "JP", "DE", "US")
        val recent = readDerived { state.sections.value }
            .first { it.kind == CountrySectionKind.Recent }
        assertEquals(2, recent.count)
    }

    @Test
    fun `recents keep their most-recent-first order`() {
        val state = loadedState()
        state.recentCountryCodes = listOf("JP", "FR")
        val recent = readDerived { state.sections.value }
            .first { it.kind == CountrySectionKind.Recent }
        assertEquals(listOf("JP", "FR"), recent.items.map { it.country.iso2Code })
    }

    @Test
    fun `an unknown code in the recents or suggestions list is skipped, not rendered blank`() {
        val state = loadedState(CountryPickerConfig(suggestedCountryCodes = listOf("ZZ", "US")))
        state.recentCountryCodes = listOf("QQ", "FR")
        val suggested = readDerived { state.sections.value }
            .first { it.kind == CountrySectionKind.Suggested }
        assertEquals(listOf("US"), suggested.items.map { it.country.iso2Code })
    }

    // ── Single selection ────────────────────────────────────────────────────────────────────────

    @Test
    fun `single selection replaces rather than accumulates`() {
        val state = loadedState()
        state.toggleCountry(germany)
        state.toggleCountry(france)
        assertEquals(setOf(france), state.pendingSelection.value)
    }

    @Test
    fun `confirming returns the full country, not just a code`() {
        val state = loadedState()
        state.toggleCountry(kenya)
        val confirmed = state.confirmSelection()
        assertNotNull(confirmed)
        val selected = confirmed!!.single()
        assertEquals("KE", selected.iso2Code)
        assertEquals("KEN", selected.iso3Code)
        assertEquals("+254", selected.dialCode)
        assertEquals(CountryRegion.Africa, selected.region)
    }

    // ── Multi selection: pending vs confirmed ───────────────────────────────────────────────────

    @Test
    fun `opening the sheet seeds the pending selection from the confirmed one`() {
        val state = loadedState(CountryPickerConfig(selectionMode = CountrySelectionMode.Multiple))
        state.syncConfirmedSelection(setOf(france, japan))
        state.open()
        assertEquals(setOf(france, japan), state.pendingSelection.value)
    }

    @Test
    fun `toggling in multi select accumulates`() {
        val state = loadedState(CountryPickerConfig(selectionMode = CountrySelectionMode.Multiple))
        state.toggleCountry(france)
        state.toggleCountry(japan)
        assertEquals(setOf(france, japan), state.pendingSelection.value)
    }

    @Test
    fun `toggling an already-selected country removes it`() {
        val state = loadedState(CountryPickerConfig(selectionMode = CountrySelectionMode.Multiple))
        state.toggleCountry(france)
        state.toggleCountry(france)
        assertTrue(state.pendingSelection.value.isEmpty())
    }

    @Test
    fun `cancelling discards pending changes and leaves the confirmed selection alone`() {
        val state = loadedState(CountryPickerConfig(selectionMode = CountrySelectionMode.Multiple))
        state.syncConfirmedSelection(setOf(france))
        state.open()
        state.toggleCountry(japan)
        assertEquals(setOf(france, japan), state.pendingSelection.value)

        state.dismiss()

        assertEquals(setOf(france), state.confirmedSelection)
        assertEquals(setOf(france), state.pendingSelection.value)
    }

    @Test
    fun `reset clears the pending selection without touching the confirmed one`() {
        val state = loadedState(CountryPickerConfig(selectionMode = CountrySelectionMode.Multiple))
        state.syncConfirmedSelection(setOf(france))
        state.open()
        state.toggleCountry(japan)
        state.resetPendingSelection()

        assertTrue(state.pendingSelection.value.isEmpty())
        assertEquals(setOf(france), state.confirmedSelection)
    }

    @Test
    fun `confirming promotes the pending selection`() {
        val state = loadedState(CountryPickerConfig(selectionMode = CountrySelectionMode.Multiple))
        state.toggleCountry(france)
        state.toggleCountry(japan)
        assertEquals(setOf(france, japan), state.confirmSelection())
        assertEquals(setOf(france, japan), state.confirmedSelection)
    }

    // ── Selection bounds ────────────────────────────────────────────────────────────────────────

    @Test
    fun `confirm is refused below the minimum`() {
        val state = loadedState(
            CountryPickerConfig(
                selectionMode = CountrySelectionMode.Multiple,
                minimumSelectionCount = 2,
            ),
        )
        state.toggleCountry(france)
        assertFalse(readDerived { state.canConfirm.value })
        assertNull(state.confirmSelection())

        state.toggleCountry(japan)
        assertTrue(readDerived { state.canConfirm.value })
        assertNotNull(state.confirmSelection())
    }

    @Test
    fun `the maximum is enforced and the refusal is explained`() {
        val state = loadedState(
            CountryPickerConfig(
                selectionMode = CountrySelectionMode.Multiple,
                maximumSelectionCount = 2,
            ),
        )
        state.toggleCountry(france)
        state.toggleCountry(japan)
        state.toggleCountry(germany)

        assertEquals(setOf(france, japan), state.pendingSelection.value)
        // A checkbox that silently refuses to tick reads as a bug, so a message is required.
        assertNotNull(state.feedbackMessage)
    }

    @Test
    fun `deselecting below the maximum clears the refusal message`() {
        val state = loadedState(
            CountryPickerConfig(
                selectionMode = CountrySelectionMode.Multiple,
                maximumSelectionCount = 1,
            ),
        )
        state.toggleCountry(france)
        state.toggleCountry(japan)
        assertNotNull(state.feedbackMessage)

        state.toggleCountry(france)
        assertNull(state.feedbackMessage)
    }

    @Test
    fun `a config with max below min is rejected at construction`() {
        val error = runCatching {
            CountryPickerConfig(minimumSelectionCount = 3, maximumSelectionCount = 2)
        }.exceptionOrNull()
        assertTrue(error is IllegalArgumentException)
    }

    // ── Disabled countries ──────────────────────────────────────────────────────────────────────

    @Test
    fun `a disabled country cannot be selected however the row is activated`() {
        val state = loadedState(CountryPickerConfig(disabledCountryCodes = setOf("KP")))
        val northKorea = country("KP")
        assertTrue(state.isDisabled(northKorea))
        state.toggleCountry(northKorea)
        assertTrue(state.pendingSelection.value.isEmpty())
    }

    @Test
    fun `a disabled country is still listed, so the user can see why it is unavailable`() {
        val state = loadedState(CountryPickerConfig(disabledCountryCodes = setOf("KP")))
        val codes = readDerived { state.matches.value }.map { it.country.iso2Code }
        assertTrue("KP" in codes)
    }

    // ── Detection precedence ────────────────────────────────────────────────────────────────────

    @Test
    fun `detection applies when the user has not chosen yet`() {
        val state = loadedState()
        val applied = state.applyDetection(
            CountryDetectionResult.detected("DE", CountryDetectionSource.Network),
        )
        assertEquals(germany, applied)
        assertEquals(CountryDetectionSource.Network, state.detectionSource)
    }

    @Test
    fun `detection never overrides an explicit user selection`() {
        // The rule that stops a late network lookup from contradicting what the user just tapped.
        val state = loadedState()
        state.toggleCountry(kenya)
        val applied = state.applyDetection(
            CountryDetectionResult.detected("DE", CountryDetectionSource.Network),
        )
        assertNull(applied)
        assertEquals(setOf(kenya), state.pendingSelection.value)
    }

    @Test
    fun `the detection source is not reported once the user has chosen`() {
        val state = loadedState()
        state.applyDetection(CountryDetectionResult.detected("DE", CountryDetectionSource.Sim))
        assertNotNull(state.detectionSource)

        state.markExplicitSelection()
        assertNull(state.detectionSource)
    }

    @Test
    fun `a detected country the config disallows is ignored`() {
        val state = loadedState(CountryPickerConfig(allowedCountryCodes = setOf("KE", "UG")))
        assertNull(
            state.applyDetection(CountryDetectionResult.detected("DE", CountryDetectionSource.Sim)),
        )
    }

    @Test
    fun `an unknown detected code is ignored`() {
        val state = loadedState()
        assertNull(
            state.applyDetection(CountryDetectionResult.detected("ZZ", CountryDetectionSource.Sim)),
        )
    }

    @Test
    fun `an unavailable detection result changes nothing`() {
        val state = loadedState()
        assertNull(state.applyDetection(CountryDetectionResult.Unavailable))
    }

    // ── Sheet lifecycle ─────────────────────────────────────────────────────────────────────────

    @Test
    fun `dismissing clears the search but keeps the confirmed selection`() {
        val state = loadedState()
        state.syncConfirmedSelection(setOf(unitedStates))
        state.open()
        state.updateSearchQuery("ken")
        state.dismiss()

        assertEquals("", state.searchQuery)
        assertFalse(state.isSheetOpen)
        assertEquals(setOf(unitedStates), state.confirmedSelection)
    }

    @Test
    fun `reopening after a confirmed selection shows that selection`() {
        val state = loadedState()
        state.toggleCountry(kenya)
        state.confirmSelection()
        state.dismiss()
        state.open()
        assertEquals(setOf(kenya), state.pendingSelection.value)
    }

    @Test
    fun `a config change does not discard an in-progress search`() {
        val state = loadedState()
        state.updateSearchQuery("ken")
        state.config = state.config.copy(showIsoCode = true)
        assertEquals("ken", state.searchQuery)
    }

    @Test
    fun `only the enabled regions are offered as filters`() {
        val state = loadedState(
            CountryPickerConfig(enabledRegions = setOf(CountryRegion.Africa, CountryRegion.Europe)),
        )
        assertEquals(
            listOf(CountryRegion.Africa, CountryRegion.Europe),
            readDerived { state.availableRegions.value },
        )
    }
}
