package org.beatonma.orbitals.rendering.renderer

import org.beatonma.orbitals.options.VisualOptions
import org.beatonma.orbitals.physics.Body
import org.beatonma.orbitals.physics.UniqueID
import org.beatonma.orbitals.rendering.CanvasDelegate
import org.beatonma.orbitals.rendering.OrbitalsRenderer


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
        val color = colors[body.id] ?: throw Exception("No color for body ${body.id}")

        delegate.drawCircle(
            canvas,
            body.position,
            body.radius,
            color,
            alpha = 1f,
            options.strokeWidth,
            options.drawStyle,
        )
    }
}
