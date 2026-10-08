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

package com.ezzy.ccp.countrypicker.phone

import com.ezzy.ccp.countrypicker.model.Country
import com.google.i18n.phonenumbers.Phonenumber

internal actual fun currentEpochMillis(): Long = System.currentTimeMillis()

/**
 * Parses [nationalNumber] as a number in [country] into libphonenumber's own
 * [Phonenumber.PhoneNumber], returning `null` when it cannot be parsed at all (rather than throwing
 * into composition).
 *
 * Android-only: for hosts that hand the parsed number on to their own libphonenumber code. Shared
 * code should use [PhoneNumberFormatter.toE164] and friends, or [PhoneNumberValidator.evaluate].
 */
public fun PhoneNumberFormatter.parse(nationalNumber: String, country: Country): Phonenumber.PhoneNumber? =
    parseNumber(nationalNumber, country)
