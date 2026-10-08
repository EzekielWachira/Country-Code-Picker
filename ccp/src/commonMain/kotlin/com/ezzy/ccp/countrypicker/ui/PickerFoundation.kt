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

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.ezzy.ccp.countrypicker.theme.CountryPickerColors
import com.ezzy.ccp.countrypicker.theme.CountryPickerHaptics
import com.ezzy.ccp.countrypicker.theme.CountryPickerShadow
import com.ezzy.ccp.countrypicker.theme.CountryPickerTheme

// ── Depth ────────────────────────────────────────────────────────────────────────────────────────

/** Draws every layer of [shadow] behind the content, tinted with [color]. */
internal fun Modifier.pickerShadow(
    shadow: CountryPickerShadow,
    shape: Shape,
    color: Color,
): Modifier = shadow.layers.fold(this) { modifier, layer ->
    modifier.dropShadow(
        shape = shape,
        shadow = Shadow(
            radius = layer.blur,
            color = color,
            spread = layer.spread,
            offset = DpOffset(0.dp, layer.offsetY),
            alpha = layer.alpha,
        ),
    )
}

/**
 * A soft ring of [color] around the content, [progress] of [width] wide — the focus glow on fields.
 * Drawn as an unblurred, spread shadow, so it follows [shape] exactly and costs no layout.
 */
internal fun Modifier.focusRing(
    progress: Float,
    width: Dp,
    color: Color,
    shape: Shape,
): Modifier = if (progress <= 0f) {
    this
} else {
    dropShadow(shape = shape, shadow = Shadow(radius = 0.dp, color = color, spread = width * progress))
}

/**
 * Fades the content out toward whichever horizontal edge can still scroll, so a row running past the
 * edge reads as scrollable rather than cut off. Apply it outside the `horizontalScroll`, on the
 * viewport.
 */
internal fun Modifier.horizontalScrollFade(state: ScrollState, length: Dp): Modifier = this
    // Offscreen, so the DstIn mask below erases this layer's pixels rather than the window's.
    .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
    .drawWithContent {
        drawContent()
        val fade = length.toPx().coerceAtMost(size.width / 2)
        // Read in the draw phase, so scrolling redraws the fade without recomposing anything.
        val towardStart = state.value > 0
        val towardEnd = state.value < state.maxValue
        val ltr = layoutDirection == LayoutDirection.Ltr
        if (if (ltr) towardStart else towardEnd) {
            drawRect(
                brush = Brush.horizontalGradient(listOf(Color.Transparent, Color.Black), startX = 0f, endX = fade),
                size = Size(fade, size.height),
                blendMode = BlendMode.DstIn,
            )
        }
        if (if (ltr) towardEnd else towardStart) {
            drawRect(
                brush = Brush.horizontalGradient(listOf(Color.Black, Color.Transparent), startX = size.width - fade, endX = size.width),
                topLeft = Offset(size.width - fade, 0f),
                size = Size(fade, size.height),
                blendMode = BlendMode.DstIn,
            )
        }
    }

// ── Motion ───────────────────────────────────────────────────────────────────────────────────────

/** The scale a pressable element should draw at — springing down while pressed, back on release. */
@Composable
internal fun pressScale(interactionSource: MutableInteractionSource, enabled: Boolean = true): State<Float> {
    val motion = CountryPickerTheme.style.motion
    val pressed by interactionSource.collectIsPressedAsState()
    return animateFloatAsState(
        targetValue = if (pressed && enabled) motion.pressedScale else 1f,
        animationSpec = motion.press,
        label = "pressScale",
    )
}

/**
 * A horizontal offset, in pixels, that shakes briefly each time [active] becomes true — the
 * "that's not right" gesture for a field entering its error state. Nothing moves when motion is off.
 */
@Composable
internal fun rememberShakeOffset(active: Boolean): State<Float> {
    val motion = CountryPickerTheme.style.motion
    val distance = with(LocalDensity.current) { motion.shakeDistance.toPx() }
    val offset = remember { Animatable(0f) }
    LaunchedEffect(active) {
        if (!active || distance == 0f) return@LaunchedEffect
        offset.animateTo(
            targetValue = 0f,
            animationSpec = keyframes {
                durationMillis = SHAKE_MILLIS
                distance at SHAKE_MILLIS / 8
                -distance at SHAKE_MILLIS * 3 / 8
                distance * 0.6f at SHAKE_MILLIS * 5 / 8
                -distance * 0.3f at SHAKE_MILLIS * 7 / 8
            },
        )
    }
    return offset.asState()
}

