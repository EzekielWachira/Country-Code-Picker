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

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

/**
 * Text styles for every piece of text the picker draws.
 *
 * Numbers — dial codes, phone numbers, counts — use tabular figures (`tnum`), so digits share one
 * width: a column of dial codes lines up, and a phone number does not jitter as it is typed and
 * regrouped.
 *
 * @property title Sheet title.
 * @property subtitle Sheet subtitle.
 * @property sectionLabel Section headers ("SUGGESTED", "ALL COUNTRIES"), drawn in capitals.
 * @property countryName A country name in a row.
 * @property countryNameSelected A selected country's name.
 * @property caption Secondary lines: ISO codes, regions, result counts.
 * @property dialCode Dial codes in rows and pills.
 * @property fieldLabel The label above a field's value.
 * @property fieldValue A field's value.
 * @property phoneNumber The number in the phone field.
 * @property helper Helper and error text under a field.
 * @property button Button labels.
 * @property chip Region filter labels.
 * @property badge Small labels such as "Detected" and "Mobile".
 * @property search Search input and placeholder.
 * @property tileLabel Quick-pick tile labels.
 * @property indexLetter Letters on the A–Z rail.
 * @property indexBubble The large letter in the scrubbing bubble.
 * @property emptyTitle Empty, error and offline state titles.
 * @property emptyBody Empty, error and offline state bodies.
 */
@Immutable
public data class CountryPickerTypography(
    val title: TextStyle,
    val subtitle: TextStyle,
    val sectionLabel: TextStyle,
    val countryName: TextStyle,
    val countryNameSelected: TextStyle,
    val caption: TextStyle,
    val dialCode: TextStyle,
    val fieldLabel: TextStyle,
    val fieldValue: TextStyle,
    val phoneNumber: TextStyle,
    val helper: TextStyle,
    val button: TextStyle,
    val chip: TextStyle,
    val badge: TextStyle,
    val search: TextStyle,
    val tileLabel: TextStyle,
    val indexLetter: TextStyle,
    val indexBubble: TextStyle,
    val emptyTitle: TextStyle,
    val emptyBody: TextStyle,
) {
    public companion object {
        /**
         * The Signature type scale in [fontFamily] — by default the platform's own (Roboto on
         * Android, SF Pro on iOS), or the host's brand family when one is passed.
         */
        public fun signature(fontFamily: FontFamily = FontFamily.Default): CountryPickerTypography {
            fun style(size: Float, weight: FontWeight, lineHeight: Float, tracking: Float = 0f) = TextStyle(
                fontFamily = fontFamily,
                fontSize = size.sp,
                fontWeight = weight,
                lineHeight = lineHeight.sp,
                letterSpacing = tracking.em,
            )
            val tabular = TABULAR_FIGURES
            return CountryPickerTypography(
                title = style(24f, FontWeight.SemiBold, 30f, -0.018f),
                subtitle = style(14.5f, FontWeight.Normal, 20f, -0.004f),
                sectionLabel = style(12f, FontWeight.SemiBold, 16f, 0.06f),
                countryName = style(16f, FontWeight.Medium, 22f, -0.011f),
                countryNameSelected = style(16f, FontWeight.SemiBold, 22f, -0.011f),
                caption = style(13f, FontWeight.Normal, 18f).copy(fontFeatureSettings = tabular),
                dialCode = style(15f, FontWeight.Medium, 20f).copy(fontFeatureSettings = tabular),
                fieldLabel = style(12.5f, FontWeight.Medium, 16f, 0.004f),
                fieldValue = style(16.5f, FontWeight.Medium, 22f, -0.011f),
                phoneNumber = style(17f, FontWeight.Medium, 22f, 0.01f).copy(fontFeatureSettings = tabular),
                helper = style(13f, FontWeight.Normal, 18f).copy(fontFeatureSettings = tabular),
                button = style(15.5f, FontWeight.SemiBold, 20f, -0.006f).copy(fontFeatureSettings = tabular),
                chip = style(14f, FontWeight.Medium, 18f, -0.004f).copy(fontFeatureSettings = tabular),
                badge = style(11f, FontWeight.SemiBold, 14f, 0.02f),
                search = style(16f, FontWeight.Normal, 22f, -0.011f),
                tileLabel = style(12.5f, FontWeight.SemiBold, 16f).copy(fontFeatureSettings = tabular),
                indexLetter = style(10.5f, FontWeight.SemiBold, 13f),
                indexBubble = style(28f, FontWeight.Bold, 32f),
                emptyTitle = style(18f, FontWeight.SemiBold, 24f, -0.014f),
                emptyBody = style(14.5f, FontWeight.Normal, 21f),
            )
        }
    }
}

/** OpenType feature string for tabular (fixed-width) figures. */
internal const val TABULAR_FIGURES: String = "tnum"
