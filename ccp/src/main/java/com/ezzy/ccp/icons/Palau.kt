package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EzzyIcons.Palau: ImageVector
    get() {
        if (_Palau != null) {
            return _Palau!!
        }
        _Palau = ImageVector.Builder(
            name = "Palau",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f
        ).apply {
            path(fill = SolidColor(Color(0xFF338AF3))) {
                moveTo(0f, 85.33f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(341.34f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFDA44))) {
                moveTo(218.9f, 255.99f)
                moveToRelative(-74.21f, 0f)
                arcToRelative(74.21f, 74.21f, 0f, isMoreThanHalf = true, isPositiveArc = true, 148.41f, 0f)
                arcToRelative(74.21f, 74.21f, 0f, isMoreThanHalf = true, isPositiveArc = true, -148.41f, 0f)
            }
        }.build()

        return _Palau!!
    }

@Suppress("ObjectPropertyName")
private var _Palau: ImageVector? = null
