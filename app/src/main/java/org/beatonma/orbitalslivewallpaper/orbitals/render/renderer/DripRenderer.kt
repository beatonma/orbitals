package org.beatonma.orbitalslivewallpaper.orbitals.render.renderer

import android.graphics.Canvas
import androidx.compose.ui.graphics.drawscope.DrawScope
import org.beatonma.orbitals.physics.Body
import org.beatonma.orbitalslivewallpaper.orbitals.options.VisualOptions
import org.beatonma.orbitalslivewallpaper.orbitals.render.AndroidCanvasDelegate
import org.beatonma.orbitalslivewallpaper.orbitals.render.CanvasDelegate
import org.beatonma.orbitalslivewallpaper.orbitals.render.ComposeDelegate
import org.beatonma.orbitalslivewallpaper.orbitals.render.OrbitalsRenderer

abstract class BaseDripRenderer<Canvas> internal constructor(
    override val delegate: CanvasDelegate<Canvas>,
    override var options: VisualOptions,
) : OrbitalsRenderer<Canvas> {
    override fun drawBody(canvas: Canvas, body: Body) {

    }
}


class CanvasDripRenderer(
    options: VisualOptions,
) : BaseDripRenderer<Canvas>(AndroidCanvasDelegate, options)

class ComposeDripRenderer(
    options: VisualOptions,
) : BaseDripRenderer<DrawScope>(ComposeDelegate, options)
