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

package com.ezzy.ccp.data

import com.ezzy.ccp.countrypicker.data.DefaultCountryDataSource
import com.ezzy.ccp.model.Country

/**
 * The legacy country list, retained for source compatibility.
 *
 * This is now a **derived view** of [DefaultCountryDataSource] rather than a second hand-maintained
 * table. Previously this file held ~200 `Country(...)` literals that had to be kept in step with the
 * dataset by hand; country metadata now lives in exactly one place, and this list projects it into the
 * older [Country] shape.
 *
 * ### What changed for existing callers
 * - **More countries.** ~200 entries became 236: every ISO 3166-1 country and territory with a dial
 *   code. Code that filters this list still works; code that assumed a specific size does not.
 * - **Dial codes no longer contain hyphens.** The Dominican Republic was `"+1-809"` and is now
 *   `"+1809"`. The hyphenated form is not a valid dial code and broke E.164 normalization for the NANP
 *   territories. Anything comparing dial codes as strings needs the new form.
 * - **Some names follow current ISO usage.** `"Czech Republic"` → `"Czechia"`, `"Turkey"` → `"Türkiye"`,
 *   `"Congo (Congo-Brazzaville)"` → `"Congo - Brazzaville"`. The former names remain searchable as
 *   aliases, so a user typing "Czech Republic" still finds it.
 * - **`flag` is always `null`.** It used to carry one of the library's bundled flag `ImageVector`s;
 *   those covered barely half the dataset, nothing rendered them, and they have been removed.
 *   [com.ezzy.ccp.countrypicker.ui.CountryFlag] draws the platform emoji glyph and covers every
 *   country, so nothing that was actually being displayed changes.
 *
 * New code should use [DefaultCountryDataSource] and [com.ezzy.ccp.countrypicker.model.Country]
 * directly — that model carries ISO alpha-3, region and search aliases, and takes its identity from the
 * ISO alpha-2 code rather than from a dial code that is not unique.
 */
@Deprecated(
    message = "Use DefaultCountryDataSource.countries, which carries ISO alpha-3, region and search " +
        "aliases. This list is a lossy projection kept for source compatibility.",
    replaceWith = ReplaceWith(
        expression = "DefaultCountryDataSource.countries",
        imports = ["com.ezzy.ccp.countrypicker.data.DefaultCountryDataSource"],
    ),
    level = DeprecationLevel.WARNING,
)
public val countryList: List<Country> by lazy(LazyThreadSafetyMode.PUBLICATION) {
    DefaultCountryDataSource.countries.map { canonical ->
        Country(
            name = canonical.displayName,
            code = canonical.iso2Code,
            dialCode = canonical.dialCode,
            // `flag` is left at its null default: the bundled vectors are gone and every component
            // renders the emoji glyph from `code` instead.
        )
    }
}
