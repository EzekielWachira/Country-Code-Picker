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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import com.ezzy.ccp.countrypicker.data.CountryRepository
import com.ezzy.ccp.countrypicker.data.DefaultCountryDataSource
import com.ezzy.ccp.countrypicker.data.compositionLocale
import com.ezzy.ccp.countrypicker.detection.CountryDetectionBehavior
import com.ezzy.ccp.countrypicker.detection.CountryDetectionResult
import com.ezzy.ccp.countrypicker.detection.CountryDetector
import com.ezzy.ccp.countrypicker.model.Country
import com.ezzy.ccp.countrypicker.model.CountryRegion
import com.ezzy.ccp.countrypicker.persistence.NoOpRecentCountryStore
import com.ezzy.ccp.countrypicker.persistence.RecentCountryStore
import kotlinx.coroutines.launch

/**
 * Creates and remembers a [CountryPickerState], wiring data loading, restoration, country detection
 * and the recents store.
 *
 * ### Restoration
 * The sheet's transient state — query, region, pending selection, open/closed — survives rotation and
 * process death via [rememberSaveable]. Only ISO codes are saved, never [Country] objects, so the
 * saved bundle stays tiny and cannot go stale against a changed dataset.
 *
 * The **confirmed** selection is not saved here: it belongs to the caller, and the caller should hold
 * it in its own `rememberSaveable` or ViewModel. That is what makes reopening the sheet after a
 * process death show the right selection rather than an empty one.
 *
 * ### Detection
 * When [detector] is supplied, detection runs once per composition of this state and is applied
 * according to [detectionBehavior]. It can never override an explicit user selection — see
 * [CountryPickerState.applyDetection].
 *
 * @param config Picker behaviour. Changes are applied to the existing state rather than recreating it,
 *   so toggling config mid-session does not wipe the user's search.
 * @param selectedCountries The caller's confirmed selection, mirrored into the state each recomposition.
 * @param repository Country source. Defaults to the bundled dataset.
 * @param recentCountryStore Recents persistence. Defaults to [NoOpRecentCountryStore] — the library
 *   writes nothing to disk unless the host opts in.
 * @param detector Country detection, or `null` to disable it.
 * @param detectionBehavior What to do with a detection result.
 * @param onDetectedCountry Invoked when detection produces a country the caller should adopt. Not
 *   called when the user has already chosen, or when [detectionBehavior] is
 *   [CountryDetectionBehavior.AskFirst].
 */
@Composable
public fun rememberCountryPickerState(
    config: CountryPickerConfig = CountryPickerConfig(),
    selectedCountries: Set<Country> = emptySet(),
    repository: CountryRepository = CountryRepository.Default,
    recentCountryStore: RecentCountryStore = NoOpRecentCountryStore,
    detector: CountryDetector? = null,
    detectionBehavior: CountryDetectionBehavior = CountryDetectionBehavior.ShowBadge,
    onDetectedCountry: (Country) -> Unit = {},
): CountryPickerState {

    val state = rememberSaveable(saver = CountryPickerStateSaver(config, repository)) {
        CountryPickerState(
            initialRegion = config.initialRegion,
            config = config,
            repository = repository,
        )
    }

    // Config is pushed in rather than being a constructor-only value so a host that recomputes its
    // config on every recomposition does not reset the sheet.
    if (state.config != config) state.config = config

    state.syncConfirmedSelection(selectedCountries)

    // Keyed on the configuration locale as well as the repository: the bundled data source names
    // countries in the device's language, so a language change has to rebuild the list. Relying on
    // Activity recreation is not enough — an app using per-app languages
    // (AppCompatDelegate.setApplicationLocales) or declaring android:configChanges="locale" changes
    // the configuration without recreating anything, and the sheet would keep the old language.
    val locale = compositionLocale()
    LaunchedEffect(repository, locale) { state.loadCountries() }

    // Recents come from a Flow the host owns; collecting here keeps the sheet reactive to selections
    // made elsewhere in the app.
    LaunchedEffect(recentCountryStore, config.recentCountryLimit) {
        recentCountryStore.observeRecentCountryCodes().collect { codes ->
            state.recentCountryCodes = codes.take(config.recentCountryLimit)
        }
    }

    LaunchedEffect(detector, detectionBehavior) {
        if (detector == null) {
            state.detectionResult = CountryDetectionResult.Idle
            return@LaunchedEffect
        }
        state.detectionResult = CountryDetectionResult.InProgress
        val result = detector.detectCountry()
        val country = state.applyDetection(result)
        if (country != null && detectionBehavior != CountryDetectionBehavior.AskFirst) {
            onDetectedCountry(country)
        }
    }

    return state
}

/**
 * Records a confirmed selection into [store] whenever it changes.
 *
 * Split out of [rememberCountryPickerState] because recording is a side effect on the *caller's*
 * selection, which the state holder does not own. Using [snapshotFlow] rather than a
 * `LaunchedEffect(selection)` means a rapid sequence of changes collapses to the last one instead of
 * writing to disk for each.
 */
@Composable
public fun RecordRecentSelections(
    selectedCountries: Set<Country>,
    store: RecentCountryStore,
) {
    val scope = rememberCoroutineScope()
    LaunchedEffect(store) {
        snapshotFlow { selectedCountries }
            .collect { selection ->
                selection.forEach { country ->
                    scope.launch { store.recordSelection(country.iso2Code) }
                }
            }
    }
}

/**
 * Saves the sheet's transient state as a flat list of primitives.
 *
 * ISO codes rather than [Country] instances: a code is two bytes, is stable across app versions, and
 * resolves against whatever dataset is current at restore time. Codes that no longer exist are
 * dropped on restore instead of resurrecting a country the app no longer offers.
 */
private fun CountryPickerStateSaver(
    config: CountryPickerConfig,
    repository: CountryRepository,
): Saver<CountryPickerState, Any> = listSaver(
    save = { state ->
        listOf(
            state.searchQuery,
            state.selectedRegion?.key ?: "",
            state.pendingSelection.value.joinToString(",") { it.iso2Code },
            state.isSheetOpen.toString(),
            state.hasExplicitSelection.toString(),
        )
    },
    restore = { saved ->
        val pending = (saved.getOrNull(2) as? String).orEmpty()
            .split(',')
            .filter { it.isNotBlank() }
            .mapNotNull { DefaultCountryDataSource.findByIso2(it) }
            .toSet()

        CountryPickerState(
            initialSearchQuery = (saved.getOrNull(0) as? String).orEmpty(),
            initialRegion = CountryRegion.fromKey(saved.getOrNull(1) as? String),
            initialPendingSelection = pending,
            initialSheetOpen = (saved.getOrNull(3) as? String).toBoolean(),
            config = config,
            repository = repository,
        ).also { restored ->
            if ((saved.getOrNull(4) as? String).toBoolean()) restored.markExplicitSelection()
        }
    },
)
