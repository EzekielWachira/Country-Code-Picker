package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EzzyIcons.Andorra: ImageVector
    get() {
        if (_Andorra != null) {
            return _Andorra!!
        }
        _Andorra = ImageVector.Builder(
            name = "Andorra",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f
        ).apply {
            path(fill = SolidColor(Color(0xFFFFDA44))) {
                moveTo(0f, 0f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(512f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFF0052B4))) {
                moveTo(0f, 0f)
                horizontalLineToRelative(170.66f)
                verticalLineToRelative(512f)
                horizontalLineToRelative(-170.66f)
                close()
            }
            path(fill = SolidColor(Color(0xFFD80027))) {
                moveTo(341.34f, 0f)
                horizontalLineToRelative(170.66f)
                verticalLineToRelative(512f)
                horizontalLineToRelative(-170.66f)
                close()
            }
            path(fill = SolidColor(Color(0xFFD80027))) {
                moveTo(256f, 321.86f)
                curveToRelative(0f, -30.64f, 0f, -66.78f, 0f, -66.78f)
                horizontalLineToRelative(50.09f)
                verticalLineToRelative(25.04f)
                curveToRelative(0f, 4.35f, -8.35f, 20.29f, -28.95f, 33.39f)
                curveTo(269.33f, 318.47f, 261.25f, 320.07f, 256f, 321.86f)
                close()
            }
            path(fill = SolidColor(Color(0xFFD80027))) {
                moveTo(205.91f, 204.99f)
                horizontalLineToRelative(50.09f)
                verticalLineToRelative(50.09f)
                horizontalLineToRelative(-50.09f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFF9811))) {
                moveTo(281.04f, 188.29f)
                curveToRelative(0f, -9.22f, -7.47f, -16.7f, -16.7f, -16.7f)
                curveToRelative(-3.05f, 0f, -5.89f, 0.83f, -8.35f, 2.25f)
                curveToRelative(-2.46f, -1.42f, -5.3f, -2.25f, -8.35f, -2.25f)
                curveToRelative(-9.22f, 0f, -16.7f, 7.47f, -16.7f, 16.7f)
                horizontalLineToRelative(-41.74f)
                verticalLineToRelative(83.48f)
                curveToRelative(0f, 31.08f, 24.68f, 49.05f, 44.03f, 58.37f)
                curveToRelative(-1.45f, 2.47f, -2.29f, 5.34f, -2.29f, 8.42f)
                curveToRelative(0f, 9.22f, 7.47f, 16.7f, 16.7f, 16.7f)
                curveToRelative(3.05f, 0f, 5.89f, -0.83f, 8.35f, -2.25f)
                curveToRelative(2.46f, 1.42f, 5.3f, 2.25f, 8.35f, 2.25f)
                curveToRelative(9.22f, 0f, 16.7f, -7.47f, 16.7f, -16.7f)
                curveToRelative(0f, -3.07f, -0.85f, -5.95f, -2.3f, -8.42f)
                curveToRelative(19.35f, -9.32f, 44.03f, -27.29f, 44.03f, -58.37f)
                verticalLineTo(188.29f)
                lineTo(281.04f, 188.29f)
                lineTo(281.04f, 188.29f)
                close()
                moveTo(297.74f, 271.77f)
                curveToRelative(0f, 4.35f, 0f, 17.59f, -20.6f, 30.69f)
                curveToRelative(-7.8f, 4.96f, -15.88f, 8.18f, -21.14f, 9.97f)
                curveToRelative(-5.25f, -1.79f, -13.33f, -5.01f, -21.14f, -9.97f)
                curveToRelative(-20.6f, -13.1f, -20.6f, -26.34f, -20.6f, -30.69f)
                verticalLineToRelative(-58.43f)
                horizontalLineToRelative(83.48f)
                verticalLineTo(271.77f)
                close()
            }
        }.build()

        return _Andorra!!
    }

@Suppress("ObjectPropertyName")
private var _Andorra: ImageVector? = null
