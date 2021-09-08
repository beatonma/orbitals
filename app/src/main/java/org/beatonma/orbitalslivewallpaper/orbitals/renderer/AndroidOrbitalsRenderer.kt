package org.beatonma.orbitalslivewallpaper.orbitals.renderer

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import org.beatonma.orbitals.RectangleSpace
import org.beatonma.orbitals.physics.Body
import org.beatonma.orbitals.physics.FixedBody
import org.beatonma.orbitals.physics.UniqueID
import org.beatonma.orbitalslivewallpaper.color.getAnyMaterialColor
import org.beatonma.orbitalslivewallpaper.orbitals.drawCircle
import org.beatonma.orbitalslivewallpaper.orbitals.options.VisualOptions
import kotlin.time.ExperimentalTime

interface OrbitalsRenderer<T> {
    fun drawBody(canvas: T, body: Body)

    fun drawBackground(canvas: T, bodies: List<Body>) {}
    fun drawForeground(canvas: T, bodies: List<Body>) {
        bodies.forEach { body ->
            drawBody(canvas, body)
        }
    }

    fun onBodyCreated(body: Body) {}
    fun onBodyDestroyed(body: Body) {}
    fun reset(space: RectangleSpace) {}
    fun recycle() {}
}


class AndroidOrbitalsRenderer(
    val options: VisualOptions,
): OrbitalsRenderer<Canvas> {
    @OptIn(ExperimentalTime::class)

    private val backgroundColor = options.colorOptions.background

    private val bodyColors: MutableMap<UniqueID, Int> = mutableMapOf()

    private val alpha: Int = (options.colorOptions.foregroundAlpha * 255f).toInt()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        options.drawStyle.setUp(this)
    }
    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        alpha = 80
    }

    override fun onBodyCreated(body: Body) {
        bodyColors[body.id] = chooseColor(body)
    }

    override fun onBodyDestroyed(body: Body) {
        super.onBodyDestroyed(body)
        bodyColors.remove(body.id)
    }

    override fun drawBody(canvas: Canvas, body: Body) {
        paint.color = bodyColors[body.id] ?: throw Exception("No color for body $body:  $bodyColors")
        paint.alpha = alpha

        canvas.drawCircle(body.position, body.radius, paint)
    }

    private fun drawGrid(canvas: Canvas) {
        val width = canvas.width
        val height = canvas.height
        for (x in 0..width step 16) {
            canvas.drawLine(x.toFloat(), 0f, x.toFloat(), height.toFloat(), gridPaint)
        }

        for (y in 0..height step 16) {
            canvas.drawLine(0f, y.toFloat(), width.toFloat(), y.toFloat(), gridPaint)
        }
    }

    override fun reset(space: RectangleSpace) {
        super.reset(space)
        bodyColors.clear()
    }
}


private fun chooseColor(body: Body) =
    when (body) {
        is FixedBody -> Color.WHITE
        else -> getAnyMaterialColor()
    }
