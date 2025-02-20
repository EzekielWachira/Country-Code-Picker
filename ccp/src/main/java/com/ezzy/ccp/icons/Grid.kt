package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import kotlin.Suppress

val EzzyIcons.Grid: ImageVector
    get() {
        if (_Grid != null) {
            return _Grid!!
        }
        _Grid = ImageVector.Builder(
            name = "Grid",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(176.79f, 0f)
                horizontalLineTo(59.21f)
                curveTo(26.56f, 0f, 0f, 26.56f, 0f, 59.21f)
                verticalLineToRelative(117.58f)
                curveTo(0f, 209.44f, 26.56f, 236f, 59.21f, 236f)
                horizontalLineToRelative(117.58f)
                curveTo(209.44f, 236f, 236f, 209.44f, 236f, 176.79f)
                verticalLineTo(59.21f)
                curveTo(236f, 26.56f, 209.44f, 0f, 176.79f, 0f)
                close()
                moveTo(196f, 176.79f)
                curveToRelative(0f, 10.59f, -8.62f, 19.21f, -19.21f, 19.21f)
                horizontalLineTo(59.21f)
                curveTo(48.62f, 196f, 40f, 187.38f, 40f, 176.79f)
                verticalLineTo(59.21f)
                curveTo(40f, 48.62f, 48.62f, 40f, 59.21f, 40f)
                horizontalLineToRelative(117.58f)
                curveTo(187.38f, 40f, 196f, 48.62f, 196f, 59.21f)
                verticalLineTo(176.79f)
                close()
            }
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(452f, 0f)
                horizontalLineTo(336f)
                curveToRelative(-33.08f, 0f, -60f, 26.92f, -60f, 60f)
                verticalLineToRelative(116f)
                curveToRelative(0f, 33.08f, 26.92f, 60f, 60f, 60f)
                horizontalLineToRelative(116f)
                curveToRelative(33.08f, 0f, 60f, -26.92f, 60f, -60f)
                verticalLineTo(60f)
                curveTo(512f, 26.92f, 485.08f, 0f, 452f, 0f)
                close()
                moveTo(472f, 176f)
                curveToRelative(0f, 11.03f, -8.97f, 20f, -20f, 20f)
                horizontalLineTo(336f)
                curveToRelative(-11.03f, 0f, -20f, -8.97f, -20f, -20f)
                verticalLineTo(60f)
                curveToRelative(0f, -11.03f, 8.97f, -20f, 20f, -20f)
                horizontalLineToRelative(116f)
                curveToRelative(11.03f, 0f, 20f, 8.97f, 20f, 20f)
                verticalLineTo(176f)
                close()
            }
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(176.79f, 276f)
                horizontalLineTo(59.21f)
                curveTo(26.56f, 276f, 0f, 302.56f, 0f, 335.21f)
                verticalLineToRelative(117.58f)
                curveTo(0f, 485.44f, 26.56f, 512f, 59.21f, 512f)
                horizontalLineToRelative(117.58f)
                curveTo(209.44f, 512f, 236f, 485.44f, 236f, 452.79f)
                verticalLineTo(335.21f)
                curveTo(236f, 302.56f, 209.44f, 276f, 176.79f, 276f)
                close()
                moveTo(196f, 452.79f)
                curveToRelative(0f, 10.59f, -8.62f, 19.21f, -19.21f, 19.21f)
                horizontalLineTo(59.21f)
                curveTo(48.62f, 472f, 40f, 463.38f, 40f, 452.79f)
                verticalLineTo(335.21f)
                curveTo(40f, 324.62f, 48.62f, 316f, 59.21f, 316f)
                horizontalLineToRelative(117.58f)
                curveToRelative(10.59f, 0f, 19.21f, 8.62f, 19.21f, 19.21f)
                verticalLineTo(452.79f)
                close()
            }
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(452f, 276f)
                horizontalLineTo(336f)
                curveToRelative(-33.08f, 0f, -60f, 26.92f, -60f, 60f)
                verticalLineToRelative(116f)
                curveToRelative(0f, 33.08f, 26.92f, 60f, 60f, 60f)
                horizontalLineToRelative(116f)
                curveToRelative(33.08f, 0f, 60f, -26.92f, 60f, -60f)
                verticalLineTo(336f)
                curveTo(512f, 302.92f, 485.08f, 276f, 452f, 276f)
                close()
                moveTo(472f, 452f)
                curveToRelative(0f, 11.03f, -8.97f, 20f, -20f, 20f)
                horizontalLineTo(336f)
                curveToRelative(-11.03f, 0f, -20f, -8.97f, -20f, -20f)
                verticalLineTo(336f)
                curveToRelative(0f, -11.03f, 8.97f, -20f, 20f, -20f)
                horizontalLineToRelative(116f)
                curveToRelative(11.03f, 0f, 20f, 8.97f, 20f, 20f)
                verticalLineTo(452f)
                close()
            }
        }.build()

        return _Grid!!
    }

@Suppress("ObjectPropertyName")
private var _Grid: ImageVector? = null
