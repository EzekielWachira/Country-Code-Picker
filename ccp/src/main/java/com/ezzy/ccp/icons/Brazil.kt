package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import kotlin.Suppress

val EzzyIcons.Brazil: ImageVector
    get() {
        if (_Brazil != null) {
            return _Brazil!!
        }
        _Brazil = ImageVector.Builder(
            name = "Brazil",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f
        ).apply {
            path(fill = SolidColor(Color(0xFF4BA543))) {
                moveToRelative(508f, 425.98f)
                horizontalLineToRelative(-504f)
                curveToRelative(-2.21f, 0f, -4f, -1.79f, -4f, -4f)
                verticalLineToRelative(-331.96f)
                curveToRelative(0f, -2.21f, 1.79f, -4f, 4f, -4f)
                horizontalLineToRelative(504f)
                curveToRelative(2.21f, 0f, 4f, 1.79f, 4f, 4f)
                verticalLineToRelative(331.96f)
                curveToRelative(0f, 2.21f, -1.79f, 4f, -4f, 4f)
                close()
            }
            path(fill = SolidColor(Color(0xFF43A047))) {
                moveToRelative(481.1f, 86.02f)
                verticalLineToRelative(339.96f)
                horizontalLineToRelative(26.91f)
                curveToRelative(2.21f, 0f, 4f, -1.79f, 4f, -4f)
                verticalLineToRelative(-331.96f)
                curveToRelative(0f, -2.21f, -1.79f, -4f, -4f, -4f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFECE3F))) {
                moveToRelative(70.12f, 252.72f)
                lineToRelative(183.59f, -128.02f)
                curveToRelative(1.38f, -0.96f, 3.2f, -0.96f, 4.58f, 0f)
                lineToRelative(183.59f, 128.02f)
                curveToRelative(2.28f, 1.59f, 2.28f, 4.97f, 0f, 6.56f)
                lineToRelative(-183.59f, 128.02f)
                curveToRelative(-1.38f, 0.96f, -3.2f, 0.96f, -4.58f, 0f)
                lineToRelative(-183.59f, -128.02f)
                curveToRelative(-2.28f, -1.59f, -2.28f, -4.97f, 0f, -6.56f)
                close()
            }
            path(fill = SolidColor(Color(0xFF2387EB))) {
                moveTo(256f, 256f)
                moveToRelative(-74.17f, 0f)
                arcToRelative(74.17f, 74.17f, 0f, isMoreThanHalf = true, isPositiveArc = true, 148.35f, 0f)
                arcToRelative(74.17f, 74.17f, 0f, isMoreThanHalf = true, isPositiveArc = true, -148.35f, 0f)
            }
            path(fill = SolidColor(Color(0xFF207BD6))) {
                moveToRelative(256f, 181.83f)
                curveToRelative(-5.3f, 0f, -10.47f, 0.57f, -15.45f, 1.62f)
                curveToRelative(33.55f, 7.11f, 58.72f, 36.89f, 58.72f, 72.55f)
                reflectiveCurveToRelative(-25.17f, 65.44f, -58.72f, 72.55f)
                curveToRelative(4.99f, 1.06f, 10.15f, 1.62f, 15.45f, 1.62f)
                curveToRelative(40.97f, 0f, 74.17f, -33.21f, 74.17f, -74.17f)
                reflectiveCurveToRelative(-33.21f, -74.17f, -74.17f, -74.17f)
                close()
            }
            path(fill = SolidColor(Color(0xFFEFEFEF))) {
                moveToRelative(183.12f, 242.25f)
                lineToRelative(143.25f, 37.2f)
                curveToRelative(2.46f, -7.37f, 3.81f, -15.25f, 3.81f, -23.45f)
                curveToRelative(0f, -2.56f, -0.13f, -5.09f, -0.38f, -7.59f)
                lineToRelative(-134.54f, -34.94f)
                curveToRelative(-5.94f, 8.47f, -10.15f, 18.23f, -12.13f, 28.78f)
                close()
            }
            path(fill = SolidColor(Color(0xFFDCDCDC))) {
                moveToRelative(297.54f, 271.96f)
                lineToRelative(28.83f, 7.49f)
                curveToRelative(2.46f, -7.37f, 3.81f, -15.25f, 3.81f, -23.45f)
                curveToRelative(0f, -2.56f, -0.13f, -5.09f, -0.38f, -7.59f)
                lineToRelative(-32.25f, -8.38f)
                curveToRelative(1.13f, 5.14f, 1.73f, 10.48f, 1.73f, 15.97f)
                curveToRelative(0f, 5.48f, -0.6f, 10.82f, -1.73f, 15.96f)
                close()
            }
        }.build()

        return _Brazil!!
    }

@Suppress("ObjectPropertyName")
private var _Brazil: ImageVector? = null
