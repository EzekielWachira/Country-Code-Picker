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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.ezzy.ccp.countrypicker.data.DefaultCountryDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

/**
 * Persistent [RecentCountryStore] with an in-memory [MutableStateFlow] mirror.
 *
 * Backed by the platform's own small key-value store — `SharedPreferences` on Android,
 * `NSUserDefaults` on iOS. Create it with `DefaultRecentCountryStore(context)` on Android,
 * `DefaultRecentCountryStore()` on iOS, or [rememberDefaultRecentCountryStore] from common code.
 *
 * ### Why not DataStore
 * This is a UI library, and a handful of ISO codes is the smallest possible piece of state. Adding
 * `androidx.datastore` — plus its Okio and protobuf transitive graph — to every consumer's app to
 * store ten two-letter strings is not a trade worth making. Both platform stores are already there,
 * so the default store costs nothing to depend on.
 *
 * The interface is what matters for hosts who disagree: an app that already uses DataStore can
 * implement [RecentCountryStore] over its own store in a dozen lines and pass it in. Nothing in the
 * library depends on this class.
 *
 * All disk work happens on [Dispatchers.IO]; reads after construction are served from memory, so the
 * recents section renders on the first frame instead of appearing a moment later.
 */
public class DefaultRecentCountryStore internal constructor(
    private val storage: RecentCountryStorage,
    private val maxEntries: Int = DEFAULT_MAX_ENTRIES,
) : RecentCountryStore {

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
        withContext(Dispatchers.IO) { storage.write(updated.joinToString(SEPARATOR)) }
    }

    override suspend fun clear() {
        recents.value = emptyList()
        withContext(Dispatchers.IO) { storage.write(null) }
    }

    /**
     * Reads the stored codes, dropping any that no longer resolve to a country.
     *
     * Runs synchronously in the constructor: this is a single small key-value read, and doing it
     * eagerly is what lets [observeRecentCountryCodes] emit real data on its first emission.
     */
    private fun readFromDisk(): List<String> =
        storage.read()
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

    public companion object {
        public const val DEFAULT_MAX_ENTRIES: Int = 5

        /**
         * Default storage name: the `SharedPreferences` file on Android, the `NSUserDefaults` key
         * prefix on iOS. Override it to isolate per-user or per-flow recents.
         */
        public const val DEFAULT_FILE_NAME: String = "ccp_recent_countries"

        internal const val KEY_RECENTS = "recent_country_codes"
        private const val SEPARATOR = ","
    }
}

/**
 * Creates and remembers the platform's persistent [DefaultRecentCountryStore].
 *
 * @param maxEntries How many codes to keep. Older entries fall off the end.
 * @param fileName Storage name; override to isolate per-user or per-flow recents.
 */
@Composable
public fun rememberDefaultRecentCountryStore(
    maxEntries: Int = DefaultRecentCountryStore.DEFAULT_MAX_ENTRIES,
    fileName: String = DefaultRecentCountryStore.DEFAULT_FILE_NAME,
): RecentCountryStore {
    val storage = rememberPlatformRecentCountryStorage(fileName)
    return remember(storage, maxEntries) { DefaultRecentCountryStore(storage, maxEntries) }
}

/** One persisted string — the comma-separated codes — in a platform key-value store. */
internal interface RecentCountryStorage {
    fun read(): String?

    /** Stores [value], or removes the entry when it is `null`. */
    fun write(value: String?)
}

/** The current platform's [RecentCountryStorage] for [fileName], stable across recompositions. */
@Composable
internal expect fun rememberPlatformRecentCountryStorage(fileName: String): RecentCountryStorage

/**
 * An in-memory [RecentCountryStore] that survives recomposition but not process death.
 *
 * Useful for previews, tests, and hosts that want the recents section without persisting anything.
 */
public class InMemoryRecentCountryStore(
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
