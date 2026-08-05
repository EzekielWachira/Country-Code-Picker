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

import com.ezzy.ccp.countrypicker.data.DefaultCountryDataSource
import com.ezzy.ccp.countrypicker.model.Country
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber

/**
 * Formatting and normalization for phone numbers, backed entirely by Google libphonenumber (already
 * a dependency of this module).
 *
 * There is no hand-written digit-grouping table here on purpose: per-country grouping rules,
 * national trunk prefixes and example numbers all come from libphonenumber metadata, so adding a
 * country to the dataset never requires touching formatting code.
 *
 * All functions are pure and thread-safe. [PhoneNumberUtil] is an immutable singleton, but
 * `AsYouTypeFormatter` is stateful, so [formatAsYouType] creates one per call rather than caching
 * an instance that concurrent fields could corrupt.
 */
object PhoneNumberFormatter {

    private val util: PhoneNumberUtil get() = PhoneNumberUtil.getInstance()

    /**
     * Groups [digits] the way a user of [country] expects to see them, mid-typing.
     *
     * Uses libphonenumber's `AsYouTypeFormatter`, which is the same engine the platform dialer
     * uses, so partial input formats progressively (`71` → `712` → `712 3` → `712 345 678`) instead
     * of only snapping into shape once complete.
     *
     * @param digits Raw digits, no separators. Anything non-digit is ignored.
     * @return The grouped national form, or [digits] unchanged when the region has no metadata.
     */
    fun formatAsYouType(digits: String, country: Country): String {
        val cleaned = digits.filter(Char::isDigit)
        if (cleaned.isEmpty()) return ""

        val direct = runAsYouType(cleaned, country) ?: return cleaned
        if (direct.hasGrouping()) return direct

        // libphonenumber's formatting rules are written against the *national* number, which in most
        // regions includes a trunk prefix ("0712 345678" for Kenya, "07400 123456" for the UK). A user
        // who types just "712345678" therefore gets no grouping at all — the formatter cannot match a
        // rule. Rather than shipping a per-country grouping table (which would then have to be
        // maintained forever), retry with the region's own national prefix and strip it back off the
        // result. Grouping stays entirely metadata-driven and works whether or not the user typed the 0.
        val nationalPrefix = nationalPrefix(country)
        if (nationalPrefix.isNullOrEmpty() || cleaned.startsWith(nationalPrefix)) return direct

        val prefixed = runAsYouType(nationalPrefix + cleaned, country) ?: return direct
        if (!prefixed.hasGrouping()) return direct

        return prefixed.removePrefix(nationalPrefix).trimStart().takeIf { it.isNotEmpty() } ?: direct
    }

    /**
     * Groups [digits] the way the *international* form of a number is written mid-typing — e.g.
     * Kenya's `"712 084 336"` rather than the national form's `"0712 084 336"`, or a NANP region's
     * `"712-345-6789"` rather than the national form's `"(712) 345-6789"`.
     *
     * This is the grouping a unified field wants for the digits it shows next to a dial-code prefix
     * that is *already displayed separately* — the number itself should read the way it would in
     * international dialing notation, not repeat national-only conventions like NANP's parentheses
     * (which exist to set off a domestic area code, a distinction that doesn't apply once the country
     * is already named by the prefix beside it).
     *
     * Uses the same technique as [formatAsYouType]'s national-trunk-prefix retry: libphonenumber's
     * `AsYouTypeFormatter` only applies its international grouping rules once fed a leading `+`, so
     * this feeds it `+<callingCode><digits>` and strips the calling code back off the result.
     *
     * @param digits Raw digits, no separators. Anything non-digit is ignored.
     * @return The grouped international-style form, or [digits] unchanged when the region has no
     *   metadata or the calling code cannot be determined.
     */
    fun formatAsYouTypeInternational(digits: String, country: Country): String {
        val cleaned = digits.filter(Char::isDigit)
        if (cleaned.isEmpty()) return ""

        val callingCode = country.dialCodeDigits
        if (callingCode.isEmpty()) return formatAsYouType(cleaned, country)

        val prefix = "+$callingCode"
        val result = runAsYouType(prefix + cleaned, country) ?: return formatAsYouType(cleaned, country)
        if (!result.startsWith(prefix)) return formatAsYouType(cleaned, country)

        return result.removePrefix(prefix).trimStart().takeIf { it.isNotEmpty() } ?: cleaned
    }

