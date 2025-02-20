package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EzzyIcons.UnitedArabEmirates: ImageVector
    get() {
        if (_UnitedArabEmirates != null) {
            return _UnitedArabEmirates!!
        }
        _UnitedArabEmirates = ImageVector.Builder(
            name = "UnitedArabEmirates",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 64f,
            viewportHeight = 64f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveToRelative(60f, 50f)
                lineToRelative(-56f, -0f)
                lineToRelative(-0f, -12f)
                lineToRelative(56f, -0f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFFFFF))) {
                moveToRelative(60f, 38f)
                lineToRelative(-56f, -0f)
                lineToRelative(-0f, -12f)
                lineToRelative(56f, -0f)
                close()
            }
            path(fill = SolidColor(Color(0xFF009900))) {
                moveToRelative(60f, 26f)
                lineToRelative(-56f, -0f)
                lineToRelative(-0f, -12f)
                lineToRelative(56f, -0f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFF0000))) {
                moveToRelative(19.5f, 50f)
                lineToRelative(-15.5f, -0f)
                lineToRelative(-0f, -36f)
                lineToRelative(15.5f, -0f)
                close()
            }
        }.build()

        return _UnitedArabEmirates!!
    }

@Suppress("ObjectPropertyName")
private var _UnitedArabEmirates: ImageVector? = null
