package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import kotlin.Suppress

val EzzyIcons.SouthAfrica: ImageVector
    get() {
        if (_SouthAfrica != null) {
            return _SouthAfrica!!
        }
        _SouthAfrica = ImageVector.Builder(
            name = "SouthAfrica",
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
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(114.02f, 256f)
                lineToRelative(-114.02f, -114.08f)
                lineToRelative(0f, 228.17f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFDA44))) {
                moveTo(161.19f, 256f)
                lineToRelative(-161.19f, -161.3f)
                lineToRelative(0f, 47.23f)
                lineToRelative(114.02f, 114.08f)
                lineToRelative(-114.02f, 114.1f)
                lineToRelative(0f, 47.14f)
                close()
            }
            path(fill = SolidColor(Color(0xFF6DA544))) {
                moveTo(509.83f, 289.39f)
                lineTo(509.83f, 289.39f)
                curveToRelative(0.06f, -0.44f, 0.8f, -0.88f, 2.17f, -1.32f)
                verticalLineTo(256f)
                verticalLineToRelative(-33.39f)
                horizontalLineToRelative(-2.17f)
                horizontalLineTo(222.6f)
                lineTo(85.33f, 85.34f)
                horizontalLineTo(0f)
                verticalLineTo(94.7f)
                lineTo(161.19f, 256f)
                lineTo(0f, 417.23f)
                verticalLineToRelative(9.43f)
                horizontalLineToRelative(85.33f)
                lineToRelative(137.27f, -137.27f)
                horizontalLineTo(509.83f)
                close()
            }
            path(fill = SolidColor(Color(0xFF0052B4))) {
                moveTo(503.18f, 322.78f)
                lineToRelative(-266.75f, 0f)
                lineToRelative(-103.88f, 103.88f)
                lineToRelative(379.45f, 0f)
                lineToRelative(0f, -103.88f)
                close()
            }
            path(fill = SolidColor(Color(0xFFD80027))) {
                moveTo(503.18f, 189.22f)
                lineToRelative(8.82f, 0f)
                lineToRelative(0f, -103.88f)
                lineToRelative(-379.45f, 0f)
                lineToRelative(103.88f, 103.88f)
                close()
            }
        }.build()

        return _SouthAfrica!!
    }

@Suppress("ObjectPropertyName")
private var _SouthAfrica: ImageVector? = null
