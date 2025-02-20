package com.ezzy.ccp.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EzzyIcons.Cuba: ImageVector
    get() {
        if (_Cuba != null) {
            return _Cuba!!
        }
        _Cuba = ImageVector.Builder(
            name = "Cuba",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 32f,
            viewportHeight = 32f
        ).apply {
            path(fill = SolidColor(Color(0xFF2B4BA5))) {
                moveToRelative(0f, 22.261f)
                horizontalLineToRelative(32f)
                verticalLineToRelative(4.239f)
                horizontalLineToRelative(-32f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFFFFF))) {
                moveToRelative(0f, 18.148f)
                horizontalLineToRelative(32f)
                verticalLineToRelative(4.239f)
                horizontalLineToRelative(-32f)
                close()
            }
            path(fill = SolidColor(Color(0xFF2B4BA5))) {
                moveToRelative(0f, 13.933f)
                horizontalLineToRelative(32f)
                verticalLineToRelative(4.239f)
                horizontalLineToRelative(-32f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFFFFF))) {
                moveToRelative(0f, 9.717f)
                horizontalLineToRelative(32f)
                verticalLineToRelative(4.239f)
                horizontalLineToRelative(-32f)
                close()
            }
            path(fill = SolidColor(Color(0xFF2B4BA5))) {
                moveToRelative(0f, 5.5f)
                horizontalLineToRelative(32f)
                verticalLineToRelative(4.239f)
                horizontalLineToRelative(-32f)
                close()
            }
            path(fill = SolidColor(Color(0xFFDD1212))) {
                moveToRelative(0.017f, 26.451f)
                verticalLineToRelative(-20.937f)
                lineToRelative(11.117f, 10.472f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFFFFF))) {
                moveToRelative(5.047f, 16.204f)
                lineToRelative(0.716f, 2.119f)
                lineToRelative(-1.71f, -1.329f)
                lineToRelative(-0.002f, -0.01f)
                lineToRelative(-0.004f, 0.01f)
                lineToRelative(-1.711f, 1.329f)
                lineToRelative(0.717f, -2.119f)
                lineToRelative(0.019f, -0.013f)
                horizontalLineToRelative(-0.01f)
                lineToRelative(-1.763f, -1.25f)
                lineToRelative(2.121f, 0.004f)
                lineToRelative(0.58f, -2.133f)
                lineToRelative(0.657f, 2.129f)
                lineToRelative(-0.004f, 0.008f)
                lineToRelative(0.006f, -0.004f)
                lineToRelative(2.142f, -0.004f)
                lineToRelative(-1.768f, 1.25f)
                horizontalLineToRelative(-0.005f)
                close()
            }
        }.build()

        return _Cuba!!
    }

@Suppress("ObjectPropertyName")
private var _Cuba: ImageVector? = null
