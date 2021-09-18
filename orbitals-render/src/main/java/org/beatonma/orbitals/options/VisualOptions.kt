package org.beatonma.orbitals.options


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
    Drip,
    ;
}
