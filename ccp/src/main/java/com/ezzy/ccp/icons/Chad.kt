package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EzzyIcons.Chad: ImageVector
    get() {
        if (_Chad != null) {
            return _Chad!!
        }
        _Chad = ImageVector.Builder(
            name = "Chad",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f
        ).apply {
            path(fill = SolidColor(Color(0xFFFFDA44))) {
                moveTo(0f, 85.33f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(341.34f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFF0052B4))) {
                moveTo(0f, 85.33f)
                horizontalLineToRelative(170.66f)
                verticalLineToRelative(341.34f)
                horizontalLineToRelative(-170.66f)
                close()
            }
            path(fill = SolidColor(Color(0xFFD80027))) {
                moveTo(341.34f, 85.33f)
                horizontalLineToRelative(170.66f)
                verticalLineToRelative(341.34f)
                horizontalLineToRelative(-170.66f)
                close()
            }
        }.build()

        return _Chad!!
    }

@Suppress("ObjectPropertyName")
private var _Chad: ImageVector? = null
