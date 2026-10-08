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

package com.ezzy.ccp.countrypicker.theme

/**
 * A named overall size for the unified phone field, shared by
 * [com.ezzy.ccp.countrypicker.ui.PhoneNumberField] and the legacy
 * `com.ezzy.ccp.components.PhoneNumberInput`.
 *
 * Neither field can simply be told a smaller height: both are built on Material's real outlined
 * text field decoration ([androidx.compose.material3.OutlinedTextFieldDefaults]), whose minimum
 * height comes from its own internal content padding plus the flag/chevron/icon sizes it lays out —
 * a smaller `defaultMinSize` floor is silently ignored once it's below that natural minimum, and
 * forcing a hard-clipped height on top of unscaled content would just crop the flag or chevron.
 *
 * [Compact] and [ExtraCompact] instead scale the field's content padding, flag size, chevron size,
 * trailing icon-button size, and value/label font size *together* via [contentScale] and
 * [fontScale], so a shorter field actually looks proportioned rather than clipped. [Regular] is the
 * field's original, unscaled look — both scales are `1f`, a guaranteed no-op.
 *
 * @property contentScale Multiplier for the field's vertical content padding, flag size, chevron
 *   size, and trailing icon-button size.
 * @property fontScale Multiplier for the field's value, label, and dial-code font size. Kept apart
 *   from [contentScale] because text tends to need a gentler reduction than icons/padding do before
 *   it becomes hard to read.
 */
public enum class PhoneFieldSize(
    public val contentScale: Float,
    public val fontScale: Float,
) {
    Regular(contentScale = 1f, fontScale = 1f),
    Compact(contentScale = 0.8f, fontScale = 0.92f),
    ExtraCompact(contentScale = 0.65f, fontScale = 0.85f),
}
