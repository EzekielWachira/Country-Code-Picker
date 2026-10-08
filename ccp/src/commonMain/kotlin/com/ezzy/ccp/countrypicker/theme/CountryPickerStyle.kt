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

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

/**
 * Everything about how the country picker looks and feels, in one value.
 *
 * Set it once for a whole screen or app with [CountryPickerTheme], or pass it to a single component's
 * `style` parameter. Start from a preset in [CountryPickerStyles] and refine it with `copy`:
 *
 * ```kotlin
 * val brand = CountryPickerStyles.signature(accent = Color(0xFF0F766E))
 * CountryPickerTheme(
 *     style = brand.copy(layout = brand.layout.copy(flagStyle = CountryFlagStyle.Circle)),
 * ) {
 *     CountrySelector(selectedCountry = country, onCountrySelected = { country = it })
 * }
 * ```
 *
 * @property colors Color roles.
 * @property shapes Corner shapes.
 * @property dimensions Sizes and spacing — see [CountryPickerDimensions.forDensity].
 * @property typography Text styles.
 * @property motion Animation specs.
 * @property elevation Shadows.
 * @property layout Structural choices: presentation, list style, flags, quick picks.
 * @property haptics Haptic feedback.
 */
@Immutable
public data class CountryPickerStyle(
    val colors: CountryPickerColors,
    val shapes: CountryPickerShapes,
    val dimensions: CountryPickerDimensions,
    val typography: CountryPickerTypography,
    val motion: CountryPickerMotion,
    val elevation: CountryPickerElevation,
    val layout: CountryPickerLayout,
    val haptics: CountryPickerHaptics,
) {
    /** This style with its dimensions replaced by those of [density]. */
    public fun withDensity(density: CountryPickerDensity): CountryPickerStyle =
        copy(dimensions = CountryPickerDimensions.forDensity(density))
}

/**
 * The style in effect, or `null` when no [CountryPickerTheme] encloses the composition — in which
 * case components fall back to [CountryPickerStyles.signature].
 */
public val LocalCountryPickerStyle: ProvidableCompositionLocal<CountryPickerStyle?> =
    staticCompositionLocalOf { null }

/**
 * Applies [style] to every picker component inside [content].
 *
 * Components read the style from here unless one is passed to them directly, so one call at the
 * root of a screen — or of the app — themes every selector, sheet and phone field below it.
 */
@Composable
public fun CountryPickerTheme(
    style: CountryPickerStyle = CountryPickerStyles.signature(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalCountryPickerStyle provides style, content = content)
}

/** Access to the style in effect. */
public object CountryPickerTheme {
    /** The style set by the nearest [CountryPickerTheme], or [CountryPickerStyles.signature]. */
    public val style: CountryPickerStyle
        @Composable
        @ReadOnlyComposable
        get() = LocalCountryPickerStyle.current ?: CountryPickerStyles.signature()
}

/**
 * Ready-made styles.
 *
 * Every preset follows the light or dark mode of the enclosing `MaterialTheme` (judged from its
 * surface color, so an app-level theme override is respected, not just the system setting), takes
 * the host's brand color as its accent unless told otherwise, and turns motion off when the system
 * asks for reduced motion.
 */
public object CountryPickerStyles {

    /**
     * The default look: inset grouped lists on a cool grey canvas, flags on soft tiles, layered
     * shadows, a sliding region filter and a quick-pick carousel.
     *
     * @param accent Brand color. Defaults to the host theme's primary.
     * @param dark Dark variant. Defaults to the enclosing theme's mode.
     * @param density Spacing.
     * @param fontFamily Typeface. Defaults to the host theme's body typeface.
     */
    @Composable
    @ReadOnlyComposable
    public fun signature(
        accent: Color = MaterialTheme.colorScheme.primary,
        dark: Boolean = isDarkTheme(),
        density: CountryPickerDensity = CountryPickerDensity.Comfortable,
        fontFamily: FontFamily = hostFontFamily(),
    ): CountryPickerStyle = CountryPickerStyle(
        colors = CountryPickerColors.signature(accent = accent, dark = dark),
        shapes = CountryPickerShapes(),
        dimensions = CountryPickerDimensions.forDensity(density),
        typography = CountryPickerTypography.signature(fontFamily),
        motion = CountryPickerMotion().respectingSystemAnimationScale(),
        elevation = CountryPickerElevation.signature(dark),
        layout = CountryPickerLayout(),
        haptics = CountryPickerHaptics(),
    )

