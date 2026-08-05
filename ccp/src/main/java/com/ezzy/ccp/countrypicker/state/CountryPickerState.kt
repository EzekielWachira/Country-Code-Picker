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

import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.ezzy.ccp.R
import com.ezzy.ccp.countrypicker.data.CountryRepository
import com.ezzy.ccp.countrypicker.data.CountrySearchEngine
import com.ezzy.ccp.countrypicker.data.DefaultCountryDataSource
import com.ezzy.ccp.countrypicker.detection.CountryDetectionResult
import com.ezzy.ccp.countrypicker.detection.CountryDetectionSource
import com.ezzy.ccp.countrypicker.model.Country
import com.ezzy.ccp.countrypicker.model.CountryMatch
import com.ezzy.ccp.countrypicker.model.CountryRegion
import com.ezzy.ccp.countrypicker.model.UiText

/**
 * How the country list is doing.
 *
 * `Loading` and `Error` only occur with a custom, usually remote,
 * [com.ezzy.ccp.countrypicker.data.CountryDataSource] — the bundled dataset is in-memory, so the
 * default path goes straight to `Loaded` on the first frame and the picker works offline.
 */
sealed interface CountryLoadState {
    data object Loading : CountryLoadState
    data object Loaded : CountryLoadState

    /** Loading failed. [message] is shown with a Retry action. */
    data class Error(val message: UiText, val offline: Boolean = false) : CountryLoadState
}

/**
 * Owns the picker's transient UI state: sheet visibility, search query, region filter, and the
 * *pending* selection.
 *
 * ### What this class deliberately does not do
 * It holds no business logic and reaches no repository of the host's. It does not own the
 * **confirmed** selection either — that is hoisted to the caller, which is what makes
 * [com.ezzy.ccp.countrypicker.ui.CountrySelector] a controlled component. The state holder knows
 * about the confirmed value only so it can render it and diff against it.
 *
 * ### Pending vs. confirmed
 * In multi-select, [pendingSelection] is scratch space. [confirmSelection] is the *only* path by
 * which it reaches the caller; [dismiss] and [resetPendingSelection] discard it. A caller's set is
 * never mutated behind their back, so "Cancel" is genuinely a cancel rather than a visual one.
 *
 * ### Detection precedence
 * [applyDetection] refuses to overwrite a selection the user made themselves ([hasExplicitSelection]).
 * A detected country is a guess; a tapped country is a statement. Once the user has stated something,
 * a late-arriving network lookup must not silently contradict it.
 *
 * Create via [rememberCountryPickerState], which also wires restoration and the recents store.
 */
