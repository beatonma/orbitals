package org.beatonma.orbitals.desktop

import androidx.compose.runtime.remember
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.beatonma.orbitals.compose.ui.EditableOrbitals
import org.beatonma.orbitals.compose.ui.OrbitalsTheme
import org.beatonma.orbitals.render.compose.rememberOrbitalsRenderEngine

fun main() = application {
    val persistence = remember { PersistentOptions() }
    val engine = rememberOrbitalsRenderEngine(persistence.options)

    Window(
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(
            size = DpSize(1600.dp, 1200.dp),
        ),
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
            )
        }
    }
}
