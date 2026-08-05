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

import com.ezzy.ccp.R
import com.ezzy.ccp.countrypicker.model.Country
import com.ezzy.ccp.countrypicker.model.PhoneNumberValidity
import com.ezzy.ccp.countrypicker.model.PhoneNumberValue
import com.ezzy.ccp.countrypicker.model.UiText
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil

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
object PhoneNumberValidator {

    private val util: PhoneNumberUtil get() = PhoneNumberUtil.getInstance()

    /**
     * Builds the complete [PhoneNumberValue] for [nationalNumber] in [country]: formatted forms,
     * E.164 normalization, and validity — in one pass, so the caller never has to keep several
     * derived strings in sync.
     */
    fun evaluate(nationalNumber: String, country: Country): PhoneNumberValue {
        val digits = nationalNumber.filter(Char::isDigit)
        if (digits.isEmpty()) return PhoneNumberValue.empty(country)

        val formattedNational = PhoneNumberFormatter.formatAsYouType(digits, country)
        val parsed = try {
            util.parse(digits, country.iso2Code)
        } catch (_: NumberParseException) {
            null
        } catch (_: IllegalArgumentException) {
            null
        }

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

        val isValid = util.isValidNumberForRegion(parsed, country.iso2Code) || util.isValidNumber(parsed)
        val lengthResult = util.isPossibleNumberWithReason(parsed)
        val isPossible = lengthResult == PhoneNumberUtil.ValidationResult.IS_POSSIBLE ||
            lengthResult == PhoneNumberUtil.ValidationResult.IS_POSSIBLE_LOCAL_ONLY

        return PhoneNumberValue(
            country = country,
            nationalNumber = digits,
            formattedNationalNumber = formattedNational,
            internationalNumber = util.format(parsed, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL),
            e164Number = util.format(parsed, PhoneNumberUtil.PhoneNumberFormat.E164)
                .takeIf { isValid || isPossible },
            isPossible = isPossible,
            isValid = isValid,
            validity = validity(isValid, lengthResult, digits, country),
        )
    }

    /** Convenience predicate for callers that only care whether the number is submittable. */
    fun isValid(nationalNumber: String, country: Country): Boolean =
        evaluate(nationalNumber, country).isValid

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
        lengthResult: PhoneNumberUtil.ValidationResult,
        digits: String,
        country: Country,
    ): PhoneNumberValidity = when {
        isValid -> PhoneNumberValidity.Valid
        lengthResult == PhoneNumberUtil.ValidationResult.INVALID_COUNTRY_CODE ->
            PhoneNumberValidity.InvalidCountryCode

        lengthResult == PhoneNumberUtil.ValidationResult.TOO_LONG -> PhoneNumberValidity.TooLong
        lengthResult == PhoneNumberUtil.ValidationResult.TOO_SHORT ||
            lengthResult == PhoneNumberUtil.ValidationResult.INVALID_LENGTH ->
            if (digits.length < PhoneNumberFormatter.expectedNationalDigits(country)) {
                PhoneNumberValidity.Incomplete
            } else {
                PhoneNumberValidity.TooShort
            }

        lengthResult == PhoneNumberUtil.ValidationResult.IS_POSSIBLE ||
            lengthResult == PhoneNumberUtil.ValidationResult.IS_POSSIBLE_LOCAL_ONLY ->
            PhoneNumberValidity.Possible

        else -> PhoneNumberValidity.Invalid
    }

    /** Validity when the number could not be parsed at all. */
    private fun validityWithoutParse(country: Country): PhoneNumberValidity =
        if (util.getCountryCodeForRegion(country.iso2Code) == 0) {
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
     */
    fun errorMessage(
        value: PhoneNumberValue,
        treatIncompleteAsError: Boolean = false,
    ): UiText? {
        val country = value.country
        return when (value.validity) {
            PhoneNumberValidity.Empty ->
                if (treatIncompleteAsError) UiText.resource(R.string.ccp_phone_error_empty) else null

            PhoneNumberValidity.Valid -> null

            PhoneNumberValidity.Incomplete -> if (treatIncompleteAsError) {
                UiText.resource(
                    R.string.ccp_phone_error_incomplete,
                    country.displayName,
                    PhoneNumberFormatter.expectedNationalDigits(country),
                    country.dialCode,
                )
            } else {
                null
            }

            PhoneNumberValidity.TooShort ->
                UiText.resource(R.string.ccp_phone_error_too_short, country.displayName)

            PhoneNumberValidity.TooLong ->
                UiText.resource(R.string.ccp_phone_error_too_long, country.displayName)

            PhoneNumberValidity.Possible, PhoneNumberValidity.Invalid ->
                UiText.resource(R.string.ccp_phone_error_invalid, country.displayName)

            PhoneNumberValidity.InvalidCountryCode ->
                UiText.resource(R.string.ccp_phone_error_invalid_country_code)

            PhoneNumberValidity.UnsupportedCountry ->
                UiText.resource(R.string.ccp_phone_error_unsupported_country, country.displayName)
        }
    }

    /**
     * The live helper text under the field: `"Formats live for Kenya · e.g. +254 712 345 678"`.
     *
     * Both the country name and the example come from the selected country, so this string changes
     * the moment the user switches country and never hardcodes a specific region.
     */
    fun helperText(country: Country): UiText {
        val example = PhoneNumberFormatter.exampleNationalNumber(country)
        return if (example == null) {
            UiText.resource(R.string.ccp_phone_helper_no_example, country.displayName)
        } else {
            UiText.resource(R.string.ccp_phone_helper, country.displayName, country.dialCode, example)
        }
    }
}
