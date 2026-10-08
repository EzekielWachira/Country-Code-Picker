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

import com.ezzy.ccp.countrypicker.data.DefaultCountryDataSource
import com.ezzy.ccp.data.countryList
import com.ezzy.ccp.model.Country
import com.ezzy.ccp.countrypicker.model.appendCodePointCompat
import com.ezzy.ccp.countrypicker.phone.PhoneNumberBackend
import com.ezzy.ccp.countrypicker.phone.PhoneNumberStyle
import com.ezzy.ccp.model.PhoneValidationResult

public fun String.isPhoneNumberValid(countryCode: String): Boolean =
    PhoneNumberBackend.parse(this, countryCode)?.let(PhoneNumberBackend::isValidNumber) ?: false

public fun formatAndValidatePhone(phone: String, countryCode: String): PhoneValidationResult {
    val number = PhoneNumberBackend.parse(phone, countryCode)
        ?: return PhoneValidationResult(phone, phone, phone, false)
    return PhoneValidationResult(
        PhoneNumberBackend.format(number, PhoneNumberStyle.International),
        PhoneNumberBackend.format(number, PhoneNumberStyle.E164),
        PhoneNumberBackend.format(number, PhoneNumberStyle.National),
        PhoneNumberBackend.isValidNumber(number),
    )
}

/**
 * Parses an international number into its country and national form.
 *
 * The country is resolved with libphonenumber's `getRegionCodeForNumber`, which inspects the whole
 * number, rather than by matching the calling code against the country list. Matching by calling code
 * is ambiguous: `+1` belongs to 20+ NANP territories and `+44` to four, so a dial-code lookup returns
 * whichever of them happens to come first — and the expanded dataset now contains all of them.
 * Region inference gets it right for cases like `+44 7911 …`, which is a Guernsey range rather than a
 * UK one.
 */
public fun parsePhoneNumber(phone: String): Pair<Country?, String> {
    val number = PhoneNumberBackend.parse(phone, null) ?: return Pair(null, phone)
    val regionCode = PhoneNumberBackend.regionCodeForNumber(number)
    val country = countryList.find { it.code.equals(regionCode, ignoreCase = true) }
        // Falls back to the conventional owner of the calling code when the number itself is not
        // specific enough for libphonenumber to name a region.
        ?: DefaultCountryDataSource.primaryForDialCode(PhoneNumberBackend.countryCallingCode(number).toString())
            ?.let { canonical -> countryList.find { it.code == canonical.iso2Code } }
    val localNumber = PhoneNumberBackend.format(number, PhoneNumberStyle.National).trim()
    return Pair(country, localNumber)
}

/**
 * Returns the maximum number of digits expected in the national part of a phone number for
 * [countryCode], derived from libphonenumber's example number for that country.
 * Falls back to 15 (ITU-T E.164 max) if the country code is unknown.
 */
public fun getMaxPhoneLength(countryCode: String): Int {
    val example = PhoneNumberBackend.exampleNumber(countryCode) ?: return 15
    return PhoneNumberBackend.format(example, PhoneNumberStyle.National)
        .filter { it.isDigit() }.length
}

public fun String?.countryToFlagEmoji(): String? {
    return this?.uppercase()
        ?.map { char ->
            StringBuilder().appendCodePointCompat(0x1F1E6 + (char.code - 'A'.code)).toString()
        }
        ?.joinToString("")
}
