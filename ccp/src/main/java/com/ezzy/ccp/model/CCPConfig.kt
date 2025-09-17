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

import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp

/**
 * Configuration options for customizing the appearance and behavior of the
 * Country Code Picker (CCP) component.
 *
 * This class allows fine-grained control over the UI elements, shapes, and
 * behavior of the phone number input and country selection sheet.
 *
 * @property phoneInputCornerRadius The corner radius applied to the phone input field container.
 * @property phoneHintStyle The [TextStyle] applied to the hint/placeholder text inside the phone input.
 * @property borderWidth The thickness of the border around the phone number input field.
 * @property autoDetectCountry Whether the component should automatically detect and set the user's country (using SIM, network, or locale).
 * @property showHeader Whether to display a header section inside the country selection sheet.
 * @property showCountryFlag Whether to display the country flag inside the phone input field.
 * @property countriesSheetShape The shape of the country selection sheet (e.g., rounded corners, rectangle).
 * @property searchBorderWidth The thickness of the border around the search input field inside the country selection sheet.
 * @property searchCornerRadius The corner radius applied to the search input field inside the country selection sheet.
 * @property searchHintStyle The [TextStyle] applied to the hint/placeholder text inside the search field.
 * @property headerStyle The [TextStyle] applied to the header text in the country selection sheet.
 * @property showDialCodeCountryItem Whether to display the country dial code (e.g., +1, +254) in the country list items.
 * @property showFlagCountryItem Whether to display the country flag in the country list items.
 * @property countryItemDialCodeTextStyle The [TextStyle] applied to the dial code text in each country list item.
 * @property countryItemNameTextStyle The [TextStyle] applied to the country name text in each country list item.
 * @property countryItemShape The shape applied to individual country list items (e.g., rounded rectangle).
 * @property phoneInputShape The shape of the phone input field container.
 * @property showCountriesHeaderDivider Whether to display a divider line below the header in the country selection sheet.
 */
data class CCPConfig(
    val phoneInputCornerRadius: Dp,
    val phoneHintStyle: TextStyle,
    val borderWidth: Dp,
    val autoDetectCountry: Boolean,
    val showHeader: Boolean,
    val showCountryFlag: Boolean,
    val countriesSheetShape: Shape,
    val searchBorderWidth: Dp,
    val searchCornerRadius: Dp,
    val searchHintStyle: TextStyle,
    val headerStyle: TextStyle,
    val showDialCodeCountryItem: Boolean,
    val showFlagCountryItem: Boolean,
    val countryItemDialCodeTextStyle: TextStyle,
    val countryItemNameTextStyle: TextStyle,
    val countryItemShape: Shape,
    val phoneInputShape: Shape,
    val showCountriesHeaderDivider: Boolean,
    val readOnly: Boolean
)