@Stable
class CountryPickerState internal constructor(
    initialSearchQuery: String = "",
    initialRegion: CountryRegion? = null,
    initialPendingSelection: Set<Country> = emptySet(),
    initialSheetOpen: Boolean = false,
    config: CountryPickerConfig = CountryPickerConfig(),
    private val repository: CountryRepository = CountryRepository.Default,
) {

    // ── Configuration ────────────────────────────────────────────────────────────────────────────

    /**
     * The active config. Settable so a recomposition with a changed config updates behaviour without
     * discarding the user's in-progress search or pending selection.
     */
    var config: CountryPickerConfig by mutableStateOf(config)
        internal set

    // ── Sheet ────────────────────────────────────────────────────────────────────────────────────

    /** Whether the picker sheet is showing. */
    var isSheetOpen: Boolean by mutableStateOf(initialSheetOpen)
        private set

    /** Whether the search field currently holds focus, which drives the filled→outlined morph. */
    var isSearchFocused: Boolean by mutableStateOf(false)
        private set

    // ── Search & filtering ───────────────────────────────────────────────────────────────────────

    /** The raw query the user typed. Never persisted anywhere. */
    var searchQuery: String by mutableStateOf(initialSearchQuery)
        private set

    /** The active region filter, or `null` for "All". */
    var selectedRegion: CountryRegion? by mutableStateOf(initialRegion)
        private set

    /**
     * The region to restore when search is cleared.
     *
     * Search hides the chip row, and a user who filtered to Africa, searched, then cleared the search
     * expects to be back in Africa — not silently reset to All.
     */
    private var regionBeforeSearch: CountryRegion? = initialRegion

    // ── Selection ────────────────────────────────────────────────────────────────────────────────

    private val pendingSelectionState = mutableStateOf(initialPendingSelection)

    /** The staged selection. In single-select this holds at most one country. */
    val pendingSelection: State<Set<Country>> get() = pendingSelectionState

    /** The caller's confirmed selection, mirrored here for rendering and diffing. */
    var confirmedSelection: Set<Country> by mutableStateOf(emptySet())
        internal set

    /**
     * True once the user has picked a country in this state's lifetime.
     *
     * The guard that stops [applyDetection] from overwriting an explicit choice.
     */
    var hasExplicitSelection: Boolean by mutableStateOf(false)
        private set

    // ── Data ─────────────────────────────────────────────────────────────────────────────────────

    /** Load state of the country list. */
    var loadState: CountryLoadState by mutableStateOf(CountryLoadState.Loading)
        private set

    /** All countries from the data source, before any availability or region filtering. */
    var allCountries: List<Country> by mutableStateOf(emptyList())
        private set

    /** Recent ISO codes, most recent first, supplied by the recents store. */
    var recentCountryCodes: List<String> by mutableStateOf(emptyList())
        internal set

    /** The latest detection outcome. */
    var detectionResult: CountryDetectionResult by mutableStateOf(CountryDetectionResult.Idle)
        internal set

    /**
     * A transient message for the user — currently only "you can select at most N countries".
     *
     * Surfacing the refusal matters: a checkbox that simply refuses to tick, with no explanation,
     * reads as a bug.
     */
    var feedbackMessage: UiText? by mutableStateOf(null)
        private set

    // ── Derived lists ────────────────────────────────────────────────────────────────────────────

    /** True when a query long enough to filter is active. */
    val isSearching: State<Boolean> = derivedStateOf {
        config.showSearch && searchQuery.trim().length >= config.minimumSearchQueryLength
    }

    /**
     * Countries surviving the allow/exclude rules and the active region filter.
     *
     * Region filtering happens here, before search, so the result count reflects "12 results in
     * Africa" rather than 12 results globally.
     */
    private val availableCountries: State<List<Country>> = derivedStateOf {
        val available = repository.applyAvailability(
            countries = allCountries,
            allowedCountryCodes = config.allowedCountryCodes,
            excludedCountryCodes = config.excludedCountryCodes,
        )
        val region = selectedRegion
        if (region == null) available else available.filter { it.region == region }
    }

    /** Ranked search results over [availableCountries]. */
    val matches: State<List<CountryMatch>> = derivedStateOf {
        CountrySearchEngine.search(
            countries = availableCountries.value,
            query = if (config.showSearch) searchQuery else "",
            fields = config.searchFields,
            minQueryLength = config.minimumSearchQueryLength,
        )
    }

    /** Number of countries matching the current query and region. */
    val resultCount: State<Int> = derivedStateOf { matches.value.size }

    /**
     * The sections to render, grouped and deduplicated.
     *
     * While searching this collapses to a single [CountrySectionKind.SearchResults] section — see
     * [CountrySectionKind] for why grouping and relevance ranking are not combined.
     */
    val sections: State<List<CountrySection>> = derivedStateOf { buildSections() }

    /** True when there is nothing to show because everything was filtered out by configuration. */
    val isEmptyByConfiguration: State<Boolean> = derivedStateOf {
        loadState is CountryLoadState.Loaded && availableCountries.value.isEmpty()
    }

    /** True when a query matched nothing but countries were available to match. */
    val isEmptyBySearch: State<Boolean> = derivedStateOf {
        loadState is CountryLoadState.Loaded &&
            matches.value.isEmpty() &&
            availableCountries.value.isNotEmpty()
    }

    /** Whether Confirm should be enabled in multi-select. */
    val canConfirm: State<Boolean> = derivedStateOf {
        config.isSelectionCountValid(pendingSelectionState.value.size)
    }

    /** Regions offered as chips, ordered by [CountryRegion] declaration order. */
    val availableRegions: State<List<CountryRegion>> = derivedStateOf {
        CountryRegion.entries.filter { it in config.enabledRegions }
    }

    // ── Sheet control ────────────────────────────────────────────────────────────────────────────

    /**
     * Opens the sheet, seeding the pending selection from the confirmed one.
     *
     * Seeding is what makes "Cancel" coherent in multi-select: the user starts from where they are,
     * and cancelling returns them there.
     */
    fun open() {
        pendingSelectionState.value = confirmedSelection
        selectedRegion = config.initialRegion ?: selectedRegion
        regionBeforeSearch = selectedRegion
        feedbackMessage = null
        isSheetOpen = true
    }

    /**
     * Dismisses the sheet, discarding any pending selection.
     *
     * The same path for the close icon, the scrim, a drag-down and system back — one exit means those
     * four gestures cannot disagree about whether changes were kept.
     */
    fun dismiss() {
        isSheetOpen = false
        isSearchFocused = false
        clearSearch()
        pendingSelectionState.value = confirmedSelection
        feedbackMessage = null
    }

    /**
     * Records whether the search field holds focus.
     *
     * Named `onSearchFocusChanged` rather than `setSearchFocused` because the latter collides on the
     * JVM with the private setter Kotlin generates for [isSearchFocused].
     */
    fun onSearchFocusChanged(focused: Boolean) {
        isSearchFocused = focused
    }

    // ── Search & region ──────────────────────────────────────────────────────────────────────────

    /**
     * Updates the query.
     *
     * Filtering is synchronous — see [CountrySearchEngine] on why local search is not debounced.
     * Entering a query stashes the region filter so [clearSearch] can restore it.
     */
    fun updateSearchQuery(query: String) {
        val wasSearching = isSearching.value
        searchQuery = query
        if (!wasSearching && isSearching.value) regionBeforeSearch = selectedRegion
    }

    /** Clears the query and restores the region filter that was active before searching. */
    fun clearSearch() {
        if (searchQuery.isEmpty()) return
        searchQuery = ""
        selectedRegion = regionBeforeSearch
    }

    /** Selects a region filter, or `null` for "All". Does not touch the selection. */
    fun selectRegion(region: CountryRegion?) {
        selectedRegion = region
        regionBeforeSearch = region
    }

    /** Dismisses the transient feedback message. */
    fun consumeFeedback() {
        feedbackMessage = null
    }

    // ── Selection ────────────────────────────────────────────────────────────────────────────────

    /** True when [country] is in the pending selection. */
    fun isPending(country: Country): Boolean = country in pendingSelectionState.value

    /** True when [country] is shown but not selectable. */
    fun isDisabled(country: Country): Boolean =
        country.iso2Code in config.disabledCountryCodes.map(DefaultCountryDataSource::normalizeIsoCode)

    /**
     * Toggles [country] in the pending selection.
     *
     * In single-select this replaces the selection. In multi-select it adds or removes, refusing
     * additions past [CountryPickerConfig.maximumSelectionCount] with a [feedbackMessage].
     *
     * A no-op for disabled countries, so a row that looks unselectable is unselectable regardless of
     * how it was activated (tap, checkbox, keyboard).
     */
    fun toggleCountry(country: Country) {
        if (isDisabled(country)) return
        val current = pendingSelectionState.value

        if (!config.isMultiSelect) {
            pendingSelectionState.value = setOf(country)
            hasExplicitSelection = true
            return
        }

        if (country in current) {
            pendingSelectionState.value = current - country
            feedbackMessage = null
        } else {
            if (config.isAtMaximum(current.size)) {
                feedbackMessage = UiText.plural(
                    R.plurals.ccp_max_selection_reached,
                    requireNotNull(config.maximumSelectionCount),
                )
                return
            }
            pendingSelectionState.value = current + country
            feedbackMessage = null
        }
        hasExplicitSelection = true
    }

    /** Clears the pending selection. The sheet's "Reset" action. */
    fun resetPendingSelection() {
        pendingSelectionState.value = emptySet()
        feedbackMessage = null
    }

    /**
     * Promotes the pending selection to confirmed and returns it, or `null` when the bounds are not
     * satisfied.
     *
     * Returning the set rather than invoking a stored callback keeps the state holder free of the
     * caller's lambda and makes the commit point explicit at the call site.
     */
    fun confirmSelection(): Set<Country>? {
        if (!canConfirm.value) return null
        val confirmed = pendingSelectionState.value
        confirmedSelection = confirmed
        hasExplicitSelection = true
        clearSearch()
        return confirmed
    }

    /** Mirrors the caller's confirmed selection into this state. Called on recomposition. */
    internal fun syncConfirmedSelection(selection: Set<Country>) {
        if (confirmedSelection == selection) return
        confirmedSelection = selection
        if (!isSheetOpen) pendingSelectionState.value = selection
    }

    /** Marks the selection as user-made, e.g. when the host restores a saved choice as explicit. */
    fun markExplicitSelection() {
        hasExplicitSelection = true
    }

    // ── Data loading ─────────────────────────────────────────────────────────────────────────────

    /**
     * Loads the country list, moving [loadState] through Loading → Loaded/Error.
     *
     * Failures become an [CountryLoadState.Error] with a Retry affordance rather than an exception
     * escaping into composition and taking the host's screen down with it.
     */
    internal suspend fun loadCountries() {
        loadState = CountryLoadState.Loading
        try {
            allCountries = repository.countries()
            loadState = CountryLoadState.Loaded
        } catch (cancellation: kotlinx.coroutines.CancellationException) {
            throw cancellation
        } catch (_: Exception) {
            loadState = CountryLoadState.Error(UiText.resource(R.string.ccp_error_body))
        }
    }

    /** Drops the cache and reloads. The sheet's Retry action. */
    internal suspend fun retryLoad() {
        repository.invalidate()
        loadCountries()
    }

    /**
     * Applies a detection result, unless the user has already chosen a country.
     *
     * Also ignores a detected country that the current configuration does not allow — a detector
     * reporting a market the app does not serve should not be able to force it into the field.
     *
     * @return The country to select, or `null` when detection should not change the selection.
     */
    internal fun applyDetection(result: CountryDetectionResult): Country? {
        detectionResult = result
        if (hasExplicitSelection) return null
        val detected = (result as? CountryDetectionResult.Detected) ?: return null
        val country = DefaultCountryDataSource.findByIso2(detected.iso2Code) ?: return null
        return country.takeIf { candidate ->
            repository.applyAvailability(
                countries = listOf(candidate),
                allowedCountryCodes = config.allowedCountryCodes,
                excludedCountryCodes = config.excludedCountryCodes,
            ).isNotEmpty()
        }
    }

    /** The detection source, when the current selection came from detection rather than the user. */
    val detectionSource: CountryDetectionSource?
        get() = (detectionResult as? CountryDetectionResult.Detected)
            ?.takeIf { !hasExplicitSelection }
            ?.source

    // ── Section building ─────────────────────────────────────────────────────────────────────────

    /**
     * Groups [matches] into sections, giving each country to exactly one.
     *
     * The `claimed` set is the deduplication mechanism: each section takes only countries no
     * higher-priority section already took, which is why a selected Germany never also shows up under
     * Suggested or All countries. Empty sections are dropped so the sheet never shows a bare header.
     */
    private fun buildSections(): List<CountrySection> {
        val currentMatches = matches.value
        if (currentMatches.isEmpty()) return emptyList()

        if (isSearching.value) {
            return listOf(CountrySection(CountrySectionKind.SearchResults, currentMatches))
        }

        val byIso = currentMatches.associateBy { it.country.iso2Code }
        val claimed = HashSet<String>(currentMatches.size)
        val sections = ArrayList<CountrySection>(4)

        /** Resolves [codes] to matches in the given order, skipping unknown and already-claimed. */
        fun take(codes: List<String>): List<CountryMatch> = codes.asSequence()
            .map(DefaultCountryDataSource::normalizeIsoCode)
            .distinct()
            .mapNotNull { byIso[it] }
            .filter { claimed.add(it.country.iso2Code) }
            .toList()

        val selectedCodes = selectionForSections().map { it.iso2Code }
        take(selectedCodes).takeIf { it.isNotEmpty() }?.let {
            sections += CountrySection(CountrySectionKind.Selected, it)
        }

        if (config.showRecentlySelected) {
            take(recentCountryCodes.take(config.recentCountryLimit)).takeIf { it.isNotEmpty() }?.let {
                sections += CountrySection(CountrySectionKind.Recent, it)
            }
        }

        if (config.showSuggestedCountries) {
            take(config.suggestedCountryCodes).takeIf { it.isNotEmpty() }?.let {
                sections += CountrySection(CountrySectionKind.Suggested, it)
            }
        }

        val remaining = currentMatches.filterNot { it.country.iso2Code in claimed }
        val ordered = config.countryComparator
            ?.let { comparator -> remaining.sortedWith(compareBy(comparator) { it.country }) }
            ?: remaining
        if (ordered.isNotEmpty()) {
            sections += CountrySection(CountrySectionKind.All, ordered)
        }

        return sections
    }

    /**
     * Which countries the "Selected" section shows.
     *
     * Multi-select shows the pending set, so ticking a country visibly moves it to the top. In
     * single-select the pending value and the confirmed value are the same thing from the user's
     * point of view, so pending wins and falls back to confirmed.
     */
    private fun selectionForSections(): Set<Country> {
        val pending = pendingSelectionState.value
        return if (pending.isNotEmpty()) pending else confirmedSelection
    }
}
