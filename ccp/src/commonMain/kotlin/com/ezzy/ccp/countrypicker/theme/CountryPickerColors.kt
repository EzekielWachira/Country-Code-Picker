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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance

/**
 * The picker's color roles.
 *
 * A deliberately small set of *semantic* roles — surfaces, lines, content and status — that every
 * component derives its colors from, rather than one property per pixel. Retheming is therefore a
 * matter of meaning ("my cards are this white, my accent is this teal") instead of hunting for the
 * one property out of forty that paints a particular border.
 *
 * Build one from a preset in [CountryPickerStyles] and adjust with `copy`, or construct it outright
 * for a fully custom palette.
 *
 * ### Surfaces
 * The picker composes four surface levels so depth reads without relying on shadows alone:
 * [background] is the canvas of a sheet or dialog; [surface] is a card, a field or a grouped list
 * sitting on it; [surfaceRaised] floats above that (the multi-select bar, quick-pick tiles); and
 * [surfaceSunken] is recessed into it (the search field, the segment track behind region filters,
 * the tile behind a flag).
 *
 * @property accent The brand color: focus, selection, primary actions.
 * @property onAccent Content drawn on [accent].
 * @property accentSoft A quiet tint of the accent, for selected rows and active filters.
 * @property onAccentSoft Content drawn on [accentSoft].
 * @property background Sheet and dialog canvas.
 * @property surface Cards, fields and grouped lists.
 * @property surfaceRaised Elements floating above cards.
 * @property surfaceSunken Elements recessed into cards.
 * @property scrim Dim layer behind a sheet or dialog.
 * @property hairline Dividers and resting card borders.
 * @property outline Field borders at rest — a step stronger than [hairline].
 * @property focusRing The soft glow drawn around a focused or open field.
 * @property textPrimary Country names and field values.
 * @property textSecondary Labels, subtitles and metadata.
 * @property textTertiary Placeholders, ghost digits and the index rail.
 * @property textDisabled Disabled content. Still legible: a field nobody can read is worse than one
 *   nobody can edit.
 * @property error Error borders, text and icons.
 * @property errorSoft Error-tinted containers.
 * @property success Valid and verified states.
 * @property successSoft Success-tinted containers, such as the number-type badge.
 * @property warning Limits and soft warnings, such as the maximum-selection notice.
 * @property warningSoft Warning-tinted containers.
 * @property highlight Background behind the part of a country name that matched a search.
 * @property shadow Tint of every shadow layer.
 * @property skeleton Loading-placeholder fill.
 * @property skeletonShine The moving highlight swept across [skeleton].
 */
