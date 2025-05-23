package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EzzyIcons.Malta: ImageVector
    get() {
        if (_Malta != null) {
            return _Malta!!
        }
        _Malta = ImageVector.Builder(
            name = "Malta",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f
        ).apply {
            path(fill = SolidColor(Color(0xFFF0F0F0))) {
                moveTo(0f, 85.33f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(341.33f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFFD80027))) {
                moveTo(256f, 85.33f)
                horizontalLineToRelative(256f)
                verticalLineToRelative(341.34f)
                horizontalLineToRelative(-256f)
                close()
            }
            path(fill = SolidColor(Color(0xFFACABB1))) {
                moveTo(208.23f, 138.67f)
                lineToRelative(0f, -21.33f)
                lineToRelative(-21.33f, 0f)
                lineToRelative(0f, 21.33f)
                lineToRelative(-21.33f, 0f)
                lineToRelative(0f, 21.33f)
                lineToRelative(21.33f, 0f)
                lineToRelative(0f, 21.33f)
                lineToRelative(21.33f, 0f)
                lineToRelative(0f, -21.33f)
                lineToRelative(21.33f, 0f)
                lineToRelative(0f, -21.33f)
                close()
            }
        }.build()

        return _Malta!!
    }

@Suppress("ObjectPropertyName")
private var _Malta: ImageVector? = null
