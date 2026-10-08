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

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

/**
 * How the picker moves.
 *
 * Interactive motion — presses, selection, layout — is spring-based, so it can be interrupted and
 * reversed mid-flight and always settles from its current velocity rather than restarting. Fades use
 * short eased tweens, which read cleaner than springs for opacity.
 *
 * Motion switches off entirely when [enabled] is false, and [CountryPickerStyles] presets turn it off
 * automatically when the user has asked the system to remove or reduce animations.
 *
 * @property enabled False collapses every animation to an instant change.
 * @property press Press-down scale feedback.
 * @property selection Check marks, toggles and the sliding filter pill — slightly bouncy, so a
 *   selection feels physical.
 * @property layout Size and position changes.
 * @property offset Item placement and slides.
 * @property size Container resizing.
 * @property dp Border widths, rings and other dimension changes.
 * @property color Color transitions.
 * @property fadeIn Content appearing.
 * @property fadeOut Content leaving.
 * @property pressedScale Scale applied to a pressed row, tile or button.
 * @property shakeDistance How far a field shakes when it enters an error state.
 * @property selectionDismissDelayMillis Pause between a single selection and the sheet closing, so the
 *   check mark visibly lands first.
 * @property staggerMillis Delay between successive list items on the sheet's first appearance.
 * @property maxStaggeredItems Items beyond this appear without delay.
 */
@Immutable
public data class CountryPickerMotion(
    val enabled: Boolean = true,
    val press: FiniteAnimationSpec<Float> = spring(dampingRatio = 0.7f, stiffness = 900f),
    val selection: FiniteAnimationSpec<Float> = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMedium),
    val layout: FiniteAnimationSpec<Float> = spring(dampingRatio = 0.86f, stiffness = 420f),
    val offset: FiniteAnimationSpec<IntOffset> = spring(
        dampingRatio = 0.86f,
        stiffness = 420f,
        visibilityThreshold = IntOffset.VisibilityThreshold,
    ),
    val size: FiniteAnimationSpec<IntSize> = spring(
        dampingRatio = 0.9f,
        stiffness = 500f,
        visibilityThreshold = IntSize.VisibilityThreshold,
    ),
    val dp: FiniteAnimationSpec<Dp> = spring(dampingRatio = 0.8f, stiffness = 600f, visibilityThreshold = 0.1.dp),
    val color: FiniteAnimationSpec<Color> = tween(COLOR_MILLIS, easing = Standard),
    val fadeIn: FiniteAnimationSpec<Float> = tween(FADE_IN_MILLIS, easing = EmphasizedDecelerate),
    val fadeOut: FiniteAnimationSpec<Float> = tween(FADE_OUT_MILLIS, easing = EmphasizedAccelerate),
    val pressedScale: Float = 0.97f,
    val shakeDistance: Dp = 7.dp,
    val selectionDismissDelayMillis: Long = 240L,
    val staggerMillis: Int = 16,
    val maxStaggeredItems: Int = 14,
) {
    /** This motion with every animation made instant when [enabled] is false. */
    public fun withMotionEnabled(enabled: Boolean): CountryPickerMotion = if (enabled) {
        this
    } else {
        copy(
            enabled = false,
            press = snap(),
            selection = snap(),
            layout = snap(),
            offset = snap(),
            size = snap(),
            dp = snap(),
            color = snap(),
            fadeIn = snap(),
            fadeOut = snap(),
            pressedScale = 1f,
            shakeDistance = 0.dp,
            selectionDismissDelayMillis = 0L,
            staggerMillis = 0,
            maxStaggeredItems = 0,
        )
    }

    public companion object {
        internal const val COLOR_MILLIS: Int = 180
        internal const val FADE_IN_MILLIS: Int = 220
        internal const val FADE_OUT_MILLIS: Int = 140

        /** Fast start, gentle settle — for things arriving. */
        public val EmphasizedDecelerate: Easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)

        /** Gentle start, fast exit — for things leaving. */
        public val EmphasizedAccelerate: Easing = CubicBezierEasing(0.3f, 0f, 0.8f, 0.15f)

        /** For state changes that are neither entrances nor exits. */
        public val Standard: Easing = CubicBezierEasing(0.2f, 0f, 0f, 1f)
    }
}

/**
 * Disables motion when the user has turned animations off at the system level: an animator duration
 * scale of zero on Android ("Remove animations" / developer options), Reduce Motion on iOS.
 *
 * Users turn animations off for real reasons — vestibular disorders, low-end hardware, focus. A
 * component library that animates anyway is overriding an explicit accessibility choice, so every
 * preset in [CountryPickerStyles] applies this by default.
 */
@Composable
@ReadOnlyComposable
public fun CountryPickerMotion.respectingSystemAnimationScale(): CountryPickerMotion =
    withMotionEnabled(enabled && systemAnimationsEnabled())

/** False when the user has asked the system to remove or reduce motion. */
@Composable
@ReadOnlyComposable
internal expect fun systemAnimationsEnabled(): Boolean
