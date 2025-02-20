package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EzzyIcons.Canada: ImageVector
    get() {
        if (_Canada != null) {
            return _Canada!!
        }
        _Canada = ImageVector.Builder(
            name = "Canada",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f
        ).apply {
            path(fill = SolidColor(Color(0xFFF0F0F0))) {
                moveTo(0f, 85.33f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(341.33f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFFD80027))) {
                moveTo(0f, 85.33f)
                horizontalLineToRelative(170.66f)
                verticalLineToRelative(341.34f)
                horizontalLineToRelative(-170.66f)
                close()
            }
            path(fill = SolidColor(Color(0xFFD80027))) {
                moveTo(341.34f, 85.33f)
                horizontalLineToRelative(170.66f)
                verticalLineToRelative(341.34f)
                horizontalLineToRelative(-170.66f)
                close()
            }
            path(fill = SolidColor(Color(0xFFD80027))) {
                moveTo(288f, 279.7f)
                lineToRelative(32f, -16f)
                lineToRelative(-16f, -8f)
                lineToRelative(0f, -16f)
                lineToRelative(-32f, 16f)
                lineToRelative(16f, -32f)
                lineToRelative(-16f, 0f)
                lineToRelative(-16f, -24f)
                lineToRelative(-16f, 24f)
                lineToRelative(-16f, 0f)
                lineToRelative(16f, 32f)
                lineToRelative(-32f, -16f)
                lineToRelative(0f, 16f)
                lineToRelative(-16f, 8f)
                lineToRelative(32f, 16f)
                lineToRelative(-8f, 16f)
                lineToRelative(32f, 0f)
                lineToRelative(0f, 24f)
                lineToRelative(16f, 0f)
                lineToRelative(0f, -24f)
                lineToRelative(32f, 0f)
                close()
            }
        }.build()

        return _Canada!!
    }

@Suppress("ObjectPropertyName")
private var _Canada: ImageVector? = null
