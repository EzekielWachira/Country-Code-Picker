package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EzzyIcons.Italy: ImageVector
    get() {
        if (_Italy != null) {
            return _Italy!!
        }
        _Italy = ImageVector.Builder(
            name = "Italy",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 64f,
            viewportHeight = 64f
        ).apply {
            path(fill = SolidColor(Color(0xFF009A49))) {
                moveToRelative(4f, 13.87f)
                horizontalLineToRelative(18.67f)
                verticalLineToRelative(36.26f)
                horizontalLineToRelative(-18.67f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFFFFF))) {
                moveToRelative(22.67f, 13.87f)
                horizontalLineToRelative(18.67f)
                verticalLineToRelative(36.26f)
                horizontalLineToRelative(-18.67f)
                close()
            }
            path(fill = SolidColor(Color(0xFFCE2B37))) {
                moveToRelative(41.33f, 13.87f)
                horizontalLineToRelative(18.67f)
                verticalLineToRelative(36.26f)
                horizontalLineToRelative(-18.67f)
                close()
            }
        }.build()

        return _Italy!!
    }

@Suppress("ObjectPropertyName")
private var _Italy: ImageVector? = null
