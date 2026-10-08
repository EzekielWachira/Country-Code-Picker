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

import androidx.compose.ui.text.intl.Locale
import com.ezzy.ccp.countrypicker.model.Country
import kotlin.concurrent.Volatile
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Caches the result of a [CountryDataSource] and applies the caller's allow/exclude rules.
 *
 * The cache exists so reopening the sheet, rotating the device, or retrying after an error does not
 * re-hit a remote source. Concurrent [countries] calls are collapsed by a [Mutex], so two selectors
 * opening at once trigger one load, not two.
 *
 * The cache is keyed by the platform's default [Locale]. The bundled source returns country names in the
 * device's language, so a cache that ignored the locale would keep serving French names after the
 * user switched to Arabic — for the whole process lifetime, since [Default] is a singleton. A
 * locale change is rare and a reload is cheap, so the check is simply made on every call.
 *
 * This class holds no UI state and is safe to keep in a singleton, a DI graph, or a `remember`.
 */
public class CountryRepository(
    private val dataSource: CountryDataSource = DefaultCountryDataSource,
) {

    private val loadLock = Mutex()

    @Volatile
    private var cache: List<Country>? = null

    /** The locale [cache] was loaded for; a change to it invalidates the cache. */
    @Volatile
    private var cachedLocale: Locale? = null

    /**
     * Returns the full country list, loading it on first call.
     *
     * Rethrows whatever the data source threw — the caller (usually
     * [com.ezzy.ccp.countrypicker.state.CountryPickerState]) turns that into an error state with a
     * retry action rather than letting it escape into composition.
     */
    public suspend fun countries(): List<Country> {
        val locale = platformDefaultLocale()
        cache?.let { if (cachedLocale == locale) return it }
        return loadLock.withLock {
            // Re-check inside the lock: another caller may have populated the cache while we waited.
            cache?.let { if (cachedLocale == locale) return@withLock it }
            dataSource.load().also {
                cache = it
                cachedLocale = locale
            }
        }
    }

    /** Drops the cache so the next [countries] call reloads. Used by the sheet's Retry action. */
    public fun invalidate() {
        cache = null
        cachedLocale = null
    }

    /**
     * Narrows [countries] to what a picker is allowed to show.
     *
     * @param allowedCountryCodes When non-null, only these ISO alpha-2 codes survive. An empty set
     *   means "nothing is allowed" and yields an empty list — that is a real configuration, and the
     *   sheet renders a dedicated "no countries available" state for it rather than silently
     *   showing everything.
     * @param excludedCountryCodes Removed even when present in [allowedCountryCodes]; exclusion wins.
     */
    public fun applyAvailability(
        countries: List<Country>,
        allowedCountryCodes: Set<String>?,
        excludedCountryCodes: Set<String>,
    ): List<Country> {
        val allowed = allowedCountryCodes?.mapTo(HashSet()) { DefaultCountryDataSource.normalizeIsoCode(it) }
        val excluded = excludedCountryCodes.mapTo(HashSet()) { DefaultCountryDataSource.normalizeIsoCode(it) }
        if (allowed == null && excluded.isEmpty()) return countries
        return countries.filter { country ->
            (allowed == null || country.iso2Code in allowed) && country.iso2Code !in excluded
        }
    }

    public companion object {
        /** Shared repository over the bundled dataset. Cheap: the dataset is a lazy singleton. */
        public val Default: CountryRepository by lazy { CountryRepository() }
    }
}
