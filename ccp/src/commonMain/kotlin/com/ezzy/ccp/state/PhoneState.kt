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

package com.ezzy.ccp.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.ezzy.ccp.countrypicker.data.DefaultCountryDataSource
import com.ezzy.ccp.countrypicker.model.PhoneNumberValidity
import com.ezzy.ccp.countrypicker.model.PhoneNumberValue
import com.ezzy.ccp.countrypicker.model.toLegacy
import com.ezzy.ccp.countrypicker.model.toLegacyPhone
import com.ezzy.ccp.countrypicker.phone.PhoneNumberFormatter
import com.ezzy.ccp.countrypicker.phone.PhoneNumberValidator
import com.ezzy.ccp.countrypicker.phone.cursorOffsetForDigitCount
import com.ezzy.ccp.model.Country
import com.ezzy.ccp.model.Phone
import com.ezzy.ccp.countrypicker.model.Country as CanonicalCountry

/**
 * State holder for [com.ezzy.ccp.components.PhoneNumberInput].
 *
 * The public surface is unchanged from previous versions — [activeCountry], [phoneNumber],
 * [phoneField], [formattedPhone], [unformattedPhone], [isValid], and the same five methods — but the
 * implementation now runs on [PhoneNumberValidator] and [PhoneNumberFormatter] instead of its own
 * parse/format helpers.
 *
 * ### What that buys existing callers
 * - **Progressive formatting.** The field previously reformatted through libphonenumber's `NATIONAL`
 *   formatter, which only produces grouping once a number is complete. It now uses
 *   `AsYouTypeFormatter`, so `71` → `712` → `712 345 678` groups as the user types.
 * - **Granular validity.** [validity] distinguishes "still typing" from "wrong"; [isValid] behaves
 *   exactly as before.
 * - **Correct E.164.** [unformattedPhone] is `null`-safe as before but is now only populated when the
 *   number is actually parseable, instead of echoing raw input back as though it were E.164.
 *
 * Hoist it as before:
 * ```kotlin
 * val phoneState = rememberPhoneState()
 * PhoneNumberInput(state = phoneState)
 * // Later: phoneState.clearPhone(), phoneState.isValid, phoneState.toPhone()
 * ```
 */
@Stable
public class PhoneState {

    /** The canonical country. The authority; [activeCountry] is a projection of it. */
    internal var canonicalCountry: CanonicalCountry by mutableStateOf(defaultCountry())
        private set

    /**
     * The selected country in the legacy model.
     *
     * Nullable for source compatibility — it was nullable before and callers guard against null —
     * but in practice it is never null, because the state always falls back to a default country.
     */
    public val activeCountry: Country? get() = canonicalCountry.toLegacy()

    /** Digits the user typed, with formatting stripped. */
    public var phoneNumber: String by mutableStateOf("")
        private set

    /** The field's text and cursor. Derived from [phoneNumber]; never set independently. */
    public var phoneField: TextFieldValue by mutableStateOf(TextFieldValue(""))
        private set

    /** International display form, e.g. `+254 712 345 678`. */
    public var formattedPhone: String by mutableStateOf("")
        private set

    /**
     * E.164 form, e.g. `+254712345678`.
     *
     * Empty — not a partial echo of the input — while the number cannot be parsed. Previously this
     * fell back to the raw text on a parse failure, which meant callers could persist a value that was
     * not E.164 at all.
     */
    public var unformattedPhone: String by mutableStateOf("")
        private set

    /** Whether the number is valid and dialable for [activeCountry]. */
    public var isValid: Boolean by mutableStateOf(false)
        private set

    /**
     * The granular reason behind [isValid].
     *
     * New in this version; use it to tell "incomplete" apart from "invalid" instead of inferring from
     * length.
     */
    public var validity: PhoneNumberValidity by mutableStateOf(PhoneNumberValidity.Empty)
        private set

    /** The full evaluated value, including the nullable E.164 form. */
    public var value: PhoneNumberValue by mutableStateOf(PhoneNumberValue.empty(defaultCountry()))
        private set

