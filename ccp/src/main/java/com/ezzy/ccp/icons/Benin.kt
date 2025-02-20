package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EzzyIcons.Benin: ImageVector
    get() {
        if (_Benin != null) {
            return _Benin!!
        }
        _Benin = ImageVector.Builder(
            name = "Benin",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f
        ).apply {
            path(fill = SolidColor(Color(0xFF6DA544))) {
                moveTo(0f, 85.34f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(341.33f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFDA44))) {
                moveTo(196.64f, 85.34f)
                horizontalLineToRelative(315.36f)
                verticalLineToRelative(170.66f)
                horizontalLineToRelative(-315.36f)
                close()
            }
            path(fill = SolidColor(Color(0xFFD80027))) {
                moveTo(196.64f, 256f)
                horizontalLineToRelative(315.36f)
                verticalLineToRelative(170.66f)
                horizontalLineToRelative(-315.36f)
                close()
            }
        }.build()

        return _Benin!!
    }

@Suppress("ObjectPropertyName")
private var _Benin: ImageVector? = null
