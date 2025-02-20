package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EzzyIcons.Guinea: ImageVector
    get() {
        if (_Guinea != null) {
            return _Guinea!!
        }
        _Guinea = ImageVector.Builder(
            name = "Guinea",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f
        ).apply {
            path(fill = SolidColor(Color(0xFF73AF00))) {
                moveTo(341.33f, 88.27f)
                horizontalLineToRelative(161.84f)
                curveToRelative(4.88f, 0f, 8.83f, 3.95f, 8.83f, 8.83f)
                verticalLineToRelative(317.79f)
                curveToRelative(0f, 4.88f, -3.95f, 8.83f, -8.83f, 8.83f)
                horizontalLineTo(341.33f)
                verticalLineTo(88.27f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFE15A))) {
                moveTo(170.67f, 88.28f)
                horizontalLineToRelative(170.67f)
                verticalLineToRelative(335.45f)
                horizontalLineToRelative(-170.67f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFF4B55))) {
                moveTo(8.83f, 88.27f)
                horizontalLineToRelative(161.84f)
                verticalLineToRelative(335.45f)
                horizontalLineTo(8.83f)
                curveToRelative(-4.88f, 0f, -8.83f, -3.95f, -8.83f, -8.83f)
                verticalLineTo(97.1f)
                curveTo(0f, 92.22f, 3.95f, 88.27f, 8.83f, 88.27f)
                close()
            }
        }.build()

        return _Guinea!!
    }

@Suppress("ObjectPropertyName")
private var _Guinea: ImageVector? = null
