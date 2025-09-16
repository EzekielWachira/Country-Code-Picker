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

package com.ezzy.ccp

import android.util.Log
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import com.ezzy.ccp.data.countryList
import com.ezzy.ccp.model.Country
import com.ezzy.ccp.utils.formatAndValidatePhone
import com.ezzy.ccp.utils.parsePhoneNumber
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val TAG = "PhoneViewModel"

class PhoneViewModel : ViewModel() {

    private val _activeCountry = MutableStateFlow(countryList.find { it.code == "US" })
    val activeCountry: StateFlow<Country?> = _activeCountry.asStateFlow()

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber.asStateFlow()

    private val _phoneField = MutableStateFlow(TextFieldValue(""))
    val phoneField: StateFlow<TextFieldValue> = _phoneField

    private val _formattedPhone = MutableStateFlow("")
    val formattedPhone: StateFlow<String> = _formattedPhone.asStateFlow()

    private val _unformattedPhone = MutableStateFlow("")
    val unformattedPhone: StateFlow<String> = _unformattedPhone.asStateFlow()

    private val _isValid = MutableStateFlow(false)
    val isValid: StateFlow<Boolean> = _isValid.asStateFlow()

    fun setCountryForLocale(code: String) {
        Log.e(TAG, "setCountryForLocale: $code")
        if (code.isEmpty()) {
            _activeCountry.value = countryList.find { it.code == "US" }
            return
        }
        _activeCountry.value = countryList.find { it.code == code }
    }

    fun setActiveCountry(country: Country) {
        _activeCountry.value = country
        updatePhoneNumber(TextFieldValue(_phoneNumber.value))
    }

    fun updatePhoneField(newValue: TextFieldValue) {
        _phoneField.value = newValue
        updatePhoneNumber(TextFieldValue(newValue.text))
    }

    private fun updatePhoneNumber(newValue: TextFieldValue) {
        val country = _activeCountry.value ?: countryList.find { it.code == "US" }

        // Take only digits to avoid double-formatting
        val digitsOnly = newValue.text.filter { it.isDigit() }

        // Format & validate
        val (formatted, unformatted, national, valid) =
            formatAndValidatePhone(digitsOnly, country?.code ?: "US")

        // Update main state
        _phoneNumber.value = digitsOnly
        _formattedPhone.value = formatted
        _unformattedPhone.value = unformatted
        _isValid.value = valid

        // Always store formatted in phoneField
        _phoneField.value = newValue.copy(
            text = national,
            selection = TextRange(national.length) // keep cursor at end
        )
    }

    fun parseAndSetPhoneNumber(value: String) {
        if (value.startsWith("+") || value.length >= 8) {
            val (country, localNumber) = parsePhoneNumber(value)
            if (country != null) {
                _activeCountry.value = country
                updatePhoneNumber(TextFieldValue(localNumber))
            } else {
                updatePhoneNumber(TextFieldValue(value))
            }
        } else {
            updatePhoneNumber(TextFieldValue(value))
        }
    }
}