package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import kotlin.Suppress

val EzzyIcons.Russia: ImageVector
    get() {
        if (_Russia != null) {
            return _Russia!!
        }
        _Russia = ImageVector.Builder(
            name = "Russia",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f
        ).apply {
            path(fill = SolidColor(Color(0xFFF5F5F5))) {
                moveTo(512f, 200.09f)
                horizontalLineTo(0f)
                verticalLineTo(97.1f)
                curveToRelative(0f, -4.88f, 3.95f, -8.83f, 8.83f, -8.83f)
                horizontalLineToRelative(494.35f)
                curveToRelative(4.88f, 0f, 8.83f, 3.95f, 8.83f, 8.83f)
                lineTo(512f, 200.09f)
                lineTo(512f, 200.09f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFF4B55))) {
                moveTo(503.17f, 423.73f)
                horizontalLineTo(8.83f)
                curveToRelative(-4.88f, 0f, -8.83f, -3.95f, -8.83f, -8.83f)
                verticalLineTo(311.91f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(102.99f)
                curveTo(512f, 419.77f, 508.05f, 423.73f, 503.17f, 423.73f)
                close()
            }
            path(fill = SolidColor(Color(0xFF41479B))) {
                moveTo(0f, 200.09f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(111.81f)
                horizontalLineToRelative(-512f)
                close()
            }
        }.build()

        return _Russia!!
    }

@Suppress("ObjectPropertyName")
private var _Russia: ImageVector? = null
