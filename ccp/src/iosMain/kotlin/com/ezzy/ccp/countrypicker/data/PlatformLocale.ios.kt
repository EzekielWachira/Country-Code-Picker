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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.text.intl.Locale
import platform.Foundation.NSCaseInsensitiveSearch
import platform.Foundation.NSDiacriticInsensitiveSearch
import platform.Foundation.NSLocale
import platform.Foundation.NSMakeRange
import platform.Foundation.NSString
import platform.Foundation.canonicalLocaleIdentifierFromString
import platform.Foundation.compare
import platform.Foundation.create
import platform.Foundation.localizedStringForCountryCode

/**
 * Compose's `Locale.current`, which on iOS is the user's first preferred language — the same
 * language Compose Multiplatform resolves the library's strings in, so country names and the rest
 * of the sheet always agree.
 */
internal actual fun platformDefaultLocale(): Locale = Locale.current

internal actual fun platformCountryName(iso2Code: String, locale: Locale): String? =
    locale.toNSLocale().localizedStringForCountryCode(iso2Code)

/**
 * `compare:options:range:locale:` with case- and diacritic-insensitive options, Foundation's
 * equivalent of a primary-strength `java.text.Collator`.
 */
internal actual fun platformCollator(locale: Locale): Comparator<String> {
    val nsLocale = locale.toNSLocale()
    return Comparator { a, b ->
        val left = NSString.create(string = a)
        left.compare(
            string = b,
            options = NSCaseInsensitiveSearch or NSDiacriticInsensitiveSearch,
            range = NSMakeRange(0u, left.length),
            locale = nsLocale,
        ).toInt()
    }
}

@Composable
@ReadOnlyComposable
internal actual fun compositionLocale(): Locale = Locale.current

private fun Locale.toNSLocale(): NSLocale =
    NSLocale(localeIdentifier = NSLocale.canonicalLocaleIdentifierFromString(toLanguageTag()))
