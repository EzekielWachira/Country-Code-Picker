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

import com.ezzy.ccp.countrypicker.model.Country
import com.ezzy.ccp.countrypicker.model.CountryPickerBidi
import com.ezzy.ccp.countrypicker.model.PhoneNumberType
import com.ezzy.ccp.countrypicker.model.PhoneNumberValidity
import com.ezzy.ccp.countrypicker.model.PhoneNumberValue
import com.ezzy.ccp.countrypicker.model.UiText
import com.ezzy.ccp.resources.Res
import com.ezzy.ccp.resources.ccp_phone_error_empty
import com.ezzy.ccp.resources.ccp_phone_error_incomplete
import com.ezzy.ccp.resources.ccp_phone_error_invalid
import com.ezzy.ccp.resources.ccp_phone_error_invalid_country_code
import com.ezzy.ccp.resources.ccp_phone_error_requires_landline
import com.ezzy.ccp.resources.ccp_phone_error_requires_mobile
import com.ezzy.ccp.resources.ccp_phone_error_too_long
import com.ezzy.ccp.resources.ccp_phone_error_too_short
import com.ezzy.ccp.resources.ccp_phone_error_unsupported_country
import com.ezzy.ccp.resources.ccp_phone_error_wrong_type
import com.ezzy.ccp.resources.ccp_phone_helper
import com.ezzy.ccp.resources.ccp_phone_helper_no_example
import kotlin.jvm.JvmOverloads

/**
 * Country-aware phone validation on top of libphonenumber.
 *
 * Validation is explicitly **not** a length comparison. libphonenumber knows which number ranges
 * are actually assigned in each region, so `+254 000 000 000` has a perfectly plausible length and
 * is still rejected. The granular [PhoneNumberValidity] result distinguishes "still typing" from
 * "wrong", which is what lets the field stay quiet until the user is actually done.
 *
 * Every function here is pure and cheap — parsing a number is a few hundred microseconds, well
 * inside a frame — so callers do not need to move validation to a background dispatcher for
 * keystroke-level work. [PhoneNumberValidator] holds no state and is safe to call from any thread.
 */
public object PhoneNumberValidator {

    /**
     * Builds the complete [PhoneNumberValue] for [nationalNumber] in [country]: formatted forms,
     * E.164 normalization, and validity — in one pass, so the caller never has to keep several
     * derived strings in sync.
     *
     * @param allowedNumberTypes Line types the caller accepts, or empty to accept any. A number
     *   that is valid but of an excluded type comes back with
     *   [PhoneNumberValidity.WrongNumberType] and `isValid = false`: a landline is not a usable
     *   answer when the next step is sending an SMS to it.
     */
    @JvmOverloads
    public fun evaluate(
        nationalNumber: String,
        country: Country,
        allowedNumberTypes: Set<PhoneNumberType> = emptySet(),
    ): PhoneNumberValue {
        val digits = nationalNumber.filter(Char::isDigit)
        if (digits.isEmpty()) return PhoneNumberValue.empty(country)

        val formattedNational = PhoneNumberFormatter.formatAsYouType(digits, country)
        val parsed = PhoneNumberBackend.parse(digits, country.iso2Code)

        if (parsed == null) {
            return PhoneNumberValue(
                country = country,
                nationalNumber = digits,
                formattedNationalNumber = formattedNational,
                internationalNumber = country.dialCode + " " + formattedNational,
                e164Number = null,
                isPossible = false,
                isValid = false,
                validity = validityWithoutParse(country),
            )
        }

        val isValidForRegion =
            PhoneNumberBackend.isValidNumberForRegion(parsed, country.iso2Code) ||
                PhoneNumberBackend.isValidNumber(parsed)
        val lengthResult = PhoneNumberBackend.possibility(parsed)
        val isPossible = lengthResult == NumberPossibility.IsPossible ||
            lengthResult == NumberPossibility.IsPossibleLocalOnly
        val numberType = PhoneNumberBackend.numberType(parsed)

        // The type gate applies only to numbers that are otherwise valid. Checking it earlier would
        // report "wrong type" for a half-typed number, whose type libphonenumber has to guess from
        // an incomplete prefix and frequently gets wrong.
        val typeAccepted = !isValidForRegion || numberType.satisfiesAny(allowedNumberTypes)
        val isValid = isValidForRegion && typeAccepted

        return PhoneNumberValue(
            country = country,
            nationalNumber = digits,
            formattedNationalNumber = formattedNational,
            internationalNumber = PhoneNumberBackend.format(parsed, PhoneNumberStyle.International),
            e164Number = PhoneNumberBackend.format(parsed, PhoneNumberStyle.E164)
                .takeIf { isValid || isPossible },
            isPossible = isPossible,
            isValid = isValid,
            validity = if (isValidForRegion && !typeAccepted) {
                PhoneNumberValidity.WrongNumberType
            } else {
                validity(isValid, lengthResult, digits, country)
            },
            numberType = numberType,
        )
    }


    /** Convenience predicate for callers that only care whether the number is submittable. */
    @JvmOverloads
    public fun isValid(
        nationalNumber: String,
        country: Country,
        allowedNumberTypes: Set<PhoneNumberType> = emptySet(),
    ): Boolean = evaluate(nationalNumber, country, allowedNumberTypes).isValid

