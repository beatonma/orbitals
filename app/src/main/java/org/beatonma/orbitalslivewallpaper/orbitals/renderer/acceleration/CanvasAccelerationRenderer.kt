package org.beatonma.orbitalslivewallpaper.orbitals.renderer.acceleration

import android.graphics.Canvas
import android.graphics.Paint
import org.beatonma.orbitals.physics.Body
import org.beatonma.orbitalslivewallpaper.orbitals.options.VisualOptions

class CanvasAccelerationRenderer(
    options: VisualOptions,
) : BaseAccelerationRenderer<Canvas>(options) {

    private val alpha: Int = (options.colorOptions.foregroundAlpha * 255f).toInt()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        options.drawStyle.setUp(this)
    }

    override fun drawBody(canvas: Canvas, body: Body) {
//        paint.color =
//            colors[body.id] ?: throw Exception("No color for body $body:  $colors")
//        paint.alpha = alpha
//
//        canvas.drawCircle(body.position, body.radius, paint)
    }
}
