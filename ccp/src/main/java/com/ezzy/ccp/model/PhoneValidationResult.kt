package com.ezzy.ccp.model

data class PhoneValidationResult(
    val formattedNumber: String,  // Pretty format: +254 724 154 958
    val unformattedNumber: String, // E164 format: +254724154958
    val formattedWithoutCountryCode: String, // National format: 0724 154 958
    val isValid: Boolean // True or False
)
