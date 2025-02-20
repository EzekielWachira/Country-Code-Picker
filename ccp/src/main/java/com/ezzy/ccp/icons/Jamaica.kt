package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EzzyIcons.Jamaica: ImageVector
    get() {
        if (_Jamaica != null) {
            return _Jamaica!!
        }
        _Jamaica = ImageVector.Builder(
            name = "Jamaica",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f
        ).apply {
            path(fill = SolidColor(Color(0xFF6DA544))) {
                moveTo(0f, 85.34f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(341.33f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(215.86f, 256.01f)
                lineToRelative(-215.86f, 143.9f)
                lineToRelative(0f, -287.82f)
                close()
            }
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(512f, 112.09f)
                lineToRelative(0f, 287.82f)
                lineToRelative(-215.86f, -143.9f)
                close()
            }
            path(fill = SolidColor(Color(0xFF0052B4))) {
                moveTo(512f, 112.09f)
                lineToRelative(-215.86f, 143.92f)
                lineToRelative(215.86f, 143.9f)
                lineToRelative(0f, 26.76f)
                lineToRelative(-40.13f, 0f)
                lineToRelative(-215.88f, -143.92f)
                lineToRelative(-215.88f, 143.92f)
                lineToRelative(-40.13f, 0f)
                lineToRelative(0f, -26.76f)
                lineToRelative(215.86f, -143.9f)
                lineToRelative(-215.86f, -143.92f)
                lineToRelative(0f, -26.76f)
                lineToRelative(40.13f, 0f)
                lineToRelative(215.88f, 143.92f)
                lineToRelative(215.88f, -143.92f)
                lineToRelative(40.13f, 0f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFDA44))) {
                moveTo(512f, 112.09f)
                lineToRelative(-215.86f, 143.92f)
                lineToRelative(215.86f, 143.9f)
                lineToRelative(0f, 26.76f)
                lineToRelative(-40.13f, 0f)
                lineToRelative(-215.88f, -143.92f)
                lineToRelative(-215.88f, 143.92f)
                lineToRelative(-40.13f, 0f)
                lineToRelative(0f, -26.76f)
                lineToRelative(215.86f, -143.9f)
                lineToRelative(-215.86f, -143.92f)
                lineToRelative(0f, -26.76f)
                lineToRelative(40.13f, 0f)
                lineToRelative(215.88f, 143.92f)
                lineToRelative(215.88f, -143.92f)
                lineToRelative(40.13f, 0f)
                close()
            }
        }.build()

        return _Jamaica!!
    }

@Suppress("ObjectPropertyName")
private var _Jamaica: ImageVector? = null
