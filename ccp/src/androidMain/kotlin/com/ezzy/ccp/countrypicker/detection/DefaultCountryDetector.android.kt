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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

/**
 * Creates a [DefaultCountryDetector] reading SIM → network → configuration locale → default locale.
 *
 * `simCountryIso` and `networkCountryIso` are permission-free reads, so no permission is needed.
 *
 * @param context Any context; only `getSystemService` and `getResources` are used, so an
 *   application context is fine and no leak is possible.
 * @param fallbackIso2Code Country reported with [CountryDetectionSource.Default] when every signal is
 *   blank. Pass `null` to report [CountryDetectionResult.Unavailable] instead of guessing — the right
 *   choice when a wrong default is worse than no default.
 */
public fun DefaultCountryDetector(
    context: Context,
    fallbackIso2Code: String? = DefaultCountryDetector.DEFAULT_FALLBACK_ISO2_CODE,
): DefaultCountryDetector = DefaultCountryDetector(AndroidCountrySignals(context), fallbackIso2Code)

@Composable
internal actual fun rememberPlatformCountrySignals(): PlatformCountrySignals {
    val context = LocalContext.current.applicationContext
    return remember(context) { AndroidCountrySignals(context) }
}

private class AndroidCountrySignals(private val context: Context) : PlatformCountrySignals {

    private fun telephony(): TelephonyManager? = runCatching {
        context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
    }.getOrNull()

    override fun simCountry(): String? = telephony()?.runCatching { simCountryIso }?.getOrNull()

    override fun networkCountry(): String? = telephony()?.runCatching { networkCountryIso }?.getOrNull()

    @SuppressLint("LocalContextConfigurationRead")
    override fun localeCountries(): List<String?> = listOf(
        runCatching { context.resources.configuration.locales[0].country }.getOrNull(),
        Locale.getDefault().country,
    )
}
