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