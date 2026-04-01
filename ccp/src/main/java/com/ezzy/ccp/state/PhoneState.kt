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

package com.ezzy.ccp.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.ezzy.ccp.data.countryList
import com.ezzy.ccp.model.Country
import com.ezzy.ccp.model.Country.Companion.toSelectedCountry
import com.ezzy.ccp.model.Phone
import com.ezzy.ccp.utils.formatAndValidatePhone
import com.ezzy.ccp.utils.parsePhoneNumber

/**
 * State holder for the phone number input. Owns all mutable state related to country
 * selection and phone formatting, and exposes methods to drive state transitions.
 *
 * Intended to be created via [rememberPhoneState] so Compose tracks it across recompositions.
 */
class PhoneState {

    var activeCountry by mutableStateOf(countryList.find { it.code == "US" })
        private set

    var phoneNumber by mutableStateOf("")
        private set

    var phoneField by mutableStateOf(TextFieldValue(""))
        private set

    var formattedPhone by mutableStateOf("")
        private set

    var unformattedPhone by mutableStateOf("")
        private set

    var isValid by mutableStateOf(false)
        private set

    /** Updates all derived phone state (formatted, unformatted, validity) from a raw input. */
    fun updatePhoneNumber(newValue: TextFieldValue) {
        val countryCode = activeCountry?.code ?: "US"
        val digitsOnly = newValue.text.filter { it.isDigit() }
        val result = formatAndValidatePhone(digitsOnly, countryCode)
        phoneNumber = digitsOnly
        formattedPhone = result.formattedNumber
        unformattedPhone = result.unformattedNumber
        isValid = result.isValid
        phoneField = newValue.copy(
            text = result.formattedWithoutCountryCode,
            selection = TextRange(result.formattedWithoutCountryCode.length)
        )
    }

    /** Sets the active country by ISO code (case-insensitive). Falls back to US if not found. */
    fun setCountryByCode(code: String) {
        activeCountry = countryList
            .find { it.code.equals(code, ignoreCase = true) }
            ?: countryList.find { it.code == "US" }
    }

    /** Sets the active country and reformats the current phone number for the new country. */
    fun selectCountry(country: Country) {
        activeCountry = country
        updatePhoneNumber(TextFieldValue(phoneNumber))
    }

    /**
     * Parses a full phone string (E.164 or local), auto-detecting the country when possible,
     * then updates all state accordingly.
     */
    fun parseAndSet(value: String) {
        if (value.startsWith("+") || value.length >= 8) {
            val (country, localNumber) = parsePhoneNumber(value)
            if (country != null) {
                activeCountry = country
                updatePhoneNumber(TextFieldValue(localNumber))
            } else {
                updatePhoneNumber(TextFieldValue(value))
            }
        } else {
            updatePhoneNumber(TextFieldValue(value))
        }
    }

    /** Builds a [Phone] snapshot from the current state. */
    fun toPhone(): Phone = Phone(
        formattedPhone = formattedPhone,
        phoneNumber = unformattedPhone,
        isValid = isValid,
        country = activeCountry?.toSelectedCountry()
    )
}

@Composable
fun rememberPhoneState(): PhoneState = remember { PhoneState() }
