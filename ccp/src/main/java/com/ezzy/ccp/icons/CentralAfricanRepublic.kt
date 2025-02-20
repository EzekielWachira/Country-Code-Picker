package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EzzyIcons.CentralAfricanRepublic: ImageVector
    get() {
        if (_CentralAfricanRepublic!= null) {
            return _CentralAfricanRepublic!!
        }
        _CentralAfricanRepublic= ImageVector.Builder(
            name = "CentralAfricanRepublic",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f
        ).apply {
            path(fill = SolidColor(Color(0xFFFFE15A))) {
                moveTo(503.17f, 423.72f)
                horizontalLineTo(8.83f)
                curveToRelative(-4.88f, 0f, -8.83f, -3.95f, -8.83f, -8.83f)
                verticalLineToRelative(-75.03f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(75.03f)
                curveTo(512f, 419.77f, 508.05f, 423.72f, 503.17f, 423.72f)
                close()
            }
            path(fill = SolidColor(Color(0xFF73AF00))) {
                moveTo(0f, 256f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(83.86f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFFF5F5F5))) {
                moveTo(0f, 172.14f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(83.86f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFF41479B))) {
                moveTo(512f, 172.13f)
                horizontalLineTo(0f)
                verticalLineTo(97.1f)
                curveToRelative(0f, -4.88f, 3.95f, -8.83f, 8.83f, -8.83f)
                horizontalLineToRelative(494.35f)
                curveToRelative(4.88f, 0f, 8.83f, 3.95f, 8.83f, 8.83f)
                lineTo(512f, 172.13f)
                lineTo(512f, 172.13f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFF4B55))) {
                moveTo(214.07f, 88.28f)
                horizontalLineToRelative(83.86f)
                verticalLineToRelative(335.45f)
                horizontalLineToRelative(-83.86f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFE15A))) {
                moveTo(76.5f, 99.57f)
                lineToRelative(7.37f, 22.09f)
                lineToRelative(23.28f, 0.18f)
                curveToRelative(1.8f, 0.01f, 2.55f, 2.32f, 1.1f, 3.39f)
                lineToRelative(-18.73f, 13.83f)
                lineToRelative(7.02f, 22.2f)
                curveToRelative(0.54f, 1.72f, -1.41f, 3.14f, -2.88f, 2.09f)
                lineTo(74.72f, 149.81f)
                lineToRelative(-18.94f, 13.54f)
                curveToRelative(-1.47f, 1.05f, -3.42f, -0.37f, -2.88f, -2.09f)
                lineToRelative(7.02f, -22.2f)
                lineToRelative(-18.73f, -13.83f)
                curveToRelative(-1.45f, -1.07f, -0.7f, -3.37f, 1.1f, -3.39f)
                lineToRelative(23.28f, -0.18f)
                lineToRelative(7.37f, -22.09f)
                curveTo(73.51f, 97.86f, 75.93f, 97.86f, 76.5f, 99.57f)
                close()
            }
        }.build()

        return _CentralAfricanRepublic!!
    }

@Suppress("ObjectPropertyName")
private var _CentralAfricanRepublic: ImageVector? = null
