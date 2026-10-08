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

package com.ezzy.ccp.countrypicker

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import com.ezzy.ccp.countrypicker.data.DefaultCountryDataSource
import com.ezzy.ccp.countrypicker.model.Country
import com.ezzy.ccp.countrypicker.model.UiText
import com.ezzy.ccp.countrypicker.state.CountryPickerConfig
import com.ezzy.ccp.countrypicker.theme.CountryPickerDefaults
import com.ezzy.ccp.countrypicker.theme.CountryPickerLayout
import com.ezzy.ccp.countrypicker.theme.CountryPickerTheme
import com.ezzy.ccp.countrypicker.ui.CountrySelector
import com.ezzy.ccp.countrypicker.ui.CountrySelectorState
import com.ezzy.ccp.countrypicker.ui.CountrySelectorVariant
import com.ezzy.ccp.countrypicker.ui.MultiCountrySelector
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

/**
 * Compose UI behaviour for the selector and sheet.
 *
 * ### Why every country-name lookup goes through content description, not visible text
 * Both the selector field's displayed value and every list row carry `clearAndSetSemantics {}` around
 * their inner `Text`s (see [com.ezzy.ccp.countrypicker.ui.CountrySelectorField] and
 * [com.ezzy.ccp.countrypicker.ui.CountryListItem]) — deliberately, so the whole field or row is one
 * merged accessibility node ("Country of residence. Kenya selected. Double tap to change.") instead of
 * TalkBack reading disjoint fragments. That means a country name is not independently visible-text; it
 * only exists as part of a merged content description. Finding nodes that way here is not a workaround
 * for that design, it is what proves it: a test that could only pass by falling back to raw text would
 * mean the merged-node accessibility story was never real to begin with.
 *
 * It also happens to route around search-field ambiguity for free: the query text lives in the search
 * field's *visible text*, which a country's *content description* never collides with.
 */
class CountryPickerUiTest {

    // An Activity-backed rule rather than createComposeRule(), so the back-press test can dispatch a
    // real system back event instead of simulating one.
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private val germany = requireNotNull(DefaultCountryDataSource.findByIso2("DE"))
    private val kenya = requireNotNull(DefaultCountryDataSource.findByIso2("KE"))
    private val france = requireNotNull(DefaultCountryDataSource.findByIso2("FR"))
    private val japan = requireNotNull(DefaultCountryDataSource.findByIso2("JP"))
    private val unitedKingdom = requireNotNull(DefaultCountryDataSource.findByIso2("GB"))
    private val algeria = requireNotNull(DefaultCountryDataSource.findByIso2("DZ"))
    private val albania = requireNotNull(DefaultCountryDataSource.findByIso2("AL"))
    private val afghanistan = requireNotNull(DefaultCountryDataSource.findByIso2("AF"))

    /** Wraps content in a Material theme, which the picker's defaults read their colors from. */
    private fun setContent(content: @Composable () -> Unit) {
        rule.setContent {
            MaterialTheme { Surface { content() } }
        }
    }

    /** Opens the sheet by tapping the selector, found by its merged content description. */
    private fun openSelector() {
        rule.onNode(hasContentDescription(LABEL, substring = true)).performClick()
    }

    /**
     * A list row's spoken description with the default layout: the name, then the dial code the row
     * shows — `"Kenya, +254"`.
     */
    private fun rowDescription(country: Country) = "${country.displayName}, ${country.dialCode}"

    /**
     * Clicks the list row for [country], found by its exact (merged) content description.
     *
     * Exact, not substring: the ModalBottomSheet is an overlay, so the selector field underneath it
     * stays composed while the sheet is open, and its own description ("Country of residence. Kenya
     * selected. Double tap to change.") *contains* the country name too. Exact match on the row's own
     * description is what disambiguates the row from the selector sitting behind it.
     */
    private fun clickRow(country: Country) {
        rule.onNodeWithContentDescription(rowDescription(country)).performClick()
    }

    /** Asserts a list row for [country] is on screen. See [clickRow] for why this is an exact match. */
    private fun assertRowDisplayed(country: Country) {
        rule.onNodeWithContentDescription(rowDescription(country)).assertIsDisplayed()
    }

    /** Asserts no row for [country] exists. See [clickRow] for why this is an exact match. */
    private fun assertRowAbsent(country: Country) {
        rule.onNodeWithContentDescription(rowDescription(country)).assertDoesNotExist()
    }

