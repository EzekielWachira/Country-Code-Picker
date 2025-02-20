package com.ezzy.ccp.model

import androidx.compose.ui.graphics.vector.ImageVector
import com.ezzy.ccp.utils.isPhoneNumberValid

data class Country(
    val name: String,
    val code: String,
    val dialCode: String,
    val flag: ImageVector,
    val selected: Boolean = false
) {
    companion object {
        internal fun Country.isValid(phone: String): Boolean =
            phone.isPhoneNumberValid(dialCode)
    }
}
