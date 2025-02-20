package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EzzyIcons.Bhutan: ImageVector
    get() {
        if (_Bhutan != null) {
            return _Bhutan!!
        }
        _Bhutan = ImageVector.Builder(
            name = "Bhutan",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f
        ).apply {
            path(fill = SolidColor(Color(0xFFFF9811))) {
                moveTo(0f, 85.33f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(341.34f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFDA44))) {
                moveTo(512f, 85.33f)
                lineToRelative(-512f, 0f)
                lineToRelative(0f, 341.33f)
            }
            path(fill = SolidColor(Color(0xFFF0F0F0))) {
                moveTo(352.55f, 181.79f)
                verticalLineToRelative(-29.68f)
                horizontalLineToRelative(-56.89f)
                lineToRelative(-4.35f, 4.35f)
                curveToRelative(-15.46f, 15.46f, -12.86f, 33.49f, -10.96f, 46.66f)
                curveToRelative(1.76f, 12.22f, 1.97f, 16.89f, -2.57f, 21.44f)
                curveToRelative(-4.54f, 4.54f, -9.22f, 4.33f, -21.44f, 2.57f)
                curveToRelative(-13.17f, -1.9f, -31.2f, -4.5f, -46.66f, 10.96f)
                curveToRelative(-15.46f, 15.46f, -12.86f, 33.49f, -10.96f, 46.66f)
                curveToRelative(1.76f, 12.21f, 1.97f, 16.89f, -2.57f, 21.43f)
                curveToRelative(-4.54f, 4.54f, -9.22f, 4.33f, -21.43f, 2.57f)
                curveToRelative(-4.76f, -0.69f, -9.69f, -1.4f, -14.94f, -1.46f)
                lineToRelative(-0.33f, 29.68f)
                curveToRelative(3.29f, 0.04f, 7.05f, 0.58f, 11.03f, 1.15f)
                curveToRelative(4.71f, 0.68f, 10.05f, 1.45f, 15.66f, 1.45f)
                curveToRelative(5.82f, 0f, 11.94f, -0.83f, 17.99f, -3.44f)
                verticalLineToRelative(23.75f)
                horizontalLineToRelative(44.52f)
                verticalLineToRelative(-22.26f)
                horizontalLineToRelative(-22.26f)
                verticalLineToRelative(-14.84f)
                horizontalLineToRelative(14.84f)
                verticalLineToRelative(-22.26f)
                horizontalLineTo(229.38f)
                curveToRelative(0.56f, -7.2f, -0.43f, -14.1f, -1.28f, -20.01f)
                curveToRelative(-1.76f, -12.21f, -1.97f, -16.89f, 2.57f, -21.43f)
                curveToRelative(4.54f, -4.54f, 9.22f, -4.33f, 21.44f, -2.57f)
                curveToRelative(9.59f, 1.38f, 21.77f, 3.13f, 33.66f, -2f)
                verticalLineToRelative(23.75f)
                horizontalLineToRelative(44.52f)
                verticalLineToRelative(-22.26f)
                horizontalLineToRelative(-22.26f)
                verticalLineToRelative(-14.84f)
                horizontalLineToRelative(14.84f)
                verticalLineToRelative(-22.26f)
                horizontalLineToRelative(-11.86f)
                curveToRelative(0.56f, -7.2f, -0.43f, -14.1f, -1.28f, -20.01f)
                curveToRelative(-1.23f, -8.53f, -1.7f, -13.39f, -0.33f, -17.09f)
                horizontalLineTo(352.55f)
                close()
            }
        }.build()

        return _Bhutan!!
    }

@Suppress("ObjectPropertyName")
private var _Bhutan: ImageVector? = null