    /** Runs the stateful formatter over [digits], or `null` when the region is unknown to it. */
    private fun runAsYouType(digits: String, country: Country): String? = try {
        // A fresh formatter per call: AsYouTypeFormatter is stateful, so a shared instance would be
        // corrupted by concurrent fields.
        val formatter = util.getAsYouTypeFormatter(country.iso2Code)
        var result = ""
        for (digit in digits) result = formatter.inputDigit(digit)
        result.trim()
    } catch (_: IllegalArgumentException) {
        null
    }

    /** True when the formatter actually applied a grouping rule rather than echoing the digits back. */
    private fun String.hasGrouping(): Boolean = any { !it.isDigit() }

    /**
     * The region's national (trunk) dialling prefix, e.g. `"0"` for Kenya and the UK, `null` for the
     * NANP regions that have none.
     */
    private fun nationalPrefix(country: Country): String? = try {
        util.getNddPrefixForRegion(country.iso2Code, true)
    } catch (_: Exception) {
        null
    }

    /**
     * Parses [nationalNumber] as a number in [country], returning `null` when it cannot be parsed
     * at all (rather than throwing into composition).
     */
    fun parse(nationalNumber: String, country: Country): Phonenumber.PhoneNumber? {
        val cleaned = nationalNumber.filter(Char::isDigit)
        if (cleaned.isEmpty()) return null
        return try {
            util.parse(cleaned, country.iso2Code)
        } catch (_: NumberParseException) {
            null
        }
    }

    /** E.164 form (`+254712345678`), or `null` when [nationalNumber] cannot be parsed. */
    fun toE164(nationalNumber: String, country: Country): String? =
        parse(nationalNumber, country)?.let { util.format(it, PhoneNumberUtil.PhoneNumberFormat.E164) }

    /** International display form (`+254 712 345 678`), or `null` when parsing fails. */
    fun toInternational(nationalNumber: String, country: Country): String? =
        parse(nationalNumber, country)
            ?.let { util.format(it, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL) }

    /** National display form (`0712 345 678`), or `null` when parsing fails. */
    fun toNational(nationalNumber: String, country: Country): String? =
        parse(nationalNumber, country)
            ?.let { util.format(it, PhoneNumberUtil.PhoneNumberFormat.NATIONAL) }

    /**
     * An example national number for [country], e.g. `"712 345 678"` for Kenya, taken from
     * libphonenumber metadata.
     *
     * This is what makes the field's helper text country-aware without the library hardcoding an
     * example for any particular country.
     */
    fun exampleNationalNumber(country: Country): String? = try {
        val example = util.getExampleNumberForType(
            country.iso2Code,
            PhoneNumberUtil.PhoneNumberType.MOBILE,
        ) ?: util.getExampleNumber(country.iso2Code)

        // Formatted through the *same* path the field uses, from the significant number rather than the
        // trunk-prefixed national form. The example the helper text shows is then literally what the
        // user's own typing will look like, instead of a differently-punctuated near-miss.
        example?.let { formatAsYouType(util.getNationalSignificantNumber(it), country) }
            ?.takeIf { it.isNotEmpty() }
    } catch (_: Exception) {
        null
    }

    /**
     * Expected number of national digits for [country], derived from its example number.
     *
     * Falls back to the E.164 maximum of 15 when the region has no metadata, so an unknown region
     * never blocks input entirely.
     */
    fun expectedNationalDigits(country: Country): Int = try {
        val example = util.getExampleNumber(country.iso2Code)
        if (example == null) E164_MAX_DIGITS
        else util.getNationalSignificantNumber(example).length
    } catch (_: Exception) {
        E164_MAX_DIGITS
    }

