package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import kotlin.Suppress

val EzzyIcons.Argentina: ImageVector
    get() {
        if (_Argentina != null) {
            return _Argentina!!
        }
        _Argentina = ImageVector.Builder(
            name = "Argentina",
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
            path(fill = SolidColor(Color(0xFFFFDA44))) {
                moveTo(296.81f, 256f)
                lineToRelative(-16.68f, 7.84f)
                lineToRelative(8.88f, 16.15f)
                lineToRelative(-18.11f, -3.46f)
                lineToRelative(-2.29f, 18.29f)
                lineToRelative(-12.61f, -13.45f)
                lineToRelative(-12.61f, 13.45f)
                lineToRelative(-2.29f, -18.29f)
                lineToRelative(-18.11f, 3.46f)
                lineToRelative(8.88f, -16.15f)
                lineToRelative(-16.68f, -7.84f)
                lineToRelative(16.68f, -7.84f)
                lineToRelative(-8.88f, -16.15f)
                lineToRelative(18.11f, 3.46f)
                lineToRelative(2.29f, -18.29f)
                lineToRelative(12.61f, 13.45f)
                lineToRelative(12.61f, -13.45f)
                lineToRelative(2.29f, 18.29f)
                lineToRelative(18.11f, -3.46f)
                lineToRelative(-8.88f, 16.15f)
                close()
            }
        }.build()

        return _Argentina!!
    }

@Suppress("ObjectPropertyName")
private var _Argentina: ImageVector? = null