    /**
     * Updates all derived phone state from a raw field edit.
     *
     * Text that names its own country — a typed or pasted `+254…`, or a number handed back by
     * Android Autofill — adopts that country instead of being flattened into national digits for
     * whichever country is currently selected. See [parseAndSet], which this delegates to; the
     * detection rule matches [com.ezzy.ccp.countrypicker.state.PhoneNumberFieldState].
     */
    public fun updatePhoneNumber(newValue: TextFieldValue) {
        val raw = newValue.text.trimStart()
        // "00" is the international access prefix, but it is also two ordinary keystrokes, so it
        // only counts when the text arrived as a block rather than one character at a time.
        val pasted = newValue.text.length - phoneField.text.length > 1
        if (raw.startsWith("+") || (pasted && raw.startsWith("00"))) {
            val parsed = PhoneNumberFormatter.parseInternational(raw, canonicalCountry)
            if (parsed.countryWasDetected) {
                canonicalCountry = parsed.country
                applyDigits(parsed.nationalDigits)
                return
            }
        }

        // Anchored by digit count, not character offset, so the cursor survives the reformat that
        // follows — see applyDigits.
        val digitsBeforeCursor = newValue.text.take(newValue.selection.end).count(Char::isDigit)
        applyDigits(newValue.text.filter(Char::isDigit), digitsBeforeCursor)
    }

    /** Sets the active country by ISO code, case-insensitively. Falls back to the default if unknown. */
    public fun setCountryByCode(code: String) {
        canonicalCountry = DefaultCountryDataSource.findByIso2(code) ?: defaultCountry()
        applyDigits(phoneNumber)
    }

    /**
     * Sets the active country and reformats the current number for it.
     *
     * The typed digits are kept and **re-validated** against the new region, so a number valid for one
     * country is never carried over as still-valid for another.
     */
    public fun selectCountry(country: Country) {
        canonicalCountry = DefaultCountryDataSource.findByIso2(country.code) ?: canonicalCountry
        applyDigits(phoneNumber)
    }

    /** Sets the active country from the canonical model. */
    public fun selectCountry(country: CanonicalCountry) {
        canonicalCountry = country
        applyDigits(phoneNumber)
    }

    /**
     * Parses a full phone string, adopting the country when the input identifies one.
     *
     * `+4915123456789` switches the field to Germany; a bare `0712345678` carries no country
     * information and leaves the current country alone.
     */
    public fun parseAndSet(value: String) {
        val parsed = PhoneNumberFormatter.parseInternational(value, canonicalCountry)
        if (parsed.countryWasDetected) canonicalCountry = parsed.country
        applyDigits(parsed.nationalDigits)
    }

    /** Clears the number, leaving the country selected. */
    public fun clearPhone() {
        applyDigits("")
    }

    /** Builds a [Phone] snapshot of the current state. */
    public fun toPhone(): Phone = value.toLegacyPhone()

    /**
     * Recomputes every derived value. The single mutation path for the number.
     *
     * @param digitsBeforeCursor How many digits preceded the edit's cursor, used to re-anchor the
     *   cursor after reformatting instead of always placing it at the end. Defaults to the full
     *   digit count (cursor at the end) for the country-change/programmatic paths below, where
     *   there is no real edit position to preserve.
     */
    private fun applyDigits(digits: String, digitsBeforeCursor: Int = digits.length) {
        val evaluated = PhoneNumberValidator.evaluate(digits, canonicalCountry)
        phoneNumber = evaluated.nationalNumber
        // The field displays international-style grouping (e.g. "712 084 336") since the dial code
        // is already shown separately by the prefix beside it — see formatAsYouTypeInternational.
        // PhoneNumberValue.formattedNationalNumber is unaffected: it stays the national form for any
        // caller reading it directly off `value`.
        val displayText = PhoneNumberFormatter.formatAsYouTypeInternational(digits, canonicalCountry)
        phoneField = TextFieldValue(
            text = displayText,
            selection = TextRange(cursorOffsetForDigitCount(displayText, digitsBeforeCursor)),
        )
        formattedPhone = evaluated.internationalNumber
        unformattedPhone = evaluated.e164Number.orEmpty()
        isValid = evaluated.isValid
        validity = evaluated.validity
        value = evaluated
    }

    private companion object {
        /**
         * The fallback country when nothing has been selected or detected.
         *
         * The United States, matching the previous behaviour, resolved through the dataset rather than
         * by scanning a list so it cannot silently become null if the dataset changes.
         */
        fun defaultCountry(): CanonicalCountry =
            DefaultCountryDataSource.findByIso2("US") ?: DefaultCountryDataSource.countries.first()
    }
}

@Composable
public fun rememberPhoneState(): PhoneState = remember { PhoneState() }
