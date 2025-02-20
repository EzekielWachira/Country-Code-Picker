package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EzzyIcons.Congo: ImageVector
    get() {
        if (_Congo != null) {
            return _Congo!!
        }
        _Congo = ImageVector.Builder(
            name = "Congo",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f
        ).apply {
            path(fill = SolidColor(Color(0xFFD80027))) {
                moveTo(0f, 85.33f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(341.34f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFF6DA544))) {
                moveTo(443.73f, 85.33f)
                lineToRelative(-341.33f, 341.33f)
                lineToRelative(-102.4f, 0f)
                lineToRelative(0f, -341.33f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFDA44))) {
                moveTo(500.12f, 85.33f)
                lineToRelative(-341.33f, 341.33f)
                lineToRelative(-146.92f, 0f)
                lineToRelative(341.33f, -341.33f)
                close()
            }
        }.build()

        return _Congo!!
    }

@Suppress("ObjectPropertyName")
private var _Congo: ImageVector? = null
