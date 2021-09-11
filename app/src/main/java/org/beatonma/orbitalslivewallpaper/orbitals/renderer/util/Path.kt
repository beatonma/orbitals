package org.beatonma.orbitalslivewallpaper.orbitals.renderer.util

import androidx.compose.ui.graphics.Path
import org.beatonma.orbitals.physics.Position

fun Path.lineTo(position: Position) {
    lineTo(position.x.metres, position.y.metres)
}

fun Path.moveTo(position: Position) {
    moveTo(position.x.metres, position.y.metres)
}
