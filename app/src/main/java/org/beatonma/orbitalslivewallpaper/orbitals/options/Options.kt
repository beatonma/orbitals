package org.beatonma.orbitalslivewallpaper.orbitals.options

import android.graphics.Paint
import androidx.compose.ui.res.integerArrayResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import org.beatonma.orbitals.options.PhysicsOptions


data class Options(
    val physics: PhysicsOptions = PhysicsOptions(),
    val visualOptions: VisualOptions = VisualOptions(),
    val frameRate: Int = 60,
)

data class VisualOptions(
    val focusCenterOfMass: Boolean = false,
    val showTraceLines: Boolean = false,
    val traceLineLength: Int = 25,
    val showAcceleration: Boolean = false,
    val drawStyle: DrawStyle = DrawStyle.Wireframe,
    val strokeWidth: Dp = 4.dp,
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
