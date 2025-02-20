package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathData
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EzzyIcons.Liechtenstein: ImageVector
    get() {
        if (_Liechtenstein != null) {
            return _Liechtenstein!!
        }
        _Liechtenstein = ImageVector.Builder(
            name = "Liechtenstein",
            defaultWidth = 64.dp,
            defaultHeight = 64.dp,
            viewportWidth = 64f,
            viewportHeight = 64f
        ).apply {
            path(fill = SolidColor(Color(0xFF00267F))) {
                moveToRelative(0.5f, 10.52f)
                horizontalLineToRelative(63f)
                verticalLineToRelative(21.45f)
                horizontalLineToRelative(-63f)
                close()
            }
            path(fill = SolidColor(Color(0xFFEC1C24))) {
                moveToRelative(0.5f, 31.98f)
                horizontalLineToRelative(63f)
                verticalLineToRelative(21.5f)
                horizontalLineToRelative(-63f)
                close()
            }
            path(fill = SolidColor(Color(0xFF000000))) {
                moveToRelative(23.22f, 21.65f)
                reflectiveCurveToRelative(0.61f, -1.59f, -0.02f, -2.72f)
                curveToRelative(-0.64f, -1.13f, -1.98f, -1.05f, -1.98f, -1.05f)
                reflectiveCurveToRelative(-1.25f, -0.14f, -3.42f, 0.64f)
                lineToRelative(-0.71f, 0.06f)
                verticalLineToRelative(6.5f)
                curveToRelative(3.22f, 0f, 5.18f, 0.25f, 5.4f, 0.56f)
                curveToRelative(0.04f, 0.06f, 0.04f, 0.12f, 0f, 0.18f)
                curveToRelative(0f, 0f, 0.35f, -0.3f, 0.65f, -1.13f)
                curveToRelative(0.31f, -0.84f, 0.51f, -1.22f, 0.51f, -1.22f)
                reflectiveCurveToRelative(0.44f, -1.13f, 0.89f, -1.3f)
                curveToRelative(0f, 0f, -0.58f, -0.56f, -1.32f, -0.52f)
                close()
            }
            path(fill = SolidColor(Color(0xFF000000))) {
                moveToRelative(12.95f, 17.88f)
                reflectiveCurveToRelative(-1.34f, -0.08f, -1.98f, 1.05f)
                curveToRelative(-0.63f, 1.13f, -0.02f, 2.72f, -0.02f, 2.72f)
                curveToRelative(-0.74f, -0.04f, -1.32f, 0.52f, -1.32f, 0.52f)
                curveToRelative(0.45f, 0.17f, 0.89f, 1.3f, 0.89f, 1.3f)
                reflectiveCurveToRelative(0.2f, 0.38f, 0.51f, 1.22f)
                curveToRelative(0.3f, 0.83f, 0.65f, 1.13f, 0.65f, 1.13f)
                curveToRelative(-0.04f, -0.06f, -0.04f, -0.12f, 0f, -0.18f)
                curveToRelative(0.22f, -0.31f, 2.18f, -0.56f, 5.4f, -0.56f)
                verticalLineToRelative(-6.5f)
                lineToRelative(-0.71f, -0.06f)
                curveToRelative(-2.17f, -0.78f, -3.42f, -0.64f, -3.42f, -0.64f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFD83D))) {
                moveToRelative(22.08f, 22.36f)
                reflectiveCurveToRelative(-0.18f, 0.25f, -0.26f, 0.53f)
                curveToRelative(-0.01f, 0.01f, -0.01f, 0.01f, -0.01f, 0.01f)
                curveToRelative(-0.09f, 0.12f, -0.15f, -0.01f, -0.15f, -0.01f)
                reflectiveCurveToRelative(0.16f, -1.12f, -0.75f, -1.59f)
                curveToRelative(-0.69f, -0.29f, -1.49f, 0.63f, -1.49f, 0.63f)
                reflectiveCurveToRelative(-0.17f, 0.17f, -0.34f, 0.39f)
                curveToRelative(-0.13f, 0.13f, -0.22f, 0.32f, -0.28f, 0.4f)
                curveToRelative(-0.03f, 0.04f, -0.07f, 0.03f, -0.1f, 0.02f)
                curveToRelative(-0.01f, -0.33f, -0.1f, -0.85f, -0.5f, -1.2f)
                curveToRelative(0f, 0f, -0.38f, -0.37f, -1.1f, -0.33f)
                reflectiveCurveToRelative(0f, 0f, -0.01f, 0f)
                horizontalLineToRelative(-0.01f)
                curveToRelative(-0.73f, -0.04f, -1.1f, 0.33f, -1.1f, 0.33f)
                curveToRelative(-0.4f, 0.35f, -0.49f, 0.87f, -0.5f, 1.2f)
                curveToRelative(-0.03f, 0.01f, -0.07f, 0.02f, -0.1f, -0.02f)
                curveToRelative(-0.06f, -0.08f, -0.15f, -0.27f, -0.28f, -0.4f)
                curveToRelative(-0.17f, -0.22f, -0.34f, -0.39f, -0.34f, -0.39f)
                reflectiveCurveToRelative(-0.8f, -0.92f, -1.49f, -0.63f)
                curveToRelative(-0.91f, 0.47f, -0.75f, 1.59f, -0.75f, 1.59f)
                reflectiveCurveToRelative(-0.06f, 0.13f, -0.15f, 0.01f)
                curveToRelative(0f, 0f, 0f, 0f, -0.01f, -0.01f)
                curveToRelative(-0.08f, -0.28f, -0.26f, -0.53f, -0.26f, -0.53f)
                curveToRelative(-1f, -1.15f, -2.21f, -0.2f, -2.21f, -0.2f)
                curveToRelative(0.54f, 0.49f, 1.06f, 1.88f, 1.29f, 2.51f)
                reflectiveCurveToRelative(0.51f, 0.97f, 0.51f, 0.97f)
                curveToRelative(0.22f, -0.31f, 2.18f, -0.56f, 5.4f, -0.56f)
                curveToRelative(3.22f, 0f, 5.18f, 0.25f, 5.4f, 0.56f)
                curveToRelative(0f, 0f, 0.28f, -0.34f, 0.51f, -0.97f)
                reflectiveCurveToRelative(0.75f, -2.02f, 1.29f, -2.51f)
                curveToRelative(0f, 0f, -1.21f, -0.95f, -2.21f, 0.2f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFD83D))) {
                moveToRelative(17.9f, 18.61f)
                lineToRelative(0.02f, 0.34f)
                reflectiveCurveToRelative(2.08f, -0.61f, 3f, -0.57f)
                curveToRelative(0.91f, 0.01f, 1.28f, 0.29f, 1.28f, 0.29f)
                reflectiveCurveToRelative(0.67f, 0.3f, 0.69f, 1.26f)
                reflectiveCurveToRelative(-0.43f, 1.92f, -0.43f, 1.92f)
                reflectiveCurveToRelative(0.31f, -0.18f, 0.59f, -0.19f)
                curveToRelative(0f, 0f, 0.29f, -0.66f, 0.3f, -1.77f)
                curveToRelative(-0.08f, -0.79f, -0.61f, -1.26f, -0.61f, -1.26f)
                reflectiveCurveToRelative(-0.42f, -0.58f, -1.58f, -0.61f)
                curveToRelative(-1.15f, -0.02f, -3.26f, 0.59f, -3.26f, 0.59f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFD83D))) {
                moveToRelative(20.77f, 18.52f)
                reflectiveCurveToRelative(1.64f, -0.12f, 1.94f, 1.04f)
                reflectiveCurveToRelative(-0.35f, 2.13f, -0.35f, 2.13f)
                reflectiveCurveToRelative(-0.31f, 0.42f, -0.93f, -0.17f)
                curveToRelative(-0.42f, -0.56f, -0.94f, -0.41f, -0.94f, -0.41f)
                reflectiveCurveToRelative(-0.69f, 0.11f, -1.09f, 0.26f)
                curveToRelative(-0.38f, 0.09f, -0.61f, 0.08f, -0.74f, 0.01f)
                reflectiveCurveToRelative(-0.24f, -0.12f, -0.35f, -0.34f)
                curveToRelative(-0.11f, -0.21f, -0.46f, -0.97f, 0.26f, -1.61f)
                curveToRelative(0.73f, -0.63f, 1.56f, -0.93f, 2.2f, -0.91f)
                close()
            }
            group(
                clipPathData = PathData {
                    moveToRelative(20.77f, 18.52f)
                    reflectiveCurveToRelative(1.64f, -0.12f, 1.94f, 1.04f)
                    reflectiveCurveToRelative(-0.35f, 2.13f, -0.35f, 2.13f)
                    reflectiveCurveToRelative(-0.31f, 0.42f, -0.93f, -0.17f)
                    curveToRelative(-0.42f, -0.56f, -0.94f, -0.41f, -0.94f, -0.41f)
                    reflectiveCurveToRelative(-0.69f, 0.11f, -1.09f, 0.26f)
                    curveToRelative(-0.38f, 0.09f, -0.61f, 0.08f, -0.74f, 0.01f)
                    reflectiveCurveToRelative(-0.24f, -0.12f, -0.35f, -0.34f)
                    curveToRelative(-0.11f, -0.21f, -0.46f, -0.97f, 0.26f, -1.61f)
                    curveToRelative(0.73f, -0.63f, 1.56f, -0.93f, 2.2f, -0.91f)
                    close()
                }
            ) {
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(22.58f, 22.01f)
                    lineToRelative(0.02f, -3.54f)
                    horizontalLineToRelative(0.1f)
                    lineToRelative(-0.02f, 3.54f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(22.22f, 22.01f)
                    lineToRelative(0.01f, -3.54f)
                    horizontalLineToRelative(0.1f)
                    lineToRelative(-0.01f, 3.54f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(21.85f, 22.01f)
                    lineToRelative(0.02f, -3.54f)
                    horizontalLineToRelative(0.1f)
                    lineToRelative(-0.01f, 3.54f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(21.49f, 22.01f)
                    lineToRelative(0.01f, -3.54f)
                    lineToRelative(0.11f, 0.01f)
                    lineToRelative(-0.02f, 3.53f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(21.13f, 22.02f)
                    lineToRelative(0.01f, -3.54f)
                    horizontalLineToRelative(0.1f)
                    lineToRelative(-0.01f, 3.54f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(20.76f, 22.02f)
                    lineToRelative(0.02f, -3.54f)
                    horizontalLineToRelative(0.1f)
                    lineToRelative(-0.01f, 3.54f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(20.4f, 22.02f)
                    lineToRelative(0.01f, -3.54f)
                    horizontalLineToRelative(0.11f)
                    lineToRelative(-0.02f, 3.54f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(20.04f, 22.02f)
                    lineToRelative(0.01f, -3.54f)
                    horizontalLineToRelative(0.1f)
                    lineToRelative(-0.01f, 3.54f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(19.67f, 22.02f)
                    lineToRelative(0.02f, -3.54f)
                    horizontalLineToRelative(0.1f)
                    lineToRelative(-0.02f, 3.54f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(19.31f, 22.02f)
                    lineToRelative(0.01f, -3.54f)
                    horizontalLineToRelative(0.1f)
                    lineToRelative(-0.01f, 3.54f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(18.95f, 22.02f)
                    lineToRelative(0.01f, -3.54f)
                    horizontalLineToRelative(0.1f)
                    lineToRelative(-0.01f, 3.54f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(18.58f, 22.02f)
                    lineToRelative(0.02f, -3.54f)
                    horizontalLineToRelative(0.1f)
                    lineToRelative(-0.02f, 3.54f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(18.22f, 22.02f)
                    lineToRelative(0.01f, -3.54f)
                    horizontalLineToRelative(0.1f)
                    lineToRelative(-0.01f, 3.54f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFFFD83D))) {
                    moveToRelative(17.4f, 18.61f)
                    reflectiveCurveToRelative(0.21f, 2.27f, 0.32f, 2.51f)
                    curveToRelative(0f, 0f, 0.23f, 0.07f, 0.51f, 0.25f)
                    curveToRelative(0f, 0f, -0.34f, -1.03f, -0.4f, -1.82f)
                    reflectiveCurveToRelative(-0.09f, -1f, -0.09f, -1f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFFFFD83D)),
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.12f
                ) {
                    moveToRelative(18.09f, 18.13f)
                    curveToRelative(0f, 0.19f, -0.16f, 0.35f, -0.35f, 0.35f)
                    reflectiveCurveToRelative(-0.35f, -0.16f, -0.35f, -0.35f)
                    reflectiveCurveToRelative(0.16f, -0.35f, 0.35f, -0.35f)
                    reflectiveCurveToRelative(0.35f, 0.16f, 0.35f, 0.35f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFFFFD83D)),
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.12f
                ) {
                    moveToRelative(18.89f, 17.94f)
                    curveToRelative(0f, 0.19f, -0.16f, 0.35f, -0.35f, 0.35f)
                    reflectiveCurveToRelative(-0.35f, -0.16f, -0.35f, -0.35f)
                    reflectiveCurveToRelative(0.16f, -0.35f, 0.35f, -0.35f)
                    curveToRelative(0.2f, 0f, 0.35f, 0.16f, 0.35f, 0.35f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFFFFD83D)),
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.12f
                ) {
                    moveToRelative(19.7f, 17.75f)
                    curveToRelative(0f, 0.19f, -0.16f, 0.35f, -0.35f, 0.35f)
                    reflectiveCurveToRelative(-0.35f, -0.16f, -0.35f, -0.35f)
                    reflectiveCurveToRelative(0.16f, -0.35f, 0.35f, -0.35f)
                    reflectiveCurveToRelative(0.35f, 0.16f, 0.35f, 0.35f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFFFFD83D)),
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.12f
                ) {
                    moveToRelative(20.51f, 17.61f)
                    curveToRelative(0f, 0.19f, -0.16f, 0.35f, -0.35f, 0.35f)
                    reflectiveCurveToRelative(-0.35f, -0.16f, -0.35f, -0.35f)
                    reflectiveCurveToRelative(0.16f, -0.35f, 0.35f, -0.35f)
                    reflectiveCurveToRelative(0.35f, 0.16f, 0.35f, 0.35f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFFFFD83D)),
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.12f
                ) {
                    moveToRelative(21.31f, 17.53f)
                    curveToRelative(0f, 0.19f, -0.16f, 0.35f, -0.35f, 0.35f)
                    reflectiveCurveToRelative(-0.35f, -0.16f, -0.35f, -0.35f)
                    reflectiveCurveToRelative(0.16f, -0.35f, 0.35f, -0.35f)
                    curveToRelative(0.2f, 0f, 0.35f, 0.16f, 0.35f, 0.35f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFFFFD83D)),
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.12f
                ) {
                    moveToRelative(22.11f, 17.59f)
                    curveToRelative(0f, 0.19f, -0.16f, 0.35f, -0.35f, 0.35f)
                    reflectiveCurveToRelative(-0.35f, -0.16f, -0.35f, -0.35f)
                    reflectiveCurveToRelative(0.16f, -0.35f, 0.35f, -0.35f)
                    reflectiveCurveToRelative(0.35f, 0.15f, 0.35f, 0.35f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFFFFD83D)),
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.12f
                ) {
                    moveToRelative(22.86f, 17.83f)
                    curveToRelative(0f, 0.19f, -0.16f, 0.35f, -0.35f, 0.35f)
                    reflectiveCurveToRelative(-0.35f, -0.16f, -0.35f, -0.35f)
                    reflectiveCurveToRelative(0.16f, -0.35f, 0.35f, -0.35f)
                    curveToRelative(0.2f, 0f, 0.35f, 0.16f, 0.35f, 0.35f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFFFFD83D)),
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.12f
                ) {
                    moveToRelative(23.51f, 18.31f)
                    curveToRelative(0f, 0.19f, -0.16f, 0.35f, -0.35f, 0.35f)
                    reflectiveCurveToRelative(-0.35f, -0.16f, -0.35f, -0.35f)
                    reflectiveCurveToRelative(0.16f, -0.35f, 0.35f, -0.35f)
                    reflectiveCurveToRelative(0.35f, 0.16f, 0.35f, 0.35f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFFFFD83D)),
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.12f
                ) {
                    moveToRelative(23.96f, 18.97f)
                    curveToRelative(0f, 0.19f, -0.16f, 0.35f, -0.35f, 0.35f)
                    reflectiveCurveToRelative(-0.35f, -0.16f, -0.35f, -0.35f)
                    reflectiveCurveToRelative(0.16f, -0.35f, 0.35f, -0.35f)
                    reflectiveCurveToRelative(0.35f, 0.16f, 0.35f, 0.35f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFFFFD83D)),
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.12f
                ) {
                    moveToRelative(24.18f, 19.74f)
                    curveToRelative(0f, 0.19f, -0.16f, 0.35f, -0.35f, 0.35f)
                    reflectiveCurveToRelative(-0.35f, -0.16f, -0.35f, -0.35f)
                    reflectiveCurveToRelative(0.16f, -0.35f, 0.35f, -0.35f)
                    reflectiveCurveToRelative(0.35f, 0.16f, 0.35f, 0.35f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFFFFD83D)),
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.12f
                ) {
                    moveTo(23.82f, 20.52f)
                    moveToRelative(-0.35f, 0f)
                    arcToRelative(0.35f, 0.35f, 0f, isMoreThanHalf = true, isPositiveArc = true, 0.7f, 0f)
                    arcToRelative(0.35f, 0.35f, 0f, isMoreThanHalf = true, isPositiveArc = true, -0.7f, 0f)
                }
                path(
                    fill = SolidColor(Color(0xFFFFD83D)),
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.12f
                ) {
                    moveToRelative(24.02f, 21.32f)
                    curveToRelative(0f, 0.19f, -0.16f, 0.35f, -0.35f, 0.35f)
                    reflectiveCurveToRelative(-0.35f, -0.16f, -0.35f, -0.35f)
                    reflectiveCurveToRelative(0.16f, -0.35f, 0.35f, -0.35f)
                    reflectiveCurveToRelative(0.35f, 0.15f, 0.35f, 0.35f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(22.88f, 22.93f)
                    curveToRelative(-0.02f, 0f, -0.05f, 0.02f, -0.08f, 0.04f)
                    curveToRelative(-0.08f, 0.06f, -0.31f, 0.3f, -0.4f, 0.42f)
                    reflectiveCurveToRelative(-0.19f, 0.1f, -0.21f, 0.07f)
                    curveToRelative(-0.02f, -0.04f, -0.06f, -0.13f, -0.08f, -0.2f)
                    curveToRelative(-0.03f, -0.11f, -0.09f, -0.11f, -0.1f, 0f)
                    reflectiveCurveToRelative(0.04f, 0.25f, 0.08f, 0.44f)
                    curveToRelative(0.04f, 0.2f, 0f, 0.52f, -0.17f, 0.9f)
                    curveToRelative(-0.05f, 0.12f, 0.04f, 0.08f, 0.11f, -0.05f)
                    curveToRelative(0.18f, -0.32f, 0.61f, -0.68f, 1.05f, -0.99f)
                    curveToRelative(0.08f, -0.06f, 0.07f, -0.12f, -0.05f, -0.1f)
                    curveToRelative(-0.12f, 0.03f, -0.18f, 0.12f, -0.31f, 0.12f)
                    curveToRelative(-0.04f, 0f, -0.09f, 0f, -0.08f, -0.09f)
                    curveToRelative(0.01f, -0.1f, 0.13f, -0.32f, 0.23f, -0.46f)
                    curveToRelative(0.06f, -0.08f, 0.05f, -0.1f, 0.01f, -0.1f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(20.46f, 22.34f)
                    curveToRelative(-0.02f, 0f, -0.05f, 0.02f, -0.07f, 0.05f)
                    curveToRelative(-0.07f, 0.07f, -0.27f, 0.33f, -0.34f, 0.46f)
                    curveToRelative(-0.08f, 0.13f, -0.17f, 0.12f, -0.2f, 0.09f)
                    reflectiveCurveToRelative(-0.07f, -0.12f, -0.1f, -0.19f)
                    curveToRelative(-0.04f, -0.1f, -0.1f, -0.1f, -0.1f, 0.01f)
                    curveToRelative(0.01f, 0.11f, 0.07f, 0.24f, 0.13f, 0.43f)
                    reflectiveCurveToRelative(0.06f, 0.52f, -0.06f, 0.92f)
                    curveToRelative(-0.04f, 0.13f, 0.05f, 0.07f, 0.11f, -0.07f)
                    curveToRelative(0.14f, -0.34f, 0.53f, -0.74f, 0.93f, -1.11f)
                    curveToRelative(0.08f, -0.07f, 0.05f, -0.13f, -0.06f, -0.09f)
                    reflectiveCurveToRelative(-0.17f, 0.14f, -0.3f, 0.16f)
                    curveToRelative(-0.04f, 0.01f, -0.09f, 0.01f, -0.09f, -0.08f)
                    curveToRelative(0f, -0.1f, 0.09f, -0.33f, 0.18f, -0.49f)
                    curveToRelative(0.02f, -0.07f, 0f, -0.09f, -0.03f, -0.09f)
                    close()
                }
                path(
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.1f,
                    strokeLineCap = StrokeCap.Round
                ) {
                    moveToRelative(23.75f, 22.31f)
                    curveToRelative(-0.04f, 0.02f, -0.1f, 0.01f, -0.14f, 0.05f)
                    curveToRelative(-0.02f, 0.02f, -0.04f, 0.04f, -0.06f, 0.05f)
                    curveToRelative(-0.01f, 0.01f, -0.03f, 0.02f, -0.04f, 0.03f)
                    reflectiveCurveToRelative(-0.02f, 0.03f, -0.03f, 0.04f)
                    curveToRelative(-0.02f, 0.03f, -0.01f, 0.08f, -0.03f, 0.11f)
                }
                path(
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.1f,
                    strokeLineCap = StrokeCap.Round
                ) {
                    moveToRelative(23.39f, 22.22f)
                    curveToRelative(-0.01f, 0.04f, -0.05f, 0.07f, -0.08f, 0.1f)
                    reflectiveCurveToRelative(-0.06f, 0.07f, -0.08f, 0.11f)
                    curveToRelative(-0.01f, 0.04f, -0.02f, 0.09f, -0.05f, 0.12f)
                }
                path(
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.1f,
                    strokeLineCap = StrokeCap.Round
                ) {
                    moveToRelative(23.03f, 22.29f)
                    curveToRelative(-0.02f, 0.03f, -0.04f, 0.06f, -0.06f, 0.1f)
                    curveToRelative(-0.01f, 0.03f, -0.02f, 0.06f, -0.03f, 0.09f)
                    curveToRelative(-0.02f, 0.07f, -0.05f, 0.14f, -0.07f, 0.2f)
                }
                path(
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.1f,
                    strokeLineCap = StrokeCap.Round
                ) {
                    moveToRelative(22.64f, 22.4f)
                    curveToRelative(-0.04f, 0.11f, -0.03f, 0.24f, -0.08f, 0.36f)
                }
                path(
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.1f,
                    strokeLineCap = StrokeCap.Round
                ) {
                    moveToRelative(22.33f, 22.54f)
                    curveToRelative(0.01f, 0.08f, 0.01f, 0.16f, -0.02f, 0.24f)
                    curveToRelative(-0.02f, 0.06f, -0.04f, 0.12f, 0.01f, 0.17f)
                }
                path(
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.1f,
                    strokeLineCap = StrokeCap.Round
                ) {
                    moveToRelative(20.33f, 21.65f)
                    lineToRelative(-0.13f, 0.35f)
                }
                path(
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.1f,
                    strokeLineCap = StrokeCap.Round
                ) {
                    moveToRelative(20.65f, 21.66f)
                    lineToRelative(-0.17f, 0.36f)
                }
                path(
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.1f,
                    strokeLineCap = StrokeCap.Round
                ) {
                    moveToRelative(20.9f, 21.8f)
                    lineToRelative(-0.13f, 0.25f)
                }
                path(
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.1f,
                    strokeLineCap = StrokeCap.Round
                ) {
                    moveToRelative(21.11f, 22f)
                    lineToRelative(-0.16f, 0.32f)
                }
                path(
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.1f,
                    strokeLineCap = StrokeCap.Round
                ) {
                    moveToRelative(21.31f, 22.28f)
                    lineToRelative(-0.24f, 0.35f)
                }
                path(
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.1f,
                    strokeLineCap = StrokeCap.Round
                ) {
                    moveToRelative(21.41f, 22.66f)
                    lineToRelative(-0.24f, 0.31f)
                }
                path(
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.1f,
                    strokeLineCap = StrokeCap.Round
                ) {
                    moveToRelative(19.99f, 21.71f)
                    lineToRelative(-0.15f, 0.44f)
                }
                path(
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.1f,
                    strokeLineCap = StrokeCap.Round
                ) {
                    moveToRelative(19.62f, 21.92f)
                    lineToRelative(-0.1f, 0.37f)
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(17.25f, 21.45f)
                    curveToRelative(-0.03f, 0f, -0.06f, 0.01f, -0.07f, 0.03f)
                    curveToRelative(-0.01f, 0.01f, -0.01f, 0.03f, -0.02f, 0.04f)
                    curveToRelative(-0.01f, 0.02f, -0.01f, 0.05f, -0.01f, 0.08f)
                    reflectiveCurveToRelative(0.01f, 0.05f, 0.01f, 0.08f)
                    verticalLineToRelative(0.04f)
                    curveToRelative(0f, 0.01f, 0.01f, 0.02f, 0.01f, 0.03f)
                    curveToRelative(0.01f, 0.02f, 0.01f, 0.04f, 0.01f, 0.06f)
                    reflectiveCurveToRelative(0f, 0.05f, 0.01f, 0.07f)
                    curveToRelative(0.01f, 0.01f, 0.02f, 0.02f, 0.03f, 0.05f)
                    curveToRelative(0.01f, 0.01f, 0.02f, 0.01f, 0.03f, 0f)
                    reflectiveCurveToRelative(0.02f, -0.02f, 0.02f, -0.03f)
                    curveToRelative(0.02f, -0.04f, 0.02f, -0.08f, 0.02f, -0.12f)
                    curveToRelative(0f, -0.02f, 0f, -0.04f, 0f, -0.07f)
                    curveToRelative(0f, -0.02f, 0.01f, -0.04f, 0.01f, -0.06f)
                    curveToRelative(0.01f, -0.04f, 0.02f, -0.08f, 0.01f, -0.12f)
                    curveToRelative(-0.01f, -0.03f, -0.03f, -0.08f, -0.06f, -0.08f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(17.46f, 21.51f)
                    curveToRelative(-0.01f, 0.01f, -0.02f, 0.02f, -0.02f, 0.03f)
                    curveToRelative(-0.01f, 0.03f, -0.01f, 0.07f, -0.01f, 0.11f)
                    curveToRelative(0f, 0.08f, 0.01f, 0.16f, 0.07f, 0.26f)
                    curveToRelative(0.02f, 0f, 0.03f, -0.01f, 0.04f, -0.02f)
                    reflectiveCurveToRelative(0.01f, -0.03f, 0.02f, -0.09f)
                    curveToRelative(-0.02f, -0.05f, 0f, -0.11f, 0.01f, -0.16f)
                    curveToRelative(0.01f, -0.04f, 0.01f, -0.1f, -0.03f, -0.13f)
                    curveToRelative(-0.03f, -0.02f, -0.06f, -0.02f, -0.08f, 0f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(17.84f, 21.63f)
                    curveToRelative(-0.01f, -0.01f, -0.03f, -0.02f, -0.05f, -0.01f)
                    curveToRelative(-0.02f, 0f, -0.03f, 0.02f, -0.04f, 0.03f)
                    curveToRelative(0f, 0.01f, -0.01f, 0.02f, -0.01f, 0.04f)
                    verticalLineToRelative(0.07f)
                    verticalLineToRelative(0.05f)
                    curveToRelative(0f, 0.02f, -0.01f, 0.04f, 0f, 0.06f)
                    curveToRelative(0f, 0.01f, 0.01f, 0.03f, 0.02f, 0.04f)
                    curveToRelative(0.01f, 0.03f, 0f, 0.05f, 0f, 0.08f)
                    curveToRelative(0f, 0.01f, 0f, 0.03f, 0.01f, 0.04f)
                    reflectiveCurveToRelative(0.02f, 0.02f, 0.04f, 0.02f)
                    curveToRelative(0.02f, -0.01f, 0.02f, -0.03f, 0.03f, -0.05f)
                    curveToRelative(0.01f, -0.07f, -0.01f, -0.13f, 0.01f, -0.19f)
                    curveToRelative(0.01f, -0.03f, 0.05f, -0.07f, 0.03f, -0.1f)
                    curveToRelative(0f, -0.01f, -0.01f, -0.02f, -0.02f, -0.03f)
                    curveToRelative(-0.01f, -0.02f, -0.01f, -0.04f, -0.02f, -0.05f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(18.06f, 22.04f)
                    curveToRelative(-0.02f, 0.09f, -0.04f, 0.19f, -0.05f, 0.3f)
                    curveToRelative(0f, 0.01f, 0.01f, 0.02f, 0.06f, 0.04f)
                    curveToRelative(0.01f, -0.01f, 0.03f, -0.01f, 0.03f, -0.03f)
                    curveToRelative(0.01f, -0.02f, 0f, -0.04f, 0.01f, -0.06f)
                    curveToRelative(0f, -0.02f, 0.01f, -0.03f, 0.02f, -0.04f)
                    curveToRelative(0.02f, -0.05f, 0.04f, -0.11f, 0.04f, -0.16f)
                    curveToRelative(0f, -0.04f, 0.03f, -0.11f, -0.01f, -0.13f)
                    curveToRelative(-0.06f, -0.04f, -0.09f, 0.04f, -0.1f, 0.08f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(18.3f, 22.63f)
                    curveToRelative(-0.01f, 0.05f, -0.04f, 0.1f, -0.08f, 0.12f)
                    curveToRelative(-0.04f, -0.03f, -0.04f, -0.09f, -0.04f, -0.13f)
                    curveToRelative(0f, -0.05f, 0f, -0.1f, 0.02f, -0.15f)
                    curveToRelative(0.01f, -0.03f, 0.03f, -0.05f, 0.04f, -0.08f)
                    curveToRelative(0f, -0.01f, 0.01f, -0.02f, 0.01f, -0.04f)
                    curveToRelative(0.04f, -0.06f, 0.12f, 0f, 0.1f, 0.05f)
                    curveToRelative(-0.01f, 0.03f, -0.03f, 0.06f, -0.04f, 0.1f)
                    curveToRelative(0f, 0.05f, 0f, 0.09f, -0.01f, 0.13f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(18.04f, 25.24f)
                    curveToRelative(-0.04f, 0f, -0.06f, -0.03f, -0.06f, -0.06f)
                    lineToRelative(0.02f, -0.4f)
                    curveToRelative(0f, -0.03f, 0.02f, -0.06f, 0.06f, -0.06f)
                    curveToRelative(0.03f, 0f, 0.06f, 0.03f, 0.06f, 0.06f)
                    lineToRelative(-0.02f, 0.4f)
                    curveToRelative(0f, 0.04f, -0.03f, 0.06f, -0.06f, 0.06f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(17.61f, 25.26f)
                    curveToRelative(-0.04f, 0f, -0.06f, -0.03f, -0.06f, -0.06f)
                    lineToRelative(0.02f, -0.44f)
                    curveToRelative(0f, -0.03f, 0.02f, -0.06f, 0.06f, -0.06f)
                    curveToRelative(0.03f, 0f, 0.06f, 0.03f, 0.06f, 0.06f)
                    lineToRelative(-0.02f, 0.44f)
                    curveToRelative(0f, 0.03f, -0.03f, 0.06f, -0.06f, 0.06f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(18.42f, 25.36f)
                    curveToRelative(-0.04f, 0f, -0.06f, -0.03f, -0.06f, -0.06f)
                    lineToRelative(0.02f, -0.5f)
                    curveToRelative(0f, -0.03f, 0.03f, -0.06f, 0.06f, -0.06f)
                    curveToRelative(0.03f, 0f, 0.06f, 0.03f, 0.06f, 0.06f)
                    lineToRelative(-0.02f, 0.5f)
                    curveToRelative(0f, 0.03f, -0.03f, 0.06f, -0.06f, 0.06f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(18.76f, 25.26f)
                    curveToRelative(-0.04f, 0f, -0.06f, -0.03f, -0.06f, -0.06f)
                    lineToRelative(0.01f, -0.39f)
                    curveToRelative(0f, -0.03f, 0.03f, -0.06f, 0.06f, -0.06f)
                    curveToRelative(0.03f, 0f, 0.06f, 0.03f, 0.06f, 0.06f)
                    lineToRelative(-0.01f, 0.39f)
                    curveToRelative(0f, 0.03f, -0.03f, 0.06f, -0.06f, 0.06f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(19.11f, 25.35f)
                    curveToRelative(-0.04f, 0f, -0.06f, -0.03f, -0.06f, -0.06f)
                    lineToRelative(0.04f, -0.47f)
                    curveToRelative(0f, -0.03f, 0.03f, -0.06f, 0.06f, -0.06f)
                    reflectiveCurveToRelative(0.06f, 0.03f, 0.05f, 0.06f)
                    lineToRelative(-0.04f, 0.47f)
                    curveToRelative(0.01f, 0.04f, -0.02f, 0.06f, -0.05f, 0.06f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(19.43f, 25.46f)
                    curveToRelative(-0.04f, -0.01f, -0.06f, -0.04f, -0.06f, -0.07f)
                    lineToRelative(0.07f, -0.54f)
                    curveToRelative(0f, -0.03f, 0.04f, -0.06f, 0.07f, -0.05f)
                    curveToRelative(0.03f, 0f, 0.06f, 0.03f, 0.05f, 0.07f)
                    lineToRelative(-0.07f, 0.54f)
                    curveToRelative(0f, 0.02f, -0.03f, 0.05f, -0.06f, 0.05f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(19.79f, 25.43f)
                    curveToRelative(-0.01f, 0f, -0.01f, 0f, 0f, 0f)
                    curveToRelative(-0.04f, -0.01f, -0.06f, -0.04f, -0.06f, -0.07f)
                    lineToRelative(0.07f, -0.47f)
                    curveToRelative(0f, -0.03f, 0.04f, -0.06f, 0.07f, -0.05f)
                    curveToRelative(0.03f, 0f, 0.06f, 0.03f, 0.05f, 0.07f)
                    lineToRelative(-0.07f, 0.47f)
                    curveToRelative(-0.01f, 0.03f, -0.03f, 0.05f, -0.06f, 0.05f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(20.14f, 25.45f)
                    curveToRelative(-0.04f, 0f, -0.06f, -0.03f, -0.06f, -0.07f)
                    lineToRelative(0.07f, -0.51f)
                    curveToRelative(0f, -0.03f, 0.03f, -0.06f, 0.07f, -0.05f)
                    curveToRelative(0.03f, 0f, 0.06f, 0.03f, 0.05f, 0.07f)
                    lineToRelative(-0.07f, 0.51f)
                    curveToRelative(0f, 0.03f, -0.03f, 0.05f, -0.06f, 0.05f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(20.55f, 25.44f)
                    curveToRelative(-0.01f, 0f, -0.01f, 0f, 0f, 0f)
                    curveToRelative(-0.04f, -0.01f, -0.06f, -0.04f, -0.06f, -0.07f)
                    lineToRelative(0.05f, -0.37f)
                    curveToRelative(0f, -0.03f, 0.03f, -0.06f, 0.07f, -0.05f)
                    curveToRelative(0.03f, 0f, 0.06f, 0.03f, 0.05f, 0.07f)
                    lineToRelative(-0.05f, 0.38f)
                    curveToRelative(-0.01f, 0.02f, -0.04f, 0.04f, -0.06f, 0.04f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(20.92f, 25.52f)
                    curveToRelative(-0.01f, 0f, -0.01f, 0f, 0f, 0f)
                    curveToRelative(-0.04f, -0.01f, -0.06f, -0.04f, -0.06f, -0.07f)
                    lineToRelative(0.08f, -0.46f)
                    curveToRelative(0f, -0.03f, 0.03f, -0.06f, 0.07f, -0.05f)
                    curveToRelative(0.03f, 0.01f, 0.05f, 0.04f, 0.05f, 0.07f)
                    lineToRelative(-0.08f, 0.46f)
                    curveToRelative(-0.01f, 0.03f, -0.03f, 0.05f, -0.06f, 0.05f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(21.26f, 25.55f)
                    horizontalLineToRelative(-0.01f)
                    curveToRelative(-0.03f, -0.01f, -0.05f, -0.04f, -0.04f, -0.07f)
                    lineToRelative(0.1f, -0.43f)
                    curveToRelative(0.01f, -0.03f, 0.04f, -0.05f, 0.07f, -0.04f)
                    reflectiveCurveToRelative(0.05f, 0.04f, 0.04f, 0.07f)
                    lineToRelative(-0.1f, 0.43f)
                    curveToRelative(0f, 0.02f, -0.03f, 0.04f, -0.06f, 0.04f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(21.58f, 25.54f)
                    horizontalLineToRelative(-0.01f)
                    curveToRelative(-0.03f, -0.01f, -0.05f, -0.04f, -0.05f, -0.07f)
                    lineToRelative(0.06f, -0.32f)
                    curveToRelative(0.01f, -0.03f, 0.04f, -0.05f, 0.07f, -0.05f)
                    curveToRelative(0.03f, 0.01f, 0.05f, 0.04f, 0.05f, 0.07f)
                    lineToRelative(-0.06f, 0.32f)
                    curveToRelative(-0.01f, 0.03f, -0.03f, 0.05f, -0.06f, 0.05f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(21.82f, 25.63f)
                    curveToRelative(-0.01f, 0f, -0.01f, 0f, -0.02f, 0f)
                    curveToRelative(-0.03f, -0.01f, -0.05f, -0.04f, -0.04f, -0.08f)
                    lineToRelative(0.12f, -0.37f)
                    curveToRelative(0.01f, -0.03f, 0.04f, -0.05f, 0.08f, -0.04f)
                    curveToRelative(0.03f, 0.01f, 0.05f, 0.04f, 0.04f, 0.08f)
                    lineToRelative(-0.12f, 0.37f)
                    curveToRelative(-0.01f, 0.02f, -0.03f, 0.04f, -0.06f, 0.04f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(22.11f, 25.8f)
                    curveToRelative(-0.01f, 0f, -0.01f, 0f, -0.02f, 0f)
                    curveToRelative(-0.03f, -0.01f, -0.05f, -0.04f, -0.04f, -0.07f)
                    lineToRelative(0.13f, -0.46f)
                    curveToRelative(0.01f, -0.03f, 0.04f, -0.05f, 0.07f, -0.04f)
                    reflectiveCurveToRelative(0.05f, 0.04f, 0.04f, 0.07f)
                    lineToRelative(-0.13f, 0.46f)
                    curveToRelative(0f, 0.02f, -0.03f, 0.04f, -0.05f, 0.04f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(17.09f, 25.33f)
                    curveToRelative(-0.03f, 0f, -0.06f, -0.03f, -0.06f, -0.06f)
                    verticalLineToRelative(-0.51f)
                    curveToRelative(0f, -0.03f, 0.03f, -0.06f, 0.06f, -0.06f)
                    reflectiveCurveToRelative(0.06f, 0.03f, 0.06f, 0.06f)
                    verticalLineToRelative(0.51f)
                    curveToRelative(0f, 0.03f, -0.03f, 0.06f, -0.06f, 0.06f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFFFD83D))) {
                    moveToRelative(16.27f, 18.61f)
                    lineToRelative(-0.02f, 0.34f)
                    reflectiveCurveToRelative(-2.08f, -0.61f, -3f, -0.57f)
                    curveToRelative(-0.91f, 0.01f, -1.28f, 0.29f, -1.28f, 0.29f)
                    reflectiveCurveToRelative(-0.67f, 0.3f, -0.69f, 1.26f)
                    reflectiveCurveToRelative(0.43f, 1.92f, 0.43f, 1.92f)
                    reflectiveCurveToRelative(-0.31f, -0.18f, -0.59f, -0.19f)
                    curveToRelative(0f, 0f, -0.29f, -0.66f, -0.3f, -1.77f)
                    curveToRelative(0.08f, -0.79f, 0.61f, -1.26f, 0.61f, -1.26f)
                    reflectiveCurveToRelative(0.42f, -0.58f, 1.58f, -0.61f)
                    reflectiveCurveToRelative(3.26f, 0.59f, 3.26f, 0.59f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFFFD83D))) {
                    moveToRelative(13.4f, 18.52f)
                    reflectiveCurveToRelative(-1.64f, -0.12f, -1.94f, 1.04f)
                    reflectiveCurveToRelative(0.35f, 2.13f, 0.35f, 2.13f)
                    reflectiveCurveToRelative(0.31f, 0.42f, 0.93f, -0.17f)
                    curveToRelative(0.42f, -0.56f, 0.94f, -0.41f, 0.94f, -0.41f)
                    reflectiveCurveToRelative(0.69f, 0.11f, 1.09f, 0.26f)
                    curveToRelative(0.38f, 0.09f, 0.61f, 0.08f, 0.74f, 0.01f)
                    reflectiveCurveToRelative(0.24f, -0.12f, 0.35f, -0.34f)
                    curveToRelative(0.11f, -0.21f, 0.46f, -0.97f, -0.26f, -1.61f)
                    reflectiveCurveToRelative(-1.56f, -0.93f, -2.2f, -0.91f)
                    close()
                }
            }
            group(
                clipPathData = PathData {
                    moveToRelative(13.4f, 18.52f)
                    reflectiveCurveToRelative(-1.64f, -0.12f, -1.94f, 1.04f)
                    reflectiveCurveToRelative(0.35f, 2.13f, 0.35f, 2.13f)
                    reflectiveCurveToRelative(0.31f, 0.42f, 0.93f, -0.17f)
                    curveToRelative(0.42f, -0.56f, 0.94f, -0.41f, 0.94f, -0.41f)
                    reflectiveCurveToRelative(0.69f, 0.11f, 1.09f, 0.26f)
                    curveToRelative(0.38f, 0.09f, 0.61f, 0.08f, 0.74f, 0.01f)
                    reflectiveCurveToRelative(0.24f, -0.12f, 0.35f, -0.34f)
                    curveToRelative(0.11f, -0.21f, 0.46f, -0.97f, -0.26f, -1.61f)
                    reflectiveCurveToRelative(-1.56f, -0.93f, -2.2f, -0.91f)
                    close()
                }
            ) {
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(11.59f, 22.01f)
                    lineToRelative(-0.01f, -3.54f)
                    horizontalLineToRelative(-0.11f)
                    lineToRelative(0.02f, 3.54f)
                    close()
                }
            }
            group(
                clipPathData = PathData {
                    moveToRelative(13.4f, 18.52f)
                    reflectiveCurveToRelative(-1.64f, -0.12f, -1.94f, 1.04f)
                    reflectiveCurveToRelative(0.35f, 2.13f, 0.35f, 2.13f)
                    reflectiveCurveToRelative(0.31f, 0.42f, 0.93f, -0.17f)
                    curveToRelative(0.42f, -0.56f, 0.94f, -0.41f, 0.94f, -0.41f)
                    reflectiveCurveToRelative(0.69f, 0.11f, 1.09f, 0.26f)
                    curveToRelative(0.38f, 0.09f, 0.61f, 0.08f, 0.74f, 0.01f)
                    reflectiveCurveToRelative(0.24f, -0.12f, 0.35f, -0.34f)
                    curveToRelative(0.11f, -0.21f, 0.46f, -0.97f, -0.26f, -1.61f)
                    reflectiveCurveToRelative(-1.56f, -0.93f, -2.2f, -0.91f)
                    close()
                }
            ) {
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(11.95f, 22.01f)
                    lineToRelative(-0.01f, -3.54f)
                    horizontalLineToRelative(-0.1f)
                    lineToRelative(0.01f, 3.54f)
                    close()
                }
            }
            group(
                clipPathData = PathData {
                    moveToRelative(13.4f, 18.52f)
                    reflectiveCurveToRelative(-1.64f, -0.12f, -1.94f, 1.04f)
                    reflectiveCurveToRelative(0.35f, 2.13f, 0.35f, 2.13f)
                    reflectiveCurveToRelative(0.31f, 0.42f, 0.93f, -0.17f)
                    curveToRelative(0.42f, -0.56f, 0.94f, -0.41f, 0.94f, -0.41f)
                    reflectiveCurveToRelative(0.69f, 0.11f, 1.09f, 0.26f)
                    curveToRelative(0.38f, 0.09f, 0.61f, 0.08f, 0.74f, 0.01f)
                    reflectiveCurveToRelative(0.24f, -0.12f, 0.35f, -0.34f)
                    curveToRelative(0.11f, -0.21f, 0.46f, -0.97f, -0.26f, -1.61f)
                    reflectiveCurveToRelative(-1.56f, -0.93f, -2.2f, -0.91f)
                    close()
                }
            ) {
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(12.32f, 22.01f)
                    lineToRelative(-0.02f, -3.54f)
                    horizontalLineToRelative(-0.1f)
                    lineToRelative(0.01f, 3.54f)
                    close()
                }
            }
            group(
                clipPathData = PathData {
                    moveToRelative(13.4f, 18.52f)
                    reflectiveCurveToRelative(-1.64f, -0.12f, -1.94f, 1.04f)
                    reflectiveCurveToRelative(0.35f, 2.13f, 0.35f, 2.13f)
                    reflectiveCurveToRelative(0.31f, 0.42f, 0.93f, -0.17f)
                    curveToRelative(0.42f, -0.56f, 0.94f, -0.41f, 0.94f, -0.41f)
                    reflectiveCurveToRelative(0.69f, 0.11f, 1.09f, 0.26f)
                    curveToRelative(0.38f, 0.09f, 0.61f, 0.08f, 0.74f, 0.01f)
                    reflectiveCurveToRelative(0.24f, -0.12f, 0.35f, -0.34f)
                    curveToRelative(0.11f, -0.21f, 0.46f, -0.97f, -0.26f, -1.61f)
                    reflectiveCurveToRelative(-1.56f, -0.93f, -2.2f, -0.91f)
                    close()
                }
            ) {
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(12.68f, 22.01f)
                    lineToRelative(-0.01f, -3.54f)
                    lineToRelative(-0.11f, 0.01f)
                    lineToRelative(0.02f, 3.53f)
                    close()
                }
            }
            group(
                clipPathData = PathData {
                    moveToRelative(13.4f, 18.52f)
                    reflectiveCurveToRelative(-1.64f, -0.12f, -1.94f, 1.04f)
                    reflectiveCurveToRelative(0.35f, 2.13f, 0.35f, 2.13f)
                    reflectiveCurveToRelative(0.31f, 0.42f, 0.93f, -0.17f)
                    curveToRelative(0.42f, -0.56f, 0.94f, -0.41f, 0.94f, -0.41f)
                    reflectiveCurveToRelative(0.69f, 0.11f, 1.09f, 0.26f)
                    curveToRelative(0.38f, 0.09f, 0.61f, 0.08f, 0.74f, 0.01f)
                    reflectiveCurveToRelative(0.24f, -0.12f, 0.35f, -0.34f)
                    curveToRelative(0.11f, -0.21f, 0.46f, -0.97f, -0.26f, -1.61f)
                    reflectiveCurveToRelative(-1.56f, -0.93f, -2.2f, -0.91f)
                    close()
                }
            ) {
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(13.04f, 22.02f)
                    lineToRelative(-0.01f, -3.54f)
                    horizontalLineToRelative(-0.1f)
                    lineToRelative(0.01f, 3.54f)
                    close()
                }
            }
            group(
                clipPathData = PathData {
                    moveToRelative(13.4f, 18.52f)
                    reflectiveCurveToRelative(-1.64f, -0.12f, -1.94f, 1.04f)
                    reflectiveCurveToRelative(0.35f, 2.13f, 0.35f, 2.13f)
                    reflectiveCurveToRelative(0.31f, 0.42f, 0.93f, -0.17f)
                    curveToRelative(0.42f, -0.56f, 0.94f, -0.41f, 0.94f, -0.41f)
                    reflectiveCurveToRelative(0.69f, 0.11f, 1.09f, 0.26f)
                    curveToRelative(0.38f, 0.09f, 0.61f, 0.08f, 0.74f, 0.01f)
                    reflectiveCurveToRelative(0.24f, -0.12f, 0.35f, -0.34f)
                    curveToRelative(0.11f, -0.21f, 0.46f, -0.97f, -0.26f, -1.61f)
                    reflectiveCurveToRelative(-1.56f, -0.93f, -2.2f, -0.91f)
                    close()
                }
            ) {
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(13.41f, 22.02f)
                    lineToRelative(-0.02f, -3.54f)
                    horizontalLineToRelative(-0.1f)
                    lineToRelative(0.02f, 3.54f)
                    close()
                }
            }
            group(
                clipPathData = PathData {
                    moveToRelative(13.4f, 18.52f)
                    reflectiveCurveToRelative(-1.64f, -0.12f, -1.94f, 1.04f)
                    reflectiveCurveToRelative(0.35f, 2.13f, 0.35f, 2.13f)
                    reflectiveCurveToRelative(0.31f, 0.42f, 0.93f, -0.17f)
                    curveToRelative(0.42f, -0.56f, 0.94f, -0.41f, 0.94f, -0.41f)
                    reflectiveCurveToRelative(0.69f, 0.11f, 1.09f, 0.26f)
                    curveToRelative(0.38f, 0.09f, 0.61f, 0.08f, 0.74f, 0.01f)
                    reflectiveCurveToRelative(0.24f, -0.12f, 0.35f, -0.34f)
                    curveToRelative(0.11f, -0.21f, 0.46f, -0.97f, -0.26f, -1.61f)
                    reflectiveCurveToRelative(-1.56f, -0.93f, -2.2f, -0.91f)
                    close()
                }
            ) {
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(13.77f, 22.02f)
                    lineToRelative(-0.01f, -3.54f)
                    horizontalLineToRelative(-0.1f)
                    lineToRelative(0.01f, 3.54f)
                    close()
                }
            }
            group(
                clipPathData = PathData {
                    moveToRelative(13.4f, 18.52f)
                    reflectiveCurveToRelative(-1.64f, -0.12f, -1.94f, 1.04f)
                    reflectiveCurveToRelative(0.35f, 2.13f, 0.35f, 2.13f)
                    reflectiveCurveToRelative(0.31f, 0.42f, 0.93f, -0.17f)
                    curveToRelative(0.42f, -0.56f, 0.94f, -0.41f, 0.94f, -0.41f)
                    reflectiveCurveToRelative(0.69f, 0.11f, 1.09f, 0.26f)
                    curveToRelative(0.38f, 0.09f, 0.61f, 0.08f, 0.74f, 0.01f)
                    reflectiveCurveToRelative(0.24f, -0.12f, 0.35f, -0.34f)
                    curveToRelative(0.11f, -0.21f, 0.46f, -0.97f, -0.26f, -1.61f)
                    reflectiveCurveToRelative(-1.56f, -0.93f, -2.2f, -0.91f)
                    close()
                }
            ) {
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(14.13f, 22.02f)
                    lineToRelative(-0.01f, -3.54f)
                    horizontalLineToRelative(-0.1f)
                    lineToRelative(0.01f, 3.54f)
                    close()
                }
            }
            group(
                clipPathData = PathData {
                    moveToRelative(13.4f, 18.52f)
                    reflectiveCurveToRelative(-1.64f, -0.12f, -1.94f, 1.04f)
                    reflectiveCurveToRelative(0.35f, 2.13f, 0.35f, 2.13f)
                    reflectiveCurveToRelative(0.31f, 0.42f, 0.93f, -0.17f)
                    curveToRelative(0.42f, -0.56f, 0.94f, -0.41f, 0.94f, -0.41f)
                    reflectiveCurveToRelative(0.69f, 0.11f, 1.09f, 0.26f)
                    curveToRelative(0.38f, 0.09f, 0.61f, 0.08f, 0.74f, 0.01f)
                    reflectiveCurveToRelative(0.24f, -0.12f, 0.35f, -0.34f)
                    curveToRelative(0.11f, -0.21f, 0.46f, -0.97f, -0.26f, -1.61f)
                    reflectiveCurveToRelative(-1.56f, -0.93f, -2.2f, -0.91f)
                    close()
                }
            ) {
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(14.5f, 22.02f)
                    lineToRelative(-0.02f, -3.54f)
                    horizontalLineToRelative(-0.1f)
                    lineToRelative(0.02f, 3.54f)
                    close()
                }
            }
            group(
                clipPathData = PathData {
                    moveToRelative(13.4f, 18.52f)
                    reflectiveCurveToRelative(-1.64f, -0.12f, -1.94f, 1.04f)
                    reflectiveCurveToRelative(0.35f, 2.13f, 0.35f, 2.13f)
                    reflectiveCurveToRelative(0.31f, 0.42f, 0.93f, -0.17f)
                    curveToRelative(0.42f, -0.56f, 0.94f, -0.41f, 0.94f, -0.41f)
                    reflectiveCurveToRelative(0.69f, 0.11f, 1.09f, 0.26f)
                    curveToRelative(0.38f, 0.09f, 0.61f, 0.08f, 0.74f, 0.01f)
                    reflectiveCurveToRelative(0.24f, -0.12f, 0.35f, -0.34f)
                    curveToRelative(0.11f, -0.21f, 0.46f, -0.97f, -0.26f, -1.61f)
                    reflectiveCurveToRelative(-1.56f, -0.93f, -2.2f, -0.91f)
                    close()
                }
            ) {
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(14.86f, 22.02f)
                    lineToRelative(-0.01f, -3.54f)
                    horizontalLineToRelative(-0.1f)
                    lineToRelative(0.01f, 3.54f)
                    close()
                }
            }
            group(
                clipPathData = PathData {
                    moveToRelative(13.4f, 18.52f)
                    reflectiveCurveToRelative(-1.64f, -0.12f, -1.94f, 1.04f)
                    reflectiveCurveToRelative(0.35f, 2.13f, 0.35f, 2.13f)
                    reflectiveCurveToRelative(0.31f, 0.42f, 0.93f, -0.17f)
                    curveToRelative(0.42f, -0.56f, 0.94f, -0.41f, 0.94f, -0.41f)
                    reflectiveCurveToRelative(0.69f, 0.11f, 1.09f, 0.26f)
                    curveToRelative(0.38f, 0.09f, 0.61f, 0.08f, 0.74f, 0.01f)
                    reflectiveCurveToRelative(0.24f, -0.12f, 0.35f, -0.34f)
                    curveToRelative(0.11f, -0.21f, 0.46f, -0.97f, -0.26f, -1.61f)
                    reflectiveCurveToRelative(-1.56f, -0.93f, -2.2f, -0.91f)
                    close()
                }
            ) {
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(15.23f, 22.02f)
                    lineToRelative(-0.02f, -3.54f)
                    horizontalLineToRelative(-0.1f)
                    lineToRelative(0.01f, 3.54f)
                    close()
                }
            }
            group(
                clipPathData = PathData {
                    moveToRelative(13.4f, 18.52f)
                    reflectiveCurveToRelative(-1.64f, -0.12f, -1.94f, 1.04f)
                    reflectiveCurveToRelative(0.35f, 2.13f, 0.35f, 2.13f)
                    reflectiveCurveToRelative(0.31f, 0.42f, 0.93f, -0.17f)
                    curveToRelative(0.42f, -0.56f, 0.94f, -0.41f, 0.94f, -0.41f)
                    reflectiveCurveToRelative(0.69f, 0.11f, 1.09f, 0.26f)
                    curveToRelative(0.38f, 0.09f, 0.61f, 0.08f, 0.74f, 0.01f)
                    reflectiveCurveToRelative(0.24f, -0.12f, 0.35f, -0.34f)
                    curveToRelative(0.11f, -0.21f, 0.46f, -0.97f, -0.26f, -1.61f)
                    reflectiveCurveToRelative(-1.56f, -0.93f, -2.2f, -0.91f)
                    close()
                }
            ) {
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(15.59f, 22.02f)
                    lineToRelative(-0.02f, -3.54f)
                    horizontalLineToRelative(-0.1f)
                    lineToRelative(0.02f, 3.54f)
                    close()
                }
            }
            group(
                clipPathData = PathData {
                    moveToRelative(13.4f, 18.52f)
                    reflectiveCurveToRelative(-1.64f, -0.12f, -1.94f, 1.04f)
                    reflectiveCurveToRelative(0.35f, 2.13f, 0.35f, 2.13f)
                    reflectiveCurveToRelative(0.31f, 0.42f, 0.93f, -0.17f)
                    curveToRelative(0.42f, -0.56f, 0.94f, -0.41f, 0.94f, -0.41f)
                    reflectiveCurveToRelative(0.69f, 0.11f, 1.09f, 0.26f)
                    curveToRelative(0.38f, 0.09f, 0.61f, 0.08f, 0.74f, 0.01f)
                    reflectiveCurveToRelative(0.24f, -0.12f, 0.35f, -0.34f)
                    curveToRelative(0.11f, -0.21f, 0.46f, -0.97f, -0.26f, -1.61f)
                    reflectiveCurveToRelative(-1.56f, -0.93f, -2.2f, -0.91f)
                    close()
                }
            ) {
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(15.95f, 22.02f)
                    lineToRelative(-0.01f, -3.54f)
                    horizontalLineToRelative(-0.1f)
                    lineToRelative(0.01f, 3.54f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFFFD83D))) {
                    moveToRelative(16.77f, 18.61f)
                    reflectiveCurveToRelative(-0.21f, 2.27f, -0.32f, 2.51f)
                    curveToRelative(0f, 0f, -0.23f, 0.07f, -0.51f, 0.25f)
                    curveToRelative(0f, 0f, 0.34f, -1.03f, 0.4f, -1.82f)
                    reflectiveCurveToRelative(0.09f, -1f, 0.09f, -1f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFFFFD83D)),
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.12f
                ) {
                    moveToRelative(16.08f, 18.13f)
                    curveToRelative(0f, 0.19f, 0.16f, 0.35f, 0.35f, 0.35f)
                    reflectiveCurveToRelative(0.35f, -0.16f, 0.35f, -0.35f)
                    reflectiveCurveToRelative(-0.16f, -0.35f, -0.35f, -0.35f)
                    reflectiveCurveToRelative(-0.35f, 0.16f, -0.35f, 0.35f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFFFFD83D)),
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.12f
                ) {
                    moveToRelative(15.28f, 17.94f)
                    curveToRelative(0f, 0.19f, 0.16f, 0.35f, 0.35f, 0.35f)
                    reflectiveCurveToRelative(0.35f, -0.16f, 0.35f, -0.35f)
                    reflectiveCurveToRelative(-0.16f, -0.35f, -0.35f, -0.35f)
                    curveToRelative(-0.2f, 0f, -0.35f, 0.16f, -0.35f, 0.35f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFFFFD83D)),
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.12f
                ) {
                    moveToRelative(14.47f, 17.75f)
                    curveToRelative(0f, 0.19f, 0.16f, 0.35f, 0.35f, 0.35f)
                    reflectiveCurveToRelative(0.35f, -0.16f, 0.35f, -0.35f)
                    reflectiveCurveToRelative(-0.16f, -0.35f, -0.35f, -0.35f)
                    reflectiveCurveToRelative(-0.35f, 0.16f, -0.35f, 0.35f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFFFFD83D)),
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.12f
                ) {
                    moveToRelative(13.66f, 17.61f)
                    curveToRelative(0f, 0.19f, 0.16f, 0.35f, 0.35f, 0.35f)
                    reflectiveCurveToRelative(0.35f, -0.16f, 0.35f, -0.35f)
                    reflectiveCurveToRelative(-0.16f, -0.35f, -0.35f, -0.35f)
                    reflectiveCurveToRelative(-0.35f, 0.16f, -0.35f, 0.35f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFFFFD83D)),
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.12f
                ) {
                    moveToRelative(12.86f, 17.53f)
                    curveToRelative(0f, 0.19f, 0.16f, 0.35f, 0.35f, 0.35f)
                    reflectiveCurveToRelative(0.35f, -0.16f, 0.35f, -0.35f)
                    reflectiveCurveToRelative(-0.16f, -0.35f, -0.35f, -0.35f)
                    reflectiveCurveToRelative(-0.35f, 0.16f, -0.35f, 0.35f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFFFFD83D)),
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.12f
                ) {
                    moveToRelative(12.07f, 17.59f)
                    curveToRelative(0f, 0.19f, 0.16f, 0.35f, 0.35f, 0.35f)
                    reflectiveCurveToRelative(0.35f, -0.16f, 0.35f, -0.35f)
                    reflectiveCurveToRelative(-0.16f, -0.35f, -0.35f, -0.35f)
                    curveToRelative(-0.2f, 0f, -0.35f, 0.15f, -0.35f, 0.35f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFFFFD83D)),
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.12f
                ) {
                    moveToRelative(11.31f, 17.83f)
                    curveToRelative(0f, 0.19f, 0.16f, 0.35f, 0.35f, 0.35f)
                    reflectiveCurveToRelative(0.35f, -0.16f, 0.35f, -0.35f)
                    reflectiveCurveToRelative(-0.16f, -0.35f, -0.35f, -0.35f)
                    curveToRelative(-0.2f, 0f, -0.35f, 0.16f, -0.35f, 0.35f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFFFFD83D)),
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.12f
                ) {
                    moveToRelative(10.66f, 18.31f)
                    curveToRelative(0f, 0.19f, 0.16f, 0.35f, 0.35f, 0.35f)
                    reflectiveCurveToRelative(0.35f, -0.16f, 0.35f, -0.35f)
                    reflectiveCurveToRelative(-0.16f, -0.35f, -0.35f, -0.35f)
                    curveToRelative(-0.2f, 0f, -0.35f, 0.16f, -0.35f, 0.35f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFFFFD83D)),
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.12f
                ) {
                    moveToRelative(10.21f, 18.97f)
                    curveToRelative(0f, 0.19f, 0.16f, 0.35f, 0.35f, 0.35f)
                    reflectiveCurveToRelative(0.35f, -0.16f, 0.35f, -0.35f)
                    reflectiveCurveToRelative(-0.16f, -0.35f, -0.35f, -0.35f)
                    reflectiveCurveToRelative(-0.35f, 0.16f, -0.35f, 0.35f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFFFFD83D)),
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.12f
                ) {
                    moveToRelative(9.99f, 19.74f)
                    curveToRelative(0f, 0.19f, 0.16f, 0.35f, 0.35f, 0.35f)
                    reflectiveCurveToRelative(0.35f, -0.16f, 0.35f, -0.35f)
                    reflectiveCurveToRelative(-0.16f, -0.35f, -0.35f, -0.35f)
                    reflectiveCurveToRelative(-0.35f, 0.16f, -0.35f, 0.35f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFFFFD83D)),
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.12f
                ) {
                    moveTo(10.35f, 20.52f)
                    moveToRelative(-0.35f, 0f)
                    arcToRelative(0.35f, 0.35f, 0f, isMoreThanHalf = true, isPositiveArc = true, 0.7f, 0f)
                    arcToRelative(0.35f, 0.35f, 0f, isMoreThanHalf = true, isPositiveArc = true, -0.7f, 0f)
                }
                path(
                    fill = SolidColor(Color(0xFFFFD83D)),
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.12f
                ) {
                    moveToRelative(10.15f, 21.32f)
                    curveToRelative(0f, 0.19f, 0.16f, 0.35f, 0.35f, 0.35f)
                    reflectiveCurveToRelative(0.35f, -0.16f, 0.35f, -0.35f)
                    reflectiveCurveToRelative(-0.16f, -0.35f, -0.35f, -0.35f)
                    reflectiveCurveToRelative(-0.35f, 0.15f, -0.35f, 0.35f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(11.29f, 23.02f)
                    curveToRelative(0.11f, 0.15f, 0.22f, 0.37f, 0.23f, 0.46f)
                    curveToRelative(0.01f, 0.1f, -0.04f, 0.09f, -0.08f, 0.09f)
                    curveToRelative(-0.13f, 0f, -0.19f, -0.1f, -0.31f, -0.12f)
                    curveToRelative(-0.12f, -0.03f, -0.14f, 0.03f, -0.05f, 0.1f)
                    curveToRelative(0.44f, 0.32f, 0.87f, 0.67f, 1.05f, 0.99f)
                    curveToRelative(0.07f, 0.13f, 0.17f, 0.18f, 0.11f, 0.05f)
                    curveToRelative(-0.16f, -0.38f, -0.2f, -0.71f, -0.17f, -0.9f)
                    curveToRelative(0.04f, -0.2f, 0.08f, -0.33f, 0.08f, -0.44f)
                    curveToRelative(-0.01f, -0.11f, -0.07f, -0.11f, -0.1f, 0f)
                    curveToRelative(-0.02f, 0.07f, -0.05f, 0.17f, -0.08f, 0.2f)
                    curveToRelative(-0.02f, 0.04f, -0.12f, 0.06f, -0.21f, -0.07f)
                    curveToRelative(-0.09f, -0.12f, -0.32f, -0.36f, -0.4f, -0.42f)
                    curveToRelative(-0.03f, -0.02f, -0.06f, -0.04f, -0.08f, -0.04f)
                    curveToRelative(-0.03f, 0.01f, -0.04f, 0.03f, 0.01f, 0.1f)
                    close()
                }
                path(
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.1f,
                    strokeLineCap = StrokeCap.Round
                ) {
                    moveToRelative(10.42f, 22.31f)
                    curveToRelative(0.04f, 0.02f, 0.1f, 0.01f, 0.14f, 0.05f)
                    curveToRelative(0.02f, 0.02f, 0.04f, 0.04f, 0.06f, 0.05f)
                    curveToRelative(0.01f, 0.01f, 0.03f, 0.02f, 0.04f, 0.03f)
                    reflectiveCurveToRelative(0.02f, 0.03f, 0.03f, 0.04f)
                    curveToRelative(0.02f, 0.03f, 0.01f, 0.08f, 0.03f, 0.11f)
                }
                path(
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.1f,
                    strokeLineCap = StrokeCap.Round
                ) {
                    moveToRelative(10.78f, 22.22f)
                    curveToRelative(0.01f, 0.04f, 0.05f, 0.07f, 0.08f, 0.1f)
                    reflectiveCurveToRelative(0.06f, 0.07f, 0.08f, 0.11f)
                    curveToRelative(0.01f, 0.04f, 0.02f, 0.09f, 0.05f, 0.12f)
                }
                path(
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.1f,
                    strokeLineCap = StrokeCap.Round
                ) {
                    moveToRelative(11.14f, 22.29f)
                    curveToRelative(0.02f, 0.03f, 0.04f, 0.06f, 0.06f, 0.1f)
                    curveToRelative(0.01f, 0.03f, 0.02f, 0.06f, 0.03f, 0.09f)
                    curveToRelative(0.02f, 0.07f, 0.05f, 0.14f, 0.07f, 0.2f)
                }
                path(
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.1f,
                    strokeLineCap = StrokeCap.Round
                ) {
                    moveToRelative(11.53f, 22.4f)
                    curveToRelative(0.04f, 0.11f, 0.03f, 0.24f, 0.08f, 0.36f)
                }
                path(
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.1f,
                    strokeLineCap = StrokeCap.Round
                ) {
                    moveToRelative(11.84f, 22.54f)
                    curveToRelative(-0.01f, 0.08f, -0.01f, 0.16f, 0.02f, 0.24f)
                    curveToRelative(0.02f, 0.06f, 0.04f, 0.12f, -0.01f, 0.17f)
                }
                path(
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.1f,
                    strokeLineCap = StrokeCap.Round
                ) {
                    moveToRelative(13.84f, 21.64f)
                    lineToRelative(0.13f, 0.36f)
                }
                path(
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.1f,
                    strokeLineCap = StrokeCap.Round
                ) {
                    moveToRelative(13.52f, 21.66f)
                    lineToRelative(0.17f, 0.36f)
                }
                path(
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.1f,
                    strokeLineCap = StrokeCap.Round
                ) {
                    moveToRelative(13.28f, 21.8f)
                    lineToRelative(0.12f, 0.25f)
                }
                path(
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.1f,
                    strokeLineCap = StrokeCap.Round
                ) {
                    moveToRelative(13.06f, 22f)
                    lineToRelative(0.16f, 0.32f)
                }
                path(
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.1f,
                    strokeLineCap = StrokeCap.Round
                ) {
                    moveToRelative(12.86f, 22.28f)
                    lineToRelative(0.24f, 0.35f)
                }
                path(
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.1f,
                    strokeLineCap = StrokeCap.Round
                ) {
                    moveToRelative(12.76f, 22.66f)
                    lineToRelative(0.24f, 0.31f)
                }
                path(
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.1f,
                    strokeLineCap = StrokeCap.Round
                ) {
                    moveToRelative(14.18f, 21.71f)
                    lineToRelative(0.15f, 0.44f)
                }
                path(
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.1f,
                    strokeLineCap = StrokeCap.Round
                ) {
                    moveToRelative(14.55f, 21.92f)
                    lineToRelative(0.1f, 0.37f)
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(16.92f, 21.45f)
                    curveToRelative(0.03f, 0f, 0.06f, 0.01f, 0.07f, 0.03f)
                    curveToRelative(0.01f, 0.01f, 0.01f, 0.03f, 0.02f, 0.04f)
                    curveToRelative(0.01f, 0.02f, 0.01f, 0.05f, 0.01f, 0.08f)
                    reflectiveCurveToRelative(-0.01f, 0.05f, -0.01f, 0.08f)
                    verticalLineToRelative(0.04f)
                    curveToRelative(0f, 0.01f, -0.01f, 0.02f, -0.01f, 0.03f)
                    verticalLineToRelative(0.05f)
                    curveToRelative(0f, 0.02f, 0f, 0.05f, -0.01f, 0.07f)
                    curveToRelative(-0.01f, 0.01f, -0.02f, 0.02f, -0.03f, 0.05f)
                    curveToRelative(-0.01f, 0.01f, -0.02f, 0.01f, -0.03f, 0f)
                    reflectiveCurveToRelative(-0.02f, -0.02f, -0.02f, -0.03f)
                    curveToRelative(-0.02f, -0.04f, -0.02f, -0.08f, -0.02f, -0.12f)
                    curveToRelative(0f, -0.02f, 0f, -0.04f, 0f, -0.07f)
                    curveToRelative(0f, -0.02f, -0.01f, -0.04f, -0.01f, -0.06f)
                    curveToRelative(-0.01f, -0.04f, -0.02f, -0.08f, -0.01f, -0.12f)
                    curveToRelative(0f, -0.02f, 0.02f, -0.07f, 0.05f, -0.07f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(16.71f, 21.51f)
                    curveToRelative(0.01f, 0.01f, 0.02f, 0.02f, 0.02f, 0.03f)
                    curveToRelative(0.01f, 0.03f, 0.01f, 0.07f, 0.01f, 0.11f)
                    curveToRelative(0f, 0.08f, -0.01f, 0.16f, -0.07f, 0.26f)
                    curveToRelative(-0.02f, 0f, -0.03f, -0.01f, -0.04f, -0.02f)
                    reflectiveCurveToRelative(-0.01f, -0.03f, -0.02f, -0.09f)
                    curveToRelative(0.02f, -0.05f, 0f, -0.11f, -0.01f, -0.16f)
                    curveToRelative(-0.01f, -0.04f, -0.01f, -0.1f, 0.03f, -0.13f)
                    curveToRelative(0.03f, -0.02f, 0.06f, -0.02f, 0.08f, 0f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(16.33f, 21.63f)
                    curveToRelative(0.01f, -0.01f, 0.03f, -0.02f, 0.05f, -0.01f)
                    curveToRelative(0.02f, 0f, 0.03f, 0.02f, 0.04f, 0.03f)
                    curveToRelative(0f, 0.01f, 0.01f, 0.02f, 0.01f, 0.04f)
                    verticalLineToRelative(0.07f)
                    verticalLineToRelative(0.05f)
                    curveToRelative(0f, 0.02f, 0.01f, 0.04f, 0f, 0.06f)
                    curveToRelative(0f, 0.01f, -0.01f, 0.03f, -0.02f, 0.04f)
                    curveToRelative(-0.01f, 0.03f, 0f, 0.05f, 0f, 0.08f)
                    curveToRelative(0f, 0.01f, 0f, 0.03f, -0.01f, 0.04f)
                    reflectiveCurveToRelative(-0.02f, 0.02f, -0.04f, 0.02f)
                    curveToRelative(-0.02f, -0.01f, -0.02f, -0.03f, -0.03f, -0.05f)
                    curveToRelative(-0.01f, -0.07f, 0.01f, -0.13f, -0.01f, -0.19f)
                    curveToRelative(-0.01f, -0.03f, -0.05f, -0.07f, -0.03f, -0.1f)
                    curveToRelative(0f, -0.01f, 0.01f, -0.02f, 0.02f, -0.03f)
                    curveToRelative(0.01f, -0.02f, 0.01f, -0.04f, 0.02f, -0.05f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(16.11f, 22.04f)
                    curveToRelative(0.02f, 0.09f, 0.04f, 0.19f, 0.05f, 0.3f)
                    curveToRelative(0f, 0.01f, -0.01f, 0.02f, -0.06f, 0.04f)
                    curveToRelative(-0.01f, -0.01f, -0.03f, -0.01f, -0.03f, -0.03f)
                    curveToRelative(-0.01f, -0.02f, 0f, -0.04f, -0.01f, -0.06f)
                    curveToRelative(0f, -0.02f, -0.01f, -0.03f, -0.02f, -0.04f)
                    curveToRelative(-0.02f, -0.05f, -0.04f, -0.11f, -0.04f, -0.16f)
                    curveToRelative(0f, -0.04f, -0.03f, -0.11f, 0.01f, -0.13f)
                    curveToRelative(0.06f, -0.04f, 0.09f, 0.04f, 0.1f, 0.08f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(15.87f, 22.63f)
                    curveToRelative(0.01f, 0.05f, 0.04f, 0.1f, 0.08f, 0.12f)
                    curveToRelative(0.04f, -0.03f, 0.04f, -0.09f, 0.04f, -0.13f)
                    curveToRelative(0f, -0.05f, 0f, -0.1f, -0.02f, -0.15f)
                    curveToRelative(-0.01f, -0.03f, -0.03f, -0.05f, -0.04f, -0.08f)
                    curveToRelative(0f, -0.01f, -0.01f, -0.02f, -0.01f, -0.04f)
                    curveToRelative(-0.04f, -0.06f, -0.12f, 0f, -0.1f, 0.05f)
                    curveToRelative(0.01f, 0.03f, 0.03f, 0.06f, 0.04f, 0.1f)
                    curveToRelative(0f, 0.05f, 0f, 0.09f, 0.01f, 0.13f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(13.62f, 22.26f)
                    reflectiveCurveToRelative(0.25f, 0.53f, 0.13f, 0.67f)
                    curveToRelative(-0.08f, 0.07f, -0.2f, -0.06f, -0.2f, -0.06f)
                    reflectiveCurveToRelative(-0.17f, -0.2f, -0.27f, -0.12f)
                    curveToRelative(0f, 0f, 0.67f, 0.65f, 0.92f, 1.33f)
                    curveToRelative(0f, 0f, 0.09f, 0.22f, 0.11f, 0.05f)
                    reflectiveCurveToRelative(-0.18f, -0.66f, 0.12f, -1.35f)
                    curveToRelative(0.24f, -0.55f, -0.09f, -0.08f, -0.22f, 0.13f)
                    curveToRelative(-0.03f, 0.04f, -0.08f, 0.04f, -0.1f, 0f)
                    curveToRelative(-0.11f, -0.22f, -0.38f, -0.7f, -0.49f, -0.65f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(22.49f, 25.64f)
                    curveToRelative(-0.22f, -0.31f, -2.16f, -0.56f, -5.34f, -0.56f)
                    horizontalLineToRelative(-0.06f)
                    horizontalLineToRelative(-0.06f)
                    curveToRelative(-3.19f, 0f, -5.13f, 0.25f, -5.34f, 0.56f)
                    curveToRelative(-0.04f, 0.06f, -0.04f, 0.12f, 0f, 0.18f)
                    curveToRelative(0.22f, 0.33f, 1.63f, 0.58f, 3.16f, 0.72f)
                    curveToRelative(0.74f, 0.06f, 1.51f, 0.1f, 2.18f, 0.1f)
                    horizontalLineToRelative(0.06f)
                    horizontalLineToRelative(0.06f)
                    curveToRelative(0.68f, 0f, 1.44f, -0.04f, 2.18f, -0.1f)
                    curveToRelative(1.53f, -0.14f, 2.94f, -0.39f, 3.16f, -0.72f)
                    curveToRelative(0.04f, -0.06f, 0.04f, -0.12f, 0f, -0.18f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(16.13f, 25.24f)
                    curveToRelative(0.04f, 0f, 0.06f, -0.03f, 0.06f, -0.06f)
                    lineToRelative(-0.02f, -0.4f)
                    curveToRelative(0f, -0.03f, -0.02f, -0.06f, -0.06f, -0.06f)
                    curveToRelative(-0.03f, 0f, -0.06f, 0.03f, -0.06f, 0.06f)
                    lineToRelative(0.02f, 0.4f)
                    curveToRelative(0f, 0.04f, 0.03f, 0.06f, 0.06f, 0.06f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(16.56f, 25.26f)
                    curveToRelative(0.04f, 0f, 0.06f, -0.03f, 0.06f, -0.06f)
                    lineToRelative(-0.02f, -0.44f)
                    curveToRelative(0f, -0.03f, -0.02f, -0.06f, -0.06f, -0.06f)
                    curveToRelative(-0.03f, 0f, -0.06f, 0.03f, -0.06f, 0.06f)
                    lineToRelative(0.02f, 0.44f)
                    curveToRelative(0f, 0.03f, 0.03f, 0.06f, 0.06f, 0.06f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(15.75f, 25.36f)
                    curveToRelative(0.04f, 0f, 0.06f, -0.03f, 0.06f, -0.06f)
                    lineToRelative(-0.02f, -0.5f)
                    curveToRelative(0f, -0.03f, -0.03f, -0.06f, -0.06f, -0.06f)
                    curveToRelative(-0.03f, 0f, -0.06f, 0.03f, -0.06f, 0.06f)
                    lineToRelative(0.02f, 0.5f)
                    curveToRelative(0f, 0.03f, 0.03f, 0.06f, 0.06f, 0.06f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(15.41f, 25.26f)
                    curveToRelative(0.04f, 0f, 0.06f, -0.03f, 0.06f, -0.06f)
                    lineToRelative(-0.01f, -0.39f)
                    curveToRelative(0f, -0.03f, -0.03f, -0.06f, -0.06f, -0.06f)
                    curveToRelative(-0.03f, 0f, -0.06f, 0.03f, -0.06f, 0.06f)
                    lineToRelative(0.01f, 0.39f)
                    curveToRelative(0f, 0.03f, 0.03f, 0.06f, 0.06f, 0.06f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(15.06f, 25.35f)
                    curveToRelative(0.04f, 0f, 0.06f, -0.03f, 0.06f, -0.06f)
                    lineToRelative(-0.04f, -0.47f)
                    curveToRelative(0f, -0.03f, -0.03f, -0.06f, -0.06f, -0.06f)
                    reflectiveCurveToRelative(-0.06f, 0.03f, -0.05f, 0.06f)
                    lineToRelative(0.03f, 0.48f)
                    curveToRelative(0f, 0.03f, 0.03f, 0.05f, 0.06f, 0.05f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(14.74f, 25.46f)
                    curveToRelative(0.04f, -0.01f, 0.06f, -0.04f, 0.06f, -0.07f)
                    lineToRelative(-0.07f, -0.54f)
                    curveToRelative(0f, -0.03f, -0.04f, -0.06f, -0.07f, -0.05f)
                    curveToRelative(-0.03f, 0f, -0.06f, 0.03f, -0.05f, 0.07f)
                    lineToRelative(0.07f, 0.54f)
                    curveToRelative(0f, 0.02f, 0.03f, 0.05f, 0.06f, 0.05f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(14.38f, 25.43f)
                    curveToRelative(0.01f, 0f, 0.01f, 0f, 0f, 0f)
                    curveToRelative(0.04f, -0.01f, 0.06f, -0.04f, 0.06f, -0.07f)
                    lineToRelative(-0.07f, -0.47f)
                    curveToRelative(0f, -0.03f, -0.04f, -0.06f, -0.07f, -0.05f)
                    curveToRelative(-0.03f, 0f, -0.06f, 0.03f, -0.05f, 0.07f)
                    lineToRelative(0.07f, 0.47f)
                    curveToRelative(0.01f, 0.03f, 0.03f, 0.05f, 0.06f, 0.05f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(14.03f, 25.45f)
                    curveToRelative(0.04f, 0f, 0.06f, -0.03f, 0.06f, -0.07f)
                    lineToRelative(-0.07f, -0.51f)
                    curveToRelative(0f, -0.03f, -0.03f, -0.06f, -0.07f, -0.05f)
                    curveToRelative(-0.03f, 0f, -0.06f, 0.03f, -0.05f, 0.07f)
                    lineToRelative(0.07f, 0.51f)
                    curveToRelative(0f, 0.03f, 0.03f, 0.05f, 0.06f, 0.05f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(13.63f, 25.44f)
                    curveToRelative(0.04f, -0.01f, 0.06f, -0.04f, 0.06f, -0.07f)
                    lineToRelative(-0.06f, -0.37f)
                    curveToRelative(0f, -0.03f, -0.03f, -0.06f, -0.07f, -0.05f)
                    curveToRelative(-0.03f, 0f, -0.06f, 0.03f, -0.05f, 0.07f)
                    lineToRelative(0.05f, 0.38f)
                    curveToRelative(0.01f, 0.02f, 0.04f, 0.04f, 0.07f, 0.04f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(13.25f, 25.52f)
                    curveToRelative(0.01f, 0f, 0.01f, 0f, 0f, 0f)
                    curveToRelative(0.04f, -0.01f, 0.06f, -0.04f, 0.06f, -0.07f)
                    lineToRelative(-0.08f, -0.46f)
                    curveToRelative(0f, -0.03f, -0.03f, -0.06f, -0.07f, -0.05f)
                    curveToRelative(-0.03f, 0.01f, -0.05f, 0.04f, -0.05f, 0.07f)
                    lineToRelative(0.08f, 0.46f)
                    curveToRelative(0.01f, 0.03f, 0.04f, 0.05f, 0.06f, 0.05f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(12.91f, 25.55f)
                    horizontalLineToRelative(0.01f)
                    curveToRelative(0.03f, -0.01f, 0.05f, -0.04f, 0.04f, -0.07f)
                    lineToRelative(-0.1f, -0.43f)
                    curveToRelative(-0.01f, -0.03f, -0.04f, -0.05f, -0.07f, -0.04f)
                    reflectiveCurveToRelative(-0.05f, 0.04f, -0.04f, 0.07f)
                    lineToRelative(0.1f, 0.43f)
                    curveToRelative(0f, 0.02f, 0.03f, 0.04f, 0.06f, 0.04f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(12.59f, 25.54f)
                    horizontalLineToRelative(0.01f)
                    curveToRelative(0.03f, -0.01f, 0.05f, -0.04f, 0.05f, -0.07f)
                    lineToRelative(-0.06f, -0.32f)
                    curveToRelative(-0.01f, -0.03f, -0.04f, -0.05f, -0.07f, -0.05f)
                    curveToRelative(-0.03f, 0.01f, -0.05f, 0.04f, -0.05f, 0.07f)
                    lineToRelative(0.06f, 0.32f)
                    curveToRelative(0.01f, 0.03f, 0.03f, 0.05f, 0.06f, 0.05f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(12.35f, 25.63f)
                    horizontalLineToRelative(0.02f)
                    curveToRelative(0.03f, -0.01f, 0.05f, -0.04f, 0.04f, -0.08f)
                    lineToRelative(-0.12f, -0.37f)
                    curveToRelative(-0.01f, -0.03f, -0.04f, -0.05f, -0.08f, -0.04f)
                    curveToRelative(-0.03f, 0.01f, -0.05f, 0.04f, -0.04f, 0.08f)
                    lineToRelative(0.12f, 0.37f)
                    curveToRelative(0.01f, 0.02f, 0.03f, 0.04f, 0.06f, 0.04f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(12.06f, 25.8f)
                    horizontalLineToRelative(0.02f)
                    curveToRelative(0.03f, -0.01f, 0.05f, -0.04f, 0.04f, -0.07f)
                    lineToRelative(-0.13f, -0.46f)
                    curveToRelative(-0.01f, -0.03f, -0.04f, -0.05f, -0.07f, -0.04f)
                    reflectiveCurveToRelative(-0.05f, 0.04f, -0.04f, 0.07f)
                    lineToRelative(0.13f, 0.46f)
                    curveToRelative(0.01f, 0.02f, 0.03f, 0.04f, 0.05f, 0.04f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(17.08f, 25.33f)
                    curveToRelative(0.03f, 0f, 0.06f, -0.03f, 0.06f, -0.06f)
                    verticalLineToRelative(-0.51f)
                    curveToRelative(0f, -0.03f, -0.03f, -0.06f, -0.06f, -0.06f)
                    reflectiveCurveToRelative(-0.06f, 0.03f, -0.06f, 0.06f)
                    verticalLineToRelative(0.51f)
                    curveToRelative(0f, 0.03f, 0.03f, 0.06f, 0.06f, 0.06f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFFFD83D))) {
                    moveToRelative(17.4f, 20.74f)
                    curveToRelative(0f, 0.17f, -0.14f, 0.31f, -0.31f, 0.31f)
                    reflectiveCurveToRelative(-0.31f, -0.14f, -0.31f, -0.31f)
                    reflectiveCurveToRelative(0.14f, -0.31f, 0.31f, -0.31f)
                    curveToRelative(0.18f, 0f, 0.31f, 0.14f, 0.31f, 0.31f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFFFD83D))) {
                    moveToRelative(17.37f, 20.03f)
                    curveToRelative(0f, 0.15f, -0.12f, 0.27f, -0.27f, 0.27f)
                    reflectiveCurveToRelative(-0.27f, -0.12f, -0.27f, -0.27f)
                    reflectiveCurveToRelative(0.12f, -0.27f, 0.27f, -0.27f)
                    reflectiveCurveToRelative(0.27f, 0.12f, 0.27f, 0.27f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFFFD83D))) {
                    moveToRelative(17.35f, 19.38f)
                    curveToRelative(0f, 0.14f, -0.11f, 0.25f, -0.25f, 0.25f)
                    reflectiveCurveToRelative(-0.25f, -0.11f, -0.25f, -0.25f)
                    reflectiveCurveToRelative(0.11f, -0.25f, 0.25f, -0.25f)
                    reflectiveCurveToRelative(0.25f, 0.11f, 0.25f, 0.25f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFFFD83D))) {
                    moveToRelative(17.33f, 18.79f)
                    curveToRelative(0f, 0.13f, -0.1f, 0.23f, -0.23f, 0.23f)
                    reflectiveCurveToRelative(-0.23f, -0.1f, -0.23f, -0.23f)
                    reflectiveCurveToRelative(0.1f, -0.23f, 0.23f, -0.23f)
                    curveToRelative(0.12f, 0f, 0.23f, 0.1f, 0.23f, 0.23f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFFFFD83D)),
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.11f
                ) {
                    moveToRelative(17.33f, 18.25f)
                    curveToRelative(0f, 0.13f, -0.1f, 0.23f, -0.23f, 0.23f)
                    reflectiveCurveToRelative(-0.23f, -0.1f, -0.23f, -0.23f)
                    reflectiveCurveToRelative(0.1f, -0.23f, 0.23f, -0.23f)
                    curveToRelative(0.12f, -0.01f, 0.23f, 0.1f, 0.23f, 0.23f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF000000))) {
                    moveToRelative(17.82f, 22.46f)
                    reflectiveCurveToRelative(-0.16f, 0.11f, -0.41f, 0.09f)
                    curveToRelative(-0.19f, -0.02f, -0.28f, -0.3f, -0.31f, -0.46f)
                    curveToRelative(0f, -0.02f, -0.03f, -0.02f, -0.04f, 0f)
                    curveToRelative(-0.03f, 0.16f, -0.12f, 0.44f, -0.31f, 0.46f)
                    curveToRelative(-0.25f, 0.02f, -0.41f, -0.09f, -0.41f, -0.09f)
                    reflectiveCurveToRelative(-0.13f, -0.09f, -0.08f, 0.06f)
                    curveToRelative(0.04f, 0.15f, 0.75f, 0.94f, 0.75f, 2f)
                    curveToRelative(0f, 0f, -0.02f, 0.09f, 0.07f, 0.12f)
                    curveToRelative(0.09f, -0.03f, 0.07f, -0.12f, 0.07f, -0.12f)
                    curveToRelative(0.01f, -1.06f, 0.71f, -1.85f, 0.75f, -2f)
                    reflectiveCurveToRelative(-0.08f, -0.06f, -0.08f, -0.06f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFFFFD83D)),
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.12f
                ) {
                    moveToRelative(17.85f, 17.2f)
                    verticalLineToRelative(0.01f)
                    lineToRelative(-0.07f, 0.3f)
                    curveToRelative(-0.11f, 0.26f, -0.37f, 0.45f, -0.67f, 0.45f)
                    curveToRelative(-0.37f, 0f, -0.69f, -0.28f, -0.74f, -0.64f)
                    curveToRelative(-0.01f, -0.04f, -0.01f, -0.07f, -0.01f, -0.11f)
                    curveToRelative(0f, -0.08f, 0.01f, -0.15f, 0.04f, -0.22f)
                    curveToRelative(0.06f, -0.22f, 0.24f, -0.4f, 0.46f, -0.47f)
                    curveToRelative(0.08f, -0.03f, 0.16f, -0.05f, 0.25f, -0.05f)
                    reflectiveCurveToRelative(0.17f, 0.02f, 0.25f, 0.05f)
                    curveToRelative(0.22f, 0.07f, 0.4f, 0.26f, 0.46f, 0.49f)
                    curveToRelative(0.02f, 0.06f, 0.03f, 0.12f, 0.03f, 0.19f)
                    close()
                }
                path(
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.12f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round
                ) {
                    moveToRelative(17.85f, 17.2f)
                    verticalLineToRelative(0.01f)
                    lineToRelative(-0.07f, 0.3f)
                    curveToRelative(-0.8f, -0.18f, -1.34f, -0.01f, -1.34f, -0.01f)
                    lineToRelative(-0.07f, -0.18f)
                    curveToRelative(-0.01f, -0.04f, -0.01f, -0.07f, -0.01f, -0.11f)
                    curveToRelative(0f, -0.08f, 0.01f, -0.15f, 0.04f, -0.22f)
                    curveToRelative(0.1f, -0.02f, 0.26f, -0.05f, 0.46f, -0.06f)
                    verticalLineToRelative(-0.46f)
                    horizontalLineToRelative(0.5f)
                    verticalLineToRelative(0.47f)
                    curveToRelative(0.14f, 0.01f, 0.3f, 0.03f, 0.46f, 0.07f)
                    curveToRelative(0.02f, 0.06f, 0.03f, 0.12f, 0.03f, 0.19f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFFFFD83D)),
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.12f,
                    strokeLineJoin = StrokeJoin.Round
                ) {
                    moveToRelative(17.09f, 13.86f)
                    lineToRelative(-0.35f, 0.64f)
                    lineToRelative(0.35f, 0.64f)
                    lineToRelative(0.35f, -0.64f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFFFFD83D)),
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.12f,
                    strokeLineJoin = StrokeJoin.Round
                ) {
                    moveToRelative(17.09f, 15.64f)
                    lineToRelative(-0.35f, 0.64f)
                    lineToRelative(0.35f, 0.64f)
                    lineToRelative(0.35f, -0.64f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFFFFD83D)),
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.12f,
                    strokeLineJoin = StrokeJoin.Round
                ) {
                    moveToRelative(18.61f, 15.39f)
                    lineToRelative(-0.64f, -0.35f)
                    lineToRelative(-0.64f, 0.35f)
                    lineToRelative(0.64f, 0.35f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFFFFD83D)),
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.12f,
                    strokeLineJoin = StrokeJoin.Round
                ) {
                    moveToRelative(16.84f, 15.39f)
                    lineToRelative(-0.64f, -0.35f)
                    lineToRelative(-0.64f, 0.35f)
                    lineToRelative(0.64f, 0.35f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFFFFD83D)),
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 0.12f,
                    strokeLineJoin = StrokeJoin.Round
                ) {
                    moveToRelative(17.34f, 15.4f)
                    curveToRelative(0f, 0.14f, -0.11f, 0.26f, -0.26f, 0.26f)
                    curveToRelative(-0.14f, 0f, -0.26f, -0.11f, -0.26f, -0.26f)
                    reflectiveCurveToRelative(0.11f, -0.26f, 0.26f, -0.26f)
                    curveToRelative(0.14f, 0.01f, 0.26f, 0.12f, 0.26f, 0.26f)
                    close()
                }
            }
        }.build()

        return _Liechtenstein!!
    }

@Suppress("ObjectPropertyName")
private var _Liechtenstein: ImageVector? = null
