package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import kotlin.Suppress

val EzzyIcons.Botswana: ImageVector
    get() {
        if (_Botswana != null) {
            return _Botswana!!
        }
        _Botswana = ImageVector.Builder(
            name = "Botswana",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f
        ).apply {
            path(fill = SolidColor(Color(0xFFF0F0F0))) {
                moveTo(0f, 85.34f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(341.33f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFF338AF3))) {
                moveTo(0f, 322.78f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(103.88f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFF338AF3))) {
                moveTo(0f, 85.34f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(104.51f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(0f, 210.88f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(89.66f)
                horizontalLineToRelative(-512f)
                close()
            }
        }.build()

        return _Botswana!!
    }

@Suppress("ObjectPropertyName")
private var _Botswana: ImageVector? = null
