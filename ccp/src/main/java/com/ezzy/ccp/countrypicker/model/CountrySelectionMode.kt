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

package com.ezzy.ccp.countrypicker.model

/**
 * Whether the picker commits a selection immediately or stages it behind a confirm action.
 *
 * - [Single]: tapping a row selects it and (by default) dismisses the sheet. The row is marked
 *   with a check icon.
 * - [Multiple]: rows carry checkboxes and toggle a *pending* selection. Nothing reaches the
 *   caller until Confirm is tapped; Cancel discards, Reset clears.
 */
enum class CountrySelectionMode { Single, Multiple }

/**
 * Fields [com.ezzy.ccp.countrypicker.data.CountrySearchEngine] matches a query against.
 *
 * Narrowing this set is a way to make search stricter — for example a residence picker that
 * should not match dial codes can pass `setOf(Name, Iso2, Iso3, AlternativeNames)`.
 */
enum class CountrySearchField {
    /** [Country.displayName], accent-folded. Also the only field that produces a highlight range. */
    Name,

    /** [Country.iso2Code], prefix match (`"ke"` → Kenya). */
    Iso2,

    /** [Country.iso3Code], prefix match (`"ken"` → Kenya). */
    Iso3,

    /** [Country.dialCode], with or without the leading `+` (`"254"` and `"+254"` → Kenya). */
    DialCode,

    /** [Country.alternativeNames], substring match (`"uae"` → United Arab Emirates). */
    AlternativeNames,
    ;

    companion object {
        /** Every field — the default, and the most forgiving for the user. */
        val All: Set<CountrySearchField> = entries.toSet()
    }
}

/**
 * A country that matched a search query, plus the information needed to rank and highlight it.
 *
 * @property country The matched country.
 * @property matchedField Which field produced the match, or `null` when the list is unfiltered.
 * @property highlightStart Index into [Country.displayName] where the query matched, or `-1` when
 *   the match was not on the name and so has nothing to highlight.
 * @property highlightLength Length of the highlighted span.
 * @property rank Lower is better. See [com.ezzy.ccp.countrypicker.data.CountrySearchEngine].
 */
@androidx.compose.runtime.Immutable
data class CountryMatch(
    val country: Country,
    val matchedField: CountrySearchField? = null,
    val highlightStart: Int = -1,
    val highlightLength: Int = 0,
    val rank: Int = Int.MAX_VALUE,
) {
    /** True when there is a name span worth highlighting in the row. */
    val hasHighlight: Boolean get() = highlightStart >= 0 && highlightLength > 0
}
