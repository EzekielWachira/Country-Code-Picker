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

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * Icons the picker draws, matching the design's SVG paths.
 *
 * These are declared here rather than pulled from `material-icons-extended` on purpose: that artifact
 * is ~10 MB before shrinking, and depending on it to get five 24dp glyphs would be the single largest
 * cost this library imposes on a consumer.
 */
internal object PickerIcons {

    /** Check mark for a selected row and the "Detected" badge tick. */
    val Check: ImageVector by lazy {
        icon("Check") {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(5f, 13f)
                lineTo(9.5f, 17.5f)
                lineTo(19f, 7f)
            }
        }
    }

    /** Circled exclamation for error states. */
    val Alert: ImageVector by lazy {
        icon("Alert") {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
            ) {
                // Circle approximated with four cubic arcs — a vector path cannot declare an ellipse.
                moveTo(21f, 12f)
                arcToRelative(9f, 9f, 0f, isMoreThanHalf = true, isPositiveArc = true, -18f, 0f)
                arcToRelative(9f, 9f, 0f, isMoreThanHalf = true, isPositiveArc = true, 18f, 0f)
                close()
                moveTo(12f, 7.5f)
                verticalLineTo(13.5f)
                moveTo(12f, 16.6f)
                verticalLineTo(16.8f)
            }
        }
    }

    /** Circled check for the success state. */
    val CheckCircle: ImageVector by lazy {
        icon("CheckCircle") {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(21f, 12f)
                arcToRelative(9f, 9f, 0f, isMoreThanHalf = true, isPositiveArc = true, -18f, 0f)
                arcToRelative(9f, 9f, 0f, isMoreThanHalf = true, isPositiveArc = true, 18f, 0f)
                close()
                moveTo(8f, 12.3f)
                lineToRelative(2.6f, 2.7f)
                lineTo(16f, 9.5f)
            }
        }
    }

    /** Globe, shown in the selector when no country is chosen and in the empty state. */
    val Globe: ImageVector by lazy {
        icon("Globe") {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.6f,
            ) {
                moveTo(21f, 12f)
                arcToRelative(9f, 9f, 0f, isMoreThanHalf = true, isPositiveArc = true, -18f, 0f)
                arcToRelative(9f, 9f, 0f, isMoreThanHalf = true, isPositiveArc = true, 18f, 0f)
                close()
                moveTo(3f, 12f)
                horizontalLineTo(21f)
                moveTo(12f, 3f)
                curveToRelative(2.5f, 2.6f, 2.5f, 15.4f, 0f, 18f)
                curveToRelative(-2.5f, -2.6f, -2.5f, -15.4f, 0f, -18f)
                close()
            }
        }
    }

    /** Back arrow for the full-height sheet header. */
    val ArrowBack: ImageVector by lazy {
        icon("ArrowBack") {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(19f, 12f)
                horizontalLineTo(5f)
                moveToRelative(6f, -7f)
                lineToRelative(-7f, 7f)
                lineToRelative(7f, 7f)
            }
        }
    }

    /** Vertical "more options" overflow glyph, used by the sample screen's app bar. */
    val MoreVert: ImageVector by lazy {
        icon("MoreVert") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(12f, 8f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = true, isPositiveArc = true, 0f, -4f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = true, isPositiveArc = true, 0f, 4f)
                close()
                moveTo(12f, 14f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = true, isPositiveArc = true, 0f, -4f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = true, isPositiveArc = true, 0f, 4f)
                close()
                moveTo(12f, 20f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = true, isPositiveArc = true, 0f, -4f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = true, isPositiveArc = true, 0f, 4f)
                close()
            }
        }
    }

    /** Magnifying glass for the search field. */
    val Search: ImageVector by lazy {
        icon("Search") {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
            ) {
                moveTo(18f, 10.5f)
                arcToRelative(7.5f, 7.5f, 0f, isMoreThanHalf = true, isPositiveArc = true, -15f, 0f)
                arcToRelative(7.5f, 7.5f, 0f, isMoreThanHalf = true, isPositiveArc = true, 15f, 0f)
                close()
                moveTo(16f, 16f)
                lineTo(21f, 21f)
            }
        }
    }

    /** Close glyph. Drawn at the size it is used — unlike the 512dp legacy asset. */
    val Close: ImageVector by lazy {
        icon("Close") {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.2f,
                strokeLineCap = StrokeCap.Round,
            ) {
                moveTo(6.5f, 6.5f)
                lineTo(17.5f, 17.5f)
                moveTo(17.5f, 6.5f)
                lineTo(6.5f, 17.5f)
            }
        }
    }

    /** Downward chevron for fields and pills. */
    val ChevronDown: ImageVector by lazy {
        icon("ChevronDown") {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(6.5f, 9.5f)
                lineTo(12f, 15f)
                lineTo(17.5f, 9.5f)
            }
        }
    }

    /** Map pin for the detected-country banner. */
    val LocationPin: ImageVector by lazy {
        icon("LocationPin") {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.9f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(12f, 21.5f)
                curveTo(12f, 21.5f, 19f, 15.2f, 19f, 10f)
                arcToRelative(7f, 7f, 0f, isMoreThanHalf = false, isPositiveArc = false, -14f, 0f)
                curveTo(5f, 15.2f, 12f, 21.5f, 12f, 21.5f)
                close()
                moveTo(14.5f, 10f)
                arcToRelative(2.5f, 2.5f, 0f, isMoreThanHalf = true, isPositiveArc = true, -5f, 0f)
                arcToRelative(2.5f, 2.5f, 0f, isMoreThanHalf = true, isPositiveArc = true, 5f, 0f)
                close()
            }
        }
    }

    /** Crossed-out wifi for the offline state. */
    val Offline: ImageVector by lazy {
        icon("Offline") {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.9f,
                strokeLineCap = StrokeCap.Round,
            ) {
                moveTo(2.5f, 8.8f)
                curveTo(5f, 6.6f, 8.3f, 5.3f, 12f, 5.3f)
                curveTo(15.7f, 5.3f, 19f, 6.6f, 21.5f, 8.8f)
                moveTo(5.6f, 12.3f)
                curveTo(7.3f, 10.9f, 9.6f, 10f, 12f, 10f)
                curveTo(14.4f, 10f, 16.7f, 10.9f, 18.4f, 12.3f)
                moveTo(8.8f, 15.7f)
                curveTo(9.7f, 15f, 10.8f, 14.6f, 12f, 14.6f)
                curveTo(13.2f, 14.6f, 14.3f, 15f, 15.2f, 15.7f)
                moveTo(12f, 19.2f)
                verticalLineTo(19.3f)
                moveTo(3.5f, 3.5f)
                lineTo(20.5f, 20.5f)
            }
        }
    }

    /** Four-point sparkle for "did you mean" suggestions. */
    val Sparkle: ImageVector by lazy {
        icon("Sparkle") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(12f, 2.5f)
                curveTo(12.6f, 8.2f, 15.8f, 11.4f, 21.5f, 12f)
                curveTo(15.8f, 12.6f, 12.6f, 15.8f, 12f, 21.5f)
                curveTo(11.4f, 15.8f, 8.2f, 12.6f, 2.5f, 12f)
                curveTo(8.2f, 11.4f, 11.4f, 8.2f, 12f, 2.5f)
                close()
            }
        }
    }

    private inline fun icon(
        name: String,
        block: ImageVector.Builder.() -> Unit,
    ): ImageVector = ImageVector.Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply(block).build()
}
