package com.ezzy.ccp.countrypicker.persistence

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * The `SharedPreferences`-backed recents store.
 *
 * The existing [RecentCountryStoreTest] covers the no-op implementation, which by construction
 * cannot exercise any of this: ordering, the cap, normalization, rejection of unknown codes, or the
 * fact that recents survive being reopened.
 */
@RunWith(RobolectricTestRunner::class)
class DefaultRecentCountryStoreTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        // Each test starts from a clean file; Robolectric keeps preferences across tests in a class.
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit().clear().commit()
    }

    private fun store(maxEntries: Int = 5) =
        DefaultRecentCountryStore(context, maxEntries = maxEntries, fileName = FILE)

    private suspend fun DefaultRecentCountryStore.codes(): List<String> =
        observeRecentCountryCodes().first()

    @Test
    fun `the most recent selection comes first`() = runTest {
        val store = store()
        store.recordSelection("KE")
        store.recordSelection("DE")
        store.recordSelection("FR")

        assertEquals(listOf("FR", "DE", "KE"), store.codes())
    }

    @Test
    fun `re-selecting a country moves it to the front instead of duplicating it`() = runTest {
        val store = store()
        store.recordSelection("KE")
        store.recordSelection("DE")
        store.recordSelection("KE")

        assertEquals(listOf("KE", "DE"), store.codes())
    }

    @Test
    fun `the list is capped and the oldest entry falls off`() = runTest {
        val store = store(maxEntries = 3)
        listOf("KE", "DE", "FR", "US").forEach { store.recordSelection(it) }

        assertEquals(listOf("US", "FR", "DE"), store.codes())
    }

    @Test
    fun `codes are normalized before being stored`() = runTest {
        val store = store()
        store.recordSelection("ke")
        // "UK" is not an ISO code; the dataset normalizes it to GB.
        store.recordSelection("uk")

        assertEquals(listOf("GB", "KE"), store.codes())
    }

    @Test
    fun `an unknown code is rejected rather than persisted`() = runTest {
        val store = store()
        store.recordSelection("KE")
        // Persisting junk would leave an entry that can never resolve to a country, so the recents
        // section would silently render one row fewer than it stored, forever.
        store.recordSelection("ZZ")
        store.recordSelection("")
        store.recordSelection("KENYA")

        assertEquals(listOf("KE"), store.codes())
    }

    @Test
    fun `recents survive reopening the store`() = runTest {
        store().recordSelection("KE")
        store().recordSelection("DE")

        // A fresh instance over the same file — the case that matters, since the store is usually
        // constructed once per process and the point of it is surviving process death.
        assertEquals(listOf("DE", "KE"), store().codes())
    }

    @Test
    fun `clear empties both memory and disk`() = runTest {
        val store = store()
        store.recordSelection("KE")
        store.clear()

        assertTrue(store.codes().isEmpty())
        assertTrue(store().codes().isEmpty())
    }

    @Test
    fun `a stored code that is no longer a known country is dropped on read`() = runTest {
        // Simulates a dataset change between app versions: a code that was valid when written is
        // not any more. It must not reach the UI as an unresolvable row.
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .edit()
            .putString("recent_country_codes", "KE,ZZ,DE")
            .commit()

        assertEquals(listOf("KE", "DE"), store().codes())
    }

    private companion object {
        const val FILE = "ccp_recents_test"
    }
}
