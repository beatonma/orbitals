package org.beatonma.orbitals.render.compose

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import org.beatonma.orbitals.render.options.CapStyle
import org.beatonma.orbitals.render.options.DrawStyle
import org.beatonma.orbitals.core.physics.Distance
import org.beatonma.orbitals.core.physics.Position
import org.beatonma.orbitals.render.CanvasDelegate


object ComposeDelegate : CanvasDelegate<DrawScope> {
    override fun drawCircle(
        canvas: DrawScope,
        position: Position,
        radius: Distance,
        color: Int,
        strokeWidth: Float,
        style: DrawStyle,
        alpha: Float,
    ) {
        canvas.drawCircle(
            center = position.toOffset(),
            radius = radius.value,
            color = color.toComposeColor(),
            alpha = alpha,
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
        color: Int,
        start: Position,
        end: Position,
        strokeWidth: Float,
        cap: CapStyle,
        alpha: Float,
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
            alpha = alpha,
        )
    }
}

fun Position.toOffset() = Offset(x.value, y.value)
fun Int.toComposeColor(): Color = Color(
    red = (this shr 16) and 0xff,
    green = (this shr 8) and 0xff,
    blue = this and 0xff,
)
