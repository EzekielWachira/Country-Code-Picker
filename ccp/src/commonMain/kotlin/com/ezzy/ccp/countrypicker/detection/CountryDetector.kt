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

/**
 * Resolves the user's likely country.
 *
 * A `fun interface` so a host can supply one inline — commonly to add a backend/IP lookup ahead of
 * the device signals:
 *
 * ```kotlin
 * val detector = CountryDetector {
 *     api.geoCountry()?.let { CountryDetectionResult.detected(it, CountryDetectionSource.Network) }
 *         ?: DefaultCountryDetector(context).detectCountry()
 * }
 * ```
 *
 * ### Rules the library relies on
 * - **No location permission.** Country-level detection never justifies a runtime permission
 *   prompt; SIM, network and locale are enough. Implementations must not request one.
 * - **Never called from composition.** [detectCountry] is `suspend` and is invoked from a
 *   `LaunchedEffect`, so a network-backed implementation is free to block for as long as it needs.
 * - **Detection is a suggestion, not a command.** An explicit user selection always outranks a
 *   detection result; see [com.ezzy.ccp.countrypicker.state.CountryPickerState].
 */
public fun interface CountryDetector {

    /**
     * Returns the detected country, or [CountryDetectionResult.Unavailable] when nothing could be
     * determined. Implementations should not throw; failures are a result, not an exception.
     */
    public suspend fun detectCountry(): CountryDetectionResult
}
