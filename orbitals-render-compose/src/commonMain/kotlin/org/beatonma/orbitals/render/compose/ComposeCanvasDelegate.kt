package org.beatonma.orbitals.render.compose

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import org.beatonma.orbitals.core.physics.Distance
import org.beatonma.orbitals.core.physics.Position
import org.beatonma.orbitals.render.CanvasDelegate
import org.beatonma.orbitals.render.color.Color
import org.beatonma.orbitals.render.options.CapStyle
import org.beatonma.orbitals.render.options.DrawStyle
import androidx.compose.ui.graphics.Color as ComposeColor


object ComposeDelegate : CanvasDelegate<DrawScope> {
    override fun drawCircle(
        canvas: DrawScope,
        position: Position,
        radius: Distance,
        color: Color,
        strokeWidth: Float,
        style: DrawStyle,
    ) {
        canvas.drawCircle(
            center = position.toOffset(),
            radius = radius.value,
            color = color.toComposeColor(),
            style = when (style) {
                DrawStyle.Wireframe -> Stroke(
                    width = strokeWidth,
                )

                DrawStyle.Solid -> Fill
            },
        )
    }

    override fun drawLine(
        canvas: DrawScope,
        color: Color,
        start: Position,
        end: Position,
        strokeWidth: Float,
        cap: CapStyle,
    ) {
        canvas.drawLine(
            color = color.toComposeColor(),
            start = start.toOffset(),
            end = end.toOffset(),
            strokeWidth = strokeWidth,
            cap = when (cap) {
                CapStyle.Round -> StrokeCap.Round
                CapStyle.Square -> StrokeCap.Square
                CapStyle.Butt -> StrokeCap.Butt
            },
        )
    }
}

fun Position.toOffset() = Offset(x.value, y.value)

fun Color.toComposeColor(): ComposeColor =
    ComposeColor(
        red = red,
        green = green,
        blue = blue,
        alpha = alpha
    )
