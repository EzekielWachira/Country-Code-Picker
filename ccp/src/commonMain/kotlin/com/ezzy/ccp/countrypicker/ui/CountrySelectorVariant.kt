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

package com.ezzy.ccp.countrypicker.ui

/**
 * The visual form of a country selector. Every variant opens the same picker with the same
 * behaviour — they differ in footprint, never in function.
 */
public enum class CountrySelectorVariant {
    /** A raised card with a hairline and soft shadow — the Signature field. */
    Elevated,

    /** A transparent field with an outline. */
    Outlined,

    /** A softly filled field without an outline. */
    Filled,

    /** Just an underline, for forms that are already boxed. */
    Underlined,

    /** A large card: a big flag, the name, and the country's region, dial code and ISO code. */
    Card,

    /** A pill with the flag and name, for toolbars and dense rows. */
    Compact,

    /** A pill with only the flag. */
    FlagOnly,

    /** A pill with the flag and dial code — the phone prefix. */
    DialCode,
    ;

    /** True for the full-width field variants; false for the pills. */
    public val isFullWidth: Boolean get() = this == Elevated || this == Outlined || this == Filled || this == Underlined || this == Card

    /** True for the pill variants. */
    public val isPill: Boolean get() = !isFullWidth
}

/** Interaction and validation state of a selector. */
public enum class CountrySelectorState {
    /** Normal, interactive. */
    Default,

    /** Not interactive and visibly inert. */
    Disabled,

    /** Shows a value but cannot be changed. Distinct from [Disabled], which implies unavailability. */
    ReadOnly,

    /** Resolving a value — during country detection, for instance. Shows a spinner. */
    Loading,

    /** Invalid. Shows the error stroke, icon and message, with a brief shake on entry. */
    Error,

    /** Validated. Shows the success stroke and message. */
    Success,
    ;

    /** True when taps should open the picker. */
    public val isInteractive: Boolean get() = this == Default || this == Error || this == Success
}
