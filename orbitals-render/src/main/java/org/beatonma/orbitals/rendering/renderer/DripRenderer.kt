package org.beatonma.orbitals.rendering.renderer

import org.beatonma.orbitals.options.VisualOptions
import org.beatonma.orbitals.physics.Body
import org.beatonma.orbitals.rendering.CanvasDelegate
import org.beatonma.orbitals.rendering.OrbitalsRenderer

class DripRenderer<Canvas> internal constructor(
    override val delegate: CanvasDelegate<Canvas>,
    override var options: VisualOptions,
) : OrbitalsRenderer<Canvas> {
    override fun drawBody(canvas: Canvas, body: Body) {

    }
}
