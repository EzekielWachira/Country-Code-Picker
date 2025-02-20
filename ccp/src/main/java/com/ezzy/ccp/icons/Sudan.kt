package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EzzyIcons.Sudan: ImageVector
    get() {
        if (_Sudan != null) {
            return _Sudan!!
        }
        _Sudan = ImageVector.Builder(
            name = "Sudan",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 64f,
            viewportHeight = 64f
        ).apply {
            path(fill = SolidColor(Color(0xFFFF0000))) {
                moveToRelative(4f, 14f)
                horizontalLineToRelative(56f)
                verticalLineToRelative(12f)
                horizontalLineToRelative(-56f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFFFFF))) {
                moveToRelative(4f, 26f)
                horizontalLineToRelative(56f)
                verticalLineToRelative(12f)
                horizontalLineToRelative(-56f)
                close()
            }
            path(fill = SolidColor(Color(0xFF000000))) {
                moveToRelative(4f, 38f)
                horizontalLineToRelative(56f)
                verticalLineToRelative(12f)
                horizontalLineToRelative(-56f)
                close()
            }
            path(fill = SolidColor(Color(0xFF009A00))) {
                moveToRelative(35.18f, 32f)
                lineToRelative(-31.18f, -18f)
                verticalLineToRelative(36f)
                close()
            }
        }.build()

        return _Sudan!!
    }

@Suppress("ObjectPropertyName")
private var _Sudan: ImageVector? = null
