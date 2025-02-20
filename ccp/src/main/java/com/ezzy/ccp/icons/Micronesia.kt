package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EzzyIcons.Micronesia: ImageVector
    get() {
        if (_Micronesia != null) {
            return _Micronesia!!
        }
        _Micronesia = ImageVector.Builder(
            name = "Micronesia",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f
        ).apply {
            path(fill = SolidColor(Color(0xFF338AF3))) {
                moveTo(0f, 85.33f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(341.34f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFFF0F0F0))) {
                moveTo(256f, 159.53f)
                lineToRelative(7.37f, 22.67f)
                lineToRelative(23.84f, 0f)
                lineToRelative(-19.29f, 14.02f)
                lineToRelative(7.37f, 22.67f)
                lineToRelative(-19.29f, -14.01f)
                lineToRelative(-19.29f, 14.01f)
                lineToRelative(7.37f, -22.67f)
                lineToRelative(-19.29f, -14.02f)
                lineToRelative(23.84f, 0f)
                close()
            }
            path(fill = SolidColor(Color(0xFFF0F0F0))) {
                moveTo(159.54f, 256f)
                lineToRelative(22.67f, -7.37f)
                lineToRelative(0f, -23.84f)
                lineToRelative(14.01f, 19.29f)
                lineToRelative(22.67f, -7.37f)
                lineToRelative(-14.01f, 19.29f)
                lineToRelative(14.01f, 19.29f)
                lineToRelative(-22.67f, -7.37f)
                lineToRelative(-14.01f, 19.29f)
                lineToRelative(0f, -23.84f)
                close()
            }
            path(fill = SolidColor(Color(0xFFF0F0F0))) {
                moveTo(256f, 352.46f)
                lineToRelative(-7.37f, -22.67f)
                lineToRelative(-23.84f, 0f)
                lineToRelative(19.29f, -14.02f)
                lineToRelative(-7.37f, -22.67f)
                lineToRelative(19.29f, 14.01f)
                lineToRelative(19.29f, -14.01f)
                lineToRelative(-7.37f, 22.67f)
                lineToRelative(19.29f, 14.02f)
                lineToRelative(-23.84f, 0f)
                close()
            }
            path(fill = SolidColor(Color(0xFFF0F0F0))) {
                moveTo(352.46f, 255.99f)
                lineToRelative(-22.67f, 7.37f)
                lineToRelative(0f, 23.84f)
                lineToRelative(-14.02f, -19.29f)
                lineToRelative(-22.67f, 7.37f)
                lineToRelative(14.01f, -19.29f)
                lineToRelative(-14.01f, -19.29f)
                lineToRelative(22.67f, 7.37f)
                lineToRelative(14.02f, -19.29f)
                lineToRelative(0f, 23.84f)
                close()
            }
        }.build()

        return _Micronesia!!
    }

@Suppress("ObjectPropertyName")
private var _Micronesia: ImageVector? = null
