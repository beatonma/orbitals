package org.beatonma.orbitals.rendering.util

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
    try {
        drawCircle(
            color = color,
            radius = radius.value,
            center = position.toOffset(),
            alpha = alpha,
            blendMode = blendMode,
            colorFilter = colorFilter,
            style = style,
        )
    } catch (e: Exception) {
        println("OFFSET IS UNSPECIFIED: $position $radius $color")
        throw e
    }
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

fun Position.toOffset() = Offset(x.value, y.value)
