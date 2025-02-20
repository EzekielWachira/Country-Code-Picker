package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EzzyIcons.Tonga: ImageVector
    get() {
        if (_Tonga != null) {
            return _Tonga!!
        }
        _Tonga = ImageVector.Builder(
            name = "Tonga",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f
        ).apply {
            path(fill = SolidColor(Color(0xFFD80027))) {
                moveTo(0f, 85.33f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(341.34f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFFF0F0F0))) {
                moveTo(0f, 85.33f)
                horizontalLineToRelative(256f)
                verticalLineToRelative(170.66f)
                horizontalLineToRelative(-256f)
                close()
            }
            path(fill = SolidColor(Color(0xFFD80027))) {
                moveTo(141.36f, 157.3f)
                lineToRelative(0f, -26.71f)
                lineToRelative(-26.71f, 0f)
                lineToRelative(0f, 26.71f)
                lineToRelative(-26.71f, 0f)
                lineToRelative(0f, 26.71f)
                lineToRelative(26.71f, 0f)
                lineToRelative(0f, 26.71f)
                lineToRelative(26.71f, 0f)
                lineToRelative(0f, -26.71f)
                lineToRelative(26.71f, 0f)
                lineToRelative(0f, -26.71f)
                close()
            }
        }.build()

        return _Tonga!!
    }

@Suppress("ObjectPropertyName")
private var _Tonga: ImageVector? = null
