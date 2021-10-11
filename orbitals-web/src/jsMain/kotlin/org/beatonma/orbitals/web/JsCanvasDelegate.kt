import org.beatonma.orbitals.render.options.Options
import org.beatonma.orbitals.render.options.CapStyle
import org.beatonma.orbitals.render.options.DrawStyle
import org.beatonma.orbitals.core.physics.Distance
import org.beatonma.orbitals.core.physics.Position
import org.beatonma.orbitals.render.CanvasDelegate
import org.w3c.dom.CanvasRenderingContext2D

object JsCanvasDelegate : CanvasDelegate<CanvasRenderingContext2D> {
    private fun setStyle(canvas: CanvasRenderingContext2D, color: String, strokeWidth: Float, alpha: Float) {
        canvas.run {
            fillStyle = color
            strokeStyle = color
            lineWidth = strokeWidth.toDouble()
            globalAlpha = alpha.toDouble()
        }
    }

    override fun drawCircle(
        canvas: CanvasRenderingContext2D,
        position: org.beatonma.orbitals.core.physics.Position,
        radius: Distance,
        color: Int,
        strokeWidth: Float,
        style: DrawStyle,
        alpha: Float,
    ) {
        setStyle(canvas, color.toHexString(), strokeWidth, alpha)

        canvas.run {
            beginPath()

            arc(
                position.x.value.toDouble(),
                position.y.value.toDouble(),
                radius.value.toDouble(),
                0.0,
                kotlin.math.PI * 2.0,
            )

            when (style) {
                DrawStyle.Wireframe -> stroke()
                DrawStyle.Solid -> fill()
            }
        }
    }

    override fun drawLine(
        canvas: CanvasRenderingContext2D,
        color: Int,
        start: org.beatonma.orbitals.core.physics.Position,
        end: org.beatonma.orbitals.core.physics.Position,
        strokeWidth: Float,
        cap: CapStyle,
        alpha: Float,
    ) {
        setStyle(canvas, color.toHexString(), strokeWidth, alpha)
        canvas.run {
            beginPath()
            moveTo(start.x.value.toDouble(), start.y.value.toDouble())
            moveTo(end.x.value.toDouble(), end.y.value.toDouble())
            stroke()
        }
    }
}
