package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import kotlin.Suppress

val EzzyIcons.Chile: ImageVector
    get() {
        if (_Chile != null) {
            return _Chile!!
        }
        _Chile = ImageVector.Builder(
            name = "Chile",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 64f,
            viewportHeight = 64f
        ).apply {
            path(fill = SolidColor(Color(0xFFDD0000))) {
                moveToRelative(60f, 50.13f)
                lineToRelative(-56f, -0f)
                lineToRelative(-0f, -18.13f)
                lineToRelative(56f, -0f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFFFFF))) {
                moveToRelative(60f, 32f)
                lineToRelative(-56f, -0f)
                lineToRelative(-0f, -18.13f)
                lineToRelative(56f, -0f)
                close()
            }
            path(fill = SolidColor(Color(0xFF0039A6))) {
                moveToRelative(23.13f, 32f)
                lineToRelative(-19.13f, -0f)
                lineToRelative(-0f, -18.13f)
                lineToRelative(19.13f, -0f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFFFFF))) {
                moveToRelative(13.56f, 25.89f)
                lineToRelative(-3.9f, 3.05f)
                lineToRelative(1.69f, -4.65f)
                lineToRelative(-4.1f, -2.77f)
                lineToRelative(4.95f, 0.17f)
                lineToRelative(1.36f, -4.76f)
                lineToRelative(1.37f, 4.76f)
                lineToRelative(4.95f, -0.17f)
                lineToRelative(-4.11f, 2.77f)
                lineToRelative(1.69f, 4.65f)
                close()
            }
        }.build()

        return _Chile!!
    }

@Suppress("ObjectPropertyName")
private var _Chile: ImageVector? = null
