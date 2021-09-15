package org.beatonma.orbitalslivewallpaper.orbitals.render.renderer

import org.beatonma.orbitals.physics.Body
import org.beatonma.orbitalslivewallpaper.orbitals.options.VisualOptions
import org.beatonma.orbitalslivewallpaper.orbitals.render.CanvasDelegate
import org.beatonma.orbitalslivewallpaper.orbitals.render.OrbitalsRenderer

class DripRenderer<Canvas> internal constructor(
    override val delegate: CanvasDelegate<Canvas>,
    override var options: VisualOptions,
) : OrbitalsRenderer<Canvas> {
    override fun drawBody(canvas: Canvas, body: Body) {

    }
}