    /** A single-select host that records the selection, as a real caller would. */
    private fun singleSelectHost(
        initial: Country? = null,
        config: CountryPickerConfig = CountryPickerDefaults.config(),
        variant: CountrySelectorVariant = CountrySelectorVariant.Outlined,
        state: CountrySelectorState = CountrySelectorState.Default,
        enabled: Boolean = true,
        onSelected: (Country) -> Unit = {},
    ) {
        setContent {
            var selected by remember { mutableStateOf(initial) }
            CountrySelector(
                selectedCountry = selected,
                onCountrySelected = {
                    selected = it
                    onSelected(it)
                },
                config = config,
                variant = variant,
                state = state,
                enabled = enabled,
                label = UiText.of(LABEL),
            )
        }
    }

    // ── Opening and dismissing ──────────────────────────────────────────────────────────────────

    @Test
    fun tappingTheSelectorOpensTheSheet() {
        singleSelectHost()

        rule.onNodeWithText(SHEET_TITLE).assertDoesNotExist()
        openSelector()
        rule.onNodeWithText(SHEET_TITLE).assertIsDisplayed()
    }

    @Test
    fun theCloseIconDismissesTheSheet() {
        singleSelectHost()
        openSelector()
        rule.onNodeWithText(SHEET_TITLE).assertIsDisplayed()

        rule.onNodeWithContentDescription(CLOSE).performClick()
        rule.waitForIdle()

        rule.onNodeWithText(SHEET_TITLE).assertDoesNotExist()
    }

    @Test
    fun systemBackDismissesTheSheet() {
        singleSelectHost()
        openSelector()
        rule.onNodeWithText(SHEET_TITLE).assertIsDisplayed()

        // Predictive back / back press goes through the same dismissal path as the close icon.
        rule.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
        rule.waitForIdle()

        rule.onNodeWithText(SHEET_TITLE).assertDoesNotExist()
    }

    @Test
    fun aDisabledSelectorDoesNotOpenTheSheet() {
        singleSelectHost(enabled = false)

        openSelector()
        rule.waitForIdle()

        rule.onNodeWithText(SHEET_TITLE).assertDoesNotExist()
    }

    @Test
    fun aLoadingSelectorDoesNotOpenTheSheet() {
        singleSelectHost(state = CountrySelectorState.Loading)

        openSelector()
        rule.waitForIdle()

        rule.onNodeWithText(SHEET_TITLE).assertDoesNotExist()
    }

    // ── Selecting ───────────────────────────────────────────────────────────────────────────────

    @Test
    fun selectingACountryUpdatesTheSelectorAndReportsTheFullCountry() {
        var received: Country? = null
        singleSelectHost(onSelected = { received = it })

        openSelector()
        // Search first, so the target row is on screen without scrolling a 236-row list.
        rule.onNodeWithContentDescription(SEARCH_LABEL).performTextInput("Kenya")
        clickRow(kenya)
        rule.waitForIdle()

        assertEquals("KE", received?.iso2Code)
        // The full model, not just a name — the point of the callback's signature.
        assertEquals("KEN", received?.iso3Code)
        assertEquals("+254", received?.dialCode)
        // The sheet closed on selection (the default); the field itself now shows Kenya.
        rule.onNode(hasContentDescription("Kenya", substring = true)).assertIsDisplayed()
    }

    @Test
    fun selectingACountryDismissesTheSheetByDefault() {
        singleSelectHost()

        openSelector()
        rule.onNodeWithContentDescription(SEARCH_LABEL).performTextInput("Kenya")
        clickRow(kenya)
        rule.waitForIdle()

        rule.onNodeWithText(SHEET_TITLE).assertDoesNotExist()
    }

    @Test
    fun keepingTheSheetOpenIsConfigurable() {
        singleSelectHost(
            config = CountryPickerDefaults.config().copy(closeOnSingleSelection = false),
        )

        openSelector()
        rule.onNodeWithContentDescription(SEARCH_LABEL).performTextInput("Kenya")
        clickRow(kenya)
        rule.waitForIdle()

        rule.onNodeWithText(SHEET_TITLE).assertIsDisplayed()
    }

    @Test
    fun theCurrentSelectionLeadsTheListWhenTheSheetOpens() {
        singleSelectHost(initial = germany)

        openSelector()

        // The selection is the first group, so it is on screen without scrolling the list.
        rule.onNodeWithText(SELECTED_SECTION).assertIsDisplayed()
        assertRowDisplayed(germany)
    }

    @Test
    fun theSelectedRowIsMarkedAsSelectedForAccessibility() {
        singleSelectHost(initial = germany)
        openSelector()

        // Selection is announced through state semantics, not only through the tinted background —
        // colour alone would be invisible to a screen reader and to anyone in greyscale.
        rule.onNodeWithContentDescription(rowDescription(germany)).assertIsSelected()
    }

