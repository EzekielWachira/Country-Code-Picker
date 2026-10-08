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

package com.ezzy.ccp.countrypicker.state

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.ezzy.ccp.countrypicker.data.DefaultCountryDataSource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Cursor-preservation behaviour for [PhoneNumberFieldState], mirroring the same fix applied to the
 * legacy [com.ezzy.ccp.state.PhoneState] — both used to reset the selection to the end of the
 * reformatted text on every edit, making it impossible to place the cursor anywhere but the tail.
 */
class PhoneNumberFieldStateTest {

    private val kenya = requireNotNull(DefaultCountryDataSource.findByIso2("KE"))

    @Test
    fun `an append-style edit still lands the cursor at the end`() {
        val state = PhoneNumberFieldState(initialCountry = kenya)
        val digits = "712345"
        state.onTextChanged(TextFieldValue(digits, selection = TextRange(digits.length)))

        assertEquals(state.textFieldValue.text.length, state.textFieldValue.selection.start)
    }

    @Test
    fun `a mid-string edit re-anchors the cursor by digit count instead of forcing it to the end`() {
        val state = PhoneNumberFieldState(initialCountry = kenya)
        // Four digits precede the cursor here ("7129"), simulating a "9" inserted after "712" while
        // editing mid-string.
        state.onTextChanged(TextFieldValue("7129345678", selection = TextRange(4)))

        val digitsBeforeCursor = state.textFieldValue.text
            .take(state.textFieldValue.selection.start)
            .count(Char::isDigit)
        assertEquals(4, digitsBeforeCursor)
        assertTrue(
            state.textFieldValue.selection.start < state.textFieldValue.text.length,
            message = "Cursor should not be pinned to the end",
        )
    }

    @Test
    fun `selecting a country still places the cursor at the end`() {
        // Not a direct edit — there is no user cursor position to preserve, so this path keeps the
        // prior "cursor at the end" behaviour.
        val state = PhoneNumberFieldState(initialCountry = kenya)
        state.onTextChanged(TextFieldValue("712345678", selection = TextRange(9)))

        val germany = requireNotNull(DefaultCountryDataSource.findByIso2("DE"))
        state.selectCountry(germany)

        assertEquals(state.textFieldValue.text.length, state.textFieldValue.selection.start)
    }
}
