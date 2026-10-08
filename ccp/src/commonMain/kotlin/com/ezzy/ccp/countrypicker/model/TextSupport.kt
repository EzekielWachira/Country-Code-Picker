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
 * Unicode canonical decomposition (NFD) of [value]: `"é"` becomes `"e"` followed by U+0301
 * COMBINING ACUTE ACCENT, which is what lets [CountryTextNormalizer] strip accents by dropping the
 * combining marks. `java.text.Normalizer` on Android, `NSString.decomposedStringWithCanonicalMapping`
 * on iOS.
 */
internal expect fun decomposeCanonical(value: String): String

/**
 * Appends a Unicode code point, as a surrogate pair when it lies outside the Basic Multilingual
 * Plane. The common-code stand-in for the JVM's `StringBuilder.appendCodePoint`, needed for the
 * regional-indicator symbols (U+1F1E6…U+1F1FF) that make up emoji flags.
 */
internal fun StringBuilder.appendCodePointCompat(codePoint: Int): StringBuilder {
    require(codePoint in 0..MAX_CODE_POINT) { "Not a Unicode code point: $codePoint" }
    if (codePoint < SUPPLEMENTARY_PLANE_START) return append(codePoint.toChar())
    val offset = codePoint - SUPPLEMENTARY_PLANE_START
    append((HIGH_SURROGATE_START + (offset ushr SURROGATE_BITS)).toChar())
    return append((LOW_SURROGATE_START + (offset and SURROGATE_MASK)).toChar())
}

private const val MAX_CODE_POINT = 0x10FFFF
private const val SUPPLEMENTARY_PLANE_START = 0x10000
private const val HIGH_SURROGATE_START = 0xD800
private const val LOW_SURROGATE_START = 0xDC00
private const val SURROGATE_BITS = 10
private const val SURROGATE_MASK = 0x3FF
