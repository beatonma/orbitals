package org.beatonma.orbitalslivewallpaper.orbitals.options

import android.graphics.Paint
import org.beatonma.orbitals.options.PhysicsOptions


data class Options(
    val frameRate: Int = 60,
    val physics: PhysicsOptions = PhysicsOptions(),
    val visualOptions: VisualOptions = VisualOptions(),
)

data class VisualOptions(
    val focusCenterOfMass: Boolean = false,
    val showTraceLines: Boolean = false,
    val traceLineLength: Int = 25,
    val showAcceleration: Boolean = false,
    val drawStyle: DrawStyle = DrawStyle.Wireframe,
    val wireframe: Boolean = false,
    val colorOptions: ColorOptions = ColorOptions(),
)

enum class DrawStyle {
    Solid,
    Wireframe,
    ;

    fun setUp(paint: Paint) {
        when (this) {
            Solid -> paint.style = Paint.Style.FILL
            Wireframe -> {
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 4f
            }
        }
    }
}
