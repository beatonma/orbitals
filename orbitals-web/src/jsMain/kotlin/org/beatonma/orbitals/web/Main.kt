package org.beatonma.orbitals.web

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import org.beatonma.orbitals.compose.ui.EditableOrbitals
import org.beatonma.orbitals.compose.ui.OrbitalsTheme
import org.beatonma.orbitals.render.compose.rememberOrbitalsRenderEngine

import org.jetbrains.skiko.wasm.onWasmReady

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val persistence = UrlOptionsPersistence()

    onWasmReady {
        CanvasBasedWindow(canvasElementId = "orbitals") {
            val engine = rememberOrbitalsRenderEngine(persistence.options)

            OrbitalsTheme(
                persistence.options.visualOptions.colorOptions,
                isDark = true,
            ) {
                EditableOrbitals(
                    settingsEnabled = persistence.webOptions.enableSettingsUI,
                    onSettingsEnabledChange = {
                        persistence.updateOption(
                            WebOptions.EnableSettingsUI,
                            it
                        )
                    },
                    settingsVisible = persistence.webOptions.showSettingsUI,
                    onSettingsVisibleChange = {
                        persistence.updateOption(
                            WebOptions.ShowSettingsUI,
                            it
                        )
                    },
                    options = persistence.options,
                    persistence = persistence,
                    engine = engine
                )
            }
        }
    }
}
