package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EzzyIcons.Greece: ImageVector
    get() {
        if (_Greece != null) {
            return _Greece!!
        }
        _Greece = ImageVector.Builder(
            name = "Greece",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f
        ).apply {
            path(fill = SolidColor(Color(0xFFF5F5F5))) {
                moveTo(0f, 125.55f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(37.27f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFFF5F5F5))) {
                moveTo(0f, 200.09f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(37.27f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFFF5F5F5))) {
                moveTo(0f, 274.64f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(37.27f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFF41479B))) {
                moveTo(0f, 311.91f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(37.27f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFFF5F5F5))) {
                moveTo(0f, 349.18f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(37.27f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFF41479B))) {
                moveTo(8.83f, 423.73f)
                horizontalLineToRelative(494.35f)
                curveToRelative(4.88f, 0f, 8.83f, -3.95f, 8.83f, -8.83f)
                verticalLineToRelative(-28.44f)
                horizontalLineTo(0f)
                verticalLineToRelative(28.44f)
                curveTo(0f, 419.77f, 3.95f, 423.73f, 8.83f, 423.73f)
                close()
            }
            path(fill = SolidColor(Color(0xFF41479B))) {
                moveTo(512f, 125.55f)
                verticalLineTo(97.1f)
                curveToRelative(0f, -4.88f, -3.95f, -8.83f, -8.83f, -8.83f)
                horizontalLineTo(8.83f)
                curveTo(3.95f, 88.28f, 0f, 92.23f, 0f, 97.1f)
                verticalLineToRelative(65.72f)
                verticalLineToRelative(37.27f)
                verticalLineToRelative(74.54f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(-37.27f)
                horizontalLineTo(176.55f)
                verticalLineToRelative(-37.27f)
                horizontalLineTo(512f)
                verticalLineToRelative(-37.27f)
                horizontalLineTo(176.55f)
                verticalLineToRelative(-37.27f)
                curveTo(176.55f, 125.55f, 512f, 125.55f, 512f, 125.55f)
                close()
            }
            path(fill = SolidColor(Color(0xFFF5F5F5))) {
                moveTo(176.55f, 162.82f)
                lineToRelative(-69.64f, 0f)
                lineToRelative(0f, -74.54f)
                lineToRelative(-37.27f, 0f)
                lineToRelative(0f, 74.54f)
                lineToRelative(-69.64f, 0f)
                lineToRelative(0f, 37.27f)
                lineToRelative(69.64f, 0f)
                lineToRelative(0f, 74.54f)
                lineToRelative(37.27f, 0f)
                lineToRelative(0f, -74.54f)
                lineToRelative(69.64f, 0f)
                close()
            }
        }.build()

        return _Greece!!
    }

@Suppress("ObjectPropertyName")
private var _Greece: ImageVector? = null
