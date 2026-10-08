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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/**
 * Creates a [DefaultRecentCountryStore] backed by a private `SharedPreferences` file.
 *
 * @param context Used once to open the preferences file. Application context is stored, so this is
 *   safe to hold in a singleton.
 * @param maxEntries How many codes to keep. Older entries fall off the end.
 * @param fileName Preferences file name; override to isolate per-user or per-flow recents.
 */
public fun DefaultRecentCountryStore(
    context: Context,
    maxEntries: Int = DefaultRecentCountryStore.DEFAULT_MAX_ENTRIES,
    fileName: String = DefaultRecentCountryStore.DEFAULT_FILE_NAME,
): DefaultRecentCountryStore = DefaultRecentCountryStore(
    storage = SharedPreferencesStorage(context.applicationContext.getSharedPreferences(fileName, Context.MODE_PRIVATE)),
    maxEntries = maxEntries,
)

@Composable
internal actual fun rememberPlatformRecentCountryStorage(fileName: String): RecentCountryStorage {
    val context = LocalContext.current.applicationContext
    return remember(context, fileName) {
        SharedPreferencesStorage(context.getSharedPreferences(fileName, Context.MODE_PRIVATE))
    }
}

private class SharedPreferencesStorage(private val preferences: SharedPreferences) : RecentCountryStorage {

    override fun read(): String? = preferences.getString(DefaultRecentCountryStore.KEY_RECENTS, null)

    override fun write(value: String?) {
        val editor = preferences.edit()
        if (value == null) editor.remove(DefaultRecentCountryStore.KEY_RECENTS)
        else editor.putString(DefaultRecentCountryStore.KEY_RECENTS, value)
        editor.apply()
    }
}
