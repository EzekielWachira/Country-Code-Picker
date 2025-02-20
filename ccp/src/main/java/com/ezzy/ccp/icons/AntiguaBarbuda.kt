package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EzzyIcons.AntiguaBarbuda: ImageVector
    get() {
        if (_AntiguaBarbuda != null) {
            return _AntiguaBarbuda!!
        }
        _AntiguaBarbuda = ImageVector.Builder(
            name = "AntiguaBarbuda",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f
        ).apply {
            path(fill = SolidColor(Color(0xFF464655))) {
                moveTo(503.17f, 88.28f)
                horizontalLineTo(8.83f)
                curveTo(3.95f, 88.28f, 0f, 92.23f, 0f, 97.1f)
                verticalLineToRelative(317.79f)
                curveToRelative(0f, 4.88f, 3.95f, 8.83f, 8.83f, 8.83f)
                horizontalLineToRelative(494.35f)
                curveToRelative(4.88f, 0f, 8.83f, -3.95f, 8.83f, -8.83f)
                verticalLineTo(97.1f)
                curveTo(512f, 92.23f, 508.05f, 88.28f, 503.17f, 88.28f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFE15A))) {
                moveTo(256f, 105.93f)
                lineToRelative(11.2f, 58.48f)
                lineToRelative(32.72f, -49.75f)
                lineToRelative(-12.04f, 58.31f)
                lineToRelative(49.27f, -33.44f)
                lineToRelative(-33.44f, 49.27f)
                lineToRelative(58.31f, -12.04f)
                lineToRelative(-49.75f, 32.72f)
                lineToRelative(58.48f, 11.19f)
                lineToRelative(-58.48f, 11.19f)
                lineToRelative(49.75f, 32.72f)
                lineToRelative(-58.31f, -12.04f)
                lineToRelative(33.44f, 49.27f)
                lineToRelative(-49.27f, -33.44f)
                lineToRelative(12.04f, 58.32f)
                lineToRelative(-32.72f, -49.75f)
                lineToRelative(-11.2f, 58.48f)
                lineToRelative(-11.19f, -58.48f)
                lineToRelative(-32.72f, 49.75f)
                lineToRelative(12.04f, -58.32f)
                lineToRelative(-49.27f, 33.44f)
                lineToRelative(33.44f, -49.27f)
                lineToRelative(-58.31f, 12.04f)
                lineToRelative(49.75f, -32.72f)
                lineToRelative(-58.48f, -11.19f)
                lineToRelative(58.48f, -11.19f)
                lineToRelative(-49.75f, -32.72f)
                lineToRelative(58.31f, 12.04f)
                lineToRelative(-33.44f, -49.27f)
                lineToRelative(49.27f, 33.44f)
                lineToRelative(-12.04f, -58.31f)
                lineToRelative(32.72f, 49.75f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFF4B55))) {
                moveTo(0f, 97.1f)
                verticalLineToRelative(317.79f)
                curveToRelative(0f, 4.88f, 3.95f, 8.83f, 8.83f, 8.83f)
                horizontalLineTo(256f)
                lineTo(2.3f, 91.29f)
                curveTo(0.91f, 92.85f, 0f, 94.85f, 0f, 97.1f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFF4B55))) {
                moveTo(256f, 423.72f)
                horizontalLineToRelative(247.17f)
                curveToRelative(4.88f, 0f, 8.83f, -3.95f, 8.83f, -8.83f)
                verticalLineTo(97.1f)
                curveToRelative(0f, -2.25f, -0.91f, -4.26f, -2.3f, -5.82f)
                lineTo(256f, 423.72f)
                close()
            }
            path(fill = SolidColor(Color(0xFF4173CD))) {
                moveTo(154.95f, 291.31f)
                lineToRelative(202.1f, 0f)
                lineToRelative(53.89f, -70.62f)
                lineToRelative(-309.89f, 0f)
                close()
            }
            path(fill = SolidColor(Color(0xFFF5F5F5))) {
                moveTo(154.95f, 291.31f)
                lineToRelative(101.05f, 132.41f)
                lineToRelative(101.05f, -132.41f)
                close()
            }
        }.build()

        return _AntiguaBarbuda!!
    }

@Suppress("ObjectPropertyName")
private var _AntiguaBarbuda: ImageVector? = null
