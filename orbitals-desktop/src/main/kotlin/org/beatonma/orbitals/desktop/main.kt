package org.beatonma.orbitals.desktop

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import org.beatonma.orbitals.render.compose.Orbitals
import org.beatonma.orbitals.render.options.Options

fun main() = application {
    val options by remember { mutableStateOf(Options()) }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Orbitals"
    ) {
        Orbitals(
            options,
            Modifier
                .background(Color.DarkGray)
                .fillMaxSize(),
        )
    }
}
