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

package com.ezzy.ccp.countrypicker.state

import androidx.compose.runtime.Immutable
import com.ezzy.ccp.countrypicker.model.CountryMatch
import com.ezzy.ccp.resources.Res
import com.ezzy.ccp.resources.ccp_section_all
import com.ezzy.ccp.resources.ccp_section_recent
import com.ezzy.ccp.resources.ccp_section_selected
import com.ezzy.ccp.resources.ccp_section_suggested
import org.jetbrains.compose.resources.StringResource

/**
 * One titled group of countries in the sheet.
 *
 * @property kind Which group this is. Also its stable identity for list keys and animations.
 * @property items The countries in the group, already ranked and deduplicated against every
 *   higher-priority group.
 * @property count Convenience for the header's count suffix.
 */
@Immutable
public data class CountrySection(
    val kind: CountrySectionKind,
    val items: List<CountryMatch>,
) {
    val count: Int get() = items.size
}

/**
 * The kinds of section the sheet can show, in the order they appear.
 *
 * Declaration order **is** the deduplication priority: a country appears in the first section that
 * claims it and nowhere else. That is what keeps a selected Germany out of "Suggested" and "All
 * countries" — showing the same row three times makes the list feel broken and makes the check marks
 * ambiguous.
 */
public enum class CountrySectionKind(public val titleRes: StringResource?) {
    /** Currently selected (single) or pending-selected (multi). Highest priority. */
    Selected(Res.string.ccp_section_selected),

    /** From the [com.ezzy.ccp.countrypicker.persistence.RecentCountryStore]. */
    Recent(Res.string.ccp_section_recent),

    /** From [CountryPickerConfig.suggestedCountryCodes]. */
    Suggested(Res.string.ccp_section_suggested),

    /** Everything not claimed above. */
    All(Res.string.ccp_section_all),

    /**
     * Flat search results.
     *
     * Untitled: while searching, the grouping is replaced by a single ranked list, because relevance
     * ordering and section grouping are competing organisations of the same rows and running both
     * makes the best match hard to find.
     */
    SearchResults(null),
    ;
}
