package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import kotlin.Suppress

val EzzyIcons.Search: ImageVector
    get() {
        if (_Search != null) {
            return _Search!!
        }
        _Search = ImageVector.Builder(
            name = "Search",
            defaultWidth = 612.01.dp,
            defaultHeight = 612.01.dp,
            viewportWidth = 612.01f,
            viewportHeight = 612.01f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(606.21f, 578.71f)
                lineTo(448.2f, 423.23f)
                curveTo(489.58f, 378.27f, 515f, 318.82f, 515f, 253.39f)
                curveTo(514.98f, 113.44f, 399.7f, 0f, 257.49f, 0f)
                curveTo(115.28f, 0f, 0.01f, 113.44f, 0.01f, 253.39f)
                reflectiveCurveToRelative(115.28f, 253.39f, 257.49f, 253.39f)
                curveToRelative(61.44f, 0f, 117.8f, -21.25f, 162.07f, -56.59f)
                lineToRelative(158.62f, 156.1f)
                curveToRelative(7.73f, 7.61f, 20.28f, 7.61f, 28.01f, 0f)
                curveTo(613.94f, 598.69f, 613.94f, 586.33f, 606.21f, 578.71f)
                close()
                moveTo(257.49f, 467.8f)
                curveToRelative(-120.33f, 0f, -217.87f, -95.99f, -217.87f, -214.41f)
                reflectiveCurveTo(137.17f, 38.99f, 257.49f, 38.99f)
                curveToRelative(120.33f, 0f, 217.87f, 95.99f, 217.87f, 214.41f)
                reflectiveCurveTo(377.82f, 467.8f, 257.49f, 467.8f)
                close()
            }
        }.build()

        return _Search!!
    }

@Suppress("ObjectPropertyName")
private var _Search: ImageVector? = null
