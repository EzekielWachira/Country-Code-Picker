package com.ezzy.ccp.model

data class Phone(
    val formattedPhone: String,
    val phoneNumber: String,
    val country: SelectedCountry?,
    val isValid: Boolean
)
