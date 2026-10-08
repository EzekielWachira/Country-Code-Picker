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
import com.ezzy.ccp.internal.libphonenumber.CCPNBAsYouTypeFormatter
import com.ezzy.ccp.internal.libphonenumber.CCPNBPhoneNumber
import com.ezzy.ccp.internal.libphonenumber.CCPNBPhoneNumberUtil
import com.ezzy.ccp.internal.libphonenumber.NBEPhoneNumberFormatE164
import com.ezzy.ccp.internal.libphonenumber.NBEPhoneNumberFormatINTERNATIONAL
import com.ezzy.ccp.internal.libphonenumber.NBEPhoneNumberFormatNATIONAL
import com.ezzy.ccp.internal.libphonenumber.NBEPhoneNumberTypeFIXED_LINE
import com.ezzy.ccp.internal.libphonenumber.NBEPhoneNumberTypeFIXED_LINE_OR_MOBILE
import com.ezzy.ccp.internal.libphonenumber.NBEPhoneNumberTypeMOBILE
import com.ezzy.ccp.internal.libphonenumber.NBEPhoneNumberTypePAGER
import com.ezzy.ccp.internal.libphonenumber.NBEPhoneNumberTypePERSONAL_NUMBER
import com.ezzy.ccp.internal.libphonenumber.NBEPhoneNumberTypePREMIUM_RATE
import com.ezzy.ccp.internal.libphonenumber.NBEPhoneNumberTypeSHARED_COST
import com.ezzy.ccp.internal.libphonenumber.NBEPhoneNumberTypeTOLL_FREE
import com.ezzy.ccp.internal.libphonenumber.NBEPhoneNumberTypeUAN
import com.ezzy.ccp.internal.libphonenumber.NBEPhoneNumberTypeVOICEMAIL
import com.ezzy.ccp.internal.libphonenumber.NBEPhoneNumberTypeVOIP
import com.ezzy.ccp.internal.libphonenumber.NBEValidationResultINVALID_COUNTRY_CODE
import com.ezzy.ccp.internal.libphonenumber.NBEValidationResultIS_POSSIBLE
import com.ezzy.ccp.internal.libphonenumber.NBEValidationResultIS_POSSIBLE_LOCAL_ONLY
import com.ezzy.ccp.internal.libphonenumber.NBEValidationResultTOO_LONG
import com.ezzy.ccp.internal.libphonenumber.NBEValidationResultTOO_SHORT

internal actual typealias PlatformPhoneNumber = CCPNBPhoneNumber

/**
 * libPhoneNumber-iOS, the Objective-C port of Google's libphonenumber, vendored and compiled into
 * this library under a `CCP` symbol prefix (see src/nativeInterop). Its shared instance guards its
 * caches with locks, so it is safe to call from any thread.
 *
 * Error out-parameters are passed as `null`: every call that can fail also signals failure through
 * its return value (`nil` / `UNKNOWN`), which is what is mapped here.
 */
internal actual object PhoneNumberBackend {

    private val util: CCPNBPhoneNumberUtil get() = CCPNBPhoneNumberUtil.sharedInstance()

    actual fun parse(number: String, defaultRegion: String?): PlatformPhoneNumber? =
        util.parse(number, defaultRegion = defaultRegion, error = null)

    actual fun isValidNumber(number: PlatformPhoneNumber): Boolean = util.isValidNumber(number)

    actual fun isValidNumberForRegion(number: PlatformPhoneNumber, regionCode: String): Boolean =
        util.isValidNumberForRegion(number, regionCode = regionCode)

    actual fun possibility(number: PlatformPhoneNumber): NumberPossibility =
        when (util.isPossibleNumberWithReason(number, error = null)) {
            NBEValidationResultIS_POSSIBLE -> NumberPossibility.IsPossible
            NBEValidationResultIS_POSSIBLE_LOCAL_ONLY -> NumberPossibility.IsPossibleLocalOnly
            NBEValidationResultINVALID_COUNTRY_CODE -> NumberPossibility.InvalidCountryCode
            NBEValidationResultTOO_SHORT -> NumberPossibility.TooShort
            NBEValidationResultTOO_LONG -> NumberPossibility.TooLong
            else -> NumberPossibility.InvalidLength
        }

    actual fun numberType(number: PlatformPhoneNumber): PhoneNumberType = when (util.getNumberType(number)) {
        NBEPhoneNumberTypeFIXED_LINE -> PhoneNumberType.FixedLine
        NBEPhoneNumberTypeMOBILE -> PhoneNumberType.Mobile
        NBEPhoneNumberTypeFIXED_LINE_OR_MOBILE -> PhoneNumberType.FixedLineOrMobile
        NBEPhoneNumberTypeTOLL_FREE -> PhoneNumberType.TollFree
        NBEPhoneNumberTypePREMIUM_RATE -> PhoneNumberType.PremiumRate
        NBEPhoneNumberTypeSHARED_COST -> PhoneNumberType.SharedCost
        NBEPhoneNumberTypeVOIP -> PhoneNumberType.Voip
        NBEPhoneNumberTypePERSONAL_NUMBER -> PhoneNumberType.PersonalNumber
        NBEPhoneNumberTypePAGER -> PhoneNumberType.Pager
        NBEPhoneNumberTypeUAN -> PhoneNumberType.Uan
        NBEPhoneNumberTypeVOICEMAIL -> PhoneNumberType.Voicemail
        else -> PhoneNumberType.Unknown
    }

    actual fun format(number: PlatformPhoneNumber, style: PhoneNumberStyle): String = util.format(
        number,
        numberFormat = when (style) {
            PhoneNumberStyle.E164 -> NBEPhoneNumberFormatE164
            PhoneNumberStyle.International -> NBEPhoneNumberFormatINTERNATIONAL
            PhoneNumberStyle.National -> NBEPhoneNumberFormatNATIONAL
        },
        error = null,
    ).orEmpty()

    actual fun regionCodeForNumber(number: PlatformPhoneNumber): String? = util.getRegionCodeForNumber(number)

    actual fun countryCallingCode(number: PlatformPhoneNumber): Int = number.countryCode?.intValue ?: 0

    actual fun nationalSignificantNumber(number: PlatformPhoneNumber): String =
        util.getNationalSignificantNumber(number)

    actual fun exampleNumber(regionCode: String): PlatformPhoneNumber? =
        util.getExampleNumber(regionCode, error = null)

    actual fun exampleMobileNumber(regionCode: String): PlatformPhoneNumber? =
        util.getExampleNumberForType(regionCode, type = NBEPhoneNumberTypeMOBILE, error = null)

    actual fun nationalPrefix(regionCode: String): String? =
        util.getNddPrefixForRegion(regionCode, stripNonDigits = true)

    actual fun countryCodeForRegion(regionCode: String): Int = util.getCountryCodeForRegion(regionCode).intValue

    actual fun formatAsYouType(input: String, regionCode: String): String? {
        if (countryCodeForRegion(regionCode) == 0) return null
        val formatter = CCPNBAsYouTypeFormatter(regionCode = regionCode)
        var result = ""
        for (char in input) result = formatter.inputDigit(char.toString()).orEmpty()
        return result
    }
}
