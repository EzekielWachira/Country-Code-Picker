package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EzzyIcons.Senegal: ImageVector
    get() {
        if (_Senegal != null) {
            return _Senegal!!
        }
        _Senegal = ImageVector.Builder(
            name = "Senegal",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f
        ).apply {
            path(fill = SolidColor(Color(0xFFFFDA44))) {
                moveTo(0f, 85.33f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(341.33f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFFD80027))) {
                moveTo(330.21f, 85.33f)
                horizontalLineToRelative(181.79f)
                verticalLineToRelative(341.34f)
                horizontalLineToRelative(-181.79f)
                close()
            }
            path(fill = SolidColor(Color(0xFF496E2D))) {
                moveTo(0f, 85.33f)
                horizontalLineToRelative(181.79f)
                verticalLineToRelative(341.34f)
                horizontalLineToRelative(-181.79f)
                close()
            }
            path(fill = SolidColor(Color(0xFF496E2D))) {
                moveTo(256f, 196.63f)
                lineToRelative(14.73f, 45.35f)
                lineToRelative(47.68f, 0f)
                lineToRelative(-38.58f, 28.03f)
                lineToRelative(14.73f, 45.35f)
                lineToRelative(-38.58f, -28.03f)
                lineToRelative(-38.58f, 28.03f)
                lineToRelative(14.74f, -45.35f)
                lineToRelative(-38.58f, -28.03f)
                lineToRelative(47.68f, 0f)
                close()
            }
        }.build()

        return _Senegal!!
    }

@Suppress("ObjectPropertyName")
private var _Senegal: ImageVector? = null
