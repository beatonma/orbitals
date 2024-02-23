package org.beatonma.orbitals.render.compose

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import org.beatonma.orbitals.core.fastForEach
import org.beatonma.orbitals.core.physics.Position
import org.beatonma.orbitals.core.physics.metres
import org.beatonma.orbitals.render.interaction.OrbitalsGestureHandler


fun Modifier.orbitalsPointerInput(touch: OrbitalsGestureHandler<PointerId>) =
    pointerInput(Unit) { detectTouchEvents(touch) }

private suspend fun PointerInputScope.detectTouchEvents(
    touch: OrbitalsGestureHandler<PointerId>
) {
    awaitEachGesture {
        val down = awaitFirstDown().apply(PointerInputChange::consume)

        touch.onDown(down.id, down.getPosition())

        do {
            val event = awaitPointerEvent()
            event.changes.fastForEach { change ->
                if (change.pressed) {
                    touch.onDown(change.id, change.getPosition())
                    touch.onMove(change.id, change.getPosition())
                } else {
                    touch.onUp(change.id)
                }
            }
        } while (event.changes.any { it.pressed })
    }
}

private fun PointerInputChange.getPosition(): Position =
    this.position.run { Position(x.metres, y.metres) }
