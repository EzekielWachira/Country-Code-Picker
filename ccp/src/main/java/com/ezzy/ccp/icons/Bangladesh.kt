package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EzzyIcons.Bangladesh: ImageVector
    get() {
        if (_Bangladesh != null) {
            return _Bangladesh!!
        }
        _Bangladesh = ImageVector.Builder(
            name = "Bangladesh",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 64f,
            viewportHeight = 64f
        ).apply {
            path(fill = SolidColor(Color(0xFF006837))) {
                moveToRelative(4f, 13.87f)
                horizontalLineToRelative(56f)
                verticalLineToRelative(36.26f)
                horizontalLineToRelative(-56f)
                close()
            }
            path(fill = SolidColor(Color(0xFFDF0000))) {
                moveTo(20.08f, 32f)
                arcToRelative(11.92f, 11.68f, 0f, isMoreThanHalf = true, isPositiveArc = false, 23.84f, 0f)
                arcToRelative(11.92f, 11.68f, 0f, isMoreThanHalf = true, isPositiveArc = false, -23.84f, 0f)
                close()
            }
        }.build()

        return _Bangladesh!!
    }

@Suppress("ObjectPropertyName")
private var _Bangladesh: ImageVector? = null
