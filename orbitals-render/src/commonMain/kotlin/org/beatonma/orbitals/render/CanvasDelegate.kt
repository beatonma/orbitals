package org.beatonma.orbitals.render

import org.beatonma.orbitals.render.options.CapStyle
import org.beatonma.orbitals.render.options.DrawStyle
import org.beatonma.orbitals.core.physics.Distance
import org.beatonma.orbitals.core.physics.Position

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