    /**
     * Material 3: every color taken from the enclosing `MaterialTheme`, tonal surfaces instead of
     * shadows, edge-to-edge rows and circular flags.
     */
    @Composable
    @ReadOnlyComposable
    public fun material(
        density: CountryPickerDensity = CountryPickerDensity.Comfortable,
    ): CountryPickerStyle {
        val scheme = MaterialTheme.colorScheme
        val type = MaterialTheme.typography
        val dark = isDarkTheme()
        val tabular = TABULAR_FIGURES
        return CountryPickerStyle(
            colors = CountryPickerColors(
                accent = scheme.primary,
                onAccent = scheme.onPrimary,
                accentSoft = scheme.secondaryContainer,
                onAccentSoft = scheme.onSecondaryContainer,
                background = scheme.surfaceContainerLow,
                surface = scheme.surfaceContainerLow,
                surfaceRaised = scheme.surfaceContainerHigh,
                surfaceSunken = scheme.surfaceContainerHighest,
                scrim = scheme.scrim.copy(alpha = 0.32f),
                hairline = scheme.outlineVariant,
                outline = scheme.outline,
                focusRing = scheme.primary.copy(alpha = 0.16f),
                textPrimary = scheme.onSurface,
                textSecondary = scheme.onSurfaceVariant,
                textTertiary = scheme.onSurfaceVariant.copy(alpha = 0.7f),
                textDisabled = scheme.onSurface.copy(alpha = 0.38f),
                error = scheme.error,
                errorSoft = scheme.errorContainer,
                success = if (dark) Color(0xFF8FD8AE) else Color(0xFF2E6B4F),
                successSoft = (if (dark) Color(0xFF8FD8AE) else Color(0xFF2E6B4F)).copy(alpha = 0.14f),
                warning = if (dark) Color(0xFFFFB95C) else Color(0xFF8A5100),
                warningSoft = Color(0xFFFFB95C).copy(alpha = 0.18f),
                highlight = scheme.primary.copy(alpha = 0.24f),
                shadow = Color.Black,
                skeleton = scheme.onSurface.copy(alpha = 0.08f),
                skeletonShine = scheme.surface.copy(alpha = 0.6f),
            ),
            shapes = CountryPickerShapes(
                sheet = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                dialog = RoundedCornerShape(28.dp),
                field = RoundedCornerShape(12.dp),
                searchField = CircleShape,
                groupCornerRadius = 16.dp,
                row = RoundedCornerShape(12.dp),
                chip = RoundedCornerShape(8.dp),
                button = RoundedCornerShape(20.dp),
                tile = RoundedCornerShape(16.dp),
                floatingBar = RoundedCornerShape(16.dp),
            ),
            dimensions = CountryPickerDimensions.forDensity(density),
            typography = CountryPickerTypography(
                title = type.headlineSmall,
                subtitle = type.bodyMedium,
                sectionLabel = type.labelMedium.copy(letterSpacing = 0.06.em),
                countryName = type.bodyLarge,
                countryNameSelected = type.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                caption = type.bodySmall.copy(fontFeatureSettings = tabular),
                dialCode = type.bodyLarge.copy(fontWeight = FontWeight.Medium, fontFeatureSettings = tabular),
                fieldLabel = type.bodySmall,
                fieldValue = type.bodyLarge,
                phoneNumber = type.bodyLarge.copy(fontFeatureSettings = tabular),
                helper = type.bodySmall,
                button = type.labelLarge,
                chip = type.labelLarge.copy(fontFeatureSettings = tabular),
                badge = type.labelSmall.copy(fontWeight = FontWeight.Medium),
                search = type.bodyLarge,
                tileLabel = type.labelMedium.copy(fontFeatureSettings = tabular),
                indexLetter = type.labelSmall,
                indexBubble = type.headlineMedium.copy(fontWeight = FontWeight.Bold),
                emptyTitle = type.titleMedium,
                emptyBody = type.bodyMedium,
            ),
            motion = CountryPickerMotion().respectingSystemAnimationScale(),
            elevation = CountryPickerElevation.Flat,
            layout = CountryPickerLayout(
                listStyle = CountryListStyle.Plain,
                flagStyle = CountryFlagStyle.Circle,
                selectionIndicator = SelectionIndicator.Radio,
                quickPicks = QuickPicksStyle.Sections,
            ),
            haptics = CountryPickerHaptics(),
        )
    }

