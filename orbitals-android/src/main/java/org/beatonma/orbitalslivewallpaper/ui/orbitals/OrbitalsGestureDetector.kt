package org.beatonma.orbitalslivewallpaper.ui.orbitals

import android.view.MotionEvent
import android.view.MotionEvent.PointerCoords
import org.beatonma.orbitals.core.physics.Position
import org.beatonma.orbitals.core.physics.metres
import org.beatonma.orbitals.render.interaction.OrbitalsGestureHandler

internal class OrbitalsGestureDetector(
    private val touch: OrbitalsGestureHandler<Int>,
) {
    private val coords = PointerCoords()

    private fun MotionEvent.getCoords(index: Int): PointerCoords {
        getPointerCoords(index, coords)
        return coords
    }

    fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> event.forEachPointer(touch::onDown)
            MotionEvent.ACTION_POINTER_DOWN -> touch.onDown(
                event.getPointerId(event.actionIndex),
                event.getCoords(event.actionIndex).toPosition()
            )

            MotionEvent.ACTION_MOVE -> event.forEachPointer(touch::onMove)
            MotionEvent.ACTION_UP -> event.forEachPointer { id, _ -> touch.onUp(id) }
            MotionEvent.ACTION_POINTER_UP -> touch.onUp(event.getPointerId(event.actionIndex))
        }

        return true
    }

    private fun MotionEvent.forEachPointer(block: (id: Int, position: Position) -> Unit) {
        for (index in 0 until pointerCount) {
            getPointerCoords(index, coords)
            block(getPointerId(index), coords.toPosition())
        }
    }
}

private fun PointerCoords.toPosition() = Position(x.metres, y.metres)
