package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EzzyIcons.Bahamas: ImageVector
    get() {
        if (_Bahamas != null) {
            return _Bahamas!!
        }
        _Bahamas = ImageVector.Builder(
            name = "Bahamas",
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
            path(fill = SolidColor(Color(0xFF338AF3))) {
                moveTo(0f, 85.34f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(113.78f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFF338AF3))) {
                moveTo(0f, 312.89f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(113.78f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(256f, 256.01f)
                lineToRelative(-256f, 170.66f)
                lineToRelative(0f, -341.34f)
                close()
            }
        }.build()

        return _Bahamas!!
    }

@Suppress("ObjectPropertyName")
private var _Bahamas: ImageVector? = null
