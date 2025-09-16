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
 * Represents the result of parsing, formatting, and validating a phone number.
 *
 * This model provides different standardized representations of the phone number
 * (international, E.164, and national formats) along with its validation state.
 *
 * @property formattedNumber The phone number in international human-readable format
 * (e.g., "+254 712 345 678").
 *
 * @property unformattedNumber The phone number in E.164 format, suitable for
 * storage or API communication (e.g., "+254712345678").
 *
 * @property formattedWithoutCountryCode The phone number in national format,
 * excluding the country code (e.g., "0712 345 678").
 *
 * @property isValid Whether the phone number is valid for the given country
 * according to libphonenumber rules.
 */
data class PhoneValidationResult(
    val formattedNumber: String,  // Pretty format: +254 712 345 678
    val unformattedNumber: String, // E164 format: +254712345678
    val formattedWithoutCountryCode: String, // National format: 0712 345 678
    val isValid: Boolean // True or False
)
