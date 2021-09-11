package org.beatonma.orbitalslivewallpaper.orbitals.options

import android.graphics.Paint

data class VisualOptions(
    val renderLayers: Set<RenderLayer> = setOf(RenderLayer.Default),
    val focusCenterOfMass: Boolean = false,
    val traceLineLength: Int = 25,
    val drawStyle: DrawStyle = DrawStyle.Wireframe,
    val strokeWidth: Float = 4f,
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


enum class RenderLayer {
    Default,
    Acceleration,
    Trails,
    Drip,
    ;
}
