package org.beatonma.orbitalslivewallpaper.orbitals.renderer

import org.beatonma.orbitals.physics.Body
import org.beatonma.orbitals.physics.UniqueID
import org.beatonma.orbitalslivewallpaper.orbitals.options.VisualOptions

abstract class BaseSimpleRenderer<Canvas, Color>(
    override var options: VisualOptions
): OrbitalsRenderer<Canvas> {
    val colors: MutableMap<UniqueID, Color> = mutableMapOf()

    abstract fun chooseColor(body: Body): Color


    override fun onBodyCreated(body: Body) {
        colors[body.id] = chooseColor(body)
    }

    override fun onBodyDestroyed(body: Body) {
        super.onBodyDestroyed(body)
        colors.remove(body.id)
    }
}
