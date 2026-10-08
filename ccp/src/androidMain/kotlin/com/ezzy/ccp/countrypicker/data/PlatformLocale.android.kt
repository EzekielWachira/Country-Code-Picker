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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.intl.Locale
import com.ezzy.ccp.countrypicker.model.Country
import java.text.Collator

internal actual fun platformDefaultLocale(): Locale = java.util.Locale.getDefault().toComposeLocale()

internal actual fun platformCountryName(iso2Code: String, locale: Locale): String? =
    java.util.Locale.Builder()
        .setRegion(iso2Code)
        .build()
        .getDisplayCountry(locale.toJavaLocale())

internal actual fun platformCollator(locale: Locale): Comparator<String> {
    val collator = Collator.getInstance(locale.toJavaLocale()).apply { strength = Collator.PRIMARY }
    return Comparator { a, b -> collator.compare(a, b) }
}

@Composable
@ReadOnlyComposable
internal actual fun compositionLocale(): Locale = LocalConfiguration.current.locales[0].toComposeLocale()

private fun java.util.Locale.toComposeLocale(): Locale = Locale(toLanguageTag())

private fun Locale.toJavaLocale(): java.util.Locale = java.util.Locale.forLanguageTag(toLanguageTag())

// ── java.util.Locale overloads ────────────────────────────────────────────────────────────────
//
// The common API takes Compose's multiplatform Locale. Android code that already holds a
// java.util.Locale (Locale.getDefault(), a Configuration's locales) keeps passing it directly.

/** [DefaultCountryDataSource.findByIso2] for a `java.util.Locale`. */
public fun DefaultCountryDataSource.findByIso2(code: String?, locale: java.util.Locale): Country? =
    findByIso2(code, locale.toComposeLocale())

/** [DefaultCountryDataSource.defaultInitialCountry] for a `java.util.Locale`. */
public fun DefaultCountryDataSource.defaultInitialCountry(locale: java.util.Locale): Country =
    defaultInitialCountry(locale.toComposeLocale())

/** [DefaultCountryDataSource.localizedNames] for a `java.util.Locale`. */
public fun DefaultCountryDataSource.localizedNames(locale: java.util.Locale): List<Country> =
    localizedNames(locale.toComposeLocale())
