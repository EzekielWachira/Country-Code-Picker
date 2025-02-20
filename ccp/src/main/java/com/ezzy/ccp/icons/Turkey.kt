package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import kotlin.Suppress

val EzzyIcons.Turkey: ImageVector
    get() {
        if (_Turkey != null) {
            return _Turkey!!
        }
        _Turkey = ImageVector.Builder(
            name = "Turkey",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f
        ).apply {
            path(fill = SolidColor(Color(0xFFD80027))) {
                moveTo(0f, 85.34f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(341.33f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFFF0F0F0))) {
                moveTo(247.21f, 216.79f)
                lineToRelative(17.59f, 24.25f)
                lineToRelative(28.49f, -9.24f)
                lineToRelative(-17.62f, 24.22f)
                lineToRelative(17.59f, 24.24f)
                lineToRelative(-28.48f, -9.27f)
                lineToRelative(-17.62f, 24.23f)
                lineToRelative(0.02f, -29.96f)
                lineToRelative(-28.48f, -9.27f)
                lineToRelative(28.49f, -9.24f)
                close()
            }
            path(fill = SolidColor(Color(0xFFF0F0F0))) {
                moveTo(199.2f, 316.6f)
                curveToRelative(-33.47f, 0f, -60.6f, -27.13f, -60.6f, -60.6f)
                reflectiveCurveToRelative(27.13f, -60.6f, 60.6f, -60.6f)
                curveToRelative(10.44f, 0f, 20.25f, 2.64f, 28.83f, 7.28f)
                curveToRelative(-13.45f, -13.15f, -31.84f, -21.27f, -52.13f, -21.27f)
                curveToRelative(-41.19f, 0f, -74.59f, 33.39f, -74.59f, 74.59f)
                reflectiveCurveToRelative(33.39f, 74.59f, 74.59f, 74.59f)
                curveToRelative(20.3f, 0f, 38.69f, -8.12f, 52.13f, -21.27f)
                curveTo(219.46f, 313.96f, 209.64f, 316.6f, 199.2f, 316.6f)
                close()
            }
        }.build()

        return _Turkey!!
    }

@Suppress("ObjectPropertyName")
private var _Turkey: ImageVector? = null
