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

package com.ezzy.ccp.countrypicker.phone

import com.ezzy.ccp.countrypicker.model.PhoneNumberType

/**
 * A number parsed by the platform engine, in that engine's own representation:
 * `com.google.i18n.phonenumbers.Phonenumber.PhoneNumber` on Android and libPhoneNumber-iOS's
 * `NBPhoneNumber` on iOS. Only ever handed back to [PhoneNumberBackend].
 */
internal expect class PlatformPhoneNumber

/** The three output formats the library uses. */
internal enum class PhoneNumberStyle { E164, International, National }

/** libphonenumber's `ValidationResult`, the reason [PhoneNumberBackend.possibility] reports. */
internal enum class NumberPossibility {
    IsPossible,
    IsPossibleLocalOnly,
    InvalidCountryCode,
    TooShort,
    InvalidLength,
    TooLong,
}

/**
 * The slice of libphonenumber this library uses, behind one seam.
 *
 * Both actuals are ports of the *same* Google library generated from the *same* metadata release —
 * the Java original on Android, libPhoneNumber-iOS on iOS — so this is a thin translation layer,
 * not two implementations that happen to agree. Every function is total: engine failures
 * (unparseable input, unknown region) come back as `null` or a neutral value rather than as an
 * exception, so callers in composition never have to guard.
 *
 * Kept deliberately small: [PhoneNumberFormatter] and [PhoneNumberValidator] are the public surface
 * and hold all the policy; this only answers questions about numbers.
 */
internal expect object PhoneNumberBackend {

    /** Parses [number], read in [defaultRegion] when it carries no `+` prefix. */
    fun parse(number: String, defaultRegion: String?): PlatformPhoneNumber?

    fun isValidNumber(number: PlatformPhoneNumber): Boolean

    fun isValidNumberForRegion(number: PlatformPhoneNumber, regionCode: String): Boolean

    /** Whether the length is plausible, and if not, how it is wrong. */
    fun possibility(number: PlatformPhoneNumber): NumberPossibility

    fun numberType(number: PlatformPhoneNumber): PhoneNumberType

    fun format(number: PlatformPhoneNumber, style: PhoneNumberStyle): String

    /** The region the number belongs to, or `null` when its calling code is not a single region. */
    fun regionCodeForNumber(number: PlatformPhoneNumber): String?

    fun countryCallingCode(number: PlatformPhoneNumber): Int

    fun nationalSignificantNumber(number: PlatformPhoneNumber): String

    /** The region's general example number, or `null` when the region has no metadata. */
    fun exampleNumber(regionCode: String): PlatformPhoneNumber?

    /** The region's example *mobile* number, or `null` when it has none. */
    fun exampleMobileNumber(regionCode: String): PlatformPhoneNumber?

    /** The region's national (trunk) prefix with non-digits stripped, e.g. `"0"`; `null` if none. */
    fun nationalPrefix(regionCode: String): String?

    /** The calling code for [regionCode], or `0` when the region is unknown. */
    fun countryCodeForRegion(regionCode: String): Int

    /**
     * Feeds [input] one character at a time through a fresh as-you-type formatter for
     * [regionCode] and returns its final output, or `null` when the engine rejects the region.
     *
     * A fresh formatter per call: the formatter is stateful, so a shared instance would be
     * corrupted by concurrent fields.
     */
    fun formatAsYouType(input: String, regionCode: String): String?
}
