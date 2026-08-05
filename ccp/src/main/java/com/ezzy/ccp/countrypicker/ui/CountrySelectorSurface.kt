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

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ezzy.ccp.countrypicker.theme.CountryPickerColors
import com.ezzy.ccp.countrypicker.theme.CountryPickerDimensions
import com.ezzy.ccp.countrypicker.theme.CountryPickerMotion
import com.ezzy.ccp.countrypicker.theme.CountryPickerShapes

/**
 * The clickable, animated container every [CountrySelector] variant is built from.
 *
 * Centralising this is what guarantees the properties that are easy to get wrong per-variant:
 *
 * - **The whole surface is the target, not just the chevron.** A dropdown whose arrow is the only hit
 *   area is a recurring accessibility failure; here the [Surface] itself is clickable.
 * - **The ripple is clipped to the actual shape.** [Surface] clips its indication to the `shape` it is
 *   given, so a pill ripples as a pill and a rounded field ripples within its corners.
 * - **The touch target never drops below 48dp**, via [CountryPickerDimensions.minimumTouchTarget],
 *   even for the visually 40dp compact pill.
 * - **State transitions are animated**, so moving between default, focused, error and disabled reads
 *   as one field changing rather than four different fields.
 *
 * @param onClick Invoked on tap. Not called when [enabled] is false.
 * @param shape Both the visual shape and the ripple mask — they cannot diverge.
 * @param minHeight Visual minimum, raised to the accessibility floor internally.
 */
@Composable
internal fun CountrySelectorSurface(
    onClick: () -> Unit,
    enabled: Boolean,
    shape: Shape,
    containerColor: Color,
    borderColor: Color?,
    borderWidth: Dp,
    minHeight: Dp,
    contentPadding: androidx.compose.foundation.layout.PaddingValues,
    horizontalArrangement: Arrangement.Horizontal,
    fillWidth: Boolean,
    colors: CountryPickerColors,
    dimensions: CountryPickerDimensions,
    motion: CountryPickerMotion,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    // RowScope so variant content can claim the space between flag and chevron with `weight`.
    content: @Composable RowScope.() -> Unit,
) {
    val isPressed by interactionSource.collectIsPressedAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()

    // Animate between states so error/focus/disabled feel like transitions rather than redraws.
    val animatedContainer by animateColorAsState(
        targetValue = containerColor,
        animationSpec = motion.colorSpec,
        label = "selectorContainer",
    )
    val animatedBorder by animateColorAsState(
        targetValue = borderColor ?: Color.Transparent,
        animationSpec = motion.colorSpec,
        label = "selectorBorder",
    )
    val animatedBorderWidth by animateDpAsState(
        targetValue = if (isFocused) dimensions.selectorFocusedBorderWidth else borderWidth,
        animationSpec = motion.dpSpec,
        label = "selectorBorderWidth",
    )
    // A slight scale-down on press is the tactile cue the design specifies; it also survives
    // reduced-motion because the ripple still communicates the touch.
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed && enabled) PRESSED_SCALE else 1f,
        animationSpec = motion.floatSpec,
        label = "selectorPressScale",
    )

    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        color = animatedContainer,
        border = if (borderColor != null && animatedBorderWidth > 0.dp) {
            BorderStroke(animatedBorderWidth, animatedBorder)
        } else {
            null
        },
        interactionSource = interactionSource,
        modifier = modifier
            .then(if (fillWidth) Modifier.fillMaxWidth() else Modifier)
            .scale(pressScale)
            // Visual minimum and the 48dp accessibility floor, whichever is larger.
            .defaultMinSize(minHeight = maxOf(minHeight, dimensions.minimumTouchTarget)),
    ) {
        Row(
            modifier = Modifier.padding(contentPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = horizontalArrangement,
        ) {
            content()
        }
    }
}

/** Resolves the shape for a variant. */
internal fun CountrySelectorVariant.shape(shapes: CountryPickerShapes): Shape = when (this) {
    CountrySelectorVariant.Filled -> shapes.selectorFilled
    CountrySelectorVariant.Outlined -> shapes.selectorOutlined
    CountrySelectorVariant.Minimal -> shapes.selectorMinimal
    CountrySelectorVariant.Compact,
    CountrySelectorVariant.FlagOnly,
    CountrySelectorVariant.DialCode,
    -> shapes.selectorPill
}

/** Resolves the resting minimum height for a variant. */
internal fun CountrySelectorVariant.minHeight(dimensions: CountryPickerDimensions): Dp = when (this) {
    CountrySelectorVariant.Filled, CountrySelectorVariant.Minimal -> dimensions.selectorMinHeight
    CountrySelectorVariant.Outlined -> dimensions.selectorOutlinedMinHeight
    CountrySelectorVariant.Compact -> dimensions.selectorCompactMinHeight
    CountrySelectorVariant.FlagOnly -> dimensions.selectorFlagOnlyMinHeight
    CountrySelectorVariant.DialCode -> dimensions.selectorDialMinHeight
}

/** Resolves the flag size for a variant. */
internal fun CountrySelectorVariant.flagSize(dimensions: CountryPickerDimensions): Dp = when (this) {
    CountrySelectorVariant.Compact -> dimensions.flagSizeCompact
    CountrySelectorVariant.FlagOnly, CountrySelectorVariant.DialCode -> 24.dp
    else -> dimensions.flagSize
}

/**
 * Resolves the container color for a variant and state.
 *
 * `Outlined` and `Minimal` are transparent by design — they express containment through their stroke,
 * and filling them as well produces the muddy double-treatment the design avoids.
 */
internal fun CountrySelectorVariant.containerColor(
    state: CountrySelectorState,
    colors: CountryPickerColors,
    surfaceContainerHigh: Color,
): Color = when {
    state == CountrySelectorState.Disabled -> colors.selectorDisabledContainer
    this == CountrySelectorVariant.Filled -> colors.selectorContainer
    this == CountrySelectorVariant.Outlined || this == CountrySelectorVariant.Minimal -> Color.Transparent
    else -> surfaceContainerHigh
}

/**
 * Resolves the border color, or `null` when the variant draws no stroke.
 *
 * Error and success override the resting stroke for every variant that has one, so validation is
 * visible on a compact pill as well as a full field.
 */
internal fun CountrySelectorVariant.borderColor(
    state: CountrySelectorState,
    colors: CountryPickerColors,
): Color? = when (this) {
    CountrySelectorVariant.Outlined -> when (state) {
        CountrySelectorState.Error -> colors.error
        CountrySelectorState.Success -> colors.success
        CountrySelectorState.Disabled -> colors.selectorDisabledContent
        else -> colors.selectorBorder
    }

    CountrySelectorVariant.Filled, CountrySelectorVariant.Minimal -> when (state) {
        CountrySelectorState.Error -> colors.error
        CountrySelectorState.Success -> colors.success
        else -> null
    }

    else -> when (state) {
        CountrySelectorState.Error -> colors.error
        CountrySelectorState.Success -> colors.success
        else -> null
    }
}

/** Scale applied while pressed. */
private const val PRESSED_SCALE = 0.985f

/** Role reported to accessibility services for every selector variant. */
internal val SelectorRole: Role = Role.Button

/** Corner radius used when a variant needs an explicit rounded fallback. */
internal val FallbackShape: Shape = RoundedCornerShape(12.dp)
