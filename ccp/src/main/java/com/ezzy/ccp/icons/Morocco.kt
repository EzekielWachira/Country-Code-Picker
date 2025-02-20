package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EzzyIcons.Morocco: ImageVector
    get() {
        if (_Morocco != null) {
            return _Morocco!!
        }
        _Morocco = ImageVector.Builder(
            name = "Morocco",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f
        ).apply {
            path(fill = SolidColor(Color(0xFFD80027))) {
                moveTo(0f, 85.3f)
                lineToRelative(0f, 170.7f)
                lineToRelative(0f, 170.7f)
                lineToRelative(512f, 0f)
                lineToRelative(0f, -170.7f)
                lineToRelative(0f, -170.7f)
                close()
            }
            path(fill = SolidColor(Color(0xFF6DA544))) {
                moveTo(352f, 226.5f)
                horizontalLineToRelative(-73.3f)
                lineTo(256f, 156.7f)
                lineToRelative(-22.7f, 69.7f)
                horizontalLineTo(160f)
                lineToRelative(59.3f, 43.1f)
                lineToRelative(-22.7f, 69.7f)
                lineToRelative(59.3f, -43.1f)
                lineToRelative(59.3f, 43.1f)
                lineToRelative(-22.7f, -69.7f)
                lineTo(352f, 226.5f)
                close()
                moveTo(235.9f, 264.2f)
                lineToRelative(7.7f, -23.6f)
                horizontalLineToRelative(24.8f)
                lineToRelative(7.7f, 23.6f)
                lineToRelative(0f, 0f)
                verticalLineToRelative(0f)
                lineTo(256f, 278.8f)
                lineTo(235.9f, 264.2f)
                lineTo(235.9f, 264.2f)
                lineTo(235.9f, 264.2f)
                close()
                moveTo(263.8f, 226.5f)
                horizontalLineToRelative(-15.6f)
                lineToRelative(7.8f, -24f)
                lineTo(263.8f, 226.5f)
                close()
                moveTo(288.1f, 255.5f)
                lineToRelative(-4.8f, -14.9f)
                horizontalLineToRelative(25.3f)
                lineTo(288.1f, 255.5f)
                close()
                moveTo(228.7f, 240.6f)
                lineToRelative(-4.8f, 14.9f)
                lineToRelative(-20.4f, -14.9f)
                horizontalLineTo(228.7f)
                close()
                moveTo(223.5f, 302.4f)
                lineToRelative(7.8f, -24f)
                lineToRelative(12.6f, 9.2f)
                lineTo(223.5f, 302.4f)
                close()
                moveTo(268f, 287.5f)
                lineToRelative(12.6f, -9.2f)
                lineToRelative(7.8f, 24f)
                lineTo(268f, 287.5f)
                close()
            }
        }.build()

        return _Morocco!!
    }

@Suppress("ObjectPropertyName")
private var _Morocco: ImageVector? = null
