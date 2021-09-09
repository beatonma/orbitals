package org.beatonma.orbitalslivewallpaper.orbitals.renderer.canvas

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import org.beatonma.orbitals.physics.Body
import org.beatonma.orbitals.physics.InertialBody
import org.beatonma.orbitals.physics.Position
import org.beatonma.orbitals.physics.UniqueID
import kotlin.math.roundToInt

class TrailRenderer(
    val maxPoints: Int = 50,
    maxAlpha: Float = .2f,
) : OrbitalsRenderer<Canvas> {
    private val bodyPaths: MutableMap<UniqueID, MutableList<Position>> = mutableMapOf()
    private var trailTicks = 0
    private val trailTickFrequency = 0
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 8f
        style = Paint.Style.STROKE
        color = Color.WHITE
    }
    private val path = Path()
    private val maxAlpha: Float = maxAlpha * 255f

    override fun onBodyCreated(body: Body) {
        super.onBodyCreated(body)
        bodyPaths[body.id] = mutableListOf()
    }

    override fun onBodyDestroyed(body: Body) {
        super.onBodyDestroyed(body)
        bodyPaths.remove(body.id)
    }

    override fun drawBackground(canvas: Canvas, bodies: List<Body>) {
        val remember = trailTicks++ > trailTickFrequency

        bodies.forEach { body ->
            if (remember && body is InertialBody) {
                remember(body)
            }
            drawBody(canvas, body)
        }
        if (remember) {
            trailTicks = 0
        }
    }

    override fun drawForeground(canvas: Canvas, bodies: List<Body>) {

    }

    override fun drawBody(canvas: Canvas, body: Body) {
        val points = bodyPaths[body.id] ?: throw Exception("drawBody $body no path")

        points.forEachIndexed { index, position ->
            paint.alpha = ((index.toFloat() / points.size.toFloat()) * maxAlpha).roundToInt()
            canvas.drawCircle(position, maxOf(1f, body.radius.metres / 10f))
        }
    }

    private fun remember(body: Body) {
        val points = bodyPaths[body.id] ?: throw Exception("remember $body no path")

        points.add(body.position.copy())
        if (points.size > maxPoints) {
            points.removeAt(0)
        }
    }

    private fun drawPath(canvas: Canvas, block: Path.() -> Unit) {
        path.block()
        canvas.drawPath(path, paint)
        path.reset()

    }

    private fun Canvas.drawCircle(position: Position, size: Float = 4f) {
        drawCircle(position.x.metres, position.y.metres, size, paint)
    }
}

private fun Path.moveTo(position: Position) {
    moveTo(position.x.metres, position.y.metres)
}

private fun Path.lineTo(position: Position) {
    lineTo(position.x.metres, position.y.metres)
}

private fun Path.addCircle(position: Position) {
    addCircle(position.x.metres, position.y.metres, 4f, Path.Direction.CW)
}
