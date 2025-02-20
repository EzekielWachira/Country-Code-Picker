package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import kotlin.Suppress

val EzzyIcons.ChevronDown: ImageVector
    get() {
        if (_ChevronDown != null) {
            return _ChevronDown!!
        }
        _ChevronDown = ImageVector.Builder(
            name = "ChevronDown",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 128f,
            viewportHeight = 128f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveToRelative(64f, 88f)
                curveToRelative(-1.02f, 0f, -2.05f, -0.39f, -2.83f, -1.17f)
                lineToRelative(-40f, -40f)
                curveToRelative(-1.56f, -1.56f, -1.56f, -4.09f, 0f, -5.66f)
                reflectiveCurveToRelative(4.09f, -1.56f, 5.66f, 0f)
                lineToRelative(37.17f, 37.17f)
                lineToRelative(37.17f, -37.17f)
                curveToRelative(1.56f, -1.56f, 4.09f, -1.56f, 5.66f, 0f)
                reflectiveCurveToRelative(1.56f, 4.09f, 0f, 5.66f)
                lineToRelative(-40f, 40f)
                curveToRelative(-0.78f, 0.78f, -1.8f, 1.17f, -2.83f, 1.17f)
                close()
            }
        }.build()

        return _ChevronDown!!
    }

@Suppress("ObjectPropertyName")
private var _ChevronDown: ImageVector? = null
