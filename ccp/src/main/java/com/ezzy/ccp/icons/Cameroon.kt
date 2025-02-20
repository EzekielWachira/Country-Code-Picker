package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import kotlin.Suppress

val EzzyIcons.Cameroon: ImageVector
    get() {
        if (_Cameroon != null) {
            return _Cameroon!!
        }
        _Cameroon = ImageVector.Builder(
            name = "Cameroon",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f
        ).apply {
            path(fill = SolidColor(Color(0xFFD80027))) {
                moveTo(0f, 85.33f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(341.34f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFF496E2D))) {
                moveTo(0f, 85.33f)
                horizontalLineToRelative(170.66f)
                verticalLineToRelative(341.34f)
                horizontalLineToRelative(-170.66f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFDA44))) {
                moveTo(341.34f, 85.33f)
                horizontalLineToRelative(170.66f)
                verticalLineToRelative(341.34f)
                horizontalLineToRelative(-170.66f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFDA44))) {
                moveTo(256f, 208.5f)
                lineToRelative(11.79f, 36.28f)
                lineToRelative(38.15f, 0f)
                lineToRelative(-30.86f, 22.42f)
                lineToRelative(11.79f, 36.28f)
                lineToRelative(-30.86f, -22.42f)
                lineToRelative(-30.86f, 22.42f)
                lineToRelative(11.79f, -36.28f)
                lineToRelative(-30.86f, -22.42f)
                lineToRelative(38.15f, 0f)
                close()
            }
        }.build()

        return _Cameroon!!
    }

@Suppress("ObjectPropertyName")
private var _Cameroon: ImageVector? = null
