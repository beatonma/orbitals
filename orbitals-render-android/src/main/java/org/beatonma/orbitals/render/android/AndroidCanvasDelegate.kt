package org.beatonma.orbitals.render.android

import android.graphics.Canvas
import android.graphics.Paint
import org.beatonma.orbitals.options.CapStyle
import org.beatonma.orbitals.options.DrawStyle
import org.beatonma.orbitals.physics.Distance
import org.beatonma.orbitals.physics.Position
import org.beatonma.orbitals.rendering.CanvasDelegate
import kotlin.math.roundToInt

object AndroidCanvasDelegate : CanvasDelegate<Canvas> {
    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun drawCircle(
        canvas: Canvas,
        position: Position,
        radius: Distance,
        color: Int,
        strokeWidth: Float,
        style: DrawStyle,
        alpha: Float,
    ) {
        canvas.drawCircle(
            position.x.value,
            position.y.value,
            radius.value,
            paint.apply {
                when (style) {
                    DrawStyle.Solid -> paint.style = Paint.Style.FILL
                    DrawStyle.Wireframe -> {
                        paint.style = Paint.Style.STROKE
                        paint.strokeWidth = strokeWidth
                    }
                }
            }
        )
    }

    override fun drawLine(
        canvas: Canvas,
        color: Int,
        start: Position,
        end: Position,
        strokeWidth: Float,
        cap: CapStyle,
        alpha: Float,
    ) {
        canvas.drawLine(
            start.x.value, start.y.value,
            end.x.value, end.y.value,
            paint.apply {
                this.color = color
                this.alpha = (alpha * 255f).roundToInt()
                this.strokeWidth = strokeWidth
                this.strokeCap = when (cap) {
                    CapStyle.Round -> Paint.Cap.ROUND
                    CapStyle.Square -> Paint.Cap.SQUARE
                    CapStyle.Butt -> Paint.Cap.BUTT
                }
            }
        )
    }
}
