import kotlin.time.Duration
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.css.*
import org.beatonma.orbitals.core.physics.Body
import org.beatonma.orbitals.render.OrbitalsRenderEngine
import org.beatonma.orbitals.render.diffRenderers
import org.beatonma.orbitals.render.getRenderers
import org.beatonma.orbitals.render.options.CapStyle
import org.beatonma.orbitals.render.options.DrawStyle
import org.beatonma.orbitals.render.options.Options
import org.beatonma.orbitals.render.touch.clearTouchBodies
import org.beatonma.orbitals.render.touch.createTouchAttractor
import org.beatonma.orbitals.render.touch.getTouchRegion
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.url.URL
import react.*
import react.dom.*
import styled.*
import kotlinx.html.Tag
import kotlinx.html.CommonAttributeGroupFacade

private const val ClickTimeout = 100

private val options = Options()

fun main() {

    render(document.getElementById("orbitals")) {
        child(App)
    }
}

@OptIn(kotlin.time.ExperimentalTime::class)
val App = functionalComponent<PropsWithChildren> {
    val canvasRef = useRef(null)
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

    useEffect {
        var previousTimestamp = 0.0
        var requestId = 0

        fun render(timestamp: Double) {
            val currentCanvas = canvasRef.current as HTMLCanvasElement

            val timeDelta = timestamp - previousTimestamp
            previousTimestamp = timestamp

            currentCanvas.width = window.innerWidth
            currentCanvas.height = window.innerHeight

            orbitals.onSizeChanged(currentCanvas.width, currentCanvas.height)
            val ctx = currentCanvas.getContext("2d") as CanvasRenderingContext2D

            ctx.apply {
                clearRect(0.0, 0.0, currentCanvas.width.toDouble(), currentCanvas.height.toDouble())
                lineWidth = options.visualOptions.strokeWidth.toDouble()
            }

            orbitals.update(ctx, Duration.milliseconds(timeDelta))

            requestId = window.requestAnimationFrame { timestamp -> render(timestamp) }
        }

        render(0.0)

        cleanup {
            window.cancelAnimationFrame(requestId)
        }
    }

    var touchBody: Body? = null
    var mouseDownAt: Double = 0.0
    var mouseDown = false
    var mouseDownPosition: org.beatonma.orbitals.core.physics.Position? = null
    var mouseDownTimeoutId = 0

    styledCanvas {
        css {
            position = Position.absolute
            top = 0.px
            left = 0.px
            background = backgroundColor
        }

        attrs {
            ref = canvasRef

            touchEvents(orbitals)
        }
    }
}

private fun CommonAttributeGroupFacade.touchEvents(orbitals: OrbitalsRenderEngine<CanvasRenderingContext2D>) {
    var touchBody: Body? = null
    var mouseDownAt: Double = 0.0
    var mouseDown = false
    var mouseDownPosition: org.beatonma.orbitals.core.physics.Position? = null
    var mouseDownTimeoutId = 0

    val onDown: (timestamp: Double, x: Number, y: Number) -> Unit = { timestamp, x, y ->
        mouseDownPosition =
            org.beatonma.orbitals.core.physics.Position(x, y)
        mouseDown = true
        mouseDownAt = timestamp
        mouseDownTimeoutId = window.setTimeout(
            timeout = ClickTimeout,
            handler = {
                val body = touchBody
                val downPosition = mouseDownPosition
                if (body == null && downPosition != null) {
                    val b = createTouchAttractor(downPosition)
                    orbitals.addBody(b)
                    touchBody = b
                }
            },
        )
    }

    val onMove: (timestamp: Double, x: Number, y: Number) -> Unit = { timestamp, x, y ->
        window.clearTimeout(mouseDownTimeoutId)
        val position = org.beatonma.orbitals.core.physics.Position(x, y)
        if (mouseDown) {
            val body = touchBody
            if (body == null) {
                if (timestamp - mouseDownAt > ClickTimeout) {
                    val b = createTouchAttractor(position)
                    orbitals.addBody(b)
                    touchBody = b
                }
            } else {
                orbitals.bodies.find { it.id == body.id }?.position = position
            }
        }
    }


    val onUp: () -> Unit = {
        window.clearTimeout(mouseDownTimeoutId)
        mouseDown = false

        val body = touchBody
        if (body != null) {
            orbitals.removeBody(body.id)
            touchBody = null
        }
    }

    onClick = { e ->
        orbitals.addBodies(
            getTouchRegion(
                org.beatonma.orbitals.core.physics.Position(e.clientX, e.clientY)
            )
        )
    }

    onMouseDown = { e ->
        onDown(e.timeStamp, e.clientX, e.clientY)
    }

    onTouchStart = { e ->
        val ev = e.touches.item(0)
        if (ev != null) {
            onDown(e.timeStamp, ev.clientX, ev.clientY)
        }
    }

    onMouseMove = { e ->
        onMove(e.timeStamp, e.clientX, e.clientY)
    }

    onTouchMove = { e ->
        val ev = e.touches.item(0)
        if (ev != null) {
            onMove(e.timeStamp, ev.clientX, ev.clientY)
        }
    }

    onMouseUp = { e ->
        onUp()
    }

    onTouchEnd = { e ->
        onUp()
    }

    onTouchCancel = { e ->
        onUp()
    }
}
