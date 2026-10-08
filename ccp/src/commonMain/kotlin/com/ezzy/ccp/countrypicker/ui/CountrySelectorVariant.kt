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
 * The selector's visual form.
 *
 * All variants open the *same* sheet with the same configuration and produce the same callback — the
 * difference is purely how much space they take and how much they say. That is deliberate: a compact
 * pill and a full-width field that behave differently when tapped would be two components wearing one
 * name.
 */
public enum class CountrySelectorVariant {
    /**
     * Full-width filled field: rounded top, underline, tonal background. Matches an M3 filled text
     * field so it sits correctly in a form.
     */
    Filled,

    /** Full-width outlined field. The design's default for "Country of residence". */
    Outlined,

    /** Full-width, underline only, no fill. For dense forms. */
    Minimal,

    /** Pill showing flag + country name: `🇩🇪 Germany ˅`. */
    Compact,

    /** Pill showing the flag alone: `🇩🇪 ˅`. Relies entirely on its content description. */
    FlagOnly,

    /** Pill showing flag + dial code: `🇰🇪 +254 ˅`. Used by the phone field. */
    DialCode,
    ;

    /** True for the variants that fill their parent's width and show a label. */
    public val isFullWidth: Boolean get() = this == Filled || this == Outlined || this == Minimal
}

/**
 * The selector's interaction/validation state.
 *
 * One enum rather than several booleans because these are mutually exclusive: a field cannot be
 * simultaneously loading and disabled, and modelling them as independent flags invites exactly that
 * contradiction.
 */
public enum class CountrySelectorState {
    /** Normal, interactive. */
    Default,

    /** Not interactive and visibly inert. */
    Disabled,

    /** Shows a value but cannot be changed. Distinct from [Disabled], which implies unavailability. */
    ReadOnly,

    /** Resolving a value — during country detection, for instance. Shows a spinner. */
    Loading,

    /** Invalid. Shows the error stroke, icon and message. */
    Error,

    /** Validated. Shows the success stroke and message. */
    Success,
    ;

    /** True when taps should be ignored. */
    public val isInteractive: Boolean get() = this == Default || this == Error || this == Success
}
