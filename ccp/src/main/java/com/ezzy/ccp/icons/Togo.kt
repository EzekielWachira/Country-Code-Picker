package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EzzyIcons.Togo: ImageVector
    get() {
        if (_Togo != null) {
            return _Togo!!
        }
        _Togo = ImageVector.Builder(
            name = "Togo",
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
            path(fill = SolidColor(Color(0xFF496E2D))) {
                moveTo(0f, 85.34f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(68.26f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFF496E2D))) {
                moveTo(0f, 358.4f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(68.26f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFF496E2D))) {
                moveTo(0f, 221.86f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(68.26f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFFD80027))) {
                moveTo(0f, 85.34f)
                horizontalLineToRelative(204.05f)
                verticalLineToRelative(204.05f)
                horizontalLineToRelative(-204.05f)
                close()
            }
            path(fill = SolidColor(Color(0xFFF0F0F0))) {
                moveTo(102.03f, 133.94f)
                lineToRelative(13.26f, 40.81f)
                lineToRelative(42.92f, 0f)
                lineToRelative(-34.72f, 25.23f)
                lineToRelative(13.26f, 40.81f)
                lineToRelative(-34.72f, -25.22f)
                lineToRelative(-34.72f, 25.22f)
                lineToRelative(13.26f, -40.81f)
                lineToRelative(-34.72f, -25.23f)
                lineToRelative(42.91f, 0f)
                close()
            }
        }.build()

        return _Togo!!
    }

@Suppress("ObjectPropertyName")
private var _Togo: ImageVector? = null
