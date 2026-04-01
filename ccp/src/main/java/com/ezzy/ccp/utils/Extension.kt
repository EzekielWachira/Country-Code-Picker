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

import com.ezzy.ccp.data.countryList
import com.ezzy.ccp.model.Country
import com.ezzy.ccp.model.PhoneValidationResult
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil

fun String.isPhoneNumberValid(countryCode: String): Boolean {
    val phoneUtil = PhoneNumberUtil.getInstance()
    return try {
        val numberProto = phoneUtil.parse(this, countryCode)
        phoneUtil.isValidNumber(numberProto)
    } catch (e: NumberParseException) {
        false
    }
}

fun formatAndValidatePhone(phone: String, countryCode: String): PhoneValidationResult {
    val phoneUtil = PhoneNumberUtil.getInstance()
    return try {
        val number = phoneUtil.parse(phone, countryCode)
        val isValid = phoneUtil.isValidNumber(number)
        val formattedNumber = phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
        val unformattedNumber = phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.E164)
        val formattedWithoutCountryCode = phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.NATIONAL)
        PhoneValidationResult(formattedNumber, unformattedNumber, formattedWithoutCountryCode, isValid)
    } catch (e: Exception) {
        PhoneValidationResult(phone, phone, phone, false)
    }
}

fun parsePhoneNumber(phone: String): Pair<Country?, String> {
    val phoneUtil = PhoneNumberUtil.getInstance()
    return try {
        val number = phoneUtil.parse(phone, null)
        val countryCode = number.countryCode
        val country = countryList.find { it.dialCode == "+$countryCode" }
        val localNumber = phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.NATIONAL).trim()
        Pair(country, localNumber)
    } catch (e: Exception) {
        Pair(null, phone)
    }
}

/**
 * Returns the maximum number of digits expected in the national part of a phone number for
 * [countryCode], derived from libphonenumber's example number for that country.
 * Falls back to 15 (ITU-T E.164 max) if the country code is unknown.
 */
fun getMaxPhoneLength(countryCode: String): Int {
    return try {
        val phoneUtil = PhoneNumberUtil.getInstance()
        val example = phoneUtil.getExampleNumber(countryCode)
        phoneUtil.format(example, PhoneNumberUtil.PhoneNumberFormat.NATIONAL)
            .filter { it.isDigit() }.length
    } catch (e: Exception) {
        15
    }
}

fun String?.countryToFlagEmoji(): String? {
    return this?.uppercase()
        ?.map { char ->
            Character.toChars(0x1F1E6 + (char.code - 'A'.code)).concatToString()
        }
        ?.joinToString("")
}
