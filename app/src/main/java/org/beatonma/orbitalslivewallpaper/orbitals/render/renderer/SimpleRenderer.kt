package org.beatonma.orbitalslivewallpaper.orbitals.render.renderer

import android.graphics.Canvas
import androidx.compose.ui.graphics.drawscope.DrawScope
import org.beatonma.orbitals.physics.Body
import org.beatonma.orbitals.physics.UniqueID
import org.beatonma.orbitalslivewallpaper.color.getAnyMaterialColor
import org.beatonma.orbitalslivewallpaper.orbitals.options.VisualOptions
import org.beatonma.orbitalslivewallpaper.orbitals.render.AndroidCanvasDelegate
import org.beatonma.orbitalslivewallpaper.orbitals.render.CanvasDelegate
import org.beatonma.orbitalslivewallpaper.orbitals.render.ComposeDelegate
import org.beatonma.orbitalslivewallpaper.orbitals.render.OrbitalsRenderer


abstract class BaseSimpleRenderer<Canvas> internal constructor(
    override val delegate: CanvasDelegate<Canvas>,
    override var options: VisualOptions,
) : OrbitalsRenderer<Canvas> {
    private val colors: MutableMap<UniqueID, Int> = mutableMapOf()

    private fun chooseColor(body: Body): Int = getAnyMaterialColor()

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

class CanvasSimpleRenderer(
    options: VisualOptions,
) : BaseSimpleRenderer<Canvas>(AndroidCanvasDelegate, options)

class ComposeSimpleRenderer(
    options: VisualOptions,
) : BaseSimpleRenderer<DrawScope>(ComposeDelegate, options)
