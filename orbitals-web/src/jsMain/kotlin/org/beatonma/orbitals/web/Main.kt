import kotlin.time.Duration
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.css.*
import org.beatonma.orbitals.render.OrbitalsRenderEngine
import org.beatonma.orbitals.render.diffRenderers
import org.beatonma.orbitals.render.getRenderers
import org.beatonma.orbitals.render.options.CapStyle
import org.beatonma.orbitals.render.options.DrawStyle
import org.beatonma.orbitals.render.options.Options
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import react.*
import react.dom.*
import styled.*

private val options = Options()

fun main() {
    render(document.body) {
        child(App)
    }
}

@OptIn(kotlin.time.ExperimentalTime::class)
val App = functionalComponent<PropsWithChildren> {
    val canvasRef = useRef(null)
    val (invalidated, setInvalidated) = useState(false)

    val (orbitals, setOrbitals) = useState {
        OrbitalsRenderEngine(
            renderers = getRenderers(options.visualOptions, JsCanvasDelegate),
            options = options,
            onOptionsChange = {
                renderers = diffRenderers(this, JsCanvasDelegate)
            }
        )
    }

    useEffect(arrayOf(invalidated)) {
        val currentCanvas = canvasRef.current as HTMLCanvasElement

        currentCanvas.width = window.innerWidth
        currentCanvas.height = window.innerHeight

        orbitals.onSizeChanged(currentCanvas.width, currentCanvas.height)
        val ctx = currentCanvas.getContext("2d") as CanvasRenderingContext2D

        ctx.apply {
            clearRect(0.0, 0.0, currentCanvas.width.toDouble(), currentCanvas.height.toDouble())
            lineWidth = options.visualOptions.strokeWidth.toDouble()
        }
        orbitals.update(ctx, Duration.milliseconds(15))

        window.setTimeout(timeout = 15, handler = {
            setInvalidated(!invalidated)
        })

        Unit
    }

    styledCanvas {
        css {
            position = Position.absolute
            top = 0.px
            left = 0.px
            background = options.visualOptions.colorOptions.background.toHexString()
        }

        attrs {
            ref = canvasRef
            onClick = {

            }
        }
    }
}
