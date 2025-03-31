package com.ezzy.ccp

import androidx.lifecycle.ViewModel
import com.ezzy.ccp.data.countryList
import com.ezzy.ccp.state.SearchState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update

class CountryViewModel : ViewModel() {

    private val _countriesState = MutableStateFlow(countryList.sortedBy {
        it.name[0]
    }.groupBy { country -> country.name[0] })
    val countriesState = _countriesState.asStateFlow()

    private val _searchState = MutableStateFlow(SearchState())
    val searchState = _searchState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val countries = searchState
        .filterNotNull()
        .distinctUntilChanged()
        .flatMapLatest { searchState ->
            val query = searchState.query
            flowOf(
                if (query.isEmpty()) {
                    if (searchState.countriesToShow.isNotEmpty()) {
                        countryList.filter { it.code in searchState.countriesToShow }
                            .sortedBy { it.name[0] }.groupBy { country -> country.name[0] }
                    } else
                        countryList.sortedBy { it.name[0] }
                            .groupBy { country -> country.name[0] }
                } else {
                    if (searchState.countriesToShow.isNotEmpty()) {
                        countryList.filter { it.code in searchState.countriesToShow }
                            .filter {
                                it.name.contains(query, ignoreCase = true) ||
                                        it.dialCode.contains(query, ignoreCase = true) ||
                                        it.code.contains(query, ignoreCase = true)
                            }
                            .sortedBy { it.name[0] }
                            .groupBy { country -> country.name[0] }
                    } else
                        countryList.filter {
                            it.name.contains(query, ignoreCase = true) ||
                                    it.dialCode.contains(query, ignoreCase = true) ||
                                    it.code.contains(query, ignoreCase = true)
                        }
                            .sortedBy { it.name[0] }
                            .groupBy { country -> country.name[0] }
                }
            )
        }

    fun updateSearchQuery(query: String) {
        _searchState.update { it.copy(query = query) }
    }

    fun setCountriesToShow(countries: List<String>) {
        _searchState.update {
            it.copy(countriesToShow = countries)
        }
    }

}