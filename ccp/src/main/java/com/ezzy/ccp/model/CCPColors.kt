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

import androidx.compose.ui.graphics.Color
import com.ezzy.ccp.utils.CCPSheetColor

/**
 * Defines the color scheme for the Country Code Picker (CCP) component.
 *
 * This class provides customization options for all major UI elements
 * within the CCP, ensuring consistency with the app's overall theme.
 *
 * @property containerColor The background color of the phone number input container.
 * @property cursorColor The color of the text cursor inside the phone number field.
 * @property borderColor The color of the border around the phone number input field.
 * @property inputTextColor The color of the text entered into the phone number field.
 * @property phoneHintColor The color of the hint/placeholder text inside the phone number input.
 * @property countryCodeTextColor The color of the country code displayed in the picker.
 * @property countryChevronColor The color of the dropdown chevron icon for selecting a country.
 * @property ccpSheetColor A set of colors applied to the country selection bottom sheet (see [CCPSheetColor]).
 */
data class CCPColors(
    val containerColor: Color,
    val cursorColor: Color,
    val borderColor: Color,
    val inputTextColor: Color,
    val phoneHintColor: Color,
    val countryCodeTextColor: Color,
    val countryChevronColor: Color,
    val ccpSheetColor: CCPSheetColor
)