    // ── Search ──────────────────────────────────────────────────────────────────────────────────

    @Test
    fun searchFiltersTheList() {
        singleSelectHost()
        openSelector()

        rule.onNodeWithContentDescription(SEARCH_LABEL).performTextInput("Kenya")
        rule.waitForIdle()

        assertRowDisplayed(kenya)
        assertRowAbsent(germany)
    }

    @Test
    fun searchMatchesByDialCode() {
        singleSelectHost()
        openSelector()

        rule.onNodeWithContentDescription(SEARCH_LABEL).performTextInput("+254")
        rule.waitForIdle()

        assertRowDisplayed(kenya)
    }

    @Test
    fun searchMatchesByIsoCode() {
        singleSelectHost()
        openSelector()

        rule.onNodeWithContentDescription(SEARCH_LABEL).performTextInput("GBR")
        rule.waitForIdle()

        assertRowDisplayed(unitedKingdom)
    }

    @Test
    fun clearingTheSearchRestoresTheQuickPicks() {
        // Suggested countries are quick picks: tiles above the list, set aside while a search runs.
        singleSelectHost(
            config = CountryPickerDefaults.config(suggestedCountryCodes = listOf("US", "GB")),
        )
        openSelector()
        rule.onNodeWithText(QUICK_PICKS).assertIsDisplayed()

        rule.onNodeWithContentDescription(SEARCH_LABEL).performTextInput("Kenya")
        rule.waitForIdle()
        rule.onNodeWithText(QUICK_PICKS).assertDoesNotExist()

        rule.onNodeWithContentDescription(CLEAR_SEARCH).performClick()
        rule.waitForIdle()

        rule.onNodeWithText(QUICK_PICKS).assertIsDisplayed()
    }

