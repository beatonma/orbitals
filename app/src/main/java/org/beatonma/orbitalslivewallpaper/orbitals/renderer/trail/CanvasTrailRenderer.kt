package org.beatonma.orbitalslivewallpaper.orbitals.renderer.trail

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import org.beatonma.orbitals.physics.Body
import org.beatonma.orbitals.physics.Position
import org.beatonma.orbitalslivewallpaper.orbitals.options.VisualOptions
import kotlin.math.roundToInt

class CanvasTrailRenderer(
    options: VisualOptions,
) : BaseTrailRenderer<Canvas>(options) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 8f
        style = Paint.Style.STROKE
        color = Color.WHITE
    }

    override fun drawBody(canvas: Canvas, body: Body) {
        val points = bodyPaths[body.id] ?: throw Exception("drawBody $body no path")

        points.forEachIndexed { index, position ->
            paint.alpha =
                ((index.toFloat() / points.size.toFloat()) * (maxAlpha * 255f)).roundToInt()
            canvas.drawCircle(
                position,
                radius = maxOf(1f, maxOf(traceThickness, body.radius.value / 10f))
            )
        }
    }

    private fun Canvas.drawCircle(position: Position, radius: Float) {
        drawCircle(position.x.value, position.y.value, radius, paint)
    }
}
