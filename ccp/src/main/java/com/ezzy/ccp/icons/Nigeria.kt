package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EzzyIcons.Nigeria: ImageVector
    get() {
        if (_Nigeria != null) {
            return _Nigeria!!
        }
        _Nigeria = ImageVector.Builder(
            name = "Nigeria",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 64f,
            viewportHeight = 64f
        ).apply {
            path(fill = SolidColor(Color(0xFF3E8051))) {
                moveToRelative(3f, 11f)
                horizontalLineToRelative(19f)
                verticalLineToRelative(42f)
                horizontalLineToRelative(-19f)
                close()
            }
            path(fill = SolidColor(Color(0xFFF3F2F0))) {
                moveToRelative(22f, 11f)
                horizontalLineToRelative(20f)
                verticalLineToRelative(42f)
                horizontalLineToRelative(-20f)
                close()
            }
            path(fill = SolidColor(Color(0xFF3E8051))) {
                moveToRelative(42f, 11f)
                horizontalLineToRelative(19f)
                verticalLineToRelative(42f)
                horizontalLineToRelative(-19f)
                close()
            }
        }.build()

        return _Nigeria!!
    }

@Suppress("ObjectPropertyName")
private var _Nigeria: ImageVector? = null
