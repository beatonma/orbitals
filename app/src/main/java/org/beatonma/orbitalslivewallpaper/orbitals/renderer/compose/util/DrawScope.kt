package org.beatonma.orbitalslivewallpaper.orbitals.renderer.compose.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import org.beatonma.orbitals.physics.Body
import org.beatonma.orbitals.physics.Distance
import org.beatonma.orbitals.physics.Position

fun DrawScope.drawCircle(
    position: Position,
    radius: Distance,
    color: Color,
    alpha: Float = 1f,
    style: DrawStyle = Fill,
    colorFilter: ColorFilter? = null,
    blendMode: BlendMode = BlendMode.SrcOver
) {
    drawCircle(
        color = color,
        radius = radius.metres,
        center = Offset(position.x.metres, position.y.metres),
        alpha = alpha,
        blendMode = blendMode,
        colorFilter = colorFilter,
        style = style,
    )
}

fun DrawScope.drawCircle(
    body: Body,
    color: Color,
    alpha: Float = 1f,
    style: DrawStyle = Fill,
    colorFilter: ColorFilter? = null,
    blendMode: BlendMode = BlendMode.SrcOver
) {
    drawCircle(
        position = body.position,
        radius = body.radius,
        color = color,
        alpha = alpha,
        blendMode = blendMode,
        colorFilter = colorFilter,
        style = style,
    )
}
