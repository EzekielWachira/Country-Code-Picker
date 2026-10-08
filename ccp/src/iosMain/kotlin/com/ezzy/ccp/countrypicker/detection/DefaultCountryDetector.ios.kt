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

package com.ezzy.ccp.countrypicker.detection

import androidx.compose.runtime.Composable
import platform.Foundation.NSLocale
import platform.Foundation.NSLocaleCountryCode
import platform.Foundation.currentLocale

/**
 * Creates a [DefaultCountryDetector] reading the user's Region setting.
 *
 * iOS exposes no usable SIM or network country: CoreTelephony's carrier properties have returned
 * placeholder values since iOS 16. The Region setting (Settings › General › Language & Region) is
 * an explicit user choice, which makes it a better signal than a language-derived locale.
 *
 * @param fallbackIso2Code Country reported with [CountryDetectionSource.Default] when no signal
 *   resolves. Pass `null` to report [CountryDetectionResult.Unavailable] instead of guessing.
 */
public fun DefaultCountryDetector(
    fallbackIso2Code: String? = DefaultCountryDetector.DEFAULT_FALLBACK_ISO2_CODE,
): DefaultCountryDetector = DefaultCountryDetector(IosCountrySignals, fallbackIso2Code)

@Composable
internal actual fun rememberPlatformCountrySignals(): PlatformCountrySignals = IosCountrySignals

private object IosCountrySignals : PlatformCountrySignals {
    override fun simCountry(): String? = null

    override fun networkCountry(): String? = null

    override fun localeCountries(): List<String?> =
        listOf(NSLocale.currentLocale.objectForKey(NSLocaleCountryCode) as? String)
}