    @Test
    fun noResultsShowsAnEmptyStateEchoingTheQuery() {
        singleSelectHost()
        openSelector()

        rule.onNodeWithContentDescription(SEARCH_LABEL).performTextInput("zzzznotacountry")
        rule.waitForIdle()

        // The query is echoed in the empty-state title so the user can see what was actually searched.
        // It also lives in the search field's own text, so two nodes legitimately contain it — assert
        // on the title specifically rather than expecting a single match.
        rule.onNode(hasText("No countries match", substring = true)).assertIsDisplayed()
        // Below the fold on a small screen with the keyboard up, so scrolled to first.
        rule.onNodeWithText(CLEAR_SEARCH).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun theEmptyStateClearActionRestoresTheList() {
        singleSelectHost()
        openSelector()
        rule.onNodeWithContentDescription(SEARCH_LABEL).performTextInput("zzzznotacountry")
        rule.waitForIdle()

        rule.onNodeWithText(CLEAR_SEARCH).performScrollTo().performClick()
        rule.waitForIdle()

        // First alphabetically, so the restored list starts with it — scrolled to, since the
        // keyboard can cover the top of the list on a small screen.
        rule.onNodeWithContentDescription(rowDescription(afghanistan)).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun theImeSearchActionDoesNotDismissTheSheet() {
        // Results are already live, so Search only hides the keyboard — it must not close the sheet or
        // clear the query.
        singleSelectHost()
        openSelector()
        rule.onNodeWithContentDescription(SEARCH_LABEL).performTextInput("Kenya")

        rule.onNodeWithContentDescription(SEARCH_LABEL).performImeAction()
        rule.waitForIdle()

        rule.onNodeWithText(SHEET_TITLE).assertIsDisplayed()
        assertRowDisplayed(kenya)
    }

    @Test
    fun theResultCountIsShownWhileSearching() {
        singleSelectHost()
        openSelector()

        rule.onNodeWithContentDescription(SEARCH_LABEL).performTextInput("Kenya")
        rule.waitForIdle()

        // Drawn in capitals, but spoken in sentence case — a screen reader must not spell "RESULT".
        rule.onNodeWithContentDescription("1 result").assertIsDisplayed()
    }

    // ── Region filters ──────────────────────────────────────────────────────────────────────────

    @Test
    fun aRegionChipFiltersTheCountries() {
        singleSelectHost()
        openSelector()

        rule.onNodeWithText("Africa").performClick()
        rule.waitForIdle()

        assertRowDisplayed(algeria)
        assertRowAbsent(germany)
    }

    @Test
    fun theAllChipRestoresEveryCountry() {
        // Albania: alphabetically second overall (after Afghanistan), so it renders without scrolling
        // a 236-row list — and it is Europe, so the Africa filter genuinely hides it first.
        singleSelectHost()
        openSelector()
        rule.onNodeWithText("Africa").performClick()
        rule.waitForIdle()
        assertRowAbsent(albania)

        rule.onNodeWithText("All").performClick()
        rule.waitForIdle()

        assertRowDisplayed(albania)
    }

    @Test
    fun regionChipsAreHiddenWhileSearching() {
        singleSelectHost()
        openSelector()
        rule.onNodeWithText("Africa").assertIsDisplayed()

        rule.onNodeWithContentDescription(SEARCH_LABEL).performTextInput("Kenya")
        rule.waitForIdle()

        rule.onNodeWithText("Africa").assertDoesNotExist()
    }

    // ── Compact and flag-only variants ──────────────────────────────────────────────────────────

    @Test
    fun theCompactSelectorOpensTheSameSheet() {
        singleSelectHost(initial = germany, variant = CountrySelectorVariant.Compact)

        // The compact pill's visible name is merged into the field's content description too.
        rule.onNode(hasContentDescription("Germany", substring = true)).performClick()

        rule.onNodeWithText(SHEET_TITLE).assertIsDisplayed()
    }

    @Test
    fun theFlagOnlySelectorAnnouncesTheCountryAndOpensTheSameSheet() {
        singleSelectHost(initial = germany, variant = CountrySelectorVariant.FlagOnly)

        // It has no visible text at all, so this description is the only thing identifying it.
        val description = "Selected country: Germany. Double tap to change."
        rule.onNodeWithContentDescription(description).assertIsDisplayed()

        rule.onNodeWithContentDescription(description).performClick()
        rule.onNodeWithText(SHEET_TITLE).assertIsDisplayed()
    }

    @Test
    fun theFlagOnlySelectorAnnouncesTheEmptyStateToo() {
        singleSelectHost(variant = CountrySelectorVariant.FlagOnly)
        rule.onNodeWithContentDescription("No country selected. Double tap to choose a country.")
            .assertIsDisplayed()
    }

    @Test
    fun theDialCodeSelectorAnnouncesTheCallingCode() {
        singleSelectHost(initial = kenya, variant = CountrySelectorVariant.DialCode)

        rule.onNodeWithContentDescription(
            "Country calling code +254, Kenya. Double tap to change.",
        ).assertIsDisplayed()
    }

    // ── Accessibility labels ────────────────────────────────────────────────────────────────────

    @Test
    fun theSelectorAnnouncesLabelAndSelectionAsOnePhrase() {
        singleSelectHost(initial = germany)

        // One phrase, not three fragments — TalkBack reads the description verbatim.
        rule.onNodeWithContentDescription("$LABEL. Germany selected. Double tap to change.")
            .assertIsDisplayed()
    }

    @Test
    fun sheetIconsHaveContentDescriptions() {
        singleSelectHost()
        openSelector()

        rule.onNodeWithContentDescription(CLOSE).assertIsDisplayed()
        rule.onNodeWithContentDescription(SEARCH_LABEL).assertIsDisplayed()

        rule.onNodeWithContentDescription(SEARCH_LABEL).performTextInput("a")
        rule.onNodeWithContentDescription(CLEAR_SEARCH).assertIsDisplayed()
    }

    // ── Multi selection ─────────────────────────────────────────────────────────────────────────

    /** A multi-select host that records only *confirmed* selections. */
    private fun multiSelectHost(
        initial: Set<Country> = emptySet(),
        config: CountryPickerConfig = CountryPickerDefaults.multiSelectConfig(),
        layout: (CountryPickerLayout) -> CountryPickerLayout = { it },
        onConfirmed: (Set<Country>) -> Unit = {},
    ) {
        setContent {
            var selected by remember { mutableStateOf(initial) }
            val style = CountryPickerTheme.style
            Column {
                MultiCountrySelector(
                    style = style.copy(layout = layout(style.layout)),
                    selectedCountries = selected,
                    onSelectionConfirmed = {
                        selected = it
                        onConfirmed(it)
                    },
                    config = config,
                    label = UiText.of(LABEL),
                )
            }
        }
    }

    @Test
    fun multiSelectionUpdatesTheCountInTheTitleAndConfirmButton() {
        multiSelectHost()
        openSelector()

        rule.onNodeWithContentDescription(SEARCH_LABEL).performTextReplacement("France")
        clickRow(france)
        rule.onNodeWithContentDescription(SEARCH_LABEL).performTextReplacement("Japan")
        clickRow(japan)
        rule.waitForIdle()

        rule.onNodeWithText("Confirm (2)").assertIsDisplayed()
    }

    @Test
    fun confirmCommitsThePendingSelection() {
        var confirmed: Set<Country>? = null
        multiSelectHost(onConfirmed = { confirmed = it })
        openSelector()

        rule.onNodeWithContentDescription(SEARCH_LABEL).performTextReplacement("France")
        clickRow(france)
        rule.onNodeWithText("Confirm (1)").performClick()
        rule.waitForIdle()

        assertEquals(setOf(france), confirmed)
    }

    @Test
    fun cancelDiscardsPendingChanges() {
        var confirmed: Set<Country>? = null
        multiSelectHost(initial = setOf(france), onConfirmed = { confirmed = it })
        openSelector()

        rule.onNodeWithContentDescription(SEARCH_LABEL).performTextReplacement("Japan")
        clickRow(japan)
        rule.onNodeWithText(CANCEL).performClick()
        rule.waitForIdle()

        // The confirmed callback must never fire on cancel.
        assertNull(confirmed)
    }

    @Test
    fun resetClearsThePendingSelectionWithoutConfirming() {
        var confirmed: Set<Country>? = null
        multiSelectHost(initial = setOf(france, japan), onConfirmed = { confirmed = it })
        openSelector()

        rule.onNodeWithText(RESET).performClick()
        rule.waitForIdle()

        rule.onNodeWithText(RESET).assertDoesNotExist()
        assertNull(confirmed)
    }

    @Test
    fun resetIsHiddenWhenNothingIsSelected() {
        multiSelectHost()
        openSelector()
        rule.onNodeWithText(RESET).assertDoesNotExist()
    }

    @Test
    fun confirmIsDisabledBelowTheMinimumSelection() {
        multiSelectHost(
            config = CountryPickerDefaults.multiSelectConfig(minimumSelectionCount = 2),
        )
        openSelector()

        rule.onNodeWithContentDescription(SEARCH_LABEL).performTextReplacement("France")
        clickRow(france)
        rule.waitForIdle()

        rule.onNodeWithText("Confirm (1)").assertIsNotEnabled()
    }

    @Test
    fun reachingTheMaximumIsExplainedRatherThanSilentlyRefused() {
        multiSelectHost(
            config = CountryPickerDefaults.multiSelectConfig(maximumSelectionCount = 1),
        )
        openSelector()

        rule.onNodeWithContentDescription(SEARCH_LABEL).performTextReplacement("France")
        clickRow(france)
        rule.onNodeWithContentDescription(SEARCH_LABEL).performTextReplacement("Japan")
        clickRow(japan)
        rule.waitForIdle()

        // A checkbox that just refuses to tick reads as a bug.
        rule.onNode(hasText("at most", substring = true)).assertIsDisplayed()
        rule.onNodeWithText("Confirm (1)").assertIsDisplayed()
    }

    @Test
    fun rowMetadataIsShownWhenConfigured() {
        // With ISO codes switched on, a row reads "France" over "FR · +33". Metadata is folded into
        // the row's own merged content description, alongside the name.
        multiSelectHost(layout = { it.copy(showIsoCode = true, showDialCode = true) })
        openSelector()
        rule.onNodeWithContentDescription(SEARCH_LABEL).performTextReplacement("France")
        rule.waitForIdle()

        rule.onNodeWithContentDescription("France, FR · +33").assertIsDisplayed()
    }

    @Test
    fun selectedCountriesMoveToTheSelectedSection() {
        multiSelectHost(initial = setOf(france, japan))
        openSelector()

        rule.onNodeWithText("SELECTED · 2").assertIsDisplayed()
    }

    @Test
    fun theMultiSelectorSummarisesRatherThanTruncatingNames() {
        // Listing both names in a 56dp field would truncate them; the field shows a count instead,
        // merged into one accessibility node rather than a separately findable text run.
        multiSelectHost(initial = setOf(france, japan))
        rule.onNode(hasContentDescription("2 countries selected", substring = true))
            .assertIsDisplayed()
    }

    @Test
    fun theMultiSelectorDescriptionMatchesWhatIsShown() {
        // The regression this guards: the merged description must summarise the actual selection
        // count, not silently announce only the first country while the visible text says otherwise.
        multiSelectHost(initial = setOf(france, japan))
        rule.onNodeWithContentDescription(
            "Country of residence. 2 countries selected. France, Japan. Double tap to change.",
        ).assertIsDisplayed()
    }

    private companion object {
        const val LABEL = "Country of residence"
        const val SHEET_TITLE = "Select country"
        const val CLOSE = "Close"
        const val SEARCH_LABEL = "Search countries"
        const val CLEAR_SEARCH = "Clear search"
        const val SELECTED_SECTION = "SELECTED"
        const val QUICK_PICKS = "QUICK PICKS"
        const val RESET = "Reset"
        const val CANCEL = "Cancel"
    }
}
