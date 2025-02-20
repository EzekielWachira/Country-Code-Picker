package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EzzyIcons.Luxembourg: ImageVector
    get() {
        if (_Luxembourg != null) {
            return _Luxembourg!!
        }
        _Luxembourg = ImageVector.Builder(
            name = "Luxembourg",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f
        ).apply {
            path(fill = SolidColor(Color(0xFFF0F0F0))) {
                moveTo(0f, 85.34f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(341.33f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFFD80027))) {
                moveTo(0f, 85.34f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(113.78f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFF338AF3))) {
                moveTo(0f, 312.89f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(113.78f)
                horizontalLineToRelative(-512f)
                close()
            }
        }.build()

        return _Luxembourg!!
    }

@Suppress("ObjectPropertyName")
private var _Luxembourg: ImageVector? = null
