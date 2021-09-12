package org.beatonma.orbitalslivewallpaper.orbitals.renderer.acceleration

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import org.beatonma.orbitals.map
import org.beatonma.orbitals.physics.Body
import org.beatonma.orbitalslivewallpaper.orbitals.options.VisualOptions
import org.beatonma.orbitalslivewallpaper.orbitals.renderer.util.toOffset

private const val Scale = 1e2F

class ComposeAccelerationRenderer(
    options: VisualOptions,
): BaseAccelerationRenderer<DrawScope>(options) {
    override fun drawBody(canvas: DrawScope, body: Body) {
        canvas.drawLine(
            color = Color.Green,
            start = body.position.toOffset(),
            end = Offset(
//                (100 * body.acceleration.x.value.coerceAtMost(1f)).map(0F, 1f, 0f, 120f),
//                (100 * body.acceleration.y.value.coerceAtMost(1f)).map(0F, 1f, 0f, 120f),
                body.acceleration.x.value * Scale,
                body.acceleration.y.value * Scale,
            ) + body.position.toOffset(),
            strokeWidth = options.strokeWidth,
        )
    }
}
