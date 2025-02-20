package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EzzyIcons.Barbados: ImageVector
    get() {
        if (_Barbados != null) {
            return _Barbados!!
        }
        _Barbados = ImageVector.Builder(
            name = "Barbados",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f
        ).apply {
            path(fill = SolidColor(Color(0xFFFFDA44))) {
                moveTo(0f, 85.33f)
                horizontalLineToRelative(512f)
                verticalLineToRelative(341.34f)
                horizontalLineToRelative(-512f)
                close()
            }
            path(fill = SolidColor(Color(0xFF0052B4))) {
                moveTo(0f, 85.33f)
                horizontalLineToRelative(170.66f)
                verticalLineToRelative(341.34f)
                horizontalLineToRelative(-170.66f)
                close()
            }
            path(fill = SolidColor(Color(0xFF0052B4))) {
                moveTo(341.34f, 85.33f)
                horizontalLineToRelative(170.66f)
                verticalLineToRelative(341.34f)
                horizontalLineToRelative(-170.66f)
                close()
            }
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(307.94f, 189.21f)
                lineToRelative(9.95f, 4.98f)
                lineTo(307.94f, 189.21f)
                lineToRelative(-9.95f, -4.98f)
                curveToRelative(-0.6f, 1.2f, -13.6f, 27.65f, -15.72f, 68.05f)
                horizontalLineTo(267.13f)
                verticalLineToRelative(-63.07f)
                lineTo(256f, 174.37f)
                lineToRelative(-11.13f, 14.84f)
                verticalLineToRelative(63.07f)
                horizontalLineToRelative(-15.13f)
                curveToRelative(-2.12f, -40.4f, -15.12f, -66.85f, -15.72f, -68.05f)
                lineToRelative(-19.91f, 9.95f)
                curveToRelative(0.14f, 0.27f, 13.67f, 27.81f, 13.67f, 69.23f)
                verticalLineToRelative(11.13f)
                horizontalLineToRelative(37.1f)
                verticalLineToRelative(63.07f)
                horizontalLineToRelative(22.26f)
                verticalLineToRelative(-63.07f)
                horizontalLineToRelative(37.1f)
                verticalLineToRelative(-11.13f)
                curveToRelative(0f, -21.41f, 3.7f, -39.07f, 6.81f, -50.11f)
                curveToRelative(3.39f, -12.04f, 6.83f, -19.07f, 6.86f, -19.14f)
                lineTo(307.94f, 189.21f)
                close()
            }
        }.build()

        return _Barbados!!
    }

@Suppress("ObjectPropertyName")
private var _Barbados: ImageVector? = null
