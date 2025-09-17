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

package com.ezzy.ccp.utils

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ezzy.ccp.model.CCPColors
import com.ezzy.ccp.model.CCPConfig

/**
 * Provides default configurations and color schemes for the Country Code Picker (CCP).
 */
object CCPDefaults {
    /**
     * Creates a [CCPColors] instance with customizable colors used across the Country Code Picker.
     *
     * @param containerColor The background color for the phone input container.
     * Defaults to [MaterialTheme.colorScheme.background].
     * @param cursorColor The color of the input cursor.
     * Defaults to [MaterialTheme.colorScheme.primary].
     * @param borderColor The border color of the phone input field.
     * Defaults to [MaterialTheme.colorScheme.outline].
     * @param inputTextColor The text color for the entered phone number.
     * Defaults to [MaterialTheme.colorScheme.onSurface].
     * @param phoneHintColor The color for the phone input hint text.
     * Defaults to [MaterialTheme.colorScheme.onSurfaceVariant].
     * @param ccpSheetColor Defines the color scheme for the country selection sheet.
     * Defaults to a [CCPSheetColor] object derived from [MaterialTheme.colorScheme].
     *
     * @return A [CCPColors] instance containing the provided or default colors.
     */
    @Composable
    fun colors(
        containerColor: Color = MaterialTheme.colorScheme.background,
        cursorColor: Color = MaterialTheme.colorScheme.primary,
        borderColor: Color = MaterialTheme.colorScheme.outline,
        inputTextColor: Color = MaterialTheme.colorScheme.onSurface,
        phoneHintColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        ccpSheetColor: CCPSheetColor = ccpSheetColor(
            sheetContainerColor = MaterialTheme.colorScheme.background,
            searchTextColor = MaterialTheme.colorScheme.onSurface,
            hintTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            countryHeaderColor = MaterialTheme.colorScheme.onSurface,
            containerColor = MaterialTheme.colorScheme.background,
            searchContainerColor = MaterialTheme.colorScheme.background,
            searchIconTint = MaterialTheme.colorScheme.onSurface,
            searchBorderColor = MaterialTheme.colorScheme.outline,
            searchHintColor = MaterialTheme.colorScheme.onSurfaceVariant,
            countryItemContainerColor = MaterialTheme.colorScheme.background,
            countryItemDialCodeTextColor = MaterialTheme.colorScheme.onSurface,
            countryItemTextColor = MaterialTheme.colorScheme.onSurface,
            headerDividerColor = MaterialTheme.colorScheme.outline
        )
    ): CCPColors {
        return CCPColors(
            containerColor = containerColor,
            cursorColor = cursorColor,
            borderColor = borderColor,
            inputTextColor = inputTextColor,
            phoneHintColor = phoneHintColor,
            ccpSheetColor = ccpSheetColor,
            countryCodeTextColor = MaterialTheme.colorScheme.onSurface,
            countryChevronColor = MaterialTheme.colorScheme.onSurface
        )
    }

    @Composable
    fun ccpSheetColor(
        sheetContainerColor: Color = MaterialTheme.colorScheme.background,
        searchTextColor: Color = MaterialTheme.colorScheme.onSurface,
        hintTextColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        countryHeaderColor: Color = MaterialTheme.colorScheme.onSurface,
        containerColor: Color = MaterialTheme.colorScheme.background,
        searchContainerColor: Color = MaterialTheme.colorScheme.background,
        searchIconTint: Color = MaterialTheme.colorScheme.onSurface,
        searchBorderColor: Color = MaterialTheme.colorScheme.outline,
        searchHintColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        countryItemContainerColor: Color = MaterialTheme.colorScheme.background,
        countryItemDialCodeTextColor: Color = MaterialTheme.colorScheme.onSurface,
        countryItemTextColor: Color = MaterialTheme.colorScheme.onSurface,
        headerDividerColor: Color = MaterialTheme.colorScheme.outline
    ): CCPSheetColor {
        return CCPSheetColor(
            sheetContainerColor = sheetContainerColor,
            searchTextColor = searchTextColor,
            hintTextColor = hintTextColor,
            countryHeaderColor = countryHeaderColor,
            containerColor = containerColor,
            searchContainerColor = searchContainerColor,
            searchIconTint = searchIconTint,
            searchBorderColor = searchBorderColor,
            searchHintColor = searchHintColor,
            countryItemContainerColor = countryItemContainerColor,
            countryItemDialCodeTextColor = countryItemDialCodeTextColor,
            countryItemTextColor = countryItemTextColor,
            headerDividerColor = headerDividerColor,
        )
    }

