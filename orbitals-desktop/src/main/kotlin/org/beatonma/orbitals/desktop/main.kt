package org.beatonma.orbitals.desktop

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.beatonma.orbitals.compose.ui.EditableOrbitals
import org.beatonma.orbitals.compose.ui.OrbitalsTheme
import org.beatonma.orbitals.compose.ui.components.ButtonData
import org.beatonma.orbitals.render.compose.rememberOrbitalsRenderEngine
import java.awt.Toolkit


fun main() = application {
    val persistence = remember { PersistentOptions() }
    val engine = rememberOrbitalsRenderEngine(persistence.options)

    var isFullscreen by remember { mutableStateOf(false) }
    val density = LocalDensity.current
    val windowState = rememberWindowState(
        size = getDisplaySize(density) / 2,
        placement = WindowPlacement.Floating,
    )

    LaunchedEffect(isFullscreen) {
        windowState.placement = when {
            isFullscreen -> WindowPlacement.Fullscreen
            else -> WindowPlacement.Floating
        }
    }

    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = "Orbitals",
    ) {
        OrbitalsTheme(
            persistence.options.visualOptions.colorOptions,
            isDark = true,
        ) {
            EditableOrbitals(
                persistence.options,
                persistence,
                engine = engine,
                uiButtons = listOf(
                    ButtonData(
                        icon = when {
                            isFullscreen -> Icons.Default.FullscreenExit
                            else -> Icons.Default.Fullscreen
                        },
                        text = null,
                        iconContentDescription = null,
                    ) { isFullscreen = !isFullscreen }
                )
            )
        }
    }
}


private fun getDisplaySize(density: Density): DpSize = density.run {
    with(Toolkit.getDefaultToolkit().screenSize) {
        DpSize(
            width.toDp(),
            height.toDp()
        )
    }
}
