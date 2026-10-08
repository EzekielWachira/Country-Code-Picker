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

import com.ezzy.ccp.resources.Res
import com.ezzy.ccp.resources.ccp_region_africa
import com.ezzy.ccp.resources.ccp_region_americas
import com.ezzy.ccp.resources.ccp_region_asia
import com.ezzy.ccp.resources.ccp_region_europe
import com.ezzy.ccp.resources.ccp_region_oceania
import org.jetbrains.compose.resources.StringResource

/**
 * Geographic region a [Country] belongs to, following the UN M49 top-level groupings used by
 * the country dataset. These are the regions offered by the sheet's quick filter chips.
 *
 * Every country in [com.ezzy.ccp.countrypicker.data.DefaultCountryDataSource] maps to exactly one
 * region, so filtering by region partitions the dataset without gaps or overlaps.
 */
public enum class CountryRegion(
    /** Stable, locale-independent key. Safe to persist. */
    public val key: String,
    public val labelRes: StringResource,
) {
    Africa("africa", Res.string.ccp_region_africa),
    Americas("americas", Res.string.ccp_region_americas),
    Asia("asia", Res.string.ccp_region_asia),
    Europe("europe", Res.string.ccp_region_europe),
    Oceania("oceania", Res.string.ccp_region_oceania),
    ;

    public companion object {
        /** Resolves a region from its [key], or `null` when the key is unknown. */
        public fun fromKey(key: String?): CountryRegion? =
            entries.firstOrNull { it.key.equals(key, ignoreCase = true) }
    }
}
