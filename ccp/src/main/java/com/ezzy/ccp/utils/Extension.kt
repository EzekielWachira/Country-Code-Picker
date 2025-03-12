package com.ezzy.ccp.utils

import com.ezzy.ccp.data.countryList
import com.ezzy.ccp.model.Country
import com.ezzy.ccp.model.PhoneValidationResult
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil

/**
 * Validates if a phone number is valid for the given country code.
 *
 * Uses the PhoneNumberUtil library to parse and validate the phone number
 * against the specified country code. Returns false if parsing fails or
 * if the number is not valid for the given country.
 *
 * @param countryCode The ISO 3166-1 two-letter country code (e.g., "US", "GB", "KE")
 * @return true if the phone number is valid for the given country, false otherwise
 */
fun String.isPhoneNumberValid(countryCode: String): Boolean {
    val phoneUtil = PhoneNumberUtil.getInstance()
    return try {
        val numberProto = phoneUtil.parse(this, countryCode)
        phoneUtil.isValidNumber(numberProto)
    } catch (e: NumberParseException) {
        false
    }
}

/**
 * Formats and validates a phone number using the libphonenumber library.
 *
 * @param phone The phone number to format and validate
 * @param countryCode The ISO 3166-1 two-letter country code
 * @return A [PhoneValidationResult] containing:
 *  - formattedNumber: The phone number in international format (e.g., +254 724 154 958)
 *  - unformattedNumber: The phone number in E.164 format (e.g., +254724154958)
 *  - formattedWithoutCountryCode: The phone number in national format (e.g., 0724 154 958)
 *  - isValid: Whether the phone number is valid for the given country
 */
fun formatAndValidatePhone(phone: String, countryCode: String): PhoneValidationResult {
    val phoneUtil = PhoneNumberUtil.getInstance()
    return try {
        val number = phoneUtil.parse(phone, countryCode)
        val isValid = phoneUtil.isValidNumber(number)

        val formattedNumber = phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL) // +254 724 154 958
        val unformattedNumber = phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.E164) // +254724154958
        val formattedWithoutCountryCode = phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.NATIONAL) // 0724 154 958

        PhoneValidationResult(formattedNumber, unformattedNumber, formattedWithoutCountryCode, isValid)
    } catch (e: Exception) {
        e.printStackTrace()
        PhoneValidationResult(phone, phone, phone, false) // Return input as-is if parsing fails
    }
}


/**
 * Parses a given phone number to extract the country and local number.
 *
 * This function attempts to determine the country associated with a phone number
 * and format the local part of the number according to the country's conventions.
 *
 * @param phone The phone number to parse, which can be in any format 
 *              (with or without country code, with or without formatting)
 * @return A [Pair] containing:
 *         - First: The [Country] object if the country code could be determined, or null if parsing failed
 *         - Second: The local number formatted according to national conventions,
 *                  or the original input if parsing failed
 */
fun parsePhoneNumber(phone: String): Pair<Country?, String> {
    val phoneUtil = PhoneNumberUtil.getInstance()
    return try {
        val number = phoneUtil.parse(phone, null)
        val countryCode = number.countryCode
        val country = countryList.find { it.dialCode == "+$countryCode" }
        val localNumber = phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.NATIONAL).trim()
        Pair(country, localNumber)
    } catch (e: Exception) {
        e.printStackTrace()
        Pair(null, phone) // Return input as-is if parsing fails
    }
}