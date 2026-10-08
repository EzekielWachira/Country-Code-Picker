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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.ezzy.ccp.countrypicker.data.DefaultCountryDataSource
import com.ezzy.ccp.countrypicker.model.Country
import com.ezzy.ccp.countrypicker.model.PhoneNumberType
import com.ezzy.ccp.countrypicker.model.PhoneNumberValue
import com.ezzy.ccp.countrypicker.phone.PhoneNumberFormatter
import com.ezzy.ccp.countrypicker.phone.PhoneNumberValidator
import com.ezzy.ccp.countrypicker.phone.cursorOffsetForDigitCount

/**
 * State for a phone number field: the selected country, the digits typed, and everything derived from
 * them.
 *
 * The digits are the single source of truth. The formatted text shown in the field is *derived* from
 * them, never stored independently, which is what keeps the display and the E.164 output from drifting
 * apart — the failure mode where a field reads `+254 712 345 678` and submits `254712345678 `.
 *
 * @property country The selected country. Drives parsing, formatting and validation.
 * @property nationalDigits Raw digits, no separators.
 * @property allowedNumberTypes Line types accepted, or empty for any. Restricting this to
 *   [PhoneNumberType.SmsCapable] makes the field reject a landline that is otherwise perfectly
 *   valid — which is what an SMS code flow needs, and what plain validity cannot express.
 */
@Stable
public class PhoneNumberFieldState internal constructor(
    initialCountry: Country,
    initialDigits: String = "",
    private val enforceMaxLength: Boolean = true,
    public val allowedNumberTypes: Set<PhoneNumberType> = emptySet(),
) {

    public var country: Country by mutableStateOf(initialCountry)
        private set

    public var nationalDigits: String by mutableStateOf(initialDigits.filter(Char::isDigit))
        private set

    /**
     * The field's text and cursor.
     *
     * The cursor is re-anchored by digit count after each edit (see [cursorOffsetForDigitCount]), so
     * placing it mid-number and continuing to type or delete keeps working the way it looks like it
     * should, instead of the field silently forcing every edit back to the end of the text.
     */
    public var textFieldValue: TextFieldValue by mutableStateOf(
        TextFieldValue(
            text = PhoneNumberFormatter.formatAsYouType(initialDigits, initialCountry),
            selection = TextRange(
                PhoneNumberFormatter.formatAsYouType(initialDigits, initialCountry).length,
            ),
        ),
    )
        private set

    /** The complete evaluated value: formatted forms, E.164, validity. */
    public var value: PhoneNumberValue by mutableStateOf(
        PhoneNumberValidator.evaluate(initialDigits, initialCountry, allowedNumberTypes),
    )
        private set

    /**
     * True once the user has left the field or attempted submission.
     *
     * Errors are withheld until this is true: flagging "invalid" after the first digit is technically
     * accurate and practically hostile.
     */
    public var hasBeenTouched: Boolean by mutableStateOf(false)
        private set

    /**
     * Applies a text edit from the field.
     *
     * Everything non-digit is stripped, so pasting `(0712) 345-678` works. Input is capped at the
     * region's maximum when [enforceMaxLength] is set, which stops a mistyped extra digit from
     * silently invalidating an otherwise correct number.
     *
     * Text that carries its own country — a typed or pasted `+254…`, or an autofill provider
     * handing back a stored `+254712345678` — adopts that country rather than being flattened into
     * national digits for whichever country happened to be selected. Without this, an autofilled
     * international number silently becomes a wrong national number: the `+` is stripped, the
     * calling code is read as part of the subscriber number, and the field reports a plausible but
     * incorrect value.
     */
    public fun onTextChanged(newValue: TextFieldValue) {
        if (adoptInternationalNumber(newValue)) return

        val digits = newValue.text.filter(Char::isDigit)
        val capped = if (enforceMaxLength) {
            digits.take(PhoneNumberFormatter.maxNationalDigits(country))
        } else {
            digits.take(PhoneNumberFormatter.E164_MAX_DIGITS)
        }
        // Anchored by digit count, not character offset, so the cursor survives the reformat that
        // follows — see setDigits. Clamped to the capped length in case capping dropped digits the
        // cursor was counted against.
        val digitsBeforeCursor = newValue.text.take(newValue.selection.end)
            .count(Char::isDigit)
            .coerceAtMost(capped.length)
        setDigits(capped, digitsBeforeCursor)
    }

    /**
     * Changes the country, keeping the digits the user already typed.
     *
     * Digits are preserved rather than cleared: a user correcting the country after typing their number
     * has not changed their number, and wiping it is the single most annoying behaviour a phone field
     * can have. The number is re-formatted and **re-validated** for the new region, so a number that is
     * valid in one country is never carried over as still-valid in another.
     */
    public fun selectCountry(newCountry: Country) {
        if (newCountry == country) return
        country = newCountry
        setDigits(
            if (enforceMaxLength) {
                nationalDigits.take(PhoneNumberFormatter.maxNationalDigits(newCountry))
            } else {
                nationalDigits
            },
        )
    }

    /**
     * Parses a full or partial number, adopting the country it identifies when [allowCountryChange].
     *
     * A pasted `+4915123456789` should switch the field to Germany; a pasted `0712345678` carries no
     * country information and must not. [PhoneNumberFormatter.parseInternational] reports which case it
     * was, and only the former changes the country.
     */
    public fun setFullNumber(raw: String, allowCountryChange: Boolean = true) {
        val parsed = PhoneNumberFormatter.parseInternational(raw, country)
        if (allowCountryChange && parsed.countryWasDetected) country = parsed.country
        setDigits(parsed.nationalDigits)
    }

    /** Clears the number. Leaves the country alone — the user did not un-choose it. */
    public fun clear() {
        setDigits("")
        hasBeenTouched = false
    }

    /** Marks the field touched, so validation messages become visible. */
    public fun markTouched() {
        hasBeenTouched = true
    }

    /**
     * Handles an edit that identifies its own country, returning true when it was consumed.
     *
     * Triggers on a leading `+` (always — a user who types `+254` means the calling code, and
     * `KeyboardType.Phone` offers the key for exactly that reason) and on a leading `00` only when
     * the text arrived as a block rather than a keystroke. That second restriction matters: `00` is
     * the international access prefix, but it is also two ordinary keystrokes, and re-parsing a
     * half-typed national number that happens to start `00` would yank the country out from under
     * the user mid-entry.
     *
     * A `+` prefix that does not yet resolve to a country (`"+2"`, `"+"`) is left to the normal
     * digit path, so the country only changes once the input actually says which country it is.
     */
    private fun adoptInternationalNumber(newValue: TextFieldValue): Boolean {
        val raw = newValue.text.trimStart()
        val pasted = newValue.text.length - textFieldValue.text.length > 1
        val international = raw.startsWith("+") || (pasted && raw.startsWith("00"))
        if (!international) return false

        val parsed = PhoneNumberFormatter.parseInternational(raw, country)
        if (!parsed.countryWasDetected) return false

        country = parsed.country
        setDigits(
            if (enforceMaxLength) {
                parsed.nationalDigits.take(PhoneNumberFormatter.maxNationalDigits(parsed.country))
            } else {
                parsed.nationalDigits.take(PhoneNumberFormatter.E164_MAX_DIGITS)
            },
        )
        return true
    }

    /**
     * Recomputes every derived value from [digits]. The only path that mutates the number.
     *
     * @param digitsBeforeCursor How many digits preceded the edit's cursor, used to re-anchor the
     *   cursor after reformatting instead of always placing it at the end. Defaults to the full
     *   digit count (cursor at the end) for the country-change/programmatic paths below, where
     *   there is no real edit position to preserve.
     */
    private fun setDigits(digits: String, digitsBeforeCursor: Int = digits.length) {
        nationalDigits = digits
        val formatted = PhoneNumberFormatter.formatAsYouType(digits, country)
        textFieldValue = TextFieldValue(
            text = formatted,
            selection = TextRange(cursorOffsetForDigitCount(formatted, digitsBeforeCursor)),
        )
        value = PhoneNumberValidator.evaluate(digits, country, allowedNumberTypes)
    }
}

