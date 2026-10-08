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

import androidx.compose.runtime.Immutable
import com.ezzy.ccp.countrypicker.model.UiText
import com.ezzy.ccp.resources.Res
import com.ezzy.ccp.resources.ccp_detected_from_locale
import com.ezzy.ccp.resources.ccp_detected_from_network
import com.ezzy.ccp.resources.ccp_detected_from_profile
import com.ezzy.ccp.resources.ccp_detected_from_saved
import com.ezzy.ccp.resources.ccp_detected_from_sim
import com.ezzy.ccp.resources.ccp_detected_generic
import org.jetbrains.compose.resources.StringResource

/**
 * Where a detected country came from.
 *
 * Exposed rather than kept internal because the *source* is what makes the detected-country row
 * honest: "Detected from your SIM" is a claim a user can evaluate, whereas a bare "Detected" is not.
 *
 * Ordinal order is the confidence order used by [DefaultCountryDetector]'s fallback ladder — earlier
 * entries are stronger signals.
 */
public enum class CountryDetectionSource(public val labelRes: StringResource) {

    /** The user picked it. The strongest signal; never overridden by detection. */
    UserSelection(Res.string.ccp_detected_generic),

    /** Restored from a previous session's saved choice. */
    SavedSelection(Res.string.ccp_detected_from_saved),

    /** Supplied by the host from the signed-in profile. */
    ActiveProfile(Res.string.ccp_detected_from_profile),

    /** The SIM card's registered country. Strongest device signal. */
    Sim(Res.string.ccp_detected_from_sim),

    /** The network the device is currently attached to, or a host-provided IP lookup. */
    Network(Res.string.ccp_detected_from_network),

    /** The device's configured language/region. Weakest — a language is not a location. */
    Locale(Res.string.ccp_detected_from_locale),

    /** A hardcoded fallback, used when nothing else resolved. */
    Default(Res.string.ccp_detected_generic),
    ;

    /** Supporting text for the detected-country row, e.g. "From your SIM card · tap to change". */
    public val label: UiText get() = UiText.resource(labelRes)
}

/**
 * Outcome of a [CountryDetector] run.
 *
 * [Detected] carries an ISO alpha-2 code rather than a [com.ezzy.ccp.countrypicker.model.Country] so
 * detectors need no access to the dataset — the picker resolves the code against whatever data
 * source is configured, and a code that is filtered out by the allow/exclude rules is simply ignored
 * instead of forcing a disallowed country into the field.
 */
@Immutable
public sealed interface CountryDetectionResult {

    /** Detection has not run yet. */
    public data object Idle : CountryDetectionResult

    /** Detection is in flight. The selector shows its loading treatment. */
    public data object InProgress : CountryDetectionResult

    /**
     * A country was resolved.
     *
     * @property iso2Code ISO alpha-2 code, case-insensitive.
     * @property source Which signal produced it.
     */
    @Immutable
    public data class Detected(
        val iso2Code: String,
        val source: CountryDetectionSource,
    ) : CountryDetectionResult

    /** Nothing could be determined. Not an error worth showing the user. */
    public data object Unavailable : CountryDetectionResult

    public companion object {
        /** Convenience factory for [Detected]. */
        public fun detected(iso2Code: String, source: CountryDetectionSource): CountryDetectionResult =
            Detected(iso2Code, source)
    }
}

/**
 * What the picker should do with a detection result.
 *
 * The default is [ShowBadge]: apply the detected country but say so, which the design shows as the
 * "✓ Detected — From your network · tap to change" row. [AskFirst] is the right choice when the
 * field has legal weight (tax residency, sanctions screening) and a silently prefilled answer would
 * be a liability.
 */
public enum class CountryDetectionBehavior {
    /** Apply the result with no visible explanation. */
    Silent,

    /** Apply the result and show the detected badge and source. */
    ShowBadge,

    /** Do not apply anything; offer the result as a suggestion the user accepts explicitly. */
    AskFirst,
}
