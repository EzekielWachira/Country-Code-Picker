package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EzzyIcons.Tunisia: ImageVector
    get() {
        if (_Tunisia != null) {
            return _Tunisia!!
        }
        _Tunisia = ImageVector.Builder(
            name = "Tunisia",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f
        ).apply {
            path(fill = SolidColor(Color(0xFFD80027))) {
                moveTo(0f, 85.33f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(341.34f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFFF0F0F0))) {
                moveTo(256f, 255.99f)
                moveToRelative(-96f, 0f)
                arcToRelative(96f, 96f, 0f, isMoreThanHalf = true, isPositiveArc = true, 192f, 0f)
                arcToRelative(96f, 96f, 0f, isMoreThanHalf = true, isPositiveArc = true, -192f, 0f)
            }
            path(fill = SolidColor(Color(0xFFD80027))) {
                moveTo(267.83f, 219.29f)
                lineToRelative(16.47f, 22.69f)
                lineToRelative(26.67f, -8.65f)
                lineToRelative(-16.5f, 22.68f)
                lineToRelative(16.47f, 22.7f)
                lineToRelative(-26.66f, -8.68f)
                lineToRelative(-16.49f, 22.68f)
                lineToRelative(0.02f, -28.04f)
                lineToRelative(-26.66f, -8.68f)
                lineToRelative(26.67f, -8.65f)
                close()
            }
            path(fill = SolidColor(Color(0xFFD80027))) {
                moveTo(277.82f, 312.72f)
                curveToRelative(-31.33f, 0f, -56.73f, -25.4f, -56.73f, -56.73f)
                reflectiveCurveToRelative(25.4f, -56.73f, 56.73f, -56.73f)
                curveToRelative(9.77f, 0f, 18.96f, 2.47f, 26.99f, 6.82f)
                curveToRelative(-12.59f, -12.31f, -29.8f, -19.91f, -48.8f, -19.91f)
                curveToRelative(-38.56f, 0f, -69.82f, 31.26f, -69.82f, 69.82f)
                reflectiveCurveToRelative(31.26f, 69.82f, 69.82f, 69.82f)
                curveToRelative(19f, 0f, 36.22f, -7.6f, 48.8f, -19.91f)
                curveTo(296.78f, 310.25f, 287.59f, 312.72f, 277.82f, 312.72f)
                close()
            }
        }.build()

        return _Tunisia!!
    }

@Suppress("ObjectPropertyName")
private var _Tunisia: ImageVector? = null
