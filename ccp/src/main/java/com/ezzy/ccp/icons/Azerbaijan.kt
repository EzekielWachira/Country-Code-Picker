package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EzzyIcons.Azerbaijan: ImageVector
    get() {
        if (_Azerbaijan != null) {
            return _Azerbaijan!!
        }
        _Azerbaijan = ImageVector.Builder(
            name = "Azerbaijan",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f
        ).apply {
            path(fill = SolidColor(Color(0xFFD80027))) {
                moveTo(0f, 85.34f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(341.33f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFF338AF3))) {
                moveTo(0f, 85.34f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(113.78f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFF6DA544))) {
                moveTo(0f, 312.89f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(113.78f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFFF0F0F0))) {
                moveTo(259.2f, 297.6f)
                curveToRelative(-22.98f, 0f, -41.6f, -18.63f, -41.6f, -41.6f)
                reflectiveCurveToRelative(18.63f, -41.6f, 41.6f, -41.6f)
                curveToRelative(7.16f, 0f, 13.9f, 1.81f, 19.79f, 5f)
                curveToRelative(-9.23f, -9.03f, -21.86f, -14.6f, -35.79f, -14.6f)
                curveToRelative(-28.28f, 0f, -51.2f, 22.92f, -51.2f, 51.2f)
                reflectiveCurveToRelative(22.92f, 51.2f, 51.2f, 51.2f)
                curveToRelative(13.93f, 0f, 26.56f, -5.57f, 35.79f, -14.6f)
                curveTo(273.1f, 295.79f, 266.36f, 297.6f, 259.2f, 297.6f)
                close()
            }
            path(fill = SolidColor(Color(0xFFF0F0F0))) {
                moveTo(291.2f, 227.2f)
                lineToRelative(5.51f, 15.5f)
                lineToRelative(14.86f, -7.06f)
                lineToRelative(-7.06f, 14.85f)
                lineToRelative(15.5f, 5.51f)
                lineToRelative(-15.5f, 5.51f)
                lineToRelative(7.06f, 14.85f)
                lineToRelative(-14.86f, -7.06f)
                lineToRelative(-5.51f, 15.5f)
                lineToRelative(-5.51f, -15.5f)
                lineToRelative(-14.86f, 7.06f)
                lineToRelative(7.06f, -14.85f)
                lineToRelative(-15.5f, -5.51f)
                lineToRelative(15.5f, -5.51f)
                lineToRelative(-7.06f, -14.85f)
                lineToRelative(14.86f, 7.06f)
                close()
            }
        }.build()

        return _Azerbaijan!!
    }

@Suppress("ObjectPropertyName")
private var _Azerbaijan: ImageVector? = null
