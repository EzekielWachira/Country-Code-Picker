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
import androidx.compose.runtime.remember
import com.ezzy.ccp.countrypicker.data.DefaultCountryDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

/**
 * Detects the country from device signals only: SIM → network → locale settings.
 *
 * This is the same ladder the library has always used (previously
 * [com.ezzy.ccp.utils.CountryDetector] on Android), behind the [CountryDetector] interface,
 * reporting which signal won, and off the main thread.
 *
 * ### Why these signals, in this order
 * The SIM's registered country is the strongest available claim about where the user actually is.
 * The attached network is next, and can differ while roaming. The device locale is last and weakest —
 * plenty of people run an English (US) device in Nairobi — so it is used only as a fallback, and
 * reported as `Based on your device language` rather than as a location claim.
 *
 * ### Per platform
 * - **Android** reads `TelephonyManager.simCountryIso` and `networkCountryIso`, then the
 *   configuration locale and the default locale. Create it with `DefaultCountryDetector(context)`.
 * - **iOS** has no public SIM or network country API any more (CoreTelephony's carrier properties
 *   have returned placeholder values since iOS 16), so it goes straight to the user's Region
 *   setting — which on iOS is an explicit choice, and a better signal than an Android locale.
 *   Create it with `DefaultCountryDetector()`.
 *
 * From common code, [rememberDefaultCountryDetector] builds the right one for the platform.
 *
 * **No location permission is requested or required** on either platform.
 */
public class DefaultCountryDetector internal constructor(
    private val signals: PlatformCountrySignals,
    private val fallbackIso2Code: String? = DEFAULT_FALLBACK_ISO2_CODE,
) : CountryDetector {

    override suspend fun detectCountry(): CountryDetectionResult = withContext(Dispatchers.IO) {
        // Telephony reads can touch a system service and block briefly; keep them off the main
        // thread even though they are usually fast.
        validate(signals.simCountry(), CountryDetectionSource.Sim)?.let { return@withContext it }
        validate(signals.networkCountry(), CountryDetectionSource.Network)?.let { return@withContext it }
        signals.localeCountries()
            .firstNotNullOfOrNull { validate(it, CountryDetectionSource.Locale) }
            ?.let { return@withContext it }

        fallbackIso2Code
            ?.let { CountryDetectionResult.detected(it, CountryDetectionSource.Default) }
            ?: CountryDetectionResult.Unavailable
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

    public companion object {
        /** Country reported with [CountryDetectionSource.Default] when no signal resolves. */
        public const val DEFAULT_FALLBACK_ISO2_CODE: String = "US"
    }
}

/**
 * Creates and remembers the platform's [DefaultCountryDetector].
 *
 * @param fallbackIso2Code Country reported with [CountryDetectionSource.Default] when every signal is
 *   blank. Pass `null` to report [CountryDetectionResult.Unavailable] instead of guessing — the right
 *   choice when a wrong default is worse than no default.
 */
@Composable
public fun rememberDefaultCountryDetector(
    fallbackIso2Code: String? = DefaultCountryDetector.DEFAULT_FALLBACK_ISO2_CODE,
): CountryDetector {
    val signals = rememberPlatformCountrySignals()
    return remember(signals, fallbackIso2Code) { DefaultCountryDetector(signals, fallbackIso2Code) }
}

/**
 * The raw, unvalidated device signals [DefaultCountryDetector] ranks. Any of them may be blank or
 * junk; validation and ordering live in the detector, so platforms only report what they see.
 */
internal interface PlatformCountrySignals {
    /** The SIM's registered country, if the platform exposes one. */
    fun simCountry(): String?

    /** The country of the network the device is attached to, if the platform exposes one. */
    fun networkCountry(): String?

    /** Region codes from the device's locale settings, strongest first. */
    fun localeCountries(): List<String?>
}

/** The current platform's [PlatformCountrySignals], stable across recompositions. */
@Composable
internal expect fun rememberPlatformCountrySignals(): PlatformCountrySignals

/**
 * Builds a detector that tries the receiver first and falls back to [fallback] when it yields nothing.
 *
 * The idiomatic way to add a backend IP lookup without giving up the device signals:
 * ```kotlin
 * val detector = CountryDetector { api.geoCountry()?.let { detected(it, Network) } ?: Unavailable }
 *     .withFallback(DefaultCountryDetector(context))
 * ```
 */
public fun CountryDetector.withFallback(fallback: CountryDetector): CountryDetector = CountryDetector {
    when (val result = detectCountry()) {
        is CountryDetectionResult.Detected -> result
        else -> fallback.detectCountry()
    }
}

/** A detector that always reports [iso2Code]. Useful in previews and tests. */
public fun staticCountryDetector(
    iso2Code: String,
    source: CountryDetectionSource = CountryDetectionSource.Default,
): CountryDetector = CountryDetector { CountryDetectionResult.detected(iso2Code, source) }
