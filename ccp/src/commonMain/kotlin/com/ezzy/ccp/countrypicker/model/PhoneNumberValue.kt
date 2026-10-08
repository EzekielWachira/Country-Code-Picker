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

package com.ezzy.ccp.countrypicker.model

import androidx.compose.runtime.Immutable

/**
 * Everything a caller needs to know about the phone number currently in the field.
 *
 * The point of this type is that callers never build an E.164 string themselves. Concatenating
 * `dialCode + digits` is wrong for any country with a national trunk prefix (a UK mobile typed as
 * `07400 123456` is `+447400123456`, not `+4407400123456`), so [e164Number] is produced by
 * libphonenumber and is `null` — deliberately, not empty-string — whenever the number is not yet
 * parseable.
 *
 * @property country The selected country. Determines the region used for parsing and formatting.
 * @property nationalNumber Digits the user typed, with formatting stripped (e.g. `"712345678"`).
 * @property formattedNationalNumber The same digits grouped for display (e.g. `"0712 345 678"`).
 * @property internationalNumber Human-readable international form (e.g. `"+254 712 345 678"`).
 *   Falls back to the raw input when parsing fails, so it is always safe to display.
 * @property e164Number Storage/API form (e.g. `"+254712345678"`), or `null` when the number cannot
 *   be parsed. **Never** treat `null` as "no number" — check [nationalNumber] for that.
 * @property isPossible libphonenumber considers the length plausible for the region. True before
 *   [isValid] becomes true, which makes it the right signal for "keep typing" affordances.
 * @property isValid libphonenumber considers this a real, dialable number for the region. This is
 *   the only flag that should gate submission or verification.
 * @property validity The granular reason behind [isValid], for precise error messaging.
 * @property numberType What kind of line this is, or `null` when nothing has been typed yet or the
 *   number could not be parsed. Note that many regions report
 *   [PhoneNumberType.FixedLineOrMobile] for every valid number — compare with
 *   [PhoneNumberType.satisfies] rather than by equality.
 */
@Immutable
public data class PhoneNumberValue(
    val country: Country,
    val nationalNumber: String,
    val formattedNationalNumber: String,
    val internationalNumber: String,
    val e164Number: String?,
    val isPossible: Boolean,
    val isValid: Boolean,
    val validity: PhoneNumberValidity = PhoneNumberValidity.Empty,
    val numberType: PhoneNumberType? = null,
) {
    /** True when the user has not typed anything. */
    val isEmpty: Boolean get() = nationalNumber.isEmpty()

    public companion object {
        /** An empty value for [country], used as the initial state of a phone field. */
        public fun empty(country: Country): PhoneNumberValue = PhoneNumberValue(
            country = country,
            nationalNumber = "",
            formattedNationalNumber = "",
            internationalNumber = "",
            e164Number = null,
            isPossible = false,
            isValid = false,
            validity = PhoneNumberValidity.Empty,
            numberType = null,
        )
    }
}

/**
 * Machine-readable validation outcome, kept separate from the user-facing message so hosts can
 * branch on the reason without string matching.
 *
 * This is intentionally finer-grained than a boolean: "too short" while typing should not look like
 * "invalid", and an unsupported region is a configuration problem rather than user error.
 */
public enum class PhoneNumberValidity {
    /** Nothing typed yet. Not an error — do not show a message. */
    Empty,

    /** Plausible prefix but not yet long enough. The normal state while typing. */
    Incomplete,

    /** Shorter than any valid number for the region. */
    TooShort,

    /** Longer than any valid number for the region. */
    TooLong,

    /** Length is plausible for the region but the number is not a valid assigned number. */
    Possible,

    /** A real, dialable number. The only value for which submission should be allowed. */
    Valid,

    /** Parsed, but not a valid number for the region and not merely a length problem. */
    Invalid,

    /** The leading country calling code does not correspond to any region. */
    InvalidCountryCode,

    /** libphonenumber has no metadata for the selected country, so it cannot be validated. */
    UnsupportedCountry,

    /**
     * A real, dialable number — but not one of the types the caller accepts.
     *
     * Distinct from [Invalid] because the remedy is different: the user did not mistype, they gave
     * a landline where the flow needs a mobile, and the message has to say so.
     */
    WrongNumberType,
    ;

    /** True for states where the user should be shown an error rather than left alone. */
    public val isError: Boolean
        get() = this != Empty && this != Valid && this != Incomplete
}
