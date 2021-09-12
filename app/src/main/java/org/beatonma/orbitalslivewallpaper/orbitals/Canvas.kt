package org.beatonma.orbitalslivewallpaper.orbitals

import android.graphics.Canvas
import android.graphics.Paint
import org.beatonma.orbitals.physics.Distance
import org.beatonma.orbitals.physics.Position

internal fun Canvas.drawCircle(position: Position, radius: Distance, paint: Paint) {
    drawCircle(position.x.value, position.y.value, radius.value, paint)
}