    /**
     * Maximum digits to accept for [country], used to cap input.
     *
     * Deliberately more permissive than [expectedNationalDigits]: several countries have variable
     * national lengths, and hard-capping at the example length would make valid numbers untypeable.
     */
    fun maxNationalDigits(country: Country): Int =
        (expectedNationalDigits(country) + LENGTH_TOLERANCE).coerceAtMost(E164_MAX_DIGITS)

    /**
     * Splits a pasted or prefilled string into the country it belongs to and its national digits.
     *
     * Handles the three shapes users actually paste:
     * - full international (`"+254712345678"`) — country resolved from the calling code;
     * - international with separators (`"+254 712 345 678"`) — separators ignored;
     * - bare national (`"0712345678"`) — no country information, so [fallbackCountry] is kept and
     *   the digits are returned as-is.
     *
     * When the calling code is shared (`+1`, `+44`), libphonenumber's own region inference decides
     * which country it is from the number itself — that is why this delegates to `parse` rather
     * than matching dial-code prefixes by hand.
     *
     * @return The resolved country (or [fallbackCountry]) and the national digits.
     */
    fun parseInternational(
        raw: String,
        fallbackCountry: Country,
    ): ParsedNumber {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return ParsedNumber(fallbackCountry, "", countryWasDetected = false)

        if (!trimmed.startsWith("+") && !trimmed.startsWith("00")) {
            return ParsedNumber(
                country = fallbackCountry,
                nationalDigits = trimmed.filter(Char::isDigit),
                countryWasDetected = false,
            )
        }

        // Normalise a "00" international access prefix to "+" so libphonenumber recognises it.
        val normalized = if (trimmed.startsWith("00")) "+" + trimmed.drop(2) else trimmed
        return try {
            val parsed = util.parse(normalized, null)
            val regionCode = util.getRegionCodeForNumber(parsed)
            val country = DefaultCountryDataSource.findByIso2(regionCode)
                ?: DefaultCountryDataSource.primaryForDialCode(parsed.countryCode.toString())
            ParsedNumber(
                country = country ?: fallbackCountry,
                nationalDigits = util.getNationalSignificantNumber(parsed),
                countryWasDetected = country != null,
            )
        } catch (_: NumberParseException) {
            ParsedNumber(
                country = fallbackCountry,
                nationalDigits = normalized.filter(Char::isDigit),
                countryWasDetected = false,
            )
        }
    }

    /**
     * Result of [parseInternational].
     *
     * @property countryWasDetected True only when the input itself identified the country. Callers
     *   use this to decide whether changing the selected country is justified — an explicit user
     *   choice must not be overwritten by a bare national number that carried no country signal.
     */
    data class ParsedNumber(
        val country: Country,
        val nationalDigits: String,
        val countryWasDetected: Boolean,
    )

    /** ITU-T E.164 maximum subscriber number length. */
    const val E164_MAX_DIGITS: Int = 15

    /** Slack allowed above the example length, covering regions with variable national lengths. */
    private const val LENGTH_TOLERANCE = 3
}

/**
 * Where to place the cursor in [formatted] so it sits right after the [digitCount]th digit —
 * the anchor that survives reformatting, since inserted or removed separators shift character
 * offsets but never digit counts.
 *
 * Shared by [com.ezzy.ccp.state.PhoneState] and
 * [com.ezzy.ccp.countrypicker.state.PhoneNumberFieldState]: both reformat the field's text on
 * every keystroke (grouping shifts as digits are added or removed), and pinning the cursor to the
 * end of the result — the simplest possible choice — makes editing anywhere but the tail
 * impossible, since every edit snaps the cursor back to the end regardless of where the user
 * placed it. Counting digits instead of characters is what makes the anchor stable across a
 * reformat: "the cursor was after the 4th digit" survives grouping changes; "the cursor was at
 * character offset 6" does not.
 */
internal fun cursorOffsetForDigitCount(formatted: String, digitCount: Int): Int {
    if (digitCount <= 0) return 0
    var seen = 0
    formatted.forEachIndexed { index, char ->
        if (char.isDigit()) {
            seen++
            if (seen == digitCount) return index + 1
        }
    }
    return formatted.length
}
