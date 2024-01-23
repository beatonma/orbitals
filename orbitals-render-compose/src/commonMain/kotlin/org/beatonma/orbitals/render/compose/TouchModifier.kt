package org.beatonma.orbitals.render.compose

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalViewConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.beatonma.orbitals.core.fastForEach
import org.beatonma.orbitals.core.physics.Position
import org.beatonma.orbitals.core.physics.UniqueID
import org.beatonma.orbitals.core.physics.metres
import org.beatonma.orbitals.render.OrbitalsRenderEngine
import org.beatonma.orbitals.render.touch.createTouchAttractor
import org.beatonma.orbitals.render.touch.getTouchRegion


fun Modifier.orbitalsPointerInput(
    orbitals: OrbitalsRenderEngine<*>,
    tapTimeout: Long = 300L,
) = composed {
    val scope = rememberCoroutineScope()
    val touchSlop = LocalViewConfiguration.current.touchSlop

    // A record of bodies which are associated with an active pointer so they can be removed
    // when the action finishes.
    val pointerBodies: MutableMap<PointerId, UniqueID> = remember { mutableMapOf() }

    fun addBody(change: PointerInputChange) {
        val body = createTouchAttractor(change.position.toPosition(), orbitals.options.physics)
        pointerBodies[change.id] = body.id
        orbitals.add(body)
    }

    this
        .pointerInput(Unit) {
            detectTouchEvents(
                scope,
                touchSlop = touchSlop,
                tapTimeout = tapTimeout,
                onTap = {
                    orbitals.addBodies(getTouchRegion(it.position.toPosition()))
                },
                onLongPress = ::addBody,
                onDragEnd = { change ->
                    pointerBodies.remove(change.id)?.let(orbitals::remove)
                },
                onDrag = { change ->
                    when (val id = pointerBodies[change.id]) {
                        null -> addBody(change)
                        else -> orbitals.bodies.find { it.id == id }?.let {
                            it.position = change.position.toPosition()
                        }
                    }
                }
            )
        }
}


private typealias OnPointerChange = (change: PointerInputChange) -> Unit

private suspend fun PointerInputScope.detectTouchEvents(
    scope: CoroutineScope,
    touchSlop: Float,
    tapTimeout: Long,
    onTap: OnPointerChange,
    onLongPress: OnPointerChange,
    onDragEnd: OnPointerChange,
    onDrag: OnPointerChange
) {
    awaitEachGesture {
        val down = awaitFirstDown().apply(PointerInputChange::consume)

        var hasMoved = false
        var isReleased = false
        var isMultitouch = false

        val onLongPressTimeout = scope.launch {
            delay(tapTimeout)
            if (!isReleased && !hasMoved) {
                // Long press
                onLongPress(down)
            }
        }

        do {
            // Handle dragging
            val event = awaitPointerEvent()

            event.changes.fastForEach { change ->
                when (change.id) {
                    down.id -> {
                        // Compare squared values to avoid sqrt() calls
                        if ((change.position - down.position).getDistanceSquared() > (touchSlop * touchSlop)) {
                            hasMoved = true
                        }
                    }

                    else -> isMultitouch = true
                }

                when {
                    change.pressed -> onDrag(change)
                    else -> onDragEnd(change)
                }
            }
        } while (event.changes.any { it.pressed })

        isReleased = true
        if (!onLongPressTimeout.isCompleted) {
            onLongPressTimeout.cancel()
            if (!isMultitouch && !hasMoved) {
                onTap(down)
            }
        }
    }
}

private fun Offset.toPosition() = Position(x.metres, y.metres)
