package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EzzyIcons.Monaco: ImageVector
    get() {
        if (_Monaco != null) {
            return _Monaco!!
        }
        _Monaco = ImageVector.Builder(
            name = "Monaco",
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
                moveTo(512f, 85.33f)
                lineToRelative(0f, 166.69f)
                lineToRelative(-512f, 4.15f)
                lineToRelative(0f, -170.84f)
                close()
            }
        }.build()

        return _Monaco!!
    }

@Suppress("ObjectPropertyName")
private var _Monaco: ImageVector? = null
