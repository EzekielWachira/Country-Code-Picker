package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EzzyIcons.Ghana: ImageVector
    get() {
        if (_Ghana != null) {
            return _Ghana!!
        }
        _Ghana = ImageVector.Builder(
            name = "Ghana",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f
        ).apply {
            path(fill = SolidColor(Color(0xFFFFDA44))) {
                moveTo(0f, 85.34f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(341.33f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFFD80027))) {
                moveTo(0f, 85.34f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(113.78f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFF496E2D))) {
                moveTo(0f, 312.89f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(113.78f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(255.88f, 199.11f)
                lineToRelative(14.12f, 43.46f)
                lineToRelative(40.37f, 3.2f)
                lineToRelative(-36.97f, 26.86f)
                lineToRelative(19.45f, 40.26f)
                lineToRelative(-36.97f, -26.86f)
                lineToRelative(-36.97f, 26.86f)
                lineToRelative(14.12f, -43.46f)
                lineToRelative(-36.97f, -26.86f)
                lineToRelative(45.7f, 0f)
                close()
            }
        }.build()

        return _Ghana!!
    }

@Suppress("ObjectPropertyName")
private var _Ghana: ImageVector? = null
