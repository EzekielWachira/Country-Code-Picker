package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import kotlin.Suppress

val EzzyIcons.Rwanda: ImageVector
    get() {
        if (_Rwanda != null) {
            return _Rwanda!!
        }
        _Rwanda = ImageVector.Builder(
            name = "Rwanda",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f
        ).apply {
            path(fill = SolidColor(Color(0xFF338AF3))) {
                moveTo(0f, 85.33f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(341.34f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFDA44))) {
                moveTo(0f, 255.99f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(81.62f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFF496E2D))) {
                moveTo(0f, 337.61f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(89.04f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFDA44))) {
                moveTo(278.26f, 185.21f)
                lineToRelative(20.84f, 9.8f)
                lineToRelative(-11.1f, 20.19f)
                lineToRelative(22.63f, -4.33f)
                lineToRelative(2.87f, 22.86f)
                lineToRelative(15.76f, -16.82f)
                lineToRelative(15.77f, 16.82f)
                lineToRelative(2.87f, -22.86f)
                lineToRelative(22.63f, 4.33f)
                lineToRelative(-11.1f, -20.19f)
                lineToRelative(20.84f, -9.8f)
                lineToRelative(-20.84f, -9.8f)
                lineToRelative(11.1f, -20.19f)
                lineToRelative(-22.63f, 4.33f)
                lineToRelative(-2.87f, -22.86f)
                lineToRelative(-15.77f, 16.82f)
                lineToRelative(-15.77f, -16.82f)
                lineToRelative(-2.87f, 22.86f)
                lineToRelative(-22.63f, -4.33f)
                lineToRelative(11.1f, 20.19f)
                close()
            }
        }.build()

        return _Rwanda!!
    }

@Suppress("ObjectPropertyName")
private var _Rwanda: ImageVector? = null
