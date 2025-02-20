package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EzzyIcons.Syria: ImageVector
    get() {
        if (_Syria != null) {
            return _Syria!!
        }
        _Syria = ImageVector.Builder(
            name = "Syria",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f
        ).apply {
            path(fill = SolidColor(Color(0xFFF0F0F0))) {
                moveTo(0f, 85.33f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(341.34f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFFD80027))) {
                moveTo(0f, 85.33f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(113.78f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(0f, 312.88f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(113.78f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFF6DA544))) {
                moveTo(187.31f, 215.18f)
                lineToRelative(9.21f, 28.34f)
                lineToRelative(29.8f, 0f)
                lineToRelative(-24.11f, 17.52f)
                lineToRelative(9.21f, 28.34f)
                lineToRelative(-24.11f, -17.52f)
                lineToRelative(-24.11f, 17.52f)
                lineToRelative(9.21f, -28.34f)
                lineToRelative(-24.11f, -17.52f)
                lineToRelative(29.8f, 0f)
                close()
            }
            path(fill = SolidColor(Color(0xFF6DA544))) {
                moveTo(324.69f, 215.18f)
                lineToRelative(9.21f, 28.34f)
                lineToRelative(29.8f, 0f)
                lineToRelative(-24.11f, 17.52f)
                lineToRelative(9.21f, 28.34f)
                lineToRelative(-24.11f, -17.52f)
                lineToRelative(-24.11f, 17.52f)
                lineToRelative(9.21f, -28.34f)
                lineToRelative(-24.11f, -17.52f)
                lineToRelative(29.8f, 0f)
                close()
            }
        }.build()

        return _Syria!!
    }

@Suppress("ObjectPropertyName")
private var _Syria: ImageVector? = null