    /**
     * iOS conventions on any platform: system blue, grouped inset lists on the iOS grouped
     * background, a large bold title, bare emoji flags and no shadows.
     *
     * @param accent Tint. Defaults to iOS system blue.
     * @param dark Dark variant. Defaults to the enclosing theme's mode.
     */
    @Composable
    @ReadOnlyComposable
    public fun cupertino(
        accent: Color? = null,
        dark: Boolean = isDarkTheme(),
        density: CountryPickerDensity = CountryPickerDensity.Comfortable,
    ): CountryPickerStyle {
        val tint = accent ?: if (dark) Color(0xFF0A84FF) else Color(0xFF007AFF)
        val label = if (dark) Color.White else Color.Black
        val secondaryLabel = if (dark) Color(0xFFEBEBF5).copy(alpha = 0.6f) else Color(0xFF3C3C43).copy(alpha = 0.6f)
        val tertiaryLabel = if (dark) Color(0xFFEBEBF5).copy(alpha = 0.3f) else Color(0xFF3C3C43).copy(alpha = 0.3f)
        val surface = if (dark) Color(0xFF1C1C1E) else Color.White
        val fill = (if (dark) Color(0xFF767680).copy(alpha = 0.24f) else Color(0xFF767680).copy(alpha = 0.12f))
            .compositeOver(surface)
        val red = if (dark) Color(0xFFFF453A) else Color(0xFFFF3B30)
        val green = if (dark) Color(0xFF30D158) else Color(0xFF248A3D)
        val orange = if (dark) Color(0xFFFF9F0A) else Color(0xFFC93400)
        val tabular = TABULAR_FIGURES
        fun text(size: Float, weight: FontWeight, line: Float, tracking: Float = 0f) =
            CountryPickerTypography.signature().title.copy(
                fontSize = size.sp,
                fontWeight = weight,
                lineHeight = line.sp,
                letterSpacing = tracking.em,
            )
        return CountryPickerStyle(
            colors = CountryPickerColors(
                accent = tint,
                onAccent = Color.White,
                accentSoft = tint.copy(alpha = if (dark) 0.22f else 0.12f).compositeOver(surface),
                onAccentSoft = tint,
                background = if (dark) Color.Black else Color(0xFFF2F2F7),
                surface = surface,
                surfaceRaised = if (dark) Color(0xFF2C2C2E) else Color.White,
                surfaceSunken = fill,
                scrim = Color.Black.copy(alpha = if (dark) 0.6f else 0.4f),
                hairline = if (dark) Color(0xFF545458).copy(alpha = 0.6f) else Color(0xFF3C3C43).copy(alpha = 0.29f),
                outline = if (dark) Color(0xFF545458) else Color(0xFFC6C6C8),
                focusRing = tint.copy(alpha = 0.24f),
                textPrimary = label,
                textSecondary = secondaryLabel,
                textTertiary = tertiaryLabel,
                textDisabled = tertiaryLabel,
                error = red,
                errorSoft = red.copy(alpha = 0.14f),
                success = green,
                successSoft = green.copy(alpha = 0.14f),
                warning = orange,
                warningSoft = orange.copy(alpha = 0.16f),
                highlight = tint.copy(alpha = 0.22f),
                shadow = Color.Black,
                skeleton = fill,
                skeletonShine = surface.copy(alpha = 0.7f),
            ),
            shapes = CountryPickerShapes(
                sheet = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp),
                dialog = RoundedCornerShape(14.dp),
                field = RoundedCornerShape(10.dp),
                searchField = RoundedCornerShape(10.dp),
                groupCornerRadius = 10.dp,
                row = RoundedCornerShape(10.dp),
                button = RoundedCornerShape(12.dp),
                tile = RoundedCornerShape(12.dp),
                floatingBar = RoundedCornerShape(14.dp),
            ),
            dimensions = CountryPickerDimensions.forDensity(density).copy(rowMinHeight = 48.dp, flagSizeRow = 30.dp),
            typography = CountryPickerTypography.signature().copy(
                title = text(28f, FontWeight.Bold, 34f, 0.012f),
                subtitle = text(15f, FontWeight.Normal, 20f, -0.016f),
                sectionLabel = text(13f, FontWeight.Normal, 18f, -0.006f),
                countryName = text(17f, FontWeight.Normal, 22f, -0.024f),
                countryNameSelected = text(17f, FontWeight.Normal, 22f, -0.024f),
                caption = text(13f, FontWeight.Normal, 18f, -0.006f).copy(fontFeatureSettings = tabular),
                dialCode = text(17f, FontWeight.Normal, 22f, -0.024f).copy(fontFeatureSettings = tabular),
                fieldValue = text(17f, FontWeight.Normal, 22f, -0.024f),
                phoneNumber = text(17f, FontWeight.Normal, 22f).copy(fontFeatureSettings = tabular),
                search = text(17f, FontWeight.Normal, 22f, -0.024f),
                button = text(17f, FontWeight.SemiBold, 22f, -0.024f),
            ),
            motion = CountryPickerMotion().respectingSystemAnimationScale(),
            elevation = CountryPickerElevation.Flat,
            layout = CountryPickerLayout(
                flagStyle = CountryFlagStyle.Plain,
                quickPicks = QuickPicksStyle.Sections,
            ),
            haptics = CountryPickerHaptics(),
        )
    }

    /**
     * Pared back: monochrome, hairlines instead of shadows, plain rows and flat flags, all one size. For products
     * whose own design language is minimal and should not be competed with.
     */
    @Composable
    @ReadOnlyComposable
    public fun minimal(
        dark: Boolean = isDarkTheme(),
        density: CountryPickerDensity = CountryPickerDensity.Comfortable,
    ): CountryPickerStyle {
        val base = signature(accent = if (dark) Color(0xFFF4F4F6) else Color(0xFF0B0C10), dark = dark, density = density)
        return base.copy(
            colors = base.colors.copy(
                background = base.colors.surface,
                accentSoft = base.colors.surfaceSunken,
                onAccentSoft = base.colors.textPrimary,
                highlight = base.colors.textPrimary.copy(alpha = 0.10f),
            ),
            shapes = base.shapes.copy(
                sheet = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                field = RoundedCornerShape(10.dp),
                searchField = RoundedCornerShape(10.dp),
                row = RoundedCornerShape(10.dp),
                button = RoundedCornerShape(10.dp),
                tile = RoundedCornerShape(12.dp),
            ),
            elevation = CountryPickerElevation.Flat,
            layout = base.layout.copy(
                listStyle = CountryListStyle.Plain,
                // Flat flags, cropped to one size: the quietest way to show them.
                flagStyle = CountryFlagStyle.Rounded,
                quickPicks = QuickPicksStyle.Sections,
            ),
        )
    }
}

/** Dark when the enclosing MaterialTheme's surface is dark — so app-level overrides are honoured. */
@Composable
@ReadOnlyComposable
internal fun isDarkTheme(): Boolean = MaterialTheme.colorScheme.surface.luminance() < DARK_SURFACE_LUMINANCE

/** The host theme's body typeface, so the picker matches surrounding text. */
@Composable
@ReadOnlyComposable
internal fun hostFontFamily(): FontFamily = MaterialTheme.typography.bodyLarge.fontFamily ?: FontFamily.Default

private const val DARK_SURFACE_LUMINANCE = 0.5f
