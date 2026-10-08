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

package com.ezzy.ccp.countrypicker.model

/**
 * What kind of line a phone number is, as classified by libphonenumber's per-region metadata.
 *
 * Most flows that collect a phone number actually want a *subset* of these. An SMS one-time-code
 * flow needs a number that can receive SMS, which means [Mobile]; a landline, a pager or a premium
 * line will pass plain validity and then silently never receive the code. Restricting the accepted
 * types moves that failure from "the user never gets their code" to "the field says so while they
 * are still on the screen".
 *
 * Pass a set of these as `allowedNumberTypes` to the phone field or to
 * [com.ezzy.ccp.countrypicker.phone.PhoneNumberValidator.evaluate]. An empty set — the default —
 * accepts any type, which is the right behaviour when the number is only being stored.
 *
 * @see FixedLineOrMobile for the case that makes naive type matching wrong.
 */
public enum class PhoneNumberType {

    /** A landline. */
    FixedLine,

    /** A mobile number. The type to require for SMS and WhatsApp flows. */
    Mobile,

    /**
     * The region's numbering plan does not separate landline from mobile, so the number is one of
     * the two and libphonenumber cannot say which.
     *
     * This is not an edge case: it is what **every** valid US, Canadian and Indian number returns,
     * among many others. A rule that requires [Mobile] and compares types by equality therefore
     * rejects every US number — which is why [satisfies] treats this value as satisfying both
     * [Mobile] and [FixedLine] rather than only itself.
     */
    FixedLineOrMobile,

    /** Freephone (e.g. `+1 800`). */
    TollFree,

    /** Premium-rate, charged above the standard rate. */
    PremiumRate,

    /** Shared-cost, billed partly to the caller. */
    SharedCost,

    /** Voice over IP. */
    Voip,

    /** A personal number that redirects to a chosen line. */
    PersonalNumber,

    /** A pager. */
    Pager,

    /** Universal Access Number — a company-wide number with no fixed location. */
    Uan,

    /** A voicemail box. */
    Voicemail,

    /**
     * libphonenumber has no classification for this number in this region.
     *
     * Returned for numbers that parse but match no type pattern, and for regions whose metadata is
     * incomplete. A type restriction treats this as *not* matching — a number nobody can classify
     * is not one to claim will receive an SMS.
     */
    Unknown,
    ;

    /**
     * Whether this actual type satisfies a requirement for [required].
     *
     * Exact equality except for [FixedLineOrMobile], which satisfies [Mobile] and [FixedLine]
     * because in the regions that report it those two are genuinely indistinguishable. Requiring
     * [FixedLineOrMobile] explicitly, conversely, is satisfied by a definite [Mobile] or
     * [FixedLine] too: the requirement is "a normal voice line", and a number known to be mobile
     * meets it.
     */
    public fun satisfies(required: PhoneNumberType): Boolean = when {
        this == required -> true
        this == FixedLineOrMobile -> required == Mobile || required == FixedLine
        required == FixedLineOrMobile -> this == Mobile || this == FixedLine
        else -> false
    }

    /** Whether this type satisfies any member of [required], or `true` when [required] is empty. */
    public fun satisfiesAny(required: Set<PhoneNumberType>): Boolean =
        required.isEmpty() || required.any { satisfies(it) }

    public companion object {

        /** The types that can receive an SMS. The set to use for one-time-code flows. */
        public val SmsCapable: Set<PhoneNumberType> = setOf(Mobile)

        /** Ordinary voice lines, mobile or landline. */
        public val Voice: Set<PhoneNumberType> = setOf(Mobile, FixedLine)
    }
}
