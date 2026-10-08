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
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber

internal actual typealias PlatformPhoneNumber = Phonenumber.PhoneNumber

/** Google libphonenumber. [PhoneNumberUtil] is an immutable, thread-safe singleton. */
internal actual object PhoneNumberBackend {

    private val util: PhoneNumberUtil get() = PhoneNumberUtil.getInstance()

    actual fun parse(number: String, defaultRegion: String?): PlatformPhoneNumber? = try {
        util.parse(number, defaultRegion)
    } catch (_: NumberParseException) {
        null
    } catch (_: IllegalArgumentException) {
        null
    }

    actual fun isValidNumber(number: PlatformPhoneNumber): Boolean = util.isValidNumber(number)

    actual fun isValidNumberForRegion(number: PlatformPhoneNumber, regionCode: String): Boolean =
        util.isValidNumberForRegion(number, regionCode)

    actual fun possibility(number: PlatformPhoneNumber): NumberPossibility =
        when (util.isPossibleNumberWithReason(number)) {
            PhoneNumberUtil.ValidationResult.IS_POSSIBLE -> NumberPossibility.IsPossible
            PhoneNumberUtil.ValidationResult.IS_POSSIBLE_LOCAL_ONLY -> NumberPossibility.IsPossibleLocalOnly
            PhoneNumberUtil.ValidationResult.INVALID_COUNTRY_CODE -> NumberPossibility.InvalidCountryCode
            PhoneNumberUtil.ValidationResult.TOO_SHORT -> NumberPossibility.TooShort
            PhoneNumberUtil.ValidationResult.INVALID_LENGTH -> NumberPossibility.InvalidLength
            PhoneNumberUtil.ValidationResult.TOO_LONG -> NumberPossibility.TooLong
            null -> NumberPossibility.InvalidLength
        }

    actual fun numberType(number: PlatformPhoneNumber): PhoneNumberType = when (util.getNumberType(number)) {
        PhoneNumberUtil.PhoneNumberType.FIXED_LINE -> PhoneNumberType.FixedLine
        PhoneNumberUtil.PhoneNumberType.MOBILE -> PhoneNumberType.Mobile
        PhoneNumberUtil.PhoneNumberType.FIXED_LINE_OR_MOBILE -> PhoneNumberType.FixedLineOrMobile
        PhoneNumberUtil.PhoneNumberType.TOLL_FREE -> PhoneNumberType.TollFree
        PhoneNumberUtil.PhoneNumberType.PREMIUM_RATE -> PhoneNumberType.PremiumRate
        PhoneNumberUtil.PhoneNumberType.SHARED_COST -> PhoneNumberType.SharedCost
        PhoneNumberUtil.PhoneNumberType.VOIP -> PhoneNumberType.Voip
        PhoneNumberUtil.PhoneNumberType.PERSONAL_NUMBER -> PhoneNumberType.PersonalNumber
        PhoneNumberUtil.PhoneNumberType.PAGER -> PhoneNumberType.Pager
        PhoneNumberUtil.PhoneNumberType.UAN -> PhoneNumberType.Uan
        PhoneNumberUtil.PhoneNumberType.VOICEMAIL -> PhoneNumberType.Voicemail
        PhoneNumberUtil.PhoneNumberType.UNKNOWN, null -> PhoneNumberType.Unknown
    }

    actual fun format(number: PlatformPhoneNumber, style: PhoneNumberStyle): String = util.format(
        number,
        when (style) {
            PhoneNumberStyle.E164 -> PhoneNumberUtil.PhoneNumberFormat.E164
            PhoneNumberStyle.International -> PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL
            PhoneNumberStyle.National -> PhoneNumberUtil.PhoneNumberFormat.NATIONAL
        },
    )

    actual fun regionCodeForNumber(number: PlatformPhoneNumber): String? = util.getRegionCodeForNumber(number)

    actual fun countryCallingCode(number: PlatformPhoneNumber): Int = number.countryCode

    actual fun nationalSignificantNumber(number: PlatformPhoneNumber): String =
        util.getNationalSignificantNumber(number)

    actual fun exampleNumber(regionCode: String): PlatformPhoneNumber? = util.getExampleNumber(regionCode)

    actual fun exampleMobileNumber(regionCode: String): PlatformPhoneNumber? =
        util.getExampleNumberForType(regionCode, PhoneNumberUtil.PhoneNumberType.MOBILE)

    actual fun nationalPrefix(regionCode: String): String? = try {
        util.getNddPrefixForRegion(regionCode, true)
    } catch (_: RuntimeException) {
        null
    }

    actual fun countryCodeForRegion(regionCode: String): Int = util.getCountryCodeForRegion(regionCode)

    actual fun formatAsYouType(input: String, regionCode: String): String? = try {
        val formatter = util.getAsYouTypeFormatter(regionCode)
        var result = ""
        for (char in input) result = formatter.inputDigit(char)
        result
    } catch (_: IllegalArgumentException) {
        null
    }
}
