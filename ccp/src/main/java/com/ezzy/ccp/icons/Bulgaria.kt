package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EzzyIcons.Bulgaria: ImageVector
    get() {
        if (_Bulgaria != null) {
            return _Bulgaria!!
        }
        _Bulgaria = ImageVector.Builder(
            name = "Bulgaria",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 100f,
            viewportHeight = 100f
        ).apply {
            path(fill = SolidColor(Color(0xFFF1F1F1))) {
                moveToRelative(7.89f, 18.31f)
                verticalLineToRelative(21.13f)
                lineToRelative(42.1f, 3f)
                lineToRelative(42.1f, -3f)
                verticalLineToRelative(-21.13f)
                close()
            }
            path(fill = SolidColor(Color(0xFFD7141A))) {
                moveToRelative(7.89f, 60.56f)
                verticalLineToRelative(21.13f)
                horizontalLineToRelative(84.21f)
                verticalLineToRelative(-21.13f)
                lineToRelative(-42.1f, -3f)
                close()
            }
            path(fill = SolidColor(Color(0xFF00966E))) {
                moveToRelative(7.89f, 39.44f)
                horizontalLineToRelative(84.21f)
                verticalLineToRelative(21.12f)
                horizontalLineToRelative(-84.21f)
                close()
            }
        }.build()

        return _Bulgaria!!
    }

@Suppress("ObjectPropertyName")
private var _Bulgaria: ImageVector? = null
