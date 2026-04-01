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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.ezzy.ccp.data.countryList
import com.ezzy.ccp.model.Country

/**
 * State holder for country search and filtering inside the countries bottom sheet.
 *
 * [filteredCountries] is a derived value — it recomputes automatically whenever [query]
 * changes. [countriesToShow] is fixed at construction time; pass a new instance via
 * [rememberCountrySearchState] when it needs to change.
 *
 * Create via [rememberCountrySearchState].
 */
class CountrySearchState(val countriesToShow: List<String> = emptyList()) {

    var query by mutableStateOf("")
        private set

    /** Grouped and filtered country list, recomputed reactively when [query] changes. */
    val filteredCountries: Map<Char, List<Country>> by derivedStateOf {
        val base = if (countriesToShow.isNotEmpty()) {
            countryList.filter { it.code in countriesToShow }
        } else {
            countryList
        }
        val filtered = if (query.isBlank()) base
        else base.filter {
            it.name.contains(query, ignoreCase = true) ||
                it.dialCode.contains(query, ignoreCase = true) ||
                it.code.contains(query, ignoreCase = true)
        }
        filtered.sortedBy { it.name[0] }.groupBy { it.name[0] }
    }

    fun updateQuery(newQuery: String) {
        query = newQuery
    }
}

@Composable
fun rememberCountrySearchState(countriesToShow: List<String> = emptyList()): CountrySearchState =
    remember(countriesToShow) { CountrySearchState(countriesToShow) }
