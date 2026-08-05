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

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Recent-country ordering, deduplication and capping.
 *
 * Exercised through [InMemoryRecentCountryStore], which shares the ordering contract with the
 * file-backed default but needs no Android context — so these run as plain JVM tests.
 */
class RecentCountryStoreTest {

    private suspend fun RecentCountryStore.codes(): List<String> =
        observeRecentCountryCodes().first()

    @Test
    fun `a new store is empty`() = runTest {
        assertTrue(InMemoryRecentCountryStore().codes().isEmpty())
    }

    @Test
    fun `the most recent selection comes first`() = runTest {
        val store = InMemoryRecentCountryStore()
        store.recordSelection("KE")
        store.recordSelection("DE")
        store.recordSelection("FR")
        assertEquals(listOf("FR", "DE", "KE"), store.codes())
    }

    @Test
    fun `reselecting a country moves it to the front instead of duplicating it`() = runTest {
        val store = InMemoryRecentCountryStore()
        store.recordSelection("KE")
        store.recordSelection("DE")
        store.recordSelection("KE")

        val codes = store.codes()
        assertEquals(listOf("KE", "DE"), codes)
        assertEquals("No duplicates allowed", codes.size, codes.distinct().size)
    }

    @Test
    fun `the list is capped and oldest entries fall off`() = runTest {
        val store = InMemoryRecentCountryStore(maxEntries = 3)
        listOf("KE", "DE", "FR", "JP", "US").forEach { store.recordSelection(it) }
        assertEquals(listOf("US", "JP", "FR"), store.codes())
    }

    @Test
    fun `codes are normalised so casing cannot create duplicates`() = runTest {
        val store = InMemoryRecentCountryStore()
        store.recordSelection("ke")
        store.recordSelection("KE")
        assertEquals(listOf("KE"), store.codes())
    }

    @Test
    fun `a non-iso alias normalises to its canonical code`() = runTest {
        val store = InMemoryRecentCountryStore()
        store.recordSelection("UK")
        assertEquals(listOf("GB"), store.codes())
    }

    @Test
    fun `clear forgets everything`() = runTest {
        val store = InMemoryRecentCountryStore()
        store.recordSelection("KE")
        store.clear()
        assertTrue(store.codes().isEmpty())
    }

    @Test
    fun `an initial list is respected and capped`() = runTest {
        val store = InMemoryRecentCountryStore(initial = listOf("KE", "DE", "FR"), maxEntries = 2)
        assertEquals(listOf("KE", "DE"), store.codes())
    }

    @Test
    fun `the no-op store never records anything`() = runTest {
        // The default, so the library writes nothing to disk unless the host opts in.
        NoOpRecentCountryStore.recordSelection("KE")
        assertTrue(NoOpRecentCountryStore.codes().isEmpty())
    }
}
