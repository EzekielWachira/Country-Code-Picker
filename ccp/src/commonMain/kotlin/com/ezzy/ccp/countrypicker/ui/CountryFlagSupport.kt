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
 * Whether this device can actually draw emoji flags.
 *
 * ## The problem this solves
 *
 * A flag emoji is a *pair* of regional-indicator code points (`🇰` + `🇪`), which the font is
 * supposed to compose into one flag glyph. Plenty of shipping Android devices do not: most Chinese
 * OEM ROMs strip flag glyphs, Android TV and many low-end builds ship a reduced emoji font, and
 * some Wear devices do the same. On those devices the pair falls back to rendering as two boxed
 * letters — `🇰🇪` shows as `KE` in boxes, or as tofu.
 *
 * Crucially, [com.ezzy.ccp.countrypicker.model.Country.flag] is still non-null there. The string
 * exists; it is the *font* that cannot draw it. So a `flag != null` check — the obvious one, and
 * what the library did before this class — cannot detect the case, and the picker silently shows a
 * column of broken boxes with no fallback taken.
 *
 * ## How it is detected
 *
 * On Android, `Paint.hasGlyph` answers exactly the right question: whether the font stack can render a string
 * as a single glyph. For a regional-indicator pair that is true only when a real flag glyph exists,
 * so it distinguishes "draws a flag" from "draws two letters in boxes" — which measuring text width
 * does not do reliably, since the two-letter fallback is a plausible width too.
 *
 * The check is a single font lookup and its result cannot change while the process is alive (the
 * system emoji font is fixed for the process), so it is computed once and cached.
 *
 * iOS always ships Apple Color Emoji with every flag, so the check is a constant there.
 */
internal object CountryFlagSupport {

    /**
     * Ethiopia's flag (`🇪🇹`), used as the probe.
     *
     * Any flag works, but a font that has been stripped of flags has been stripped of all of them,
     * and one that has flags at all has this one — it is in the base Unicode emoji set, not a
     * recent addition or a subdivision flag like `🏴󠁧󠁢󠁳󠁣󠁴󠁿` whose support is genuinely patchy even on
     * fonts that draw ordinary country flags.
     */
    private const val PROBE_FLAG = "🇪🇹"

    /**
     * True when the platform font can compose a regional-indicator pair into a flag glyph.
     *
     * Computed once on first read. A failure to probe is treated as "supported": the emoji path is
     * the better-looking one when it works, and assuming the worst on an unknown device would
     * downgrade every flag in the app on the strength of a check that itself did not run.
     */
    val emojiFlagsSupported: Boolean by lazy(LazyThreadSafetyMode.PUBLICATION) {
        runCatching { platformHasGlyph(PROBE_FLAG) }.getOrDefault(true)
    }
}

/** Whether the platform's font stack draws [text] as a single glyph. */
internal expect fun platformHasGlyph(text: String): Boolean
