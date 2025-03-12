package com.ezzy.ccp.model

import androidx.compose.ui.graphics.vector.ImageVector
import com.ezzy.ccp.utils.isPhoneNumberValid

/**
 * Data class representing a country.
 *
 * @property name The full name of the country.
 * @property code The ISO country code (e.g. "US", "GB", "KE").
 * @property dialCode The international dial code (e.g. "+1", "+44", "+254").
 * @property flag The vector image representing the country's flag.
 * @property selected Whether the country is currently selected in the UI, defaults to false.
 */
data class Country(
    val name: String,
    val code: String,
    val dialCode: String,
    val flag: ImageVector,
    val selected: Boolean = false
)
