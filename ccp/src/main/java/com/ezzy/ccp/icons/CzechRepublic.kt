package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EzzyIcons.CzechRepublic: ImageVector
    get() {
        if (_CzechRepublic!= null) {
            return _CzechRepublic!!
        }
        _CzechRepublic= ImageVector.Builder(
            name = "CzechRepublic",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f
        ).apply {
            path(fill = SolidColor(Color(0xFFF8FAFC))) {
                moveToRelative(16f, 81f)
                lineToRelative(120f, 110f)
                lineToRelative(91.33f, 60f)
                lineToRelative(128.67f, 10f)
                lineToRelative(140f, -10f)
                verticalLineToRelative(-30f)
                verticalLineToRelative(-10f)
                verticalLineToRelative(-60f)
                verticalLineToRelative(-70f)
                close()
            }
            path(fill = SolidColor(Color(0xFFDB002A))) {
                moveToRelative(16f, 431f)
                horizontalLineToRelative(480f)
                verticalLineToRelative(-70f)
                verticalLineToRelative(-70f)
                verticalLineToRelative(-40f)
                horizontalLineToRelative(-268.67f)
                lineToRelative(-82.18f, 60f)
                close()
            }
            path(fill = SolidColor(Color(0xFF0053B5))) {
                moveToRelative(16f, 431f)
                verticalLineToRelative(-350f)
                lineToRelative(211.33f, 170f)
                close()
            }
        }.build()

        return _CzechRepublic!!
    }

@Suppress("ObjectPropertyName")
private var _CzechRepublic: ImageVector? = null
