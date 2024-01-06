import org.beatonma.orbitals.render.options.CapStyle
import org.beatonma.orbitals.render.options.DrawStyle
import org.beatonma.orbitals.core.physics.Distance
import org.beatonma.orbitals.core.physics.Position
import org.beatonma.orbitals.render.CanvasDelegate
import org.beatonma.orbitals.render.color.Color
import org.w3c.dom.BUTT
import org.w3c.dom.CanvasLineCap
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.ROUND
import org.w3c.dom.SQUARE

object JsCanvasDelegate : CanvasDelegate<CanvasRenderingContext2D> {
    private fun setStyle(
        canvas: CanvasRenderingContext2D,
        color: String,
        strokeWidth: Float,
        cap: CapStyle? = null
    ) {
        canvas.run {
            fillStyle = color
            strokeStyle = color
            lineWidth = strokeWidth.toDouble()
            cap?.let {
                lineCap = when(cap) {
                    CapStyle.Round -> CanvasLineCap.ROUND
                    CapStyle.Butt -> CanvasLineCap.BUTT
                    CapStyle.Square -> CanvasLineCap.SQUARE
                }
            }
        }
    }

    override fun drawCircle(
        canvas: CanvasRenderingContext2D,
        position: Position,
        radius: Distance,
        color: Color,
        strokeWidth: Float,
        style: DrawStyle,
    ) {
        setStyle(canvas, color.toHexString(), strokeWidth)

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
        color: Color,
        start: Position,
        end: Position,
        strokeWidth: Float,
        cap: CapStyle,
    ) {
        setStyle(canvas, color.toHexString(), strokeWidth, cap)
        canvas.run {
            beginPath()
            moveTo(start.x.value.toDouble(), start.y.value.toDouble())
            lineTo(end.x.value.toDouble(), end.y.value.toDouble())
            stroke()
        }
    }
}

internal fun Color.toHexString(): String {
    return "#" + rgba().joinToString("") { it.toString(16).padStart(2, padChar = '0') }
}