/** A diagonal highlight sweeping across loading placeholders. A flat fill when motion is off. */
@Composable
internal fun rememberShimmerBrush(colors: CountryPickerColors): Brush {
    val motion = CountryPickerTheme.style.motion
    if (!motion.enabled) return Brush.linearGradient(listOf(colors.skeleton, colors.skeleton))
    val transition = rememberInfiniteTransition(label = "shimmer")
    val progress by transition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(tween(SHIMMER_MILLIS, easing = LinearEasing), RepeatMode.Restart),
        label = "shimmerProgress",
    )
    val width = with(LocalDensity.current) { SHIMMER_WIDTH.toPx() }
    val start = progress * width * 2
    return Brush.linearGradient(
        colors = listOf(colors.skeleton, colors.skeletonShine, colors.skeleton),
        start = Offset(start - width, 0f),
        end = Offset(start, width / 3),
    )
}

// ── Haptics ──────────────────────────────────────────────────────────────────────────────────────

/** Plays the style's haptics, respecting [CountryPickerHaptics.enabled] and silenced moments. */
internal class PickerHaptics(
    private val feedback: HapticFeedback,
    private val config: CountryPickerHaptics,
) {
    fun select() = play(config.select)

    fun toggle(on: Boolean) = play(if (on) config.toggleOn else config.toggleOff)

    fun scrub() = play(config.scrub)

    fun filter() = play(config.filter)

    fun limitReached() = play(config.limitReached)

    fun error() = play(config.error)

    private fun play(type: HapticFeedbackType?) {
        if (config.enabled && type != null) feedback.performHapticFeedback(type)
    }
}

@Composable
internal fun rememberPickerHaptics(): PickerHaptics {
    val feedback = LocalHapticFeedback.current
    val config = CountryPickerTheme.style.haptics
    return remember(feedback, config) { PickerHaptics(feedback, config) }
}

// ── Selection indicators ─────────────────────────────────────────────────────────────────────────

/** A check mark that draws itself in from left to right when [visible] becomes true. */
@Composable
internal fun AnimatedCheckMark(
    visible: Boolean,
    color: Color,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 2.4.dp,
) {
    val motion = CountryPickerTheme.style.motion
    val progress by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = motion.selection,
        label = "checkProgress",
    )
    if (progress <= 0f) {
        Box(modifier)
        return
    }
    val measure = remember { PathMeasure() }
    val segment = remember { Path() }
    Canvas(modifier) {
        val path = Path().apply {
            moveTo(size.width * 0.20f, size.height * 0.53f)
            lineTo(size.width * 0.41f, size.height * 0.73f)
            lineTo(size.width * 0.80f, size.height * 0.29f)
        }
        measure.setPath(path, forceClosed = false)
        segment.reset()
        measure.getSegment(0f, measure.length * progress.coerceIn(0f, 1f), segment, startWithMoveTo = true)
        drawPath(
            path = segment,
            color = color,
            style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
}

/** A radio button: a ring that fills with a dot when [selected]. */
@Composable
internal fun RadioIndicator(
    selected: Boolean,
    enabled: Boolean,
    colors: CountryPickerColors,
    size: Dp,
    modifier: Modifier = Modifier,
) {
    val motion = CountryPickerTheme.style.motion
    val dot by animateFloatAsState(if (selected) 1f else 0f, motion.selection, label = "radioDot")
    val ring = when {
        !enabled -> colors.textDisabled
        selected -> colors.accent
        else -> colors.outline
    }
    Canvas(modifier.size(size)) {
        val stroke = RING_STROKE.toPx()
        drawCircle(color = ring, radius = this.size.minDimension / 2 - stroke / 2, style = Stroke(stroke))
        if (dot > 0f) drawCircle(color = colors.accent, radius = this.size.minDimension * 0.26f * dot)
    }
}

/**
 * The multiple-selection checkbox: a circle that fills with the accent and draws a check when
 * [checked]. Purely visual — the row owns the click, so there is one target and one node for
 * accessibility.
 */
@Composable
internal fun CheckboxIndicator(
    checked: Boolean,
    enabled: Boolean,
    colors: CountryPickerColors,
    size: Dp,
    modifier: Modifier = Modifier,
) {
    val motion = CountryPickerTheme.style.motion
    val fill by animateFloatAsState(if (checked) 1f else 0f, motion.selection, label = "checkboxFill")
    Box(modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(size)) {
            val stroke = RING_STROKE.toPx()
            val radius = this.size.minDimension / 2
            drawCircle(
                color = if (enabled) colors.outline else colors.textDisabled,
                radius = radius - stroke / 2,
                style = Stroke(stroke),
            )
            if (fill > 0f) drawCircle(color = colors.accent, radius = radius * fill.coerceAtMost(1.1f))
        }
        AnimatedCheckMark(
            visible = checked,
            color = colors.onAccent,
            strokeWidth = 2.2.dp,
            modifier = Modifier.size(size * CHECK_IN_CIRCLE_RATIO),
        )
    }
}

// ── Small building blocks ────────────────────────────────────────────────────────────────────────