    /**
     * Maps libphonenumber's length verdict plus overall validity onto [PhoneNumberValidity].
     *
     * The interesting case is `TOO_SHORT`: while the user is still typing towards a plausible
     * number we report [PhoneNumberValidity.Incomplete] (no error shown) rather than
     * [PhoneNumberValidity.TooShort] (error shown). The distinction is whether the digits typed so
     * far could still grow into a valid number for the region.
     */
    private fun validity(
        isValid: Boolean,
        lengthResult: NumberPossibility,
        digits: String,
        country: Country,
    ): PhoneNumberValidity = when {
        isValid -> PhoneNumberValidity.Valid
        lengthResult == NumberPossibility.InvalidCountryCode ->
            PhoneNumberValidity.InvalidCountryCode

        lengthResult == NumberPossibility.TooLong -> PhoneNumberValidity.TooLong
        lengthResult == NumberPossibility.TooShort ||
            lengthResult == NumberPossibility.InvalidLength ->
            if (digits.length < PhoneNumberFormatter.expectedNationalDigits(country)) {
                PhoneNumberValidity.Incomplete
            } else {
                PhoneNumberValidity.TooShort
            }

        lengthResult == NumberPossibility.IsPossible ||
            lengthResult == NumberPossibility.IsPossibleLocalOnly ->
            PhoneNumberValidity.Possible

        else -> PhoneNumberValidity.Invalid
    }

    /** Validity when the number could not be parsed at all. */
    private fun validityWithoutParse(country: Country): PhoneNumberValidity =
        if (PhoneNumberBackend.countryCodeForRegion(country.iso2Code) == 0) {
            PhoneNumberValidity.UnsupportedCountry
        } else {
            PhoneNumberValidity.Invalid
        }

    /**
     * The message to show under the field for [value], or `null` when the field should stay quiet.
     *
     * Returns `null` for [PhoneNumberValidity.Empty], [PhoneNumberValidity.Valid] and
     * [PhoneNumberValidity.Incomplete] — nagging a user who is still mid-number is the most common
     * way phone fields feel hostile. Callers that validate on submit should pass
     * `treatIncompleteAsError = true` to surface a message at that point.
     *
     * @param allowedNumberTypes The same restriction passed to [evaluate]. Used only to phrase the
     *   [PhoneNumberValidity.WrongNumberType] message — "enter a mobile number" rather than a
     *   generic rejection.
     */
    @JvmOverloads
    public fun errorMessage(
        value: PhoneNumberValue,
        treatIncompleteAsError: Boolean = false,
        allowedNumberTypes: Set<PhoneNumberType> = emptySet(),
    ): UiText? {
        val country = value.country
        return when (value.validity) {
            PhoneNumberValidity.Empty ->
                if (treatIncompleteAsError) UiText.resource(Res.string.ccp_phone_error_empty) else null

            PhoneNumberValidity.Valid -> null

            PhoneNumberValidity.Incomplete -> if (treatIncompleteAsError) {
                // The digit count both selects the plural form and is substituted into it, so a
                // one-digit region reads "1 digit after +xx" rather than "1 digits after +xx".
                val expectedDigits = PhoneNumberFormatter.expectedNationalDigits(country)
                UiText.plural(
                    Res.plurals.ccp_phone_error_incomplete,
                    expectedDigits,
                    country.displayName,
                    expectedDigits,
                    // The dial code sits inside a translated sentence, which in Arabic is RTL. An
                    // isolate keeps "+254" left-to-right without forcing the sentence around it.
                    CountryPickerBidi.isolateLtr(country.dialCode),
                )
            } else {
                null
            }

            PhoneNumberValidity.TooShort ->
                UiText.resource(Res.string.ccp_phone_error_too_short, country.displayName)

            PhoneNumberValidity.TooLong ->
                UiText.resource(Res.string.ccp_phone_error_too_long, country.displayName)

            PhoneNumberValidity.Possible, PhoneNumberValidity.Invalid ->
                UiText.resource(Res.string.ccp_phone_error_invalid, country.displayName)

            PhoneNumberValidity.InvalidCountryCode ->
                UiText.resource(Res.string.ccp_phone_error_invalid_country_code)

            PhoneNumberValidity.UnsupportedCountry ->
                UiText.resource(Res.string.ccp_phone_error_unsupported_country, country.displayName)

            // Named messages for the two restrictions anyone actually configures. A generic
            // "that type isn't accepted" tells the user nothing they can act on, whereas "enter a
            // mobile number" says exactly what to do.
            PhoneNumberValidity.WrongNumberType -> when {
                allowedNumberTypes == PhoneNumberType.SmsCapable ->
                    UiText.resource(Res.string.ccp_phone_error_requires_mobile)

                allowedNumberTypes == setOf(PhoneNumberType.FixedLine) ->
                    UiText.resource(Res.string.ccp_phone_error_requires_landline)

                else -> UiText.resource(Res.string.ccp_phone_error_wrong_type)
            }
        }
    }

    /**
     * The live helper text under the field: `"Formats live for Kenya · e.g. +254 712 345 678"`.
     *
     * Both the country name and the example come from the selected country, so this string changes
     * the moment the user switches country and never hardcodes a specific region.
     */
    public fun helperText(country: Country): UiText {
        val example = PhoneNumberFormatter.exampleNationalNumber(country)
        return if (example == null) {
            UiText.resource(Res.string.ccp_phone_helper_no_example, country.displayName)
        } else {
            UiText.resource(
                Res.string.ccp_phone_helper,
                country.displayName,
                // Both runs are numbers embedded in a translated sentence; see CountryPickerBidi.
                CountryPickerBidi.isolateLtr(country.dialCode),
                CountryPickerBidi.isolateLtr(example),
            )
        }
    }
}
