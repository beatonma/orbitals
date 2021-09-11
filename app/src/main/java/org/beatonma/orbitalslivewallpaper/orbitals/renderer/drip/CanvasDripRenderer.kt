package org.beatonma.orbitalslivewallpaper.orbitals.renderer.drip

import android.graphics.Canvas
import org.beatonma.orbitals.physics.Body
import org.beatonma.orbitalslivewallpaper.orbitals.options.VisualOptions
import org.beatonma.orbitalslivewallpaper.orbitals.renderer.drip.BaseDripRenderer

class CanvasDripRenderer(
    options: VisualOptions,
): BaseDripRenderer<Canvas>(options) {
    override fun drawBody(canvas: Canvas, body: Body) {

    }
}
