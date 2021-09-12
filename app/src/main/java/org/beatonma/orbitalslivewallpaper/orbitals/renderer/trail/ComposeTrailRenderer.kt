package org.beatonma.orbitalslivewallpaper.orbitals.renderer.trail

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import org.beatonma.orbitals.physics.Body
import org.beatonma.orbitals.physics.metres
import org.beatonma.orbitalslivewallpaper.orbitals.options.VisualOptions
import org.beatonma.orbitalslivewallpaper.orbitals.renderer.util.drawCircle

class ComposeTrailRenderer(
    options: VisualOptions,
    maxPoints: Int = options.traceLineLength,
    maxAlpha: Float = .2f,
) : BaseTrailRenderer<DrawScope>(options, maxPoints, maxAlpha) {

    override fun drawBody(canvas: DrawScope, body: Body) {
        val points = bodyPaths[body.id] ?: throw Exception("drawBody $body no path")

        points.forEachIndexed { index, position ->
            canvas.drawCircle(
                color = Color.White,
                position = position,
                radius = maxOf(1f, maxOf(traceThickness, body.radius.value / 10f)).metres,
                alpha = (index.toFloat() / points.size.toFloat()) * maxAlpha
            )
        }
    }
}
