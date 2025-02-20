package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EzzyIcons.Cambodia: ImageVector
    get() {
        if (_Cambodia != null) {
            return _Cambodia!!
        }
        _Cambodia = ImageVector.Builder(
            name = "Cambodia",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f
        ).apply {
            path(fill = SolidColor(Color(0xFFE92B2B))) {
                moveToRelative(32f, 96f)
                horizontalLineToRelative(448f)
                verticalLineToRelative(320f)
                horizontalLineToRelative(-448f)
                close()
            }
            path(fill = SolidColor(Color(0xFF2A2FAF))) {
                moveToRelative(32f, 96f)
                horizontalLineToRelative(448f)
                verticalLineToRelative(88f)
                horizontalLineToRelative(-448f)
                close()
            }
            path(fill = SolidColor(Color(0xFF2A2FAF))) {
                moveToRelative(32f, 328f)
                horizontalLineToRelative(448f)
                verticalLineToRelative(88f)
                horizontalLineToRelative(-448f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFBFBFB))) {
                moveToRelative(184f, 288f)
                horizontalLineToRelative(144f)
                verticalLineToRelative(20f)
                horizontalLineToRelative(-144f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFBFBFB))) {
                moveToRelative(196f, 268f)
                horizontalLineToRelative(120f)
                verticalLineToRelative(20f)
                horizontalLineToRelative(-120f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFBFBFB))) {
                moveToRelative(208f, 232f)
                lineToRelative(12f, -12f)
                lineToRelative(12f, 12f)
                verticalLineToRelative(40f)
                horizontalLineToRelative(-24f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFBFBFB))) {
                moveToRelative(280f, 232f)
                lineToRelative(12f, -12f)
                lineToRelative(12f, 12f)
                verticalLineToRelative(40f)
                horizontalLineToRelative(-24f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFBFBFB))) {
                moveToRelative(244f, 216f)
                lineToRelative(12f, -12f)
                lineToRelative(12f, 12f)
                verticalLineToRelative(56f)
                horizontalLineToRelative(-24f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFBFBFB))) {
                moveToRelative(224f, 248f)
                horizontalLineToRelative(64f)
                verticalLineToRelative(32f)
                horizontalLineToRelative(-64f)
                close()
            }
        }.build()

        return _Cambodia!!
    }

@Suppress("ObjectPropertyName")
private var _Cambodia: ImageVector? = null
