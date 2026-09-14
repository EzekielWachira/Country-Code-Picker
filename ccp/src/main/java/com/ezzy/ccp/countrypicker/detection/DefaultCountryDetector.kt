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

import android.annotation.SuppressLint
import android.content.Context
import android.telephony.TelephonyManager
import com.ezzy.ccp.countrypicker.data.DefaultCountryDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

/**
 * Detects the country from device signals only: SIM → network → configuration locale → default
 * locale.
 *
 * This is the same ladder the library has always used (previously
 * [com.ezzy.ccp.utils.CountryDetector]), now behind the [CountryDetector] interface, reporting which
 * signal won, and off the main thread.
 *
 * ### Why these signals, in this order
 * The SIM's registered country is the strongest available claim about where the user actually is.
 * The attached network is next, and can differ while roaming. The device locale is last and weakest —
 * plenty of people run an English (US) device in Nairobi — so it is used only as a fallback, and
 * reported as `Based on your device language` rather than as a location claim.
 *
 * **No location permission is requested or required.** `simCountryIso` and `networkCountryIso` are
 * permission-free reads.
 *
 * @param context Any context; only [Context.getSystemService] and [Context.getResources] are used, so
 *   an application context is fine and no leak is possible.
 * @param fallbackIso2Code Country reported with [CountryDetectionSource.Default] when every signal is
 *   blank. Pass `null` to report [CountryDetectionResult.Unavailable] instead of guessing — the right
 *   choice when a wrong default is worse than no default.
 */
class DefaultCountryDetector(
    private val context: Context,
    private val fallbackIso2Code: String? = "US",
) : CountryDetector {

    override suspend fun detectCountry(): CountryDetectionResult = withContext(Dispatchers.IO) {
        // TelephonyManager reads can touch the telephony service and block briefly; keep them off
        // the main thread even though they are usually fast.
        val telephony = runCatching {
            context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
        }.getOrNull()

        simCountry(telephony)?.let { return@withContext it }
        networkCountry(telephony)?.let { return@withContext it }
        localeCountry()?.let { return@withContext it }

        fallbackIso2Code
            ?.let { CountryDetectionResult.detected(it, CountryDetectionSource.Default) }
            ?: CountryDetectionResult.Unavailable
    }

    private fun simCountry(telephony: TelephonyManager?): CountryDetectionResult? =
        telephony?.runCatching { simCountryIso }?.getOrNull()
            ?.let { validate(it, CountryDetectionSource.Sim) }

    private fun networkCountry(telephony: TelephonyManager?): CountryDetectionResult? =
        telephony?.runCatching { networkCountryIso }?.getOrNull()
            ?.let { validate(it, CountryDetectionSource.Network) }

    @SuppressLint("LocalContextConfigurationRead")
    private fun localeCountry(): CountryDetectionResult? {
        val configured = runCatching {
            context.resources.configuration.locales[0].country
        }.getOrNull()
        return validate(configured, CountryDetectionSource.Locale)
            ?: validate(Locale.getDefault().country, CountryDetectionSource.Locale)
    }

    /**
     * Accepts a raw code only if it normalizes to a country the dataset actually knows.
     *
     * Without this check a malformed `simCountryIso` (some devices return `""`, `"--"`, or a
     * three-letter code) would propagate as a bogus selection.
     */
    private fun validate(raw: String?, source: CountryDetectionSource): CountryDetectionResult? {
        val code = raw?.takeIf { it.isNotBlank() } ?: return null
        val country = DefaultCountryDataSource.findByIso2(code) ?: return null
        return CountryDetectionResult.detected(country.iso2Code, source)
    }
}

/**
 * Builds a detector that tries the receiver first and falls back to [fallback] when it yields nothing.
 *
 * The idiomatic way to add a backend IP lookup without giving up the device signals:
 * ```kotlin
 * val detector = CountryDetector { api.geoCountry()?.let { detected(it, Network) } ?: Unavailable }
 *     .withFallback(DefaultCountryDetector(context))
 * ```
 */
fun CountryDetector.withFallback(fallback: CountryDetector): CountryDetector = CountryDetector {
    when (val result = detectCountry()) {
        is CountryDetectionResult.Detected -> result
        else -> fallback.detectCountry()
    }
}

/** A detector that always reports [iso2Code]. Useful in previews and tests. */
fun staticCountryDetector(
    iso2Code: String,
    source: CountryDetectionSource = CountryDetectionSource.Default,
): CountryDetector = CountryDetector { CountryDetectionResult.detected(iso2Code, source) }
