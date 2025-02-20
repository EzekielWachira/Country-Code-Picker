package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EzzyIcons.Bahrain: ImageVector
    get() {
        if (_Bahrain != null) {
            return _Bahrain!!
        }
        _Bahrain = ImageVector.Builder(
            name = "Bahrain",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f
        ).apply {
            path(fill = SolidColor(Color(0xFFD80027))) {
                moveTo(2.22f, 86.08f)
                horizontalLineToRelative(509.78f)
                verticalLineToRelative(339.85f)
                horizontalLineToRelative(-509.78f)
                close()
            }
            path(fill = SolidColor(Color(0xFFF0F0F0))) {
                moveTo(209.46f, 154.97f)
                lineToRelative(-50.24f, 25.27f)
                lineToRelative(50.24f, 25.26f)
                lineToRelative(-50.24f, 25.27f)
                lineToRelative(50.24f, 25.26f)
                lineToRelative(-50.24f, 25.27f)
                lineToRelative(50.24f, 25.26f)
                lineToRelative(-50.24f, 25.27f)
                lineToRelative(50.24f, 25.26f)
                lineToRelative(-50.24f, 25.27f)
                lineToRelative(50.24f, 25.27f)
                lineToRelative(-36.45f, 18.33f)
                lineToRelative(-173.01f, 0f)
                lineToRelative(0f, -339.86f)
                lineToRelative(172.94f, 0f)
                lineToRelative(36.52f, 18.36f)
                lineToRelative(-50.24f, 25.27f)
                close()
            }
        }.build()

        return _Bahrain!!
    }

@Suppress("ObjectPropertyName")
private var _Bahrain: ImageVector? = null
