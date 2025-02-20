package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EzzyIcons.Indonesia: ImageVector
    get() {
        if (_Indonesia != null) {
            return _Indonesia!!
        }
        _Indonesia = ImageVector.Builder(
            name = "Indonesia",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f
        ).apply {
            path(fill = SolidColor(Color(0xFFC8414B))) {
                moveTo(512f, 256f)
                horizontalLineTo(0f)
                verticalLineTo(97.1f)
                curveToRelative(0f, -4.88f, 3.95f, -8.83f, 8.83f, -8.83f)
                horizontalLineToRelative(494.35f)
                curveToRelative(4.88f, 0f, 8.83f, 3.95f, 8.83f, 8.83f)
                lineTo(512f, 256f)
                lineTo(512f, 256f)
                close()
            }
            path(fill = SolidColor(Color(0xFFF5F5F5))) {
                moveTo(0f, 256f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(158.9f)
                curveToRelative(0f, 4.88f, -3.95f, 8.83f, -8.83f, 8.83f)
                horizontalLineTo(8.83f)
                curveToRelative(-4.88f, 0f, -8.83f, -3.95f, -8.83f, -8.83f)
                verticalLineTo(256f)
                close()
            }
        }.build()

        return _Indonesia!!
    }

@Suppress("ObjectPropertyName")
private var _Indonesia: ImageVector? = null
