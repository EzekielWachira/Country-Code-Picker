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

import androidx.compose.runtime.Immutable

/**
 * The canonical country representation for the whole library.
 *
 * Identity is [iso2Code] and nothing else. Dial codes are explicitly **not** unique — `+1` is
 * shared by the US, Canada and a dozen Caribbean territories, and `+44` by the UK, Jersey,
 * Guernsey and the Isle of Man — so [equals]/[hashCode] and every list key are derived from the
 * ISO alpha-2 code alone.
 *
 * Instances are cheap to compare and safe to use as Compose keys: the class is [Immutable], and
 * the normalized search fields are computed once when the dataset is built rather than per
 * recomposition (see [normalizedName], [normalizedAliases]).
 *
 * @property iso2Code ISO 3166-1 alpha-2 code, uppercase (e.g. `"KE"`). The stable identity.
 * @property iso3Code ISO 3166-1 alpha-3 code, uppercase (e.g. `"KEN"`).
 * @property displayName Name shown to the user (e.g. `"Kenya"`). See [com.ezzy.ccp.countrypicker.data.DefaultCountryDataSource.localizedNames] for the
 *   locale-aware variant resolved by the data source.
 * @property dialCode International dial code including the leading `+` (e.g. `"+254"`).
 * @property flag Default flag representation — a regional-indicator emoji, or `null` for the few
 *   codes with no emoji sequence (e.g. `XK`), which fall back to rendering the ISO code.
 * @property region Geographic region used by the sheet's region filters.
 * @property alternativeNames Extra names and abbreviations matched by search (e.g. `"uae"`,
 *   `"holland"`, `"deutschland"`). Never shown in the UI.
 */
@Immutable
data class Country(
    val iso2Code: String,
    val iso3Code: String,
    val displayName: String,
    val dialCode: String,
    val flag: String?,
    val region: CountryRegion,
    val alternativeNames: List<String> = emptyList(),
) {
    /** [dialCode] without the leading `+` (e.g. `"254"`). Used for numeric dial-code search. */
    val dialCodeDigits: String get() = dialCode.removePrefix("+")

    /**
     * Accent-folded, lowercased [displayName]. Precomputed so search never normalizes inside a
     * hot loop or during composition.
     */
    internal val normalizedName: String = CountryTextNormalizer.normalize(displayName)

    /** [normalizedName] split on spaces and hyphens, for word-prefix ranking. */
    internal val normalizedWords: List<String> = normalizedName.split(' ', '-').filter { it.isNotEmpty() }

    /** Accent-folded, lowercased [alternativeNames]. */
    internal val normalizedAliases: List<String> = alternativeNames.map(CountryTextNormalizer::normalize)

    override fun equals(other: Any?): Boolean =
        this === other || (other is Country && iso2Code == other.iso2Code)

    override fun hashCode(): Int = iso2Code.hashCode()

    override fun toString(): String = "Country($iso2Code, $displayName, $dialCode)"
}

/**
 * Accent-insensitive text folding shared by the dataset and [
 * com.ezzy.ccp.countrypicker.data.CountrySearchEngine], so a query is always normalized the same
 * way as the fields it is compared against.
 */
internal object CountryTextNormalizer {

    /**
     * Lowercases, trims, and strips combining diacritics so `"Côte d'Ivoire"` folds to
     * `"cote d'ivoire"` and a search for `"cote"` matches it.
     */
    fun normalize(value: String): String {
        val decomposed = java.text.Normalizer.normalize(value, java.text.Normalizer.Form.NFD)
        val builder = StringBuilder(decomposed.length)
        for (char in decomposed) {
            // Mn = non-spacing mark: the combining accents NFD split out of the base letters.
            if (Character.getType(char) != Character.NON_SPACING_MARK.toInt()) builder.append(char)
        }
        return builder.toString().lowercase().trim()
    }
}
