package org.beatonma.orbitalslivewallpaper.orbitals.renderer.compose

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import org.beatonma.orbitals.physics.Body
import org.beatonma.orbitals.physics.metres
import org.beatonma.orbitalslivewallpaper.orbitals.renderer.BaseTrailRenderer
import org.beatonma.orbitalslivewallpaper.orbitals.renderer.compose.util.drawCircle

class TrailRenderer(
    maxPoints: Int,
    maxAlpha: Float = .2f,
) : BaseTrailRenderer<DrawScope>(maxPoints, maxAlpha) {

    override fun drawBody(canvas: DrawScope, body: Body) {
        val points = bodyPaths[body.id] ?: throw Exception("drawBody $body no path")

        points.forEachIndexed { index, position ->
            canvas.drawCircle(
                color = Color.White,
                position = position,
                radius = 16.metres,
                alpha = (index.toFloat() / points.size.toFloat()) * maxAlpha
            )
        }
    }
}
