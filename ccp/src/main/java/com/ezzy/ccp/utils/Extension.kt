package com.ezzy.ccp.utils

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

        val formattedNumber = phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL) // +254 724 154 958
        val unformattedNumber = phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.E164) // +254724154958
        val formattedWithoutCountryCode = phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.NATIONAL) // 0724 154 958

        PhoneValidationResult(formattedNumber, unformattedNumber, formattedWithoutCountryCode, isValid)
    } catch (e: Exception) {
        e.printStackTrace()
        PhoneValidationResult(phone, phone, phone, false) // Return input as-is if parsing fails
    }
}

