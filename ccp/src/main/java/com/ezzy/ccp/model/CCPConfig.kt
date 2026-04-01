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
 * Configuration options for customizing the appearance and behavior of the CCP component.
 *
 * @property phoneInputCornerRadius Corner radius of the phone input field container.
 * @property phoneHintStyle [TextStyle] for the placeholder text inside the phone input.
 * @property borderWidth Thickness of the border around the phone number input field.
 * @property autoDetectCountry Automatically detect the user's country via SIM, network, or locale.
 * @property showHeader Display a letter header inside the country selection sheet.
 * @property showCountryFlag Display the country flag inside the phone input field.
 * @property countriesSheetShape Shape of the country selection bottom sheet.
 * @property searchBorderWidth Border thickness of the search field inside the sheet.
 * @property searchCornerRadius Corner radius of the search field.
 * @property searchHintStyle [TextStyle] for the search field placeholder.
 * @property headerStyle [TextStyle] for the letter headers in the country list.
 * @property showDialCodeCountryItem Show the dial code (e.g. +1) inside each country list item.
 * @property showFlagCountryItem Show the flag emoji inside each country list item.
 * @property countryItemDialCodeTextStyle [TextStyle] for the dial code in each country item.
 * @property countryItemNameTextStyle [TextStyle] for the country name in each country item.
 * @property countryItemShape Shape of individual country list items.
 * @property phoneInputShape Shape of the phone input field container.
 * @property showCountriesHeaderDivider Show a divider line next to the letter header.
 * @property readOnly Make the phone input non-editable and the country selector non-tappable.
 * @property showClearButton Show an × button inside the field to clear the entered number.
 * @property enforceMaxLength Cap input at the maximum digit count for the selected country.
 * @property defaultCountryListStyle Initial layout of the country list — [CountryListStyle.List]
 * or [CountryListStyle.Grid]. The user can toggle this at runtime via the sort button.
 * @property countryPickerStyle How to present the country picker — [CountryPickerStyle.BottomSheet]
 * (default, full-screen modal) or [CountryPickerStyle.Dropdown] (compact inline dropdown).
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
    val readOnly: Boolean,
    val showClearButton: Boolean,
    val enforceMaxLength: Boolean,
    val defaultCountryListStyle: CountryListStyle,
    val countryPickerStyle: CountryPickerStyle,
)
