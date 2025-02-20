package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import kotlin.Suppress

val EzzyIcons.Tanzania2: ImageVector
    get() {
        if (_Tanzania2 != null) {
            return _Tanzania2!!
        }
        _Tanzania2 = ImageVector.Builder(
            name = "Tanzania2",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f
        ).apply {
            path(fill = SolidColor(Color(0xFF338AF3))) {
                moveTo(0f, 85.34f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(341.33f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFF6DA544))) {
                moveTo(0f, 426.66f)
                lineToRelative(0f, -341.33f)
                lineToRelative(512f, 0f)
            }
            path(fill = SolidColor(Color(0xFFFFDA44))) {
                moveTo(512f, 152.22f)
                lineToRelative(0f, -66.89f)
                lineToRelative(-100.33f, 0f)
                lineToRelative(-411.67f, 274.44f)
                lineToRelative(0f, 66.88f)
                lineToRelative(100.33f, 0f)
                close()
            }
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(512f, 85.34f)
                lineToRelative(0f, 40.13f)
                lineToRelative(-451.81f, 301.2f)
                lineToRelative(-60.19f, 0f)
                lineToRelative(0f, -40.13f)
                lineToRelative(451.81f, -301.2f)
                close()
            }
        }.build()

        return _Tanzania2!!
    }

@Suppress("ObjectPropertyName")
private var _Tanzania2: ImageVector? = null
