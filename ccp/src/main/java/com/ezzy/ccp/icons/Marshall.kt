package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EzzyIcons.Marshall: ImageVector
    get() {
        if (_Marshall != null) {
            return _Marshall!!
        }
        _Marshall = ImageVector.Builder(
            name = "Marshall",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f
        ).apply {
            path(fill = SolidColor(Color(0xFF41479B))) {
                moveTo(503.17f, 423.73f)
                horizontalLineTo(8.83f)
                curveToRelative(-4.88f, 0f, -8.83f, -3.95f, -8.83f, -8.83f)
                verticalLineTo(97.1f)
                curveToRelative(0f, -4.88f, 3.95f, -8.83f, 8.83f, -8.83f)
                horizontalLineToRelative(494.35f)
                curveToRelative(4.88f, 0f, 8.83f, 3.95f, 8.83f, 8.83f)
                verticalLineToRelative(317.79f)
                curveTo(512f, 419.77f, 508.05f, 423.73f, 503.17f, 423.73f)
                close()
            }
            path(fill = SolidColor(Color(0xFFF5F5F5))) {
                moveTo(3.98f, 422.08f)
                lineTo(512f, 211.86f)
                verticalLineTo(150.07f)
                lineTo(0f, 414.9f)
                curveTo(0f, 417.94f, 1.63f, 420.49f, 3.98f, 422.08f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFF9B55))) {
                moveTo(508.66f, 90.35f)
                lineTo(0f, 406.07f)
                verticalLineToRelative(8.83f)
                lineTo(512f, 150.07f)
                verticalLineTo(97.1f)
                curveTo(512f, 94.34f, 510.65f, 91.97f, 508.66f, 90.35f)
                close()
            }
            path(fill = SolidColor(Color(0xFFF5F5F5))) {
                moveTo(145.9f, 210.17f)
                lineToRelative(57.13f, -7.13f)
                lineToRelative(-57.13f, -7.13f)
                lineToRelative(28.96f, -17.76f)
                lineToRelative(-36.03f, 8.81f)
                lineToRelative(21.93f, -29.91f)
                lineToRelative(-29.91f, 21.93f)
                lineToRelative(8.81f, -36.03f)
                lineToRelative(-17.76f, 28.96f)
                lineToRelative(-7.13f, -57.13f)
                lineToRelative(-7.14f, 57.13f)
                lineToRelative(-17.76f, -28.96f)
                lineToRelative(8.81f, 36.03f)
                lineToRelative(-29.91f, -21.93f)
                lineToRelative(21.93f, 29.91f)
                lineToRelative(-36.03f, -8.81f)
                lineToRelative(28.95f, 17.76f)
                lineToRelative(-57.13f, 7.13f)
                lineToRelative(57.13f, 7.13f)
                lineToRelative(-28.95f, 17.76f)
                lineToRelative(36.03f, -8.81f)
                lineToRelative(-21.93f, 29.91f)
                lineToRelative(29.91f, -21.93f)
                lineToRelative(-8.81f, 36.03f)
                lineToRelative(17.76f, -28.96f)
                lineToRelative(7.14f, 57.13f)
                lineToRelative(7.13f, -57.13f)
                lineToRelative(17.76f, 28.96f)
                lineToRelative(-8.81f, -36.03f)
                lineToRelative(29.91f, 21.93f)
                lineToRelative(-21.93f, -29.91f)
                lineToRelative(36.03f, 8.81f)
                close()
            }
        }.build()

        return _Marshall!!
    }

@Suppress("ObjectPropertyName")
private var _Marshall: ImageVector? = null
