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
import org.w3c.dom.url.URL
import react.*
import react.dom.*
import styled.*

private const val FrameDelay = 15

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
    val (frameTimeoutId, setFrameTimeoutId) = useState(0)
    val (backgroundColor, setBackgroundColor) = useState("#000000")

    val (orbitals, setOrbitals) = useState {
        val options = createOptions(URL(document.URL).searchParams)
        setBackgroundColor(options.visualOptions.colorOptions.background.toHexString())
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
        orbitals.update(ctx, Duration.milliseconds(FrameDelay))
        window.clearTimeout(frameTimeoutId)
        setFrameTimeoutId(
            window.setTimeout(
                timeout = FrameDelay,
                handler = { setInvalidated(!invalidated) }
            )
        )

        Unit
    }

    styledCanvas {
        css {
            position = Position.absolute
            top = 0.px
            left = 0.px
            background = backgroundColor
        }

        attrs {
            ref = canvasRef
            onClick = {
                println("clicked!")
            }
        }
    }
}
