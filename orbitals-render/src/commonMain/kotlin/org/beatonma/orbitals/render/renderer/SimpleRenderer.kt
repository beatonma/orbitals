package org.beatonma.orbitals.render.renderer

import org.beatonma.orbitals.render.options.DrawStyle
import org.beatonma.orbitals.render.options.VisualOptions
import org.beatonma.orbitals.core.physics.Body
import org.beatonma.orbitals.core.physics.GreatAttractor
import org.beatonma.orbitals.core.physics.UniqueID
import org.beatonma.orbitals.core.physics.metres
import org.beatonma.orbitals.render.CanvasDelegate
import org.beatonma.orbitals.render.OrbitalsRenderer


//fun <Canvas> simpleRenderer(delegate: CanvasDelegate<Canvas>, options: VisualOptions) =
//    SimpleRenderer(delegate, options)

class SimpleRenderer<Canvas> internal constructor(
    override val delegate: CanvasDelegate<Canvas>,
    override var options: VisualOptions,
) : OrbitalsRenderer<Canvas> {
    private val colors: MutableMap<UniqueID, Int> = mutableMapOf()

    private fun chooseColor(body: Body): Int = options.colorOptions.colorForBody

    override fun onBodyCreated(body: Body) {
        super.onBodyCreated(body)
        colors[body.id] = chooseColor(body)
    }

    override fun onBodyDestroyed(body: Body) {
        super.onBodyDestroyed(body)
        colors.remove(body.id)
    }

    override fun drawBody(canvas: Canvas, body: Body) {
        if (body is GreatAttractor) {
            drawAttractor(canvas, body)
            return
        }

        val color = colors[body.id] ?: throw Exception("No color for body ${body.id}")

        delegate.drawCircle(
            canvas,
            body.position,
            body.radius,
            color,
            strokeWidth = options.strokeWidth,
            style = options.drawStyle,
        )
    }

    private fun drawAttractor(canvas: Canvas,body: GreatAttractor) {
        delegate.drawCircle(
            canvas,
            body.position,
            100.metres,
            0xffffff,
            strokeWidth = 8f,
            style = DrawStyle.Wireframe
        )
    }
}
