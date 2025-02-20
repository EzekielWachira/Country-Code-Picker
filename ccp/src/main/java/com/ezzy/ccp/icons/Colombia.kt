package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EzzyIcons.Colombia: ImageVector
    get() {
        if (_Colombia != null) {
            return _Colombia!!
        }
        _Colombia = ImageVector.Builder(
            name = "Colombia",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f
        ).apply {
            path(fill = SolidColor(Color(0xFFFFDA44))) {
                moveTo(0f, 85.34f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(341.33f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFFD80027))) {
                moveTo(0f, 343.1f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(83.57f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFF0052B4))) {
                moveTo(0f, 256f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(87.1f)
                horizontalLineToRelative(-512f)
                close()
            }
        }.build()

        return _Colombia!!
    }

@Suppress("ObjectPropertyName")
private var _Colombia: ImageVector? = null
