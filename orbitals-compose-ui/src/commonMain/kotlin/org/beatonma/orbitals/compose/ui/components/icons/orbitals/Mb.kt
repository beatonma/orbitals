package org.beatonma.orbitals.compose.ui.components.icons.orbitals

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import org.beatonma.orbitals.compose.ui.components.icons.OrbitalsIcons

val OrbitalsIcons.Mb: ImageVector
    get() {
        if (_icon != null) {
            return _icon!!
        }
        _icon = Builder(
            name = "Mb",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f
        ).apply {
//            path(
//                fill = SolidColor(Color(0xFF222222)), stroke = null, strokeLineWidth = 0.0f,
//                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
//                pathFillType = NonZero
//            ) {
//                moveTo(0.0f, 0.0f)
//                horizontalLineToRelative(24.0f)
//                verticalLineToRelative(24.0f)
//                horizontalLineToRelative(-24.0f)
//                close()
//            }

            path(
                fill = SolidColor(Color(0xFFdbdbdf)), stroke = null, strokeLineWidth = 1.5919f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(3.467f, 8.989f)
                lineToRelative(0.08f, 0.94f)
                quadToRelative(0.876f, -1.131f, 2.34f, -1.131f)
                quadToRelative(1.512f, 0.0f, 2.133f, 1.372f)
                quadToRelative(0.86f, -1.372f, 2.47f, -1.372f)
                quadToRelative(2.66f, 0.0f, 2.72f, 3.682f)
                verticalLineToRelative(6.6f)
                horizontalLineToRelative(-2.64f)
                verticalLineToRelative(-6.44f)
                quadToRelative(0.0f, -0.88f, -0.24f, -1.24f)
                quadToRelative(-0.26f, -0.39f, -0.829f, -0.39f)
                quadToRelative(-0.733f, 0.0f, -1.099f, 0.91f)
                lineToRelative(0.02f, 0.32f)
                verticalLineToRelative(6.84f)
                horizontalLineToRelative(-2.646f)
                verticalLineToRelative(-6.42f)
                quadToRelative(0.0f, -0.86f, -0.223f, -1.25f)
                quadToRelative(-0.239f, -0.4f, -0.828f, -0.4f)
                quadToRelative(-0.684f, 0.0f, -1.082f, 0.74f)
                verticalLineToRelative(7.33f)
                horizontalLineToRelative(-2.643f)
                verticalLineToRelative(-10.091f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFdbdbdf)), stroke = null, strokeLineWidth = 1.5919f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(23.0f, 14.38f)
                quadToRelative(0.0f, 2.47f, -0.8f, 3.58f)
                quadToRelative(-0.79f, 1.29f, -2.38f, 1.29f)
                quadToRelative(-1.28f, 0.0f, -2.07f, -1.13f)
                lineToRelative(-0.16f, 0.96f)
                horizontalLineToRelative(-2.42f)
                verticalLineToRelative(-14.325f)
                horizontalLineToRelative(2.58f)
                verticalLineToRelative(5.062f)
                quadToRelative(0.79f, -1.019f, 2.07f, -1.019f)
                quadToRelative(1.59f, 0.0f, 2.38f, 1.222f)
                quadToRelative(0.8f, 1.21f, 0.8f, 3.58f)
                close()
                moveTo(20.45f, 13.71f)
                quadToRelative(0.0f, -1.6f, -0.32f, -2.14f)
                quadToRelative(-0.31f, -0.56f, -1.11f, -0.56f)
                reflectiveQuadToRelative(-1.27f, 0.76f)
                verticalLineToRelative(4.6f)
                quadToRelative(0.47f, 0.64f, 1.27f, 0.64f)
                reflectiveQuadToRelative(1.11f, -0.48f)
                quadToRelative(0.32f, -0.48f, 0.32f, -1.88f)
                close()
            }
        }
            .build()
        return _icon!!
    }

private var _icon: ImageVector? = null
