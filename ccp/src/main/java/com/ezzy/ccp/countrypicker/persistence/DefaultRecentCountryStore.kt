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

package com.ezzy.ccp.countrypicker.persistence

import android.content.Context
import android.content.SharedPreferences
import com.ezzy.ccp.countrypicker.data.DefaultCountryDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

/**
 * File-backed [RecentCountryStore] with an in-memory [MutableStateFlow] mirror.
 *
 * ### Why SharedPreferences and not DataStore
 * This is a UI library, and a handful of ISO codes is the smallest possible piece of state. Adding
 * `androidx.datastore` — plus its Okio and protobuf transitive graph — to every consumer's APK to
 * store ten two-letter strings is not a trade worth making. `SharedPreferences` is already in the
 * platform, so the default store costs nothing to depend on.
 *
 * The interface is what matters for hosts who disagree: an app that already uses DataStore can
 * implement [RecentCountryStore] over its own store in a dozen lines and pass it in. Nothing in the
 * library depends on this class.
 *
 * All disk work happens on [Dispatchers.IO]; reads after construction are served from memory, so the
 * recents section renders on the first frame instead of appearing a moment later.
 *
 * @param context Used once to open the preferences file. Application context is stored, so this is
 *   safe to hold in a singleton.
 * @param maxEntries How many codes to keep. Older entries fall off the end.
 * @param fileName Preferences file name; override to isolate per-user or per-flow recents.
 */
class DefaultRecentCountryStore(
    context: Context,
    private val maxEntries: Int = DEFAULT_MAX_ENTRIES,
    fileName: String = DEFAULT_FILE_NAME,
) : RecentCountryStore {

    private val preferences: SharedPreferences =
        context.applicationContext.getSharedPreferences(fileName, Context.MODE_PRIVATE)

    private val recents = MutableStateFlow(readFromDisk())

    override fun observeRecentCountryCodes(): Flow<List<String>> = recents.asStateFlow()

    override suspend fun recordSelection(countryCode: String) {
        val normalized = DefaultCountryDataSource.normalizeIsoCode(countryCode)
        // Reject unknown codes rather than persisting junk that will never resolve to a country.
        if (DefaultCountryDataSource.findByIso2(normalized) == null) return

        val updated = (listOf(normalized) + recents.value.filterNot { it == normalized })
            .take(maxEntries)
        if (updated == recents.value) return

        recents.value = updated
        withContext(Dispatchers.IO) {
            preferences.edit().putString(KEY_RECENTS, updated.joinToString(SEPARATOR)).apply()
        }
    }

    override suspend fun clear() {
        recents.value = emptyList()
        withContext(Dispatchers.IO) { preferences.edit().remove(KEY_RECENTS).apply() }
    }

    /**
     * Reads the stored codes, dropping any that no longer resolve to a country.
     *
     * Runs synchronously in the constructor: this is a single small preferences read, and doing it
     * eagerly is what lets [observeRecentCountryCodes] emit real data on its first emission.
     */
    private fun readFromDisk(): List<String> =
        preferences.getString(KEY_RECENTS, null)
            ?.split(SEPARATOR)
            ?.asSequence()
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?.map { DefaultCountryDataSource.normalizeIsoCode(it) }
            ?.filter { DefaultCountryDataSource.findByIso2(it) != null }
            ?.distinct()
            ?.take(maxEntries)
            ?.toList()
            .orEmpty()

    companion object {
        const val DEFAULT_MAX_ENTRIES: Int = 5
        private const val DEFAULT_FILE_NAME = "ccp_recent_countries"
        private const val KEY_RECENTS = "recent_country_codes"
        private const val SEPARATOR = ","
    }
}

/**
 * An in-memory [RecentCountryStore] that survives recomposition but not process death.
 *
 * Useful for previews, tests, and hosts that want the recents section without persisting anything.
 */
class InMemoryRecentCountryStore(
    initial: List<String> = emptyList(),
    private val maxEntries: Int = DefaultRecentCountryStore.DEFAULT_MAX_ENTRIES,
) : RecentCountryStore {

    private val recents = MutableStateFlow(initial.take(maxEntries))

    override fun observeRecentCountryCodes(): Flow<List<String>> = recents.asStateFlow()

    override suspend fun recordSelection(countryCode: String) {
        val normalized = DefaultCountryDataSource.normalizeIsoCode(countryCode)
        recents.value = (listOf(normalized) + recents.value.filterNot { it == normalized })
            .take(maxEntries)
    }

    override suspend fun clear() {
        recents.value = emptyList()
    }
}
