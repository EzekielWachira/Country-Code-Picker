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
import platform.Foundation.NSUserDefaults

/**
 * Creates a [DefaultRecentCountryStore] backed by the app's standard `NSUserDefaults`.
 *
 * @param maxEntries How many codes to keep. Older entries fall off the end.
 * @param fileName Prefix of the defaults key; override to isolate per-user or per-flow recents.
 */
public fun DefaultRecentCountryStore(
    maxEntries: Int = DefaultRecentCountryStore.DEFAULT_MAX_ENTRIES,
    fileName: String = DefaultRecentCountryStore.DEFAULT_FILE_NAME,
): DefaultRecentCountryStore = DefaultRecentCountryStore(UserDefaultsStorage(fileName), maxEntries)

@Composable
internal actual fun rememberPlatformRecentCountryStorage(fileName: String): RecentCountryStorage =
    remember(fileName) { UserDefaultsStorage(fileName) }

private class UserDefaultsStorage(fileName: String) : RecentCountryStorage {

    private val defaults = NSUserDefaults.standardUserDefaults
    private val key = "$fileName.${DefaultRecentCountryStore.KEY_RECENTS}"

    override fun read(): String? = defaults.stringForKey(key)

    override fun write(value: String?) {
        if (value == null) defaults.removeObjectForKey(key) else defaults.setObject(value, forKey = key)
    }
}
