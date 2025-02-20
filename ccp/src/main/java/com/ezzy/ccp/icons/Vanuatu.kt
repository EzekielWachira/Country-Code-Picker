package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EzzyIcons.Vanuatu: ImageVector
    get() {
        if (_Vanuatu != null) {
            return _Vanuatu!!
        }
        _Vanuatu = ImageVector.Builder(
            name = "Vanuatu",
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
            path(fill = SolidColor(Color(0xFF6DA544))) {
                moveTo(512f, 256f)
                lineToRelative(0f, 170.66f)
                lineToRelative(-512f, 0f)
                lineToRelative(215.18f, -170.66f)
                close()
            }
            path(fill = SolidColor(Color(0xFFD80027))) {
                moveTo(512f, 85.34f)
                lineToRelative(0f, 170.66f)
                lineToRelative(-296.82f, 0f)
                lineToRelative(-215.18f, -170.66f)
                close()
            }
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(221f, 239.3f)
                lineToRelative(-194.13f, -153.97f)
                lineToRelative(-17.91f, 0f)
                lineToRelative(208.17f, 165.1f)
                lineToRelative(294.88f, 0f)
                lineToRelative(0f, -11.13f)
                close()
            }
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(8.96f, 426.66f)
                lineToRelative(17.91f, 0f)
                lineToRelative(194.13f, -153.97f)
                lineToRelative(291f, 0f)
                lineToRelative(0f, -11.13f)
                lineToRelative(-294.88f, 0f)
                close()
            }
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(0f, 92.44f)
                lineToRelative(0f, 14.21f)
                lineToRelative(188.32f, 149.35f)
                lineToRelative(-188.32f, 149.35f)
                lineToRelative(0f, 14.2f)
                lineToRelative(206.23f, -163.56f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFDA44))) {
                moveTo(8.96f, 85.34f)
                lineToRelative(-8.96f, 0f)
                lineToRelative(0f, 7.1f)
                lineToRelative(206.23f, 163.56f)
                lineToRelative(-206.23f, 163.56f)
                lineToRelative(0f, 7.1f)
                lineToRelative(8.96f, 0f)
                lineToRelative(208.17f, -165.1f)
                lineToRelative(294.88f, 0f)
                lineToRelative(0f, -11.13f)
                lineToRelative(-294.88f, 0f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFDA44))) {
                moveTo(63.72f, 292.38f)
                verticalLineToRelative(-14.3f)
                curveToRelative(14.27f, 0f, 25.87f, -11.61f, 25.87f, -25.87f)
                curveToRelative(0f, -10.09f, -8.21f, -18.3f, -18.3f, -18.3f)
                curveToRelative(-6.88f, 0f, -12.47f, 5.59f, -12.47f, 12.47f)
                curveToRelative(0f, 4.4f, 3.58f, 7.97f, 7.97f, 7.97f)
                curveToRelative(2.48f, 0f, 4.51f, -2.02f, 4.51f, -4.51f)
                horizontalLineToRelative(14.3f)
                curveToRelative(0f, 10.37f, -8.44f, 18.8f, -18.8f, 18.8f)
                curveToRelative(-12.28f, -0f, -22.27f, -9.99f, -22.27f, -22.27f)
                curveToRelative(0f, -14.76f, 12.01f, -26.76f, 26.76f, -26.76f)
                curveToRelative(17.98f, 0f, 32.6f, 14.62f, 32.6f, 32.6f)
                curveTo(103.88f, 274.36f, 85.87f, 292.38f, 63.72f, 292.38f)
                close()
            }
        }.build()

        return _Vanuatu!!
    }

@Suppress("ObjectPropertyName")
private var _Vanuatu: ImageVector? = null
