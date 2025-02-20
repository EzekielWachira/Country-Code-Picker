package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EzzyIcons.Jordan: ImageVector
    get() {
        if (_Jordan != null) {
            return _Jordan!!
        }
        _Jordan = ImageVector.Builder(
            name = "Jordan",
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
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(0f, 85.34f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(113.78f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFF6DA544))) {
                moveTo(0f, 312.89f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(113.78f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFFD80027))) {
                moveTo(256f, 256.01f)
                lineToRelative(-256f, 170.66f)
                lineToRelative(0f, -341.34f)
                close()
            }
            path(fill = SolidColor(Color(0xFFF0F0F0))) {
                moveTo(77.91f, 224.8f)
                lineToRelative(7.88f, 16.47f)
                lineToRelative(17.79f, -4.11f)
                lineToRelative(-7.96f, 16.43f)
                lineToRelative(14.3f, 11.34f)
                lineToRelative(-17.81f, 4.01f)
                lineToRelative(0.05f, 18.26f)
                lineToRelative(-14.24f, -11.42f)
                lineToRelative(-14.24f, 11.42f)
                lineToRelative(0.05f, -18.26f)
                lineToRelative(-17.81f, -4.01f)
                lineToRelative(14.3f, -11.34f)
                lineToRelative(-7.97f, -16.43f)
                lineToRelative(17.79f, 4.11f)
                close()
            }
        }.build()

        return _Jordan!!
    }

@Suppress("ObjectPropertyName")
private var _Jordan: ImageVector? = null
