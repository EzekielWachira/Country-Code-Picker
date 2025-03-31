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
import androidx.lifecycle.ViewModel
import com.ezzy.ccp.data.countryList
import com.ezzy.ccp.model.Country
import kotlinx.coroutines.flow.MutableStateFlow

private const val TAG = "PhoneViewModel"
class PhoneViewModel : ViewModel() {

    private val _activeCountry = MutableStateFlow(countryList.find { it.code == "US" })
    val activeCountry = _activeCountry

    init {

    }

    fun setCountryForLocale(code: String) {
        Log.e(TAG, "setCountryForLocale: $code", )
        if (code.isEmpty()) {
            _activeCountry.value = countryList.find { it.code == "US" }
            return
        }
        _activeCountry.value = countryList.find { it.code == code }
    }

    fun setActiveCountry(country: Country) {
        _activeCountry.value = country
    }

}