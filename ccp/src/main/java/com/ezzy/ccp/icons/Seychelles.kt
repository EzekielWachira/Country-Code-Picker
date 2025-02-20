package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EzzyIcons.Seychelles: ImageVector
    get() {
        if (_Seychelles != null) {
            return _Seychelles!!
        }
        _Seychelles = ImageVector.Builder(
            name = "Seychelles",
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
            path(fill = SolidColor(Color(0xFFFFDA44))) {
                moveTo(235.45f, 85.34f)
                lineToRelative(-235.45f, 341.33f)
                lineToRelative(427.35f, -341.33f)
                close()
            }
            path(fill = SolidColor(Color(0xFF6DA544))) {
                moveTo(512f, 329.39f)
                lineToRelative(-512f, 97.27f)
                lineToRelative(512f, 0f)
                close()
            }
            path(fill = SolidColor(Color(0xFFD80027))) {
                moveTo(512f, 85.34f)
                lineToRelative(-84.65f, 0f)
                lineToRelative(-427.35f, 341.33f)
                lineToRelative(512f, -204.51f)
                close()
            }
            path(fill = SolidColor(Color(0xFF0052B4))) {
                moveTo(0f, 85.34f)
                lineToRelative(0f, 341.33f)
                lineToRelative(235.45f, -341.33f)
                close()
            }
        }.build()

        return _Seychelles!!
    }

@Suppress("ObjectPropertyName")
private var _Seychelles: ImageVector? = null
