import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import org.beatonma.orbitals.compose.ui.EditableOrbitals
import org.beatonma.orbitals.render.compose.rememberOrbitalsRenderEngine

import org.jetbrains.skiko.wasm.onWasmReady

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val persistence = UrlOptionsPersistence()

    onWasmReady {
        CanvasBasedWindow(canvasElementId = "orbitals") {
            val engine = rememberOrbitalsRenderEngine(persistence.options)

            EditableOrbitals(persistence.options, persistence, engine = engine)
        }
    }
}