@Immutable
public data class CountryPickerColors(
    val accent: Color,
    val onAccent: Color,
    val accentSoft: Color,
    val onAccentSoft: Color,
    val background: Color,
    val surface: Color,
    val surfaceRaised: Color,
    val surfaceSunken: Color,
    val scrim: Color,
    val hairline: Color,
    val outline: Color,
    val focusRing: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val textDisabled: Color,
    val error: Color,
    val errorSoft: Color,
    val success: Color,
    val successSoft: Color,
    val warning: Color,
    val warningSoft: Color,
    val highlight: Color,
    val shadow: Color,
    val skeleton: Color,
    val skeletonShine: Color,
) {
    /** True when this palette is a dark one, judged from its [background]. */
    val isDark: Boolean get() = background.luminance() < DARK_LUMINANCE_THRESHOLD

    public companion object {
        /**
         * The Signature neutrals around an [accent]: a cool grey canvas with white cards in light
         * mode, near-black layers in dark mode.
         *
         * Status colors are tuned for at least 4.5:1 contrast on [surface] in both modes.
         *
         * @param onAccent Content on the accent. Defaults to white when that reaches 3:1 contrast
         *   against [accent], and black otherwise.
         */
        public fun signature(
            accent: Color,
            onAccent: Color = readableOn(accent),
            dark: Boolean = false,
        ): CountryPickerColors = if (dark) {
            val surface = Color(0xFF17181C)
            CountryPickerColors(
                accent = accent,
                onAccent = onAccent,
                accentSoft = accent.copy(alpha = 0.18f).compositeOver(surface),
                onAccentSoft = accent.blendTowards(Color.White, 0.35f),
                background = Color(0xFF0C0D10),
                surface = surface,
                surfaceRaised = Color(0xFF1F2025),
                surfaceSunken = Color(0xFF111215),
                scrim = Color.Black.copy(alpha = 0.6f),
                hairline = Color.White.copy(alpha = 0.08f),
                outline = Color.White.copy(alpha = 0.16f),
                focusRing = accent.copy(alpha = 0.32f),
                textPrimary = Color(0xFFF4F4F6),
                textSecondary = Color(0xFFF4F4F6).copy(alpha = 0.64f),
                textTertiary = Color(0xFFF4F4F6).copy(alpha = 0.40f),
                textDisabled = Color(0xFFF4F4F6).copy(alpha = 0.32f),
                error = Color(0xFFFF6369),
                errorSoft = Color(0xFFFF6369).copy(alpha = 0.16f),
                success = Color(0xFF4CC38A),
                successSoft = Color(0xFF4CC38A).copy(alpha = 0.16f),
                warning = Color(0xFFFFB224),
                warningSoft = Color(0xFFFFB224).copy(alpha = 0.16f),
                highlight = accent.copy(alpha = 0.30f),
                shadow = Color.Black,
                skeleton = Color.White.copy(alpha = 0.06f),
                skeletonShine = Color.White.copy(alpha = 0.12f),
            )
        } else {
            val surface = Color.White
            CountryPickerColors(
                accent = accent,
                onAccent = onAccent,
                accentSoft = accent.copy(alpha = 0.10f).compositeOver(surface),
                onAccentSoft = accent.blendTowards(Color.Black, 0.25f),
                background = Color(0xFFF4F5F7),
                surface = surface,
                surfaceRaised = surface,
                surfaceSunken = Color(0xFFEFF0F3),
                scrim = Color(0xFF0B0C10).copy(alpha = 0.42f),
                hairline = Color(0xFF0B0C10).copy(alpha = 0.07f),
                outline = Color(0xFF0B0C10).copy(alpha = 0.14f),
                focusRing = accent.copy(alpha = 0.20f),
                textPrimary = Color(0xFF0B0C10),
                textSecondary = Color(0xFF0B0C10).copy(alpha = 0.60f),
                textTertiary = Color(0xFF0B0C10).copy(alpha = 0.38f),
                textDisabled = Color(0xFF0B0C10).copy(alpha = 0.30f),
                error = Color(0xFFDC3E42),
                errorSoft = Color(0xFFDC3E42).copy(alpha = 0.10f),
                success = Color(0xFF218358),
                successSoft = Color(0xFF218358).copy(alpha = 0.10f),
                warning = Color(0xFFAB6400),
                warningSoft = Color(0xFFFFB224).copy(alpha = 0.18f),
                highlight = accent.copy(alpha = 0.18f),
                shadow = Color(0xFF0B0C10),
                skeleton = Color(0xFF0B0C10).copy(alpha = 0.06f),
                skeletonShine = Color.White.copy(alpha = 0.7f),
            )
        }
    }
}

/** Linear blend of this color towards [other] by [fraction], keeping this color's alpha. */
internal fun Color.blendTowards(other: Color, fraction: Float): Color = Color(
    red = red + (other.red - red) * fraction,
    green = green + (other.green - green) * fraction,
    blue = blue + (other.blue - blue) * fraction,
    alpha = alpha,
)

/**
 * White or black content for [background]: white when it reaches 3:1 contrast — the WCAG minimum for
 * UI components and bold text — and black otherwise.
 *
 * Biased towards white rather than picking the strictly higher contrast, because that is the
 * convention for saturated accents: iOS's system blue carries white text at about 4:1, where black
 * would technically contrast more. A pale accent — a dark theme's lavender primary — gets black.
 */
internal fun readableOn(background: Color): Color {
    val whiteContrast = (1f + WCAG_FLARE) / (background.luminance() + WCAG_FLARE)
    return if (whiteContrast >= MIN_ON_ACCENT_CONTRAST) Color.White else Color.Black
}

private const val DARK_LUMINANCE_THRESHOLD = 0.4f
private const val WCAG_FLARE = 0.05f
private const val MIN_ON_ACCENT_CONTRAST = 3f
