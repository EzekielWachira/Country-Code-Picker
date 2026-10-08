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

package com.ezzy.ccp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.telephony.TelephonyManager
import java.util.Locale

/**
 * Detects the user's country from device signals in priority order:
 * SIM country → network country → configuration locale → default locale → "US".
 *
 * Also normalises known ISO oddities (e.g. "UK" → "GB").
 */
public object CountryDetector {

    @SuppressLint("LocalContextConfigurationRead")
    public fun detect(context: Context): String {
        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?

        val simCountry = telephonyManager?.simCountryIso
            ?.takeIf { it.isNotBlank() }?.uppercase(Locale.ROOT)

        val networkCountry = telephonyManager?.networkCountryIso
            ?.takeIf { it.isNotBlank() }?.uppercase(Locale.ROOT)

        val configLocaleCountry = try {
            context.resources.configuration.locales[0].country
                .takeIf { it.isNotBlank() }?.uppercase(Locale.ROOT)
        } catch (e: Exception) {
            null
        }

        val defaultLocaleCountry = Locale.getDefault().country
            .takeIf { it.isNotBlank() }?.uppercase(Locale.ROOT)

        val raw = simCountry ?: networkCountry ?: configLocaleCountry ?: defaultLocaleCountry ?: "US"
        return normalize(raw)
    }

    /** Maps non-standard or ambiguous codes to their canonical ISO 3166-1 alpha-2 form. */
    private fun normalize(code: String): String = when (code) {
        "UK" -> "GB"
        else -> code
    }
}
