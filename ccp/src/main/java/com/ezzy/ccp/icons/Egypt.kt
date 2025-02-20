package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EzzyIcons.Egypt: ImageVector
    get() {
        if (_Egypt != null) {
            return _Egypt!!
        }
        _Egypt = ImageVector.Builder(
            name = "Egypt",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f
        ).apply {
            path(fill = SolidColor(Color(0xFFFF4B55))) {
                moveTo(512f, 200.09f)
                horizontalLineTo(0f)
                verticalLineTo(97.1f)
                curveToRelative(0f, -4.88f, 3.95f, -8.83f, 8.83f, -8.83f)
                horizontalLineToRelative(494.35f)
                curveToRelative(4.88f, 0f, 8.83f, 3.95f, 8.83f, 8.83f)
                lineTo(512f, 200.09f)
                lineTo(512f, 200.09f)
                close()
            }
            path(fill = SolidColor(Color(0xFF464655))) {
                moveTo(503.17f, 423.73f)
                horizontalLineTo(8.83f)
                curveToRelative(-4.88f, 0f, -8.83f, -3.95f, -8.83f, -8.83f)
                verticalLineTo(311.91f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(102.99f)
                curveTo(512f, 419.77f, 508.05f, 423.73f, 503.17f, 423.73f)
                close()
            }
            path(fill = SolidColor(Color(0xFFF5F5F5))) {
                moveTo(0f, 200.09f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(111.81f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFFF0C727))) {
                moveTo(284.19f, 238.57f)
                curveToRelative(0f, -4.38f, -4.29f, -7.47f, -8.44f, -6.08f)
                lineToRelative(-7.8f, 2.6f)
                lineToRelative(-1.89f, -11.44f)
                curveToRelative(-1.13f, -6.83f, -6.98f, -11.78f, -13.91f, -11.78f)
                horizontalLineToRelative(-7.3f)
                lineToRelative(-7.63f, 9.4f)
                horizontalLineToRelative(10.11f)
                lineToRelative(-2.83f, 13.97f)
                lineToRelative(-8.23f, -2.74f)
                curveToRelative(-4.15f, -1.38f, -8.44f, 1.71f, -8.44f, 6.08f)
                verticalLineToRelative(47.47f)
                lineToRelative(11.36f, -11.36f)
                lineToRelative(-5.35f, 16.06f)
                horizontalLineToRelative(-6.01f)
                verticalLineToRelative(9.4f)
                horizontalLineToRelative(56.37f)
                verticalLineToRelative(-9.4f)
                horizontalLineToRelative(-6.01f)
                lineToRelative(-5.35f, -16.06f)
                lineToRelative(11.36f, 11.36f)
                verticalLineToRelative(-47.48f)
                horizontalLineTo(284.19f)
                close()
                moveTo(251.3f, 290.74f)
                horizontalLineToRelative(-7.57f)
                lineToRelative(5.21f, -13.91f)
                lineToRelative(2.37f, 3.02f)
                lineTo(251.3f, 290.74f)
                lineTo(251.3f, 290.74f)
                close()
                moveTo(260.7f, 290.74f)
                verticalLineToRelative(-10.89f)
                lineToRelative(2.37f, -3.02f)
                lineToRelative(5.21f, 13.91f)
                horizontalLineTo(260.7f)
                close()
                moveTo(256f, 273.71f)
                curveToRelative(0f, 0f, -14.53f, -9.84f, -13.95f, -28.19f)
                curveToRelative(0f, 0f, 8.96f, -1.47f, 13.95f, -9.4f)
                curveToRelative(4.99f, 7.93f, 13.95f, 9.4f, 13.95f, 9.4f)
                curveTo(270.53f, 263.88f, 256f, 273.71f, 256f, 273.71f)
                close()
            }
        }.build()

        return _Egypt!!
    }

@Suppress("ObjectPropertyName")
private var _Egypt: ImageVector? = null
