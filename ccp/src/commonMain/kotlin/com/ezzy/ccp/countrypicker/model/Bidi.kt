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

import androidx.compose.ui.text.style.TextDirection

/**
 * Bidirectional-text handling for the one thing in this library that is never reordered by the
 * reading direction: a phone number.
 *
 * ## Why this is needed
 *
 * A dial code is a `+` followed by digits. `+` is a neutral character under the Unicode
 * bidirectional algorithm and digits are weakly directional, so a run like `+254` has no strong
 * direction of its own — it takes the direction of the surrounding paragraph. Inside Arabic or
 * Hebrew UI that paragraph is right-to-left, and the `+` is resolved to the *right* of the digits:
 * `254+`. The same applies to the grouped national number in the phone field and to the `KE · +254`
 * metadata line under a country name, where the separator and the two runs can be reordered
 * independently.
 *
 * Phone numbers are written left-to-right in every locale, including RTL ones — this is what E.123
 * and E.164 specify, and it is what users of RTL scripts actually expect, because a dial code read
 * backwards is a different number.
 *
 * ## The two mechanisms
 *
 * [LTR] forces an entire `Text` to lay out left-to-right, and is the right tool when the composable
 * renders nothing but a number. [isolateLtr] wraps a number in Unicode isolate characters so it
 * keeps its own direction while sitting *inside* a translated sentence — an error message like
 * "أدخل رقم Kenya صالحًا — 9 رقم بعد ‎+254‎" needs the sentence to stay RTL and only the `+254` to
 * be LTR, which a whole-`Text` direction override cannot express.
 */
internal object CountryPickerBidi {

    /**
     * The direction to apply to a `Text` (or `BasicTextField`) whose entire content is a phone
     * number, dial code, or ISO code.
     *
     * Pass as `style.copy(textDirection = CountryPickerBidi.LTR)`. Note this sets the direction of
     * the text's *content*, not the alignment of the composable in its parent: a right-aligned
     * field in an RTL layout stays right-aligned, it just stops reordering the number inside it.
     */
    val LTR: TextDirection = TextDirection.Ltr

    /** U+2066 LEFT-TO-RIGHT ISOLATE — opens a run with its own, LTR, direction. */
    private const val LRI = '⁦'

    /** U+2069 POP DIRECTIONAL ISOLATE — closes the innermost isolate. */
    private const val PDI = '⁩'

    /**
     * Wraps [value] in an LTR isolate so it renders left-to-right and does not disturb the
     * direction of the text around it.
     *
     * Isolates rather than embeddings (U+202A/U+202C): an embedding lets the wrapped run still
     * participate in the surrounding run's ordering, which is exactly the reordering being
     * prevented here. Isolates are also what `BidiFormatter` emits on API 18+.
     *
     * Returns [value] unchanged when it is blank — wrapping an empty string just adds two invisible
     * characters to every content description that includes it.
     */
    fun isolateLtr(value: String): String =
        if (value.isBlank()) value else "$LRI$value$PDI"
}
