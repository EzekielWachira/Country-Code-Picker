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
 * A named overall size for the phone field, shared by [com.ezzy.ccp.countrypicker.ui.PhoneNumberField]
 * and the legacy `com.ezzy.ccp.components.PhoneNumberInput`.
 *
 * [Compact] and [ExtraCompact] scale the field's padding, flag, icons and text *together* via
 * [contentScale] and [fontScale], so a shorter field stays proportioned instead of clipping its
 * content. [Regular] is unscaled.
 *
 * @property contentScale Multiplier for padding, flag, chevron and icon-button sizes.
 * @property fontScale Multiplier for the number, label and dial-code text. Gentler than
 *   [contentScale], because text becomes hard to read sooner than icons do.
 */
public enum class PhoneFieldSize(
    public val contentScale: Float,
    public val fontScale: Float,
) {
    Regular(contentScale = 1f, fontScale = 1f),
    Compact(contentScale = 0.8f, fontScale = 0.92f),
    ExtraCompact(contentScale = 0.65f, fontScale = 0.85f),
}
