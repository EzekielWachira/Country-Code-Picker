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

/**
 * Represents a phone number along with its metadata and validation state.
 *
 * This model is useful for managing both the raw and formatted versions
 * of a phone number, while also keeping track of the associated country
 * and validation status.
 *
 * @property formattedPhone The user-facing, formatted phone number (e.g., "(202) 555-0199").
 * @property phoneNumber The raw/unformatted phone number digits (e.g., "2025550199").
 * @property country The [SelectedCountry] associated with this phone number, if available.
 * @property isValid Whether the phone number is considered valid according to the country rules and validation logic.
 */
data class Phone(
    val formattedPhone: String,
    val phoneNumber: String,
    val country: SelectedCountry?,
    val isValid: Boolean
)
