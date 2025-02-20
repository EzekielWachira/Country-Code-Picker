package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import kotlin.Suppress

val EzzyIcons.List: ImageVector
    get() {
        if (_List != null) {
            return _List!!
        }
        _List = ImageVector.Builder(
            name = "List",
            defaultWidth = 60.123.dp,
            defaultHeight = 60.123.dp,
            viewportWidth = 60.123f,
            viewportHeight = 60.123f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(57.124f, 51.893f)
                horizontalLineTo(16.92f)
                curveToRelative(-1.657f, 0f, -3f, -1.343f, -3f, -3f)
                reflectiveCurveToRelative(1.343f, -3f, 3f, -3f)
                horizontalLineToRelative(40.203f)
                curveToRelative(1.657f, 0f, 3f, 1.343f, 3f, 3f)
                reflectiveCurveTo(58.781f, 51.893f, 57.124f, 51.893f)
                close()
            }
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(57.124f, 33.062f)
                horizontalLineTo(16.92f)
                curveToRelative(-1.657f, 0f, -3f, -1.343f, -3f, -3f)
                reflectiveCurveToRelative(1.343f, -3f, 3f, -3f)
                horizontalLineToRelative(40.203f)
                curveToRelative(1.657f, 0f, 3f, 1.343f, 3f, 3f)
                curveTo(60.124f, 31.719f, 58.781f, 33.062f, 57.124f, 33.062f)
                close()
            }
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(57.124f, 14.231f)
                horizontalLineTo(16.92f)
                curveToRelative(-1.657f, 0f, -3f, -1.343f, -3f, -3f)
                reflectiveCurveToRelative(1.343f, -3f, 3f, -3f)
                horizontalLineToRelative(40.203f)
                curveToRelative(1.657f, 0f, 3f, 1.343f, 3f, 3f)
                reflectiveCurveTo(58.781f, 14.231f, 57.124f, 14.231f)
                close()
            }
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(4.029f, 11.463f)
                moveToRelative(-4.029f, 0f)
                arcToRelative(4.029f, 4.029f, 0f, isMoreThanHalf = true, isPositiveArc = true, 8.058f, 0f)
                arcToRelative(4.029f, 4.029f, 0f, isMoreThanHalf = true, isPositiveArc = true, -8.058f, 0f)
            }
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(4.029f, 30.062f)
                moveToRelative(-4.029f, 0f)
                arcToRelative(4.029f, 4.029f, 0f, isMoreThanHalf = true, isPositiveArc = true, 8.058f, 0f)
                arcToRelative(4.029f, 4.029f, 0f, isMoreThanHalf = true, isPositiveArc = true, -8.058f, 0f)
            }
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(4.029f, 48.661f)
                moveToRelative(-4.029f, 0f)
                arcToRelative(4.029f, 4.029f, 0f, isMoreThanHalf = true, isPositiveArc = true, 8.058f, 0f)
                arcToRelative(4.029f, 4.029f, 0f, isMoreThanHalf = true, isPositiveArc = true, -8.058f, 0f)
            }
        }.build()

        return _List!!
    }

@Suppress("ObjectPropertyName")
private var _List: ImageVector? = null
