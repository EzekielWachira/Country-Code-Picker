package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EzzyIcons.Portugal: ImageVector
    get() {
        if (_Portugal != null) {
            return _Portugal!!
        }
        _Portugal = ImageVector.Builder(
            name = "Portugal",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f
        ).apply {
            path(fill = SolidColor(Color(0xFFE92B2B))) {
                moveToRelative(480f, 96f)
                horizontalLineToRelative(-448f)
                verticalLineToRelative(320f)
                horizontalLineToRelative(448f)
                close()
            }
            path(fill = SolidColor(Color(0xFF32AB45))) {
                moveToRelative(32f, 416f)
                horizontalLineToRelative(160f)
                verticalLineToRelative(-320f)
                horizontalLineToRelative(-160f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFE70E)),
                pathFillType = PathFillType.EvenOdd
            ) {
                moveToRelative(192f, 208f)
                curveToRelative(-26.51f, 0f, -48f, 21.49f, -48f, 48f)
                reflectiveCurveToRelative(21.49f, 48f, 48f, 48f)
                reflectiveCurveToRelative(48f, -21.49f, 48f, -48f)
                reflectiveCurveToRelative(-21.49f, -48f, -48f, -48f)
                close()
                moveTo(128f, 256f)
                curveToRelative(0f, -35.35f, 28.65f, -64f, 64f, -64f)
                reflectiveCurveToRelative(64f, 28.65f, 64f, 64f)
                reflectiveCurveToRelative(-28.65f, 64f, -64f, 64f)
                reflectiveCurveToRelative(-64f, -28.65f, -64f, -64f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFE70E)),
                pathFillType = PathFillType.EvenOdd
            ) {
                moveToRelative(226.34f, 301.66f)
                lineToRelative(-80f, -80f)
                lineToRelative(11.31f, -11.31f)
                lineToRelative(80f, 80f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFE70E)),
                pathFillType = PathFillType.EvenOdd
            ) {
                moveToRelative(184f, 312.57f)
                verticalLineToRelative(-113.14f)
                horizontalLineToRelative(16f)
                verticalLineToRelative(113.14f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFE70E)),
                pathFillType = PathFillType.EvenOdd
            ) {
                moveToRelative(135.99f, 248.57f)
                horizontalLineToRelative(113.14f)
                verticalLineToRelative(16f)
                horizontalLineToRelative(-113.14f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFE70E)),
                pathFillType = PathFillType.EvenOdd
            ) {
                moveToRelative(157.66f, 301.66f)
                lineToRelative(80f, -80f)
                lineToRelative(-11.31f, -11.31f)
                lineToRelative(-80f, 80f)
                close()
            }
            path(fill = SolidColor(Color(0xFFE92B2B))) {
                moveToRelative(216f, 224f)
                horizontalLineToRelative(-48f)
                curveToRelative(-4.42f, 0f, -8f, 3.58f, -8f, 8f)
                verticalLineToRelative(32f)
                curveToRelative(0f, 17.67f, 14.33f, 32f, 32f, 32f)
                reflectiveCurveToRelative(32f, -14.33f, 32f, -32f)
                verticalLineToRelative(-32f)
                curveToRelative(0f, -4.42f, -3.58f, -8f, -8f, -8f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFBFBFB))) {
                moveToRelative(208f, 240f)
                horizontalLineToRelative(-32f)
                verticalLineToRelative(24f)
                curveToRelative(0f, 8.84f, 7.16f, 16f, 16f, 16f)
                reflectiveCurveToRelative(16f, -7.16f, 16f, -16f)
                close()
            }
        }.build()

        return _Portugal!!
    }

@Suppress("ObjectPropertyName")
private var _Portugal: ImageVector? = null
