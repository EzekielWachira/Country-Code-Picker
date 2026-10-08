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

/**
 * The locale the bundled data source names countries in when the caller does not pass one:
 * `java.util.Locale.getDefault()` on Android, the user's preferred language on iOS.
 */
internal expect fun platformDefaultLocale(): Locale

/**
 * The platform's name for the region [iso2Code] in [locale] — ICU's `getDisplayCountry` on Android,
 * `NSLocale.localizedStringForCountryCode` on iOS — or `null` when the platform has no translation.
 *
 * Both platforms echo the region code back when they have nothing better ("KE"), which callers
 * must treat as "no translation" rather than show to a user.
 */
internal expect fun platformCountryName(iso2Code: String, locale: Locale): String?

/**
 * A primary-strength, locale-aware comparator for display names: accents and case are ignored, and
 * letters sort where a speaker of [locale] expects them ("Åland" under A, not after "Zimbabwe").
 */
internal expect fun platformCollator(locale: Locale): Comparator<String>

/**
 * The locale of the current composition — on Android the configuration locale, which follows a
 * per-app language change (`AppCompatDelegate.setApplicationLocales`) even when nothing is
 * recreated; on iOS the preferred language. Reading it in composition is what makes a language
 * change reload the localized country names.
 */
@Composable
@ReadOnlyComposable
internal expect fun compositionLocale(): Locale
