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
 * The metadata line a [Country] selector or row shows under the country name — e.g. `"DE · +49"`.
 *
 * This governs the *selector field's own displayed value* / a row's supporting text; it is a distinct
 * concern from search matching ([CountrySearchField]) and from what the country sheet's rows show,
 * which the sheet's own configuration controls independently.
 */
enum class CountrySupportingContent {
    /** No metadata line at all — just the country name. */
    None,

    /** ISO alpha-2 code only, e.g. `"DE"`. */
    IsoCode,

    /** Dial code only, e.g. `"+49"`. */
    DialCode,

    /** Both, e.g. `"DE · +49"`. */
    IsoAndDialCode,
    ;

    /** Whether this mode includes the ISO code. */
    val showsIsoCode: Boolean get() = this == IsoCode || this == IsoAndDialCode

    /** Whether this mode includes the dial code. */
    val showsDialCode: Boolean get() = this == DialCode || this == IsoAndDialCode
}

/**
 * What the phone country-code prefix shows before the divider — e.g. `🇰🇪 +254` is [FlagAndDialCode].
 */
enum class PhonePrefixContentMode {
    /** Flag alone, no dial code. */
    FlagOnly,

    /** Dial code alone, no flag. */
    DialCodeOnly,

    /** Flag and dial code — the default, matching `🇰🇪 +254`. */
    FlagAndDialCode,

    /** ISO alpha-2 code and dial code, no flag — e.g. `KE +254`. */
    CountryCodeAndDialCode,
    ;

    /** Whether this mode shows the flag. */
    val showsFlag: Boolean get() = this == FlagOnly || this == FlagAndDialCode

    /** Whether this mode shows the dial code. */
    val showsDialCode: Boolean get() = this != FlagOnly

    /** Whether this mode shows the bare ISO alpha-2 code instead of the flag. */
    val showsCountryCode: Boolean get() = this == CountryCodeAndDialCode
}
