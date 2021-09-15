package org.beatonma.orbitalslivewallpaper.orbitals.render.util

import androidx.compose.ui.graphics.Path
import org.beatonma.orbitals.physics.Position

fun Path.lineTo(position: Position) {
    lineTo(position.x.value, position.y.value)
}

fun Path.moveTo(position: Position) {
    moveTo(position.x.value, position.y.value)
}
