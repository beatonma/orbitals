import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import kotlinx.browser.document
import kotlinx.browser.window
import org.beatonma.orbitals.core.physics.Position
import org.beatonma.orbitals.core.physics.UniqueID
import org.beatonma.orbitals.core.physics.metres
import org.beatonma.orbitals.core.util.currentTimeMillis
import org.beatonma.orbitals.core.util.info
import org.beatonma.orbitals.render.OrbitalsRenderEngine
import org.beatonma.orbitals.render.getRenderers
import org.beatonma.orbitals.render.touch.clearTouchBodies
import org.beatonma.orbitals.render.touch.createTouchAttractor
import org.beatonma.orbitals.render.touch.getTouchRegion
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.get
import org.w3c.dom.pointerevents.PointerEvent
import org.w3c.dom.url.URLSearchParams
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds


private data class Size(val width: Int, val height: Int)

fun main() {
    val options = createOptions(URLSearchParams(window.location.search))

    val canvas = document.getElementById("orbitals") as? HTMLCanvasElement
    val context: CanvasRenderingContext2D =
        canvas?.getContext("2d") as? CanvasRenderingContext2D
            ?: throw Exception("Failed to get canvas context")

    canvas.style.backgroundColor = options.visualOptions.colorOptions.background.toHexString()

    val orbitals = OrbitalsRenderEngine(
        renderers = getRenderers(
            options.visualOptions,
            JsCanvasDelegate
        ),
        options = options,
        onOptionsChange = {}
    )
    canvas.setupSize(canvas.dataset["fullscreen"]?.lowercase() == "true")
    canvas.setupTouchInteractions(orbitals)

    renderComposable("orbitals") {
        var size by remember { mutableStateOf(Size(1, 1)) }

        LaunchedEffect(size) {
            orbitals.onSizeChanged(size.width, size.height)
        }

        val duration = getFrameDuration()
        size = Size(canvas.width, canvas.height)
        context.clear()
        orbitals.update(context, duration)
    }
}

@Composable
private fun getFrameDuration(): Duration {
    var previousFrameMillis by remember { mutableStateOf(0L) }
    var frameMillis by remember { mutableStateOf(0L) }

    LaunchedEffect(frameMillis) {
        while (true) {
            withFrameMillis { frameTime ->
                frameMillis = frameTime - previousFrameMillis
                previousFrameMillis = frameTime
            }
        }
    }

    return frameMillis.milliseconds
}


private fun HTMLCanvasElement.setupSize(isFullscreen: Boolean) {
    val func: () -> Unit = when {
        isFullscreen -> {
            {
                width = window.innerWidth
                height = window.innerHeight
            }
        }

        else -> {
            {
                this.parentElement?.let {
                    width = it.clientWidth
                    height = it.clientHeight
                }
            }
        }
    }

    func()
    window.addEventListener("resize", { func() })
}

private fun CanvasRenderingContext2D.clear() {
    clearRect(0.0, 0.0, canvas.width.toDouble(), canvas.height.toDouble())
}


private fun HTMLCanvasElement.setupTouchInteractions(orbitals: OrbitalsRenderEngine<*>) {
    class Touch(val id: Int, val x: Int, val y: Int) {
        fun toPosition() = Position(x.metres, y.metres)
    }

    fun PointerEvent.toTouch() = Touch(pointerId, clientX, clientY)

    val tapMaxDuration = 200
    val activeTouches: MutableMap<Int, Touch> = mutableMapOf()
    val pointerBodies: MutableMap<Int, UniqueID> = mutableMapOf()
    var eventStart: Long? = null
    var timeoutId: Int? = null

    fun pointerEventListener(type: String, handler: (Touch) -> Unit) {
        timeoutId?.let(window::clearTimeout)
        timeoutId = null
        addEventListener(type, {
            handler((it as PointerEvent).toTouch())
        })
    }

    fun onTap(touch: Touch) {
        orbitals.addBodies(getTouchRegion(touch.toPosition()))
    }

    fun cancel(pointer: Touch) {
        val eventDuration = eventStart?.let { currentTimeMillis() - it } ?: 0L
        val touch = activeTouches.remove(pointer.id)
        clearTouchBodies(orbitals, pointerBodies)

        if (activeTouches.isEmpty() && eventDuration > 0 && touch != null) {
            if (eventDuration < tapMaxDuration) {
                onTap(touch)
            }
        }

        eventStart = null
    }

    fun ongoingTouch(touch: Touch) {
        eventStart ?: return

        val id = pointerBodies[touch.id]
        if (id == null) {
            val body = createTouchAttractor(touch.toPosition())
            pointerBodies[touch.id] = body.id
            orbitals.add(body)
        } else {
            val body = orbitals.bodies.find { it.id == id } ?: return
            body.position = touch.toPosition()
        }
    }

    pointerEventListener("pointerdown") { touch ->
        if (activeTouches.isEmpty()) {
            // This is the first active event
            eventStart = currentTimeMillis()

            timeoutId = window.setTimeout(
                { activeTouches[touch.id]?.let(::ongoingTouch) },
                tapMaxDuration
            )
        }
        activeTouches[touch.id] = touch
    }

    arrayOf("pointerup", "pointercancel", "pointerleave").forEach {
        pointerEventListener(it, ::cancel)
    }

    pointerEventListener("pointermove", ::ongoingTouch)
}
