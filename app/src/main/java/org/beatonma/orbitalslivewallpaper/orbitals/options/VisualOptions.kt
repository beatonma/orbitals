package org.beatonma.orbitalslivewallpaper.orbitals.options

import android.graphics.Paint
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke

private const val DefaultStrokeWidth = 4f


data class VisualOptions(
    val renderLayers: Set<RenderLayer> = setOf(RenderLayer.Default),
    val focusCenterOfMass: Boolean = false,
    val traceLineLength: Int = 25,
    val drawStyle: DrawStyle = DrawStyle.Wireframe,
    val strokeWidth: Float = DefaultStrokeWidth,
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
                paint.strokeWidth = DefaultStrokeWidth
            }
        }
    }

    fun toComposeDrawStyle(
        strokeWidth: Float = DefaultStrokeWidth,
        strokeCap: StrokeCap = StrokeCap.Round,
        strokeJoin: StrokeJoin = StrokeJoin.Round
    ) = when (this) {
        Solid -> androidx.compose.ui.graphics.drawscope.Fill
        Wireframe -> Stroke(
            width = strokeWidth,
            cap = strokeCap,
            join = strokeJoin,
        )
    }
}


enum class RenderLayer {
    Default,
    Acceleration,
    Trails,
    Drip,
    ;
}