/**
 * Remembers a [PhoneNumberFieldState], restoring the country and digits across configuration change and
 * process death.
 *
 * @param initialCountry Starting country. Falls back to the first dataset entry if the code is unknown.
 * @param initialNumber Starting number; may be E.164, in which case the country is taken from it.
 * @param enforceMaxLength Cap input at the region's maximum length.
 * @param allowedNumberTypes Line types the field accepts, or empty for any. Pass
 *   [PhoneNumberType.SmsCapable] for a one-time-code flow.
 */
@Composable
public fun rememberPhoneNumberFieldState(
    initialCountry: Country,
    initialNumber: String = "",
    enforceMaxLength: Boolean = true,
    allowedNumberTypes: Set<PhoneNumberType> = emptySet(),
): PhoneNumberFieldState = rememberSaveable(
    inputs = arrayOf(allowedNumberTypes),
    saver = listSaver(
        save = { listOf(it.country.iso2Code, it.nationalDigits, it.hasBeenTouched.toString()) },
        restore = { saved ->
            val country = DefaultCountryDataSource.findByIso2(saved.getOrNull(0))
                ?: initialCountry
            PhoneNumberFieldState(
                initialCountry = country,
                initialDigits = saved.getOrNull(1).orEmpty(),
                enforceMaxLength = enforceMaxLength,
                allowedNumberTypes = allowedNumberTypes,
            ).also { if (saved.getOrNull(2).toBoolean()) it.markTouched() }
        },
    ),
) {
    val parsed = PhoneNumberFormatter.parseInternational(initialNumber, initialCountry)
    PhoneNumberFieldState(
        initialCountry = if (parsed.countryWasDetected) parsed.country else initialCountry,
        initialDigits = parsed.nationalDigits,
        enforceMaxLength = enforceMaxLength,
        allowedNumberTypes = allowedNumberTypes,
    )
}
