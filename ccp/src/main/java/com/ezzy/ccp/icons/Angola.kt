package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import kotlin.Suppress

val EzzyIcons.Angola: ImageVector
    get() {
        if (_Angola != null) {
            return _Angola!!
        }
        _Angola = ImageVector.Builder(
            name = "Angola",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(0f, 85.33f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(341.34f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFFD80027))) {
                moveTo(0f, 85.33f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(170.66f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFDA44))) {
                moveTo(232.6f, 221.07f)
                lineToRelative(14.47f, 10.5f)
                lineToRelative(-5.51f, 17.01f)
                lineToRelative(14.46f, -10.52f)
                lineToRelative(14.47f, 10.5f)
                lineToRelative(-5.54f, -17f)
                lineToRelative(14.46f, -10.52f)
                lineToRelative(-17.88f, 0.01f)
                lineToRelative(-5.53f, -17f)
                lineToRelative(-5.51f, 17.01f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFDA44))) {
                moveTo(298.67f, 182.09f)
                curveToRelative(-13.45f, -7.76f, -28.15f, -11.43f, -42.67f, -11.4f)
                verticalLineToRelative(22.25f)
                curveToRelative(10.73f, -0.02f, 21.59f, 2.69f, 31.54f, 8.43f)
                curveToRelative(30.12f, 17.39f, 40.48f, 56.04f, 23.09f, 86.16f)
                curveToRelative(-17.39f, 30.12f, -56.04f, 40.48f, -86.16f, 23.09f)
                curveToRelative(-8.77f, -5.07f, -15.86f, -11.94f, -21.08f, -19.88f)
                lineToRelative(-18.58f, 12.27f)
                curveToRelative(7.07f, 10.74f, 16.66f, 20.04f, 28.53f, 26.89f)
                curveToRelative(40.75f, 23.53f, 93.04f, 9.52f, 116.57f, -31.23f)
                curveTo(353.43f, 257.91f, 339.42f, 205.62f, 298.67f, 182.09f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFDA44))) {
                moveTo(206.79f, 241.15f)
                curveToRelative(-5.9f, 10.79f, -1.94f, 24.31f, 8.85f, 30.21f)
                lineToRelative(72.3f, 39.51f)
                curveToRelative(-4.92f, 8.99f, -2.31f, 19.93f, 6.68f, 24.84f)
                lineTo(314.15f, 346.4f)
                curveToRelative(8.99f, 4.92f, 20.26f, 1.62f, 25.18f, -7.37f)
                lineToRelative(10.68f, -19.53f)
                lineTo(206.79f, 241.15f)
                close()
            }
        }.build()

        return _Angola!!
    }

@Suppress("ObjectPropertyName")
private var _Angola: ImageVector? = null
