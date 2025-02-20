package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EzzyIcons.BurkinaFaso: ImageVector
    get() {
        if (_BurkinaFaso != null) {
            return _BurkinaFaso!!
        }
        _BurkinaFaso = ImageVector.Builder(
            name = "BurkinaFaso",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f
        ).apply {
            path(fill = SolidColor(Color(0xFF6DA544))) {
                moveTo(0f, 85.33f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(341.34f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFFD80027))) {
                moveTo(0f, 85.33f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(170.66f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFDA44))) {
                moveTo(256f, 196.63f)
                lineToRelative(12.89f, 39.68f)
                lineToRelative(41.73f, 0f)
                lineToRelative(-33.76f, 24.52f)
                lineToRelative(12.89f, 39.68f)
                lineToRelative(-33.75f, -24.52f)
                lineToRelative(-33.75f, 24.52f)
                lineToRelative(12.89f, -39.68f)
                lineToRelative(-33.76f, -24.52f)
                lineToRelative(41.72f, 0f)
                close()
            }
        }.build()

        return _BurkinaFaso!!
    }

@Suppress("ObjectPropertyName")
private var _BurkinaFaso: ImageVector? = null
