package org.beatonma.orbitalslivewallpaper.orbitals.renderer.canvas

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import org.beatonma.orbitals.physics.Body
import org.beatonma.orbitals.physics.InertialBody
import org.beatonma.orbitals.physics.Position
import org.beatonma.orbitals.physics.UniqueID
import org.beatonma.orbitalslivewallpaper.orbitals.renderer.BaseTrailRenderer
import kotlin.math.roundToInt

class TrailRenderer(
    maxPoints: Int,
    maxAlpha: Float = .2f,
) : BaseTrailRenderer<Canvas>(maxPoints, maxAlpha) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 8f
        style = Paint.Style.STROKE
        color = Color.WHITE
    }

    override fun drawBody(canvas: Canvas, body: Body) {
        val points = bodyPaths[body.id] ?: throw Exception("drawBody $body no path")

        points.forEachIndexed { index, position ->
            paint.alpha = ((index.toFloat() / points.size.toFloat()) * (maxAlpha * 255f)).roundToInt()
            canvas.drawCircle(
                position,
                radius = maxOf(1f, maxOf(traceThickness, body.radius.metres / 10f))
            )
        }
    }

    private fun Canvas.drawCircle(position: Position, radius: Float) {
        drawCircle(position.x.metres, position.y.metres, radius, paint)
    }
}
