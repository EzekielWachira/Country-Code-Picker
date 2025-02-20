package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import kotlin.Suppress

val EzzyIcons.Algeria: ImageVector
    get() {
        if (_Algeria != null) {
            return _Algeria!!
        }
        _Algeria = ImageVector.Builder(
            name = "Algeria",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f
        ).apply {
            path(fill = SolidColor(Color(0xFF496E2D))) {
                moveTo(0f, 85.33f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(341.33f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFFF0F0F0))) {
                moveTo(256f, 85.33f)
                horizontalLineToRelative(256f)
                verticalLineToRelative(341.34f)
                horizontalLineToRelative(-256f)
                close()
            }
            path(fill = SolidColor(Color(0xFFD80027))) {
                moveTo(292.67f, 223.26f)
                lineToRelative(-14f, 19.29f)
                lineToRelative(-22.68f, -7.35f)
                lineToRelative(14.02f, 19.28f)
                lineToRelative(-14f, 19.3f)
                lineToRelative(22.67f, -7.38f)
                lineToRelative(14.02f, 19.28f)
                lineToRelative(-0.01f, -23.84f)
                lineToRelative(22.67f, -7.38f)
                lineToRelative(-22.68f, -7.35f)
                close()
            }
            path(fill = SolidColor(Color(0xFFD80027))) {
                moveTo(270.16f, 304.23f)
                curveToRelative(-26.64f, 0f, -48.23f, -21.59f, -48.23f, -48.23f)
                reflectiveCurveToRelative(21.59f, -48.23f, 48.23f, -48.23f)
                curveToRelative(8.31f, 0f, 16.12f, 2.1f, 22.94f, 5.8f)
                curveToRelative(-10.7f, -10.47f, -25.34f, -16.93f, -41.49f, -16.93f)
                curveToRelative(-32.78f, 0f, -59.36f, 26.58f, -59.36f, 59.36f)
                reflectiveCurveToRelative(26.58f, 59.36f, 59.36f, 59.36f)
                curveToRelative(16.15f, 0f, 30.79f, -6.46f, 41.49f, -16.93f)
                curveTo(286.28f, 302.13f, 278.46f, 304.23f, 270.16f, 304.23f)
                close()
            }
        }.build()

        return _Algeria!!
    }

@Suppress("ObjectPropertyName")
private var _Algeria: ImageVector? = null