    /**
     * Creates a [CCPConfig] instance with customizable UI and behavioral options
     * for the Country Code Picker.
     *
     * @param phoneInputCornerRadius The corner radius for the phone input field. Defaults to `16.dp`.
     * @param phoneHintStyle The text style for the phone input hint. Defaults to [TextStyle.Default].
     * @param borderWidth The width of the border around the phone input field. Defaults to `0.dp`.
     * @param autoDetectCountry Whether to automatically detect the user's country. Defaults to `false`.
     * @param showHeader Whether to display the sheet header in the country picker. Defaults to `true`.
     * @param showCountryFlag Whether to display the country flag in the input and sheet. Defaults to `true`.
     * @param searchBorderWidth The border width for the search field in the sheet. Defaults to `1.dp`.
     * @param searchCornerRadius The corner radius for the search field. Defaults to `16.dp`.
     * @param searchHintStyle The text style for the search hint. Defaults to [TextStyle.Default].
     * @param headerStyle The text style for the sheet header.
     * Defaults to [MaterialTheme.typography.titleMedium].
     * @param countriesSheetShape The shape of the country picker sheet.
     * Defaults to a [RoundedCornerShape] with 16.dp top corners.
     *
     * @return A [CCPConfig] instance containing the provided or default configuration.
     */
    @Composable
    fun defaultConfig(
        phoneInputCornerRadius: Dp = 16.dp,
        phoneHintStyle: TextStyle = TextStyle.Default,
        borderWidth: Dp = 0.dp,
        autoDetectCountry: Boolean = false,
        showHeader: Boolean = true,
        showCountryFlag: Boolean = true,
        searchBorderWidth: Dp = 1.dp,
        searchCornerRadius: Dp = 16.dp,
        searchHintStyle: TextStyle = TextStyle.Default,
        headerStyle: TextStyle = MaterialTheme.typography.titleMedium,
        countriesSheetShape: Shape = RoundedCornerShape(topEnd = 16.dp, topStart = 16.dp),
        showDialCodeCountryItem: Boolean = true,
        showFlagCountryItem: Boolean = true,
        phoneInputShape: Shape = RoundedCornerShape(phoneInputCornerRadius),
        showCountriesHeaderDivider: Boolean = true,
        readOnly: Boolean = false
    ): CCPConfig {
        return CCPConfig(
            phoneInputCornerRadius = phoneInputCornerRadius,
            phoneHintStyle = phoneHintStyle,
            borderWidth = borderWidth,
            autoDetectCountry = autoDetectCountry,
            showHeader = showHeader,
            showCountryFlag = showCountryFlag,
            countriesSheetShape = countriesSheetShape,
            searchBorderWidth = searchBorderWidth,
            searchCornerRadius = searchCornerRadius,
            searchHintStyle = searchHintStyle,
            headerStyle = headerStyle,
            showDialCodeCountryItem = showDialCodeCountryItem,
            showFlagCountryItem = showFlagCountryItem,
            countryItemDialCodeTextStyle = MaterialTheme.typography.titleSmall,
            countryItemNameTextStyle = MaterialTheme.typography.bodyMedium,
            countryItemShape = RoundedCornerShape(16.dp),
            phoneInputShape = phoneInputShape,
            showCountriesHeaderDivider = showCountriesHeaderDivider,
            readOnly = readOnly
        )
    }
}
