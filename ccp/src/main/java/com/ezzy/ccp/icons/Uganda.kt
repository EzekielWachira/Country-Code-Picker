package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import kotlin.Suppress

val EzzyIcons.Uganda: ImageVector
    get() {
        if (_Uganda != null) {
            return _Uganda!!
        }
        _Uganda = ImageVector.Builder(
            name = "Uganda",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(0f, 85.34f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(341.33f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFDA44))) {
                moveTo(0f, 142.23f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(56.89f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFFD80027))) {
                moveTo(0f, 199.11f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(56.89f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFDA44))) {
                moveTo(0f, 312.89f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(56.89f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFFD80027))) {
                moveTo(0f, 369.77f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(56.89f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFFF0F0F0))) {
                moveTo(256f, 256f)
                moveToRelative(-56.95f, 0f)
                arcToRelative(56.95f, 56.95f, 0f, isMoreThanHalf = true, isPositiveArc = true, 113.91f, 0f)
                arcToRelative(56.95f, 56.95f, 0f, isMoreThanHalf = true, isPositiveArc = true, -113.91f, 0f)
            }
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(276.86f, 258.9f)
                lineTo(256f, 249.86f)
                curveToRelative(0f, 0f, 4.88f, -16.2f, 5.14f, -17.31f)
                curveToRelative(0.19f, -0.81f, 0.29f, -1.65f, 0.29f, -2.52f)
                curveToRelative(0f, -3.07f, -1.25f, -5.86f, -3.26f, -7.87f)
                lineToRelative(5.25f, -5.25f)
                curveToRelative(-3.36f, -3.36f, -7.99f, -5.43f, -13.12f, -5.43f)
                curveToRelative(-5.12f, 0f, -9.76f, 2.08f, -13.12f, 5.43f)
                lineToRelative(5.25f, 5.25f)
                curveToRelative(-2.02f, 2.01f, -3.26f, 4.8f, -3.26f, 7.87f)
                curveToRelative(0f, 1.12f, 0.17f, 2.21f, 0.48f, 3.23f)
                lineToRelative(-7.9f, 7.9f)
                horizontalLineToRelative(14.36f)
                curveToRelative(0f, 0f, -5.94f, 8.91f, -9.18f, 16.24f)
                curveToRelative(-3.24f, 7.32f, 0.05f, 16.26f, 7.53f, 19.59f)
                lineToRelative(4.33f, 1.92f)
                lineToRelative(7.2f, 6.77f)
                verticalLineToRelative(7.42f)
                lineToRelative(-7.42f, 7.42f)
                horizontalLineToRelative(14.84f)
                verticalLineToRelative(-14.84f)
                lineToRelative(6.77f, -6.77f)
                horizontalLineToRelative(14f)
                curveToRelative(0.07f, -0.14f, 0.14f, -0.28f, 0.2f, -0.42f)
                curveTo(287.73f, 271f, 284.35f, 262.23f, 276.86f, 258.9f)
                close()
            }
        }.build()

        return _Uganda!!
    }

@Suppress("ObjectPropertyName")
private var _Uganda: ImageVector? = null
