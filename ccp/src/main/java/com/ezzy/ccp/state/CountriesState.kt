package com.ezzy.ccp.state

import com.ezzy.ccp.data.countryList
import com.ezzy.ccp.model.Country

data class CountriesState(
    val countries: List<Country> = countryList,
)
