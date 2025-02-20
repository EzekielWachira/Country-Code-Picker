package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EzzyIcons.Djibouti: ImageVector
    get() {
        if (_Djibouti != null) {
            return _Djibouti!!
        }
        _Djibouti = ImageVector.Builder(
            name = "Djibouti",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f
        ).apply {
            path(fill = SolidColor(Color(0xFFF5F5F5))) {
                moveTo(3.26f, 421.59f)
                lineTo(256f, 256f)
                lineTo(3.26f, 90.41f)
                curveTo(1.31f, 92.03f, 0f, 94.38f, 0f, 97.1f)
                verticalLineToRelative(317.79f)
                curveTo(0f, 417.63f, 1.31f, 419.97f, 3.26f, 421.59f)
                close()
            }
            path(fill = SolidColor(Color(0xFF73AF00))) {
                moveTo(3.26f, 421.59f)
                curveToRelative(1.53f, 1.27f, 3.42f, 2.13f, 5.57f, 2.13f)
                horizontalLineToRelative(494.35f)
                curveToRelative(4.88f, 0f, 8.83f, -3.95f, 8.83f, -8.83f)
                verticalLineTo(256f)
                horizontalLineTo(256f)
                lineTo(3.26f, 421.59f)
                close()
            }
            path(fill = SolidColor(Color(0xFF82AFFF))) {
                moveTo(3.26f, 90.41f)
                curveToRelative(1.53f, -1.27f, 3.42f, -2.13f, 5.57f, -2.13f)
                horizontalLineToRelative(494.35f)
                curveToRelative(4.88f, 0f, 8.83f, 3.95f, 8.83f, 8.83f)
                verticalLineTo(256f)
                horizontalLineTo(256f)
                lineTo(3.26f, 90.41f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFF4B55))) {
                moveTo(91.77f, 216.13f)
                lineToRelative(-8.94f, 26.81f)
                lineToRelative(-28.26f, 0.22f)
                curveToRelative(-1.55f, 0.01f, -2.19f, 1.99f, -0.95f, 2.91f)
                lineToRelative(22.73f, 16.79f)
                lineTo(67.83f, 289.8f)
                curveToRelative(-0.47f, 1.48f, 1.22f, 2.7f, 2.47f, 1.8f)
                lineToRelative(22.99f, -16.43f)
                lineToRelative(22.99f, 16.43f)
                curveToRelative(1.26f, 0.9f, 2.94f, -0.32f, 2.47f, -1.8f)
                lineToRelative(-8.52f, -26.94f)
                lineToRelative(22.73f, -16.79f)
                curveToRelative(1.25f, -0.92f, 0.6f, -2.9f, -0.95f, -2.91f)
                lineToRelative(-28.26f, -0.22f)
                lineToRelative(-8.94f, -26.81f)
                curveTo(94.33f, 214.66f, 92.26f, 214.66f, 91.77f, 216.13f)
                close()
            }
        }.build()

        return _Djibouti!!
    }

@Suppress("ObjectPropertyName")
private var _Djibouti: ImageVector? = null
