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
import com.ezzy.ccp.countrypicker.model.CountryMatch
import com.ezzy.ccp.countrypicker.model.CountrySearchField
import com.ezzy.ccp.countrypicker.model.CountryTextNormalizer

/**
 * Ranked, accent-insensitive, purely local country search.
 *
 * Matching is deterministic and allocation-light: the country fields are normalized once when the
 * dataset is built, so a query does one pass over the candidate list with only substring
 * comparisons. That is fast enough to run synchronously on every keystroke for the full 236-country
 * dataset — there is deliberately no debounce on local search, because the sub-millisecond filter
 * makes the list feel immediate.
 *
 * ### Ranking
 * Results are ordered by match quality first and country name second:
 *
 * | Rank | Match |
 * |------|-------|
 * | 0 | Country name equals the query |
 * | 1 | Country name starts with the query |
 * | 2 | ISO alpha-2 or alpha-3 equals the query |
 * | 3 | Dial code equals the query |
 * | 4 | Country name contains the query |
 * | 5 | An alternative name contains the query |
 * | 6 | ISO alpha-2 or alpha-3 starts with the query |
 * | 7 | Dial code starts with the query |
 *
 * The last two tiers are partial-input fallbacks so a half-typed code (`"25"`, `"ke"`) still
 * surfaces something, without ever outranking a real name or exact-code match.
 *
 * Ties break on [Country.displayName] so the same query always produces the same order.
 */
object CountrySearchEngine {

    private const val RANK_NAME_EXACT = 0
    private const val RANK_NAME_PREFIX = 1
    private const val RANK_ISO_EXACT = 2
    private const val RANK_DIAL_EXACT = 3
    private const val RANK_NAME_CONTAINS = 4
    private const val RANK_ALIAS_CONTAINS = 5
    private const val RANK_ISO_PREFIX = 6
    private const val RANK_DIAL_PREFIX = 7

    /**
     * Filters and ranks [countries] against [query].
     *
     * A blank query (or one shorter than [minQueryLength]) is not a filter: every country is
     * returned in the incoming order, unranked, so the caller can apply its own grouping.
     *
     * @param countries Candidates, already narrowed by allow/exclude/region rules.
     * @param query Raw user input. Trimmed, lowercased, accent-folded, and stripped of a leading
     *   `+` so `"+254"` and `"254"` behave identically.
     * @param fields Which [CountrySearchField]s to consider. Omitting [CountrySearchField.DialCode]
     *   makes a purely numeric query match nothing, which is the point for non-phone pickers.
     * @param minQueryLength Below this length the query is ignored entirely.
     */
    fun search(
        countries: List<Country>,
        query: String,
        fields: Set<CountrySearchField> = CountrySearchField.All,
        minQueryLength: Int = 1,
    ): List<CountryMatch> {
        val normalized = CountryTextNormalizer.normalize(query).removePrefix("+").trim()
        if (normalized.isEmpty() || normalized.length < minQueryLength) {
            return countries.map { CountryMatch(country = it) }
        }

        val isNumeric = normalized.all { it.isDigit() }
        val results = ArrayList<CountryMatch>(countries.size)

        for (country in countries) {
            val match = match(country, normalized, isNumeric, fields)
            if (match != null) results += match
        }

        results.sortWith(
            compareBy<CountryMatch> { it.rank }.thenBy { it.country.normalizedName },
        )
        return results
    }

    /**
     * Scores one country against an already-normalized query, returning `null` when nothing matched.
     *
     * Tiers are tested in rank order and the first hit wins, so a country is never counted twice
     * and the returned [CountryMatch.rank] is always its best available tier.
     */
    private fun match(
        country: Country,
        query: String,
        isNumeric: Boolean,
        fields: Set<CountrySearchField>,
    ): CountryMatch? {
        if (CountrySearchField.Name in fields) {
            val name = country.normalizedName
            when {
                name == query ->
                    return CountryMatch(country, CountrySearchField.Name, 0, query.length, RANK_NAME_EXACT)

                name.startsWith(query) ->
                    return CountryMatch(country, CountrySearchField.Name, 0, query.length, RANK_NAME_PREFIX)
            }
        }

        val iso2 = country.iso2Code.lowercase()
        val iso3 = country.iso3Code.lowercase()
        val isoEnabled = CountrySearchField.Iso2 in fields || CountrySearchField.Iso3 in fields
        if (isoEnabled) {
            val exact = (CountrySearchField.Iso2 in fields && iso2 == query) ||
                (CountrySearchField.Iso3 in fields && iso3 == query)
            if (exact) {
                val field =
                    if (iso2 == query) CountrySearchField.Iso2 else CountrySearchField.Iso3
                return CountryMatch(country, field, rank = RANK_ISO_EXACT)
            }
        }

        val dialEnabled = CountrySearchField.DialCode in fields && isNumeric
        if (dialEnabled && country.dialCodeDigits == query) {
            return CountryMatch(country, CountrySearchField.DialCode, rank = RANK_DIAL_EXACT)
        }

        if (CountrySearchField.Name in fields) {
            // Word-boundary matches ("guinea" → Equatorial Guinea, Guinea-Bissau, Papua New Guinea)
            // and plain infix matches share a tier; the alphabetical tie-break orders them.
            val index = country.normalizedName.indexOf(query)
            if (index > 0) {
                return CountryMatch(
                    country,
                    CountrySearchField.Name,
                    index,
                    query.length,
                    RANK_NAME_CONTAINS,
                )
            }
        }

        if (CountrySearchField.AlternativeNames in fields) {
            if (country.normalizedAliases.any { it.contains(query) }) {
                return CountryMatch(
                    country,
                    CountrySearchField.AlternativeNames,
                    rank = RANK_ALIAS_CONTAINS,
                )
            }
        }

        if (isoEnabled) {
            val prefix = (CountrySearchField.Iso2 in fields && iso2.startsWith(query)) ||
                (CountrySearchField.Iso3 in fields && iso3.startsWith(query))
            if (prefix) {
                val field =
                    if (iso2.startsWith(query)) CountrySearchField.Iso2 else CountrySearchField.Iso3
                return CountryMatch(country, field, rank = RANK_ISO_PREFIX)
            }
        }

        if (dialEnabled && country.dialCodeDigits.startsWith(query)) {
            return CountryMatch(country, CountrySearchField.DialCode, rank = RANK_DIAL_PREFIX)
        }

        return null
    }
}
