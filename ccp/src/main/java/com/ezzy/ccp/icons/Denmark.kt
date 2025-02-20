package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import kotlin.Suppress

val EzzyIcons.Denmark: ImageVector
    get() {
        if (_Denmark != null) {
            return _Denmark!!
        }
        _Denmark = ImageVector.Builder(
            name = "Denmark",
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
            path(fill = SolidColor(Color(0xFFF0F0F0))) {
                moveTo(192f, 85.33f)
                lineToRelative(-64f, 0f)
                lineToRelative(0f, 138.67f)
                lineToRelative(-128f, 0f)
                lineToRelative(0f, 64f)
                lineToRelative(128f, 0f)
                lineToRelative(0f, 138.67f)
                lineToRelative(64f, 0f)
                lineToRelative(0f, -138.67f)
                lineToRelative(320f, 0f)
                lineToRelative(0f, -64f)
                lineToRelative(-320f, 0f)
                close()
            }
        }.build()

        return _Denmark!!
    }

@Suppress("ObjectPropertyName")
private var _Denmark: ImageVector? = null
