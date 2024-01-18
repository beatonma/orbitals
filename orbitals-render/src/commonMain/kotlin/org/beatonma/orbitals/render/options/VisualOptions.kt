package org.beatonma.orbitals.render.options


data class VisualOptions(
    val renderLayers: Set<RenderLayer> = setOf(RenderLayer.Default),
    val focusCenterOfMass: Boolean = false,
    val traceLineLength: Int = 25,
    val drawStyle: DrawStyle = DrawStyle.Solid,
    val strokeWidth: Float = 1f,
    val bodyScale: Float = 1f,
    val colorOptions: ColorOptions = ColorOptions(),
)


enum class DrawStyle {
    Solid,
    Wireframe,
    ;
}


enum class CapStyle {
    Round,
    Butt,
    Square,
    ;
}


enum class RenderLayer {
    Default,
    Acceleration,
    Trails,
    ;
}
