package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EzzyIcons.Finland: ImageVector
    get() {
        if (_Finland != null) {
            return _Finland!!
        }
        _Finland = ImageVector.Builder(
            name = "Finland",
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
            path(fill = SolidColor(Color(0xFF2E52B2))) {
                moveTo(512f, 234.67f)
                lineToRelative(0f, 42.66f)
                lineToRelative(-328.35f, 0f)
                lineToRelative(0f, 149.34f)
                lineToRelative(-42.67f, 0f)
                lineToRelative(0f, -149.34f)
                lineToRelative(-140.98f, 0f)
                lineToRelative(0f, -42.66f)
                lineToRelative(140.98f, 0f)
                lineToRelative(0f, -149.34f)
                lineToRelative(42.67f, 0f)
                lineToRelative(0f, 149.34f)
                close()
            }
        }.build()

        return _Finland!!
    }

@Suppress("ObjectPropertyName")
private var _Finland: ImageVector? = null
