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

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize

/**
 * Centralised animation specs, so the picker's motion is consistent and adjustable in one place
 * instead of being twenty inline `tween(300)` calls.
 *
 * ### Durations
 * Grouped by what the motion is communicating, following Material 3's emphasis guidance:
 * - **micro** (140ms): state flips the user already expects — chevron rotation, ripple, checkbox.
 * - **content** (240ms): something changed on screen — list swaps, count changes, section resizes.
 * - **selection**: a low-bounce spring, because selection should feel physical without wobbling.
 *
 * Sheet motion is deliberately absent: `ModalBottomSheet` owns its own entrance and exit, and
 * animating it by hand fights Material's sheet state and produces the janky half-open sheets that
 * hand-rolled implementations are known for.
 *
 * ### Reduced motion
 * [respectingSystemAnimationScale] collapses every duration to near-zero when the user has turned
 * animations off in Developer options or accessibility settings. State remains fully legible without
 * motion — nothing in this library conveys information *only* through movement.
 *
 * @property micro Micro-interactions.
 * @property content Content transitions.
 * @property fadeIn/[fadeOut] Enter/exit fades for appearing content.
 * @property selectionSpring Selection feedback.
 * @property colorSpec Color transitions (borders, backgrounds, tints).
 * @property sizeSpec Size and layout transitions.
 * @property dpSpec Dp transitions (border thickness).
 * @property floatSpec Float transitions (rotation, alpha, scale).
 * @property listItemPlacement Placement spec for `Modifier.animateItem`, used when a country moves
 *   between sections.
 * @property staggerPerItemMillis Per-item delay for the sheet's initial reveal.
 * @property maxStaggeredItems How many rows stagger before the effect stops. Capped because
 *   staggering an entire 236-row list would animate items the user will never see and cost frames
 *   during the very scroll it is meant to decorate.
 */
@Immutable
data class CountryPickerMotion(
    val micro: FiniteAnimationSpec<Float> = tween(MICRO_MILLIS, easing = EmphasizedDecelerate),
    val content: FiniteAnimationSpec<Float> = tween(CONTENT_MILLIS, easing = EmphasizedDecelerate),
    val fadeIn: FiniteAnimationSpec<Float> = tween(CONTENT_MILLIS, easing = EmphasizedDecelerate),
    val fadeOut: FiniteAnimationSpec<Float> = tween(MICRO_MILLIS, easing = EmphasizedAccelerate),
    // Finite (not merely AnimationSpec) because scaleIn/scaleOut and the other EnterTransition
    // builders require a spec that is guaranteed to terminate.
    val selectionSpring: FiniteAnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMediumLow,
    ),
    val colorSpec: FiniteAnimationSpec<Color> = tween(MICRO_MILLIS, easing = EmphasizedDecelerate),
    val sizeSpec: FiniteAnimationSpec<IntSize> = tween(CONTENT_MILLIS, easing = EmphasizedDecelerate),
    val dpSpec: FiniteAnimationSpec<Dp> = tween(MICRO_MILLIS, easing = EmphasizedDecelerate),
    val floatSpec: FiniteAnimationSpec<Float> = tween(MICRO_MILLIS, easing = EmphasizedDecelerate),
    val listItemPlacement: FiniteAnimationSpec<androidx.compose.ui.unit.IntOffset> =
        tween(CONTENT_MILLIS, easing = EmphasizedDecelerate),
    val staggerPerItemMillis: Int = 14,
    val maxStaggeredItems: Int = 12,
) {

    /**
     * Returns a copy with every duration collapsed when [enabled] is false.
     *
     * Applied by [respectingSystemAnimationScale]; call it directly to disable motion for a
     * particular screen.
     */
    fun withMotionEnabled(enabled: Boolean): CountryPickerMotion = if (enabled) {
        this
    } else {
        CountryPickerMotion(
            micro = tween(0),
            content = tween(0),
            fadeIn = tween(0),
            fadeOut = tween(0),
            selectionSpring = tween(0),
            colorSpec = tween(0),
            sizeSpec = tween(0),
            dpSpec = tween(0),
            floatSpec = tween(0),
            listItemPlacement = tween(0),
            staggerPerItemMillis = 0,
            maxStaggeredItems = 0,
        )
    }

    companion object {
        /** Micro-interaction duration, inside the 100–180ms band. */
        const val MICRO_MILLIS: Int = 140

        /** Content-transition duration, inside the 180–280ms band. */
        const val CONTENT_MILLIS: Int = 240

        /** Material 3 emphasized-decelerate easing: fast start, gentle settle. */
        val EmphasizedDecelerate: Easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)

        /** Material 3 emphasized-accelerate easing, for exits. */
        val EmphasizedAccelerate: Easing = CubicBezierEasing(0.3f, 0f, 0.8f, 0.15f)

        /** Standard easing for state changes that are not entrances or exits. */
        val Standard: Easing = CubicBezierEasing(0.2f, 0f, 0f, 1f)
    }
}

/**
 * Reads the platform's animator duration scale and disables motion when the user has set it to zero.
 *
 * Users turn animations off for real reasons — vestibular disorders, low-end hardware, focus. A
 * component library that animates anyway is overriding an explicit accessibility choice, so this is
 * applied by default in [CountryPickerDefaults.motion].
 */
@Composable
@ReadOnlyComposable
fun CountryPickerMotion.respectingSystemAnimationScale(): CountryPickerMotion {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scale = android.provider.Settings.Global.getFloat(
        context.contentResolver,
        android.provider.Settings.Global.ANIMATOR_DURATION_SCALE,
        1f,
    )
    return withMotionEnabled(scale > 0f)
}
