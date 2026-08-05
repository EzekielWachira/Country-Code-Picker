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

package com.ezzy.ccp.countrypicker.data

import com.ezzy.ccp.countrypicker.model.Country

/**
 * Supplies the country list the picker displays.
 *
 * Implement this to serve countries from somewhere other than the bundled dataset — a backend
 * "supported markets" endpoint, a localized name provider, or a cut-down test fixture. The default
 * implementation, [DefaultCountryDataSource], is fully local so the picker works offline.
 *
 * [load] is `suspend` so remote sources are natural; the default implementation returns
 * immediately from an in-memory list and never touches disk or network.
 *
 * A `fun interface`, so a one-off source is a lambda rather than an object declaration:
 * ```kotlin
 * val source = CountryDataSource { api.supportedMarkets().map(::toCountry) }
 * val localized = CountryDataSource { DefaultCountryDataSource.localizedNames(Locale.getDefault()) }
 * ```
 */
fun interface CountryDataSource {

    /**
     * Returns every country this source knows about.
     *
     * Implementations should be cheap to call repeatedly — the repository caches the result, but
     * a retry after failure will call this again. Throwing is how a source reports failure; the
     * repository turns it into an error state rather than crashing the sheet.
     */
    suspend fun load(): List<Country>
}
