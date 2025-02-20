package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import kotlin.Suppress

val EzzyIcons.Tanzania: ImageVector
    get() {
        if (_Tanzania != null) {
            return _Tanzania!!
        }
        _Tanzania = ImageVector.Builder(
            name = "Tanzania",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f
        ).apply {
            path(fill = SolidColor(Color(0xFF73AF00))) {
                moveTo(0f, 344.28f)
                verticalLineTo(97.1f)
                curveToRelative(0f, -4.88f, 3.95f, -8.83f, 8.83f, -8.83f)
                horizontalLineToRelative(379.59f)
                lineTo(0f, 344.28f)
                close()
            }
            path(fill = SolidColor(Color(0xFF4173CD))) {
                moveTo(512f, 167.72f)
                verticalLineToRelative(247.17f)
                curveToRelative(0f, 4.88f, -3.95f, 8.83f, -8.83f, 8.83f)
                horizontalLineTo(123.59f)
                lineTo(512f, 167.72f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFE15A))) {
                moveTo(512f, 172.7f)
                verticalLineTo(97.1f)
                curveToRelative(0f, -4.88f, -3.95f, -8.83f, -8.83f, -8.83f)
                horizontalLineTo(383.14f)
                lineTo(0f, 339.3f)
                verticalLineToRelative(75.6f)
                curveToRelative(0f, 4.88f, 3.95f, 8.83f, 8.83f, 8.83f)
                horizontalLineToRelative(120.03f)
                lineTo(512f, 172.7f)
                close()
            }
            path(fill = SolidColor(Color(0xFF464655))) {
                moveTo(512f, 141.05f)
                verticalLineTo(97.1f)
                curveToRelative(0f, -4.88f, -3.95f, -8.83f, -8.83f, -8.83f)
                horizontalLineToRelative(-71.72f)
                lineTo(0f, 370.95f)
                verticalLineToRelative(43.94f)
                curveToRelative(0f, 4.88f, 3.95f, 8.83f, 8.83f, 8.83f)
                horizontalLineToRelative(71.72f)
                lineTo(512f, 141.05f)
                close()
            }
        }.build()

        return _Tanzania!!
    }

@Suppress("ObjectPropertyName")
private var _Tanzania: ImageVector? = null
