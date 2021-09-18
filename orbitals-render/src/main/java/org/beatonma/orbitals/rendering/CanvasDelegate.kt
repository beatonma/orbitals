package org.beatonma.orbitals.rendering

import org.beatonma.orbitals.options.CapStyle
import org.beatonma.orbitals.options.DrawStyle
import org.beatonma.orbitals.physics.Distance
import org.beatonma.orbitals.physics.Position

interface CanvasDelegate<T> {
    fun drawCircle(
        canvas: T,
        position: Position,
        radius: Distance,
        color: Int,
        strokeWidth: Float,
        style: DrawStyle,
        alpha: Float = 1f,
    )

    fun drawLine(
        canvas: T,
        color: Int,
        start: Position,
        end: Position,
        strokeWidth: Float,
        cap: CapStyle,
        alpha: Float = 1f,
    )
}
