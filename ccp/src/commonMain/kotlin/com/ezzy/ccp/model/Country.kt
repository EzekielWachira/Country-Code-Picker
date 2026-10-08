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

package com.ezzy.ccp.model

import androidx.compose.ui.graphics.vector.ImageVector
import com.ezzy.ccp.utils.countryToFlagEmoji

/**
 * Data class representing a country.
 *
 * @property name The full name of the country.
 * @property code The ISO country code (e.g. "US", "GB", "KE").
 * @property dialCode The international dial code (e.g. "+1", "+44", "+254").
 * @property flag Always `null`.
 *
 *   This carried one of the library's bundled flag `ImageVector`s, which have been removed — they
 *   covered barely half the dataset and nothing rendered them, since every component draws the
 *   platform's emoji glyph instead. The parameter is kept so existing `Country(...)` and `copy(...)`
 *   calls still compile; use [code] with `countryToFlagEmoji()` for a flag, or the `CountryFlag`
 *   composable to draw one.
 * @property selected Whether the country is currently selected in the UI, defaults to false.
 */
public data class Country(
    val name: String,
    val code: String,
    val dialCode: String,
    @Deprecated(
        "The bundled flag vectors were removed; this is always null. Use code.countryToFlagEmoji(), " +
            "or the CountryFlag composable.",
        level = DeprecationLevel.WARNING,
    )
    val flag: ImageVector? = null,
    val selected: Boolean = false
) {
    public companion object {
        internal fun Country.toSelectedCountry(): SelectedCountry =
            SelectedCountry(
                name = name,
                code = code,
                dialCode = dialCode,
                flag = code.countryToFlagEmoji() ?: ""
            )
    }
}
