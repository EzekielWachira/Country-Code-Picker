package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EzzyIcons.SierraLeone: ImageVector
    get() {
        if (_SierraLeone != null) {
            return _SierraLeone!!
        }
        _SierraLeone = ImageVector.Builder(
            name = "SierraLeone",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f
        ).apply {
            path(fill = SolidColor(Color(0xFF41479B))) {
                moveTo(0f, 311.91f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(102.99f)
                curveToRelative(0f, 4.88f, -3.95f, 8.83f, -8.83f, 8.83f)
                horizontalLineTo(8.83f)
                curveToRelative(-4.88f, 0f, -8.83f, -3.95f, -8.83f, -8.83f)
                verticalLineTo(311.91f)
                close()
            }
            path(fill = SolidColor(Color(0xFF73AF00))) {
                moveTo(8.83f, 88.28f)
                horizontalLineToRelative(494.35f)
                curveToRelative(4.88f, 0f, 8.83f, 3.95f, 8.83f, 8.83f)
                verticalLineToRelative(102.99f)
                horizontalLineTo(0f)
                verticalLineTo(97.1f)
                curveTo(0f, 92.23f, 3.95f, 88.28f, 8.83f, 88.28f)
                close()
            }
            path(fill = SolidColor(Color(0xFFF5F5F5))) {
                moveTo(0f, 200.09f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(111.81f)
                horizontalLineToRelative(-512f)
                close()
            }
        }.build()

        return _SierraLeone!!
    }

@Suppress("ObjectPropertyName")
private var _SierraLeone: ImageVector? = null
