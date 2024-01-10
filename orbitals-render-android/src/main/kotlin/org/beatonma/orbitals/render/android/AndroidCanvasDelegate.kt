package org.beatonma.orbitals.render.android

import android.graphics.Canvas
import android.graphics.Paint
import org.beatonma.orbitals.core.physics.Distance
import org.beatonma.orbitals.core.physics.Position
import org.beatonma.orbitals.render.CanvasDelegate
import org.beatonma.orbitals.render.color.Color
import org.beatonma.orbitals.render.options.CapStyle
import org.beatonma.orbitals.render.options.DrawStyle

object AndroidCanvasDelegate : CanvasDelegate<Canvas> {
    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun drawCircle(
        canvas: Canvas,
        position: Position,
        radius: Distance,
        color: Color,
        strokeWidth: Float,
        style: DrawStyle,
        alpha: Float,
    ) {
        canvas.drawCircle(
            position.x.value,
            position.y.value,
            radius.value,
            paint.apply {
                paint.color = color.toRgbInt()
                paint.alpha = (color.alpha.toFloat() * alpha).toInt()

                when (style) {
                    DrawStyle.Solid -> {
                        paint.style = Paint.Style.FILL
                    }

                    DrawStyle.Wireframe -> {
                        paint.style = Paint.Style.STROKE
                        paint.strokeWidth = strokeWidth
                    }
                }
            },
        )
    }

    override fun drawLine(
        canvas: Canvas,
        color: Color,
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
                this.color = color.toRgbInt()
                paint.alpha = (color.alpha.toFloat() * alpha).toInt()
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
