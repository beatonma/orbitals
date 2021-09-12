package org.beatonma.orbitalslivewallpaper.orbitals.renderer.drip

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import org.beatonma.orbitals.Space
import org.beatonma.orbitals.physics.Body
import org.beatonma.orbitals.physics.UniqueID
import org.beatonma.orbitalslivewallpaper.orbitals.options.VisualOptions
import org.beatonma.orbitalslivewallpaper.orbitals.renderer.util.lineTo
import org.beatonma.orbitalslivewallpaper.orbitals.renderer.util.moveTo

class ComposeDripRenderer(
    options: VisualOptions,
) : BaseDripRenderer<DrawScope>(options) {
    private var bitmap: ImageBitmap = ImageBitmap(1, 1)
    private val paths: MutableMap<UniqueID, Path> = mutableMapOf()

//    private val color = Color.Red
//    private val paint = Paint().apply {
//        strokeCap = StrokeCap.Round
//        strokeJoin = if (chance(20.percent)) StrokeJoin.Bevel else StrokeJoin.Round
//        strokeWidth = options.strokeWidth
//        color = color
//    }
    private var pathActive = false

    override fun onBodyCreated(body: Body) {
        super.onBodyCreated(body)
        paths[body.id] = Path().apply {
            moveTo(body.position)
        }
    }

    override fun onBodyDestroyed(body: Body) {
        super.onBodyDestroyed(body)
        paths.remove(body.id)
    }

    override fun drawBody(canvas: DrawScope, body: Body) {
        val path = paths[body.id] ?: return

        if (pathActive) {
            path.lineTo(body.position)
            pathActive = false
        } else {
            path.moveTo(body.position)
            pathActive = true
        }

        canvas.drawPath(
            path,
            color = Color.Red,
            style = Stroke(width = options.strokeWidth)
        )
    }

    override fun onSizeChanged(space: Space) {
        super.onSizeChanged(space)
        bitmap = ImageBitmap(space.width, space.height)
    }
}
