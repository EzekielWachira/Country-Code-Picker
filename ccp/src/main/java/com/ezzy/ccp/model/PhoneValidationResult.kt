package com.ezzy.ccp.model

data class PhoneValidationResult(
    val formattedNumber: String,  // Pretty format: +254 712 345 678
    val unformattedNumber: String, // E164 format: +254712345678
    val formattedWithoutCountryCode: String, // National format: 0712 345 678
    val isValid: Boolean // True or False
)
