package org.beatonma.orbitals.render.interaction

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.beatonma.orbitals.core.engine.Config
import org.beatonma.orbitals.core.engine.Region
import org.beatonma.orbitals.core.options.PhysicsOptions
import org.beatonma.orbitals.core.physics.GreatAttractor
import org.beatonma.orbitals.core.physics.Motion
import org.beatonma.orbitals.core.physics.Position
import org.beatonma.orbitals.core.physics.UniqueID
import org.beatonma.orbitals.core.physics.distanceSquaredTo
import org.beatonma.orbitals.core.physics.uniqueID
import org.beatonma.orbitals.core.squared
import org.beatonma.orbitals.core.util.currentTimeMillis
import org.beatonma.orbitals.render.OrbitalsRenderEngine

private const val TouchRegionSize = 250

private fun createTouchAttractor(position: Position, physics: PhysicsOptions) =
    GreatAttractor(
        id = uniqueID("touch"),
        mass = Config.getGreatAttractorMass(),
        density = physics.bodyDensity,
        motion = Motion(position),
    ).apply { isMortal = false }

private fun getTouchRegion(position: Position): Region {
    val x = position.x.value.toInt()
    val y = position.y.value.toInt()

    return Region(
        x - TouchRegionSize,
        y - TouchRegionSize,
        x + TouchRegionSize,
        y + TouchRegionSize
    )
}


/**
 * Multitouch gesture handler.
 *
 * Native touch inputs should call [onDown], [onMove] and [onUp] as appropriate.
 * Multitouch-capable tap, long-press and drag gestures will be detected internally.
 *
 * See [onTap], [onLongPress] and [onDrag] to change what happens when these gestures are detected.
 *
 * (Each pointer is currently treated as an individual gesture source - no pinching, etc.)
 */
class OrbitalsGestureHandler<PointerID>(
    private val scope: CoroutineScope,
    private val engine: OrbitalsRenderEngine<*>,
    private val touchSlop: Float,
    private val tapTimeout: Long = 300L,
) {
    private val pointerBodies: MutableMap<PointerID, UniqueID> = mutableMapOf()
    private val pointers: MutableMap<PointerID, Pointer> = mutableMapOf()

    // Gestures
    private fun onTap(position: Position) {
        engine.addBodies(getTouchRegion(position))
    }

    private fun onLongPress(id: PointerID, position: Position) {
        addBody(id, position)
    }

    private fun onDrag(id: PointerID, position: Position) {
        when (val pointerId = pointerBodies[id]) {
            null -> addBody(id, position)
            else -> engine.bodies.find { it.id == pointerId }?.let {
                it.position = position
            }
        }
    }
    // End of gestures

    fun onDown(id: PointerID, position: Position) {
        val now = currentTimeMillis()
        if (id !in pointers) {
            pointers[id] = Pointer(id, now, position)
        }
    }

    fun onUp(id: PointerID) {
        pointers[id]?.onUp()
        pointers.remove(id)
        pointerBodies.remove(id)?.let(engine::remove)
    }

    fun onMove(id: PointerID, position: Position) {
        pointers[id]?.onMove(position)
    }

    private fun addBody(id: PointerID, position: Position) {
        if (pointerBodies.contains(id)) return

        val body = createTouchAttractor(position, engine.options.physics)
        pointerBodies[id] = body.id
        engine.add(body)
    }

    private inner class Pointer(
        val id: PointerID,
        val downAtMillis: Long,
        val downAtPosition: Position
    ) {
        var hasMoved = false
        var isReleased = false
        var isLongPress = false

        private val longPressCheck = scope.launch {
            delay(tapTimeout)
            if (!isReleased && !hasMoved) {
                isLongPress = true
                onLongPress(id, downAtPosition)
            }
        }

        fun onUp() {
            isReleased = true
            longPressCheck.cancel()
            if (isLongPress) return

            if (hasMoved) {
                return
            }

            val now = currentTimeMillis()
            if (now - downAtMillis < tapTimeout) {
                onTap(downAtPosition)
            }
        }

        fun onMove(position: Position) {
            if (hasMoved) {
                onDrag(id, position)
                return
            }
            if (downAtPosition.distanceSquaredTo(position).value > touchSlop.squared) {
                hasMoved = true
                onDrag(id, position)
            }
        }
    }
}
