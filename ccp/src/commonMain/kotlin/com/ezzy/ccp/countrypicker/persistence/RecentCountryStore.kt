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

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Persists the countries a user has recently chosen, so the sheet can offer them first.
 *
 * Only ISO alpha-2 codes cross this boundary — never search queries, and never anything else the
 * user typed. Recording a country the user picked is a preference; recording what they searched for
 * is tracking, and this interface makes the former impossible to confuse with the latter.
 *
 * Hosts that must not persist anything (kiosk, guest, incognito modes) pass
 * [NoOpRecentCountryStore] or set `showRecentlySelected = false` in the config.
 */
public interface RecentCountryStore {

    /**
     * Emits the recent ISO alpha-2 codes, most recent first, whenever they change.
     *
     * Must be deduplicated and ordered. The sheet observes this directly, so an implementation
     * backed by disk should emit its cached value immediately rather than making the section pop in.
     */
    public fun observeRecentCountryCodes(): Flow<List<String>>

    /**
     * Records that the user selected [countryCode], moving it to the front.
     *
     * Implementations must deduplicate — selecting the same country twice should not produce two
     * entries — and must cap the list so it cannot grow without bound.
     */
    public suspend fun recordSelection(countryCode: String)

    /** Forgets everything. Wire this to the host's "clear data" affordance. */
    public suspend fun clear()
}

/**
 * A store that remembers nothing.
 *
 * The default when no store is supplied, so the library never writes to disk unless the host asks
 * for it.
 */
public object NoOpRecentCountryStore : RecentCountryStore {
    private val empty = MutableStateFlow(emptyList<String>())
    override fun observeRecentCountryCodes(): Flow<List<String>> = empty.asStateFlow()
    override suspend fun recordSelection(countryCode: String): Unit = Unit
    override suspend fun clear(): Unit = Unit
}
