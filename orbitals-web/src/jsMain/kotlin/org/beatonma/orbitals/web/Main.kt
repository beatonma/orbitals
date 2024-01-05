
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import kotlinx.browser.document
import kotlinx.browser.window
import org.beatonma.orbitals.render.OrbitalsRenderEngine
import org.beatonma.orbitals.render.getRenderers
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.get
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

    canvas.setupSize(canvas.dataset["fullscreen"]?.lowercase() == "true")
    canvas.style.backgroundColor = options.visualOptions.colorOptions.background.toHexString()

    renderComposable("orbitals") {
        var size by remember { mutableStateOf(Size(1, 1)) }

        val orbitals = remember {
            OrbitalsRenderEngine(
                renderers = getRenderers(
                    options.visualOptions,
                    JsCanvasDelegate
                ),
                options = options,
                onOptionsChange = { opts -> }
            )
        }

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
        isFullscreen -> {{
            width = window.innerWidth
            height = window.innerHeight
        }}
        else -> {{
            this.parentElement?.let {
                width = it.clientWidth
                height = it.clientHeight
            }
        }}
    }

    func()
    window.addEventListener("resize", { func() })
}

private fun CanvasRenderingContext2D.clear() {
    clearRect(0.0, 0.0, canvas.width.toDouble(), canvas.height.toDouble())
}
