package org.beatonma.orbitals.compose.ui.components.icons.orbitals

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.EvenOdd
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import org.beatonma.orbitals.compose.ui.components.icons.OrbitalsIcons

val OrbitalsIcons.Github: ImageVector
    get() {
        if (_icon != null) {
            return _icon!!
        }
        _icon = Builder(
            name = "Github-mark-white",
            defaultWidth = 98.0.dp,
            defaultHeight = 96.0.dp,
            viewportWidth = 98.0f,
            viewportHeight = 96.0f,
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000)),
                stroke = null,
                strokeLineWidth = 0.0f,
                strokeLineCap = Butt,
                strokeLineJoin = Miter,
                strokeLineMiter = 4.0f,
                pathFillType = EvenOdd
            ) {
                moveTo(48.854f, 0.0f)
                curveTo(21.839f, 0.0f, 0.0f, 22.0f, 0.0f, 49.217f)
                curveToRelative(0.0f, 21.756f, 13.993f, 40.172f, 33.405f, 46.69f)
                curveToRelative(2.427f, 0.49f, 3.316f, -1.059f, 3.316f, -2.362f)
                curveToRelative(0.0f, -1.141f, -0.08f, -5.052f, -0.08f, -9.127f)
                curveToRelative(-13.59f, 2.934f, -16.42f, -5.867f, -16.42f, -5.867f)
                curveToRelative(-2.184f, -5.704f, -5.42f, -7.17f, -5.42f, -7.17f)
                curveToRelative(-4.448f, -3.015f, 0.324f, -3.015f, 0.324f, -3.015f)
                curveToRelative(4.934f, 0.326f, 7.523f, 5.052f, 7.523f, 5.052f)
                curveToRelative(4.367f, 7.496f, 11.404f, 5.378f, 14.235f, 4.074f)
                curveToRelative(0.404f, -3.178f, 1.699f, -5.378f, 3.074f, -6.6f)
                curveToRelative(-10.839f, -1.141f, -22.243f, -5.378f, -22.243f, -24.283f)
                curveToRelative(0.0f, -5.378f, 1.94f, -9.778f, 5.014f, -13.2f)
                curveToRelative(-0.485f, -1.222f, -2.184f, -6.275f, 0.486f, -13.038f)
                curveToRelative(0.0f, 0.0f, 4.125f, -1.304f, 13.426f, 5.052f)
                arcToRelative(46.97f, 46.97f, 0.0f, false, true, 12.214f, -1.63f)
                curveToRelative(4.125f, 0.0f, 8.33f, 0.571f, 12.213f, 1.63f)
                curveToRelative(9.302f, -6.356f, 13.427f, -5.052f, 13.427f, -5.052f)
                curveToRelative(2.67f, 6.763f, 0.97f, 11.816f, 0.485f, 13.038f)
                curveToRelative(3.155f, 3.422f, 5.015f, 7.822f, 5.015f, 13.2f)
                curveToRelative(0.0f, 18.905f, -11.404f, 23.06f, -22.324f, 24.283f)
                curveToRelative(1.78f, 1.548f, 3.316f, 4.481f, 3.316f, 9.126f)
                curveToRelative(0.0f, 6.6f, -0.08f, 11.897f, -0.08f, 13.526f)
                curveToRelative(0.0f, 1.304f, 0.89f, 2.853f, 3.316f, 2.364f)
                curveToRelative(19.412f, -6.52f, 33.405f, -24.935f, 33.405f, -46.691f)
                curveTo(97.707f, 22.0f, 75.788f, 0.0f, 48.854f, 0.0f)
                close()
            }
        }
            .build()
        return _icon!!
    }

private var _icon: ImageVector? = null
