package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EzzyIcons.France: ImageVector
    get() {
        if (_France != null) {
            return _France!!
        }
        _France = ImageVector.Builder(
            name = "France",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f
        ).apply {
            path(fill = SolidColor(Color(0xFF41479B))) {
                moveTo(170.67f, 423.72f)
                horizontalLineTo(8.83f)
                curveToRelative(-4.88f, 0f, -8.83f, -3.95f, -8.83f, -8.83f)
                verticalLineTo(97.1f)
                curveToRelative(0f, -4.88f, 3.95f, -8.83f, 8.83f, -8.83f)
                horizontalLineToRelative(161.84f)
                verticalLineTo(423.72f)
                close()
            }
            path(fill = SolidColor(Color(0xFFF5F5F5))) {
                moveTo(170.67f, 88.28f)
                horizontalLineToRelative(170.67f)
                verticalLineToRelative(335.45f)
                horizontalLineToRelative(-170.67f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFF4B55))) {
                moveTo(503.17f, 423.72f)
                horizontalLineTo(341.33f)
                verticalLineTo(88.27f)
                horizontalLineToRelative(161.84f)
                curveToRelative(4.88f, 0f, 8.83f, 3.95f, 8.83f, 8.83f)
                verticalLineToRelative(317.79f)
                curveTo(512f, 419.77f, 508.05f, 423.72f, 503.17f, 423.72f)
                close()
            }
        }.build()

        return _France!!
    }

@Suppress("ObjectPropertyName")
private var _France: ImageVector? = null
