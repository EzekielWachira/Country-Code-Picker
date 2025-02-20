package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EzzyIcons.Israel: ImageVector
    get() {
        if (_Israel != null) {
            return _Israel!!
        }
        _Israel = ImageVector.Builder(
            name = "Israel",
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
                moveTo(0f, 150.07f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(211.86f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFF41479B))) {
                moveTo(301.87f, 256f)
                lineToRelative(22.93f, -39.72f)
                horizontalLineToRelative(-45.87f)
                lineTo(256f, 176.55f)
                lineToRelative(-22.94f, 39.73f)
                horizontalLineToRelative(-45.87f)
                lineToRelative(22.93f, 39.72f)
                lineToRelative(-22.93f, 39.72f)
                horizontalLineToRelative(45.87f)
                lineTo(256f, 335.45f)
                lineToRelative(22.94f, -39.73f)
                horizontalLineToRelative(45.87f)
                lineTo(301.87f, 256f)
                close()
                moveTo(302.3f, 229.27f)
                lineToRelative(-7.93f, 13.74f)
                lineToRelative(-7.93f, -13.74f)
                horizontalLineTo(302.3f)
                close()
                moveTo(286.87f, 256f)
                lineToRelative(-15.43f, 26.73f)
                horizontalLineToRelative(-30.87f)
                lineToRelative(-15.43f, -26.73f)
                lineToRelative(15.43f, -26.73f)
                horizontalLineToRelative(30.87f)
                lineTo(286.87f, 256f)
                close()
                moveTo(256f, 202.54f)
                lineToRelative(7.94f, 13.75f)
                horizontalLineToRelative(-15.87f)
                lineTo(256f, 202.54f)
                close()
                moveTo(209.7f, 229.27f)
                horizontalLineToRelative(15.86f)
                lineToRelative(-7.93f, 13.74f)
                lineTo(209.7f, 229.27f)
                close()
                moveTo(209.7f, 282.73f)
                lineToRelative(7.93f, -13.74f)
                lineToRelative(7.93f, 13.74f)
                horizontalLineTo(209.7f)
                close()
                moveTo(256f, 309.47f)
                lineToRelative(-7.94f, -13.75f)
                horizontalLineToRelative(15.87f)
                lineTo(256f, 309.47f)
                close()
                moveTo(294.37f, 268.99f)
                lineToRelative(7.93f, 13.74f)
                horizontalLineToRelative(-15.86f)
                lineTo(294.37f, 268.99f)
                close()
            }
        }.build()

        return _Israel!!
    }

@Suppress("ObjectPropertyName")
private var _Israel: ImageVector? = null
