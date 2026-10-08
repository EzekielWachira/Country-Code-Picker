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

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ezzy.ccp.countrypicker.model.UiText
import com.ezzy.ccp.countrypicker.model.resolve
import com.ezzy.ccp.countrypicker.theme.CountryPickerStyle

/** The validation tone a field is drawn in. */
internal enum class FieldTone { Default, Error, Success }

/**
 * The container every field in the library is drawn in — selectors, the multiple-selection field and
 * the phone field — so they share one look for depth, focus, validation and press feedback.
 *
 * - **Active** (focused, or its picker is open): the border turns to the accent and thickens, and a
 *   soft ring blooms around the field.
 * - **Error**: the border and ring turn to the error color, and the field shakes once on entry.
 * - **Success**: the border turns to the success color.
 * - **Disabled**: content dims and the shadow drops away.
 *
 * Thickening the border, not only recolouring it, gives focus and error a cue that does not depend
 * on color alone.
 *
 * @param onClick Makes the whole field one button. `null` for a field whose content handles its own
 *   input (the phone field).
 * @param overlay Drawn over the content, clipped to the field and moving with it — the phone field's
 *   progress bar.
 */
@Composable
internal fun PickerFieldContainer(
    variant: CountrySelectorVariant,
    active: Boolean,
    tone: FieldTone,
    enabled: Boolean,
    style: CountryPickerStyle,
    minHeight: Dp,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource? = null,
    onClick: (() -> Unit)? = null,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(style.dimensions.fieldContentSpacing),
    overlay: (@Composable BoxScope.() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit,
) {
    val colors = style.colors
    val dimensions = style.dimensions
    val motion = style.motion
    val shape = variant.shape(style)
    val haptics = rememberPickerHaptics()

    val toneColor = when (tone) {
        FieldTone.Error -> colors.error
        FieldTone.Success -> colors.success
        FieldTone.Default -> null
    }
    val restingBorder = when (variant) {
        CountrySelectorVariant.Elevated, CountrySelectorVariant.Card -> colors.hairline
        CountrySelectorVariant.Outlined -> colors.outline
        else -> Color.Transparent
    }
    val border by animateColorAsState(
        targetValue = when {
            !enabled -> restingBorder
            toneColor != null -> toneColor
            active -> colors.accent
            else -> restingBorder
        },
        animationSpec = motion.color,
        label = "fieldBorder",
    )
    val emphasised = enabled && (active || tone == FieldTone.Error)
    val borderWidth by animateDpAsState(
        targetValue = if (emphasised) dimensions.fieldFocusedBorderWidth else dimensions.fieldBorderWidth,
        animationSpec = motion.dp,
        label = "fieldBorderWidth",
    )
    val ring by animateFloatAsState(if (emphasised) 1f else 0f, motion.layout, label = "fieldRing")
    val ringColor = if (tone == FieldTone.Error) colors.error.copy(alpha = 0.18f) else colors.focusRing
    val shake by rememberShakeOffset(tone == FieldTone.Error)
    LaunchedEffect(tone) { if (tone == FieldTone.Error) haptics.error() }

    val container = when (variant) {
        CountrySelectorVariant.Elevated, CountrySelectorVariant.Card -> colors.surface
        CountrySelectorVariant.Filled, CountrySelectorVariant.Compact,
        CountrySelectorVariant.FlagOnly, CountrySelectorVariant.DialCode -> colors.surfaceSunken
        CountrySelectorVariant.Outlined, CountrySelectorVariant.Underlined -> Color.Transparent
    }
    val elevated = enabled && (variant == CountrySelectorVariant.Elevated || variant == CountrySelectorVariant.Card)
    val shadow = if (active) style.elevation.fieldFocused else style.elevation.field
    val scale = if (interactionSource != null && onClick != null) pressScale(interactionSource, enabled).value else 1f
    val underline = variant == CountrySelectorVariant.Underlined

    Box(
        modifier = modifier
            .graphicsLayer {
                translationX = shake
                scaleX = scale
                scaleY = scale
                alpha = if (enabled) 1f else DISABLED_ALPHA
            }
            .then(if (underline) Modifier else Modifier.focusRing(ring, dimensions.focusRingWidth, ringColor, shape))
            .then(if (elevated) Modifier.pickerShadow(shadow, shape, colors.shadow) else Modifier)
            .clip(shape)
            .background(container)
            .then(
                if (underline) {
                    Modifier.drawWithContent {
                        drawContent()
                        val width = (if (emphasised) dimensions.fieldFocusedBorderWidth * 1.5f else dimensions.fieldBorderWidth).toPx()
                        val color = if (border == Color.Transparent) colors.outline else border
                        drawLine(color, Offset(0f, size.height - width / 2), Offset(size.width, size.height - width / 2), width)
                    }
                } else {
                    Modifier.border(borderWidth, border, shape)
                },
            )
            .then(
                if (onClick != null && interactionSource != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        enabled = enabled,
                        role = Role.Button,
                        onClick = onClick,
                    )
                } else {
                    Modifier
                },
            ),
    ) {
        Row(
            modifier = Modifier
                .defaultMinSize(minHeight = minHeight)
                .padding(contentPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = horizontalArrangement,
            content = content,
        )
        overlay?.invoke(this)
    }
}

/** The container shape for each variant. */
internal fun CountrySelectorVariant.shape(style: CountryPickerStyle): Shape = when (this) {
    CountrySelectorVariant.Compact, CountrySelectorVariant.FlagOnly, CountrySelectorVariant.DialCode -> style.shapes.pill
    CountrySelectorVariant.Underlined -> androidx.compose.ui.graphics.RectangleShape
    CountrySelectorVariant.Card -> androidx.compose.foundation.shape.RoundedCornerShape(style.shapes.groupCornerRadius)
    else -> style.shapes.field
}

/**
 * Helper, error or success text under a field, with an icon for the two validation tones. Changes
 * cross-fade, and the line is a polite live region so a new error is announced without interrupting.
 *
 * Sized to its text, never stretched to the parent's width: an empty line under a pill selector
 * must not widen the pill's column and push the pill beside it onto the next line.
 */
@Composable
internal fun FieldHelperText(
    text: UiText?,
    tone: FieldTone,
    style: CountryPickerStyle,
    modifier: Modifier = Modifier,
) {
    val colors = style.colors
    AnimatedContent(
        targetState = text to tone,
        transitionSpec = { fadeIn(style.motion.fadeIn) togetherWith fadeOut(style.motion.fadeOut) },
        modifier = modifier,
        label = "fieldHelper",
    ) { (message, messageTone) ->
        if (message == null) return@AnimatedContent
        val color = when (messageTone) {
            FieldTone.Error -> colors.error
            FieldTone.Success -> colors.success
            FieldTone.Default -> colors.textSecondary
        }
        Row(
            modifier = Modifier
                .padding(start = 4.dp, end = 4.dp, top = 7.dp)
                .semantics { liveRegion = LiveRegionMode.Polite },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            when (messageTone) {
                FieldTone.Error -> Icon(PickerIcons.Alert, contentDescription = null, tint = color, modifier = Modifier.size(15.dp))
                FieldTone.Success -> Icon(PickerIcons.CheckCircle, contentDescription = null, tint = color, modifier = Modifier.size(15.dp))
                FieldTone.Default -> Unit
            }
            Text(text = message.resolve(), style = style.typography.helper, color = color)
        }
    }
}

private const val DISABLED_ALPHA = 0.5f