/** A compact label on a tinted capsule: "Detected", "Mobile", "Not available". */
@Composable
internal fun PickerBadge(
    text: String,
    container: Color,
    content: Color,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    val style = CountryPickerTheme.style
    Row(
        modifier = modifier
            .clip(style.shapes.badge)
            .background(container)
            .padding(horizontal = 7.dp, vertical = 2.5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = content, modifier = Modifier.size(11.dp))
            Box(Modifier.size(3.dp))
        }
        Text(text = text, style = style.typography.badge, color = content, maxLines = 1)
    }
}

/**
 * A circular icon button: a small visual on a sunken disc, inside a full 48dp touch target.
 *
 * The disc is what the design shows; the target is what a thumb needs. Keeping them separate is how
 * the button stays compact without becoming hard to hit.
 */
@Composable
internal fun PickerIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = CountryPickerTheme.style.colors.textSecondary,
    container: Color = CountryPickerTheme.style.colors.surfaceSunken,
    visualSize: Dp = CountryPickerTheme.style.dimensions.iconButtonSize,
    iconSize: Dp = 16.dp,
) {
    val style = CountryPickerTheme.style
    val interaction = remember { MutableInteractionSource() }
    val scale by pressScale(interaction)
    Box(
        modifier = modifier
            .size(style.dimensions.minimumTouchTarget)
            .clickable(
                interactionSource = interaction,
                indication = null,
                role = Role.Button,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(visualSize)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .clip(CircleShape)
                .background(container),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = contentDescription, tint = tint, modifier = Modifier.size(iconSize))
        }
    }
}

/** The importance of a [PickerButton]. */
internal enum class PickerButtonKind { Primary, Secondary, Text }

/**
 * The picker's button: accent-filled ([PickerButtonKind.Primary]), sunken ([PickerButtonKind.Secondary])
 * or bare ([PickerButtonKind.Text]), with press scale and a dimmed disabled state.
 */
@Composable
internal fun PickerButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    kind: PickerButtonKind = PickerButtonKind.Primary,
    enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
    content: @Composable RowScope.() -> Unit,
) {
    val style = CountryPickerTheme.style
    val colors = style.colors
    val interaction = remember { MutableInteractionSource() }
    val scale by pressScale(interaction, enabled)
    val alpha by animateFloatAsState(if (enabled) 1f else DISABLED_ALPHA, style.motion.layout, label = "buttonAlpha")
    val container = when (kind) {
        PickerButtonKind.Primary -> colors.accent
        PickerButtonKind.Secondary -> colors.surfaceSunken
        PickerButtonKind.Text -> Color.Transparent
    }
    val contentColor = when (kind) {
        PickerButtonKind.Primary -> colors.onAccent
        PickerButtonKind.Secondary -> colors.textPrimary
        PickerButtonKind.Text -> colors.accent
    }
    Row(
        modifier = modifier
            .defaultMinSize(minHeight = style.dimensions.minimumTouchTarget)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .clip(style.shapes.button)
            .background(container)
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(contentPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
    ) {
        androidx.compose.runtime.CompositionLocalProvider(
            androidx.compose.material3.LocalContentColor provides contentColor,
            androidx.compose.material3.LocalTextStyle provides style.typography.button.copy(color = contentColor),
        ) {
            content()
        }
    }
}

/**
 * An icon on a softly tinted disc with a hairline halo — the illustration at the top of the empty,
 * error and offline states.
 */
@Composable
internal fun StateIllustration(
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier,
) {
    val colors = CountryPickerTheme.style.colors
    Box(
        modifier = modifier
            .size(ILLUSTRATION_SIZE)
            .clip(CircleShape)
            .background(
                Brush.verticalGradient(listOf(tint.copy(alpha = 0.16f), tint.copy(alpha = 0.06f))),
            )
            .border(1.dp, tint.copy(alpha = 0.14f), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(ILLUSTRATION_SIZE * 0.62f)
                .clip(CircleShape)
                .background(colors.surface),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(26.dp))
        }
    }
}

/** A single line of text that never wraps or overflows its slot. */
@Composable
internal fun SingleLineText(
    text: String,
    style: androidx.compose.ui.text.TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Text(text = text, style = style, color = color, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = modifier)
}

/**
 * A label drawn in capitals but spoken in [text]'s own case. Screen readers — VoiceOver especially —
 * tend to spell short all-caps words out letter by letter, and a visual style must never cause that.
 */
@Composable
internal fun CapsLabel(
    text: String,
    style: androidx.compose.ui.text.TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
) {
    SingleLineText(
        text = text.uppercase(),
        style = style,
        color = color,
        modifier = modifier.semantics { contentDescription = text },
    )
}

private const val SHAKE_MILLIS = 360
private const val SHIMMER_MILLIS = 1400
private val SHIMMER_WIDTH = 220.dp
private val RING_STROKE = 1.75.dp
private const val CHECK_IN_CIRCLE_RATIO = 0.72f
private const val DISABLED_ALPHA = 0.42f
private val ILLUSTRATION_SIZE = 76.dp
