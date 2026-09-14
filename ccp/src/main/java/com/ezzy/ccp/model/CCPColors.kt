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
 * @property containerColor Background color of the phone number input container.
 * @property cursorColor Color of the text cursor inside the phone number field.
 * @property borderColor Border color of the phone number input field (normal state).
 * @property errorBorderColor Border color shown when [com.ezzy.ccp.components.PhoneNumberInput] is in error state.
 * @property inputTextColor Color of the text entered into the phone number field.
 * @property phoneHintColor Color of the hint/placeholder text.
 * @property countryCodeTextColor Color of the country dial code displayed in the picker.
 * @property countryChevronColor Color of the dropdown chevron icon.
 * @property errorColor Color of the error message text shown below the field.
 * @property ccpSheetColor Colors applied to the country selection bottom sheet.
 */
data class CCPColors(
    val containerColor: Color,
    val cursorColor: Color,
    val borderColor: Color,
    val errorBorderColor: Color,
    val inputTextColor: Color,
    val phoneHintColor: Color,
    val countryCodeTextColor: Color,
    val countryChevronColor: Color,
    val errorColor: Color,
    val ccpSheetColor: CCPSheetColor
)
