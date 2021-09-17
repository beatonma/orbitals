package org.beatonma.orbitalslivewallpaper.orbitals.touch

import android.view.ViewConfiguration
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChangeConsumed
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.beatonma.orbitals.fastForEach
import org.beatonma.orbitals.physics.UniqueID
import org.beatonma.orbitalslivewallpaper.orbitals.OrbitalsRenderEngine


fun Modifier.orbitalsPointerInput(orbitals: OrbitalsRenderEngine<DrawScope>) = composed {
    val tapTimeout by remember { mutableStateOf(ViewConfiguration.getTapTimeout().toLong()) }
    val coroutineScope = rememberCoroutineScope()
    val pointerBodies: MutableMap<PointerId, UniqueID> by remember { mutableStateOf(mutableMapOf()) }
    var tapConsumed by remember { mutableStateOf(false) }

    fun clearBodies() {
        clearTouchBodies(orbitals, pointerBodies)
    }

    this
        .pointerInput(Unit) {
            detectDragMultitouch(
                onDragEnd = ::clearBodies,
                onDragCancel = ::clearBodies,
                onDrag = { change ->
                    if (!tapConsumed) {
                        return@detectDragMultitouch
                    }
                    val id = pointerBodies[change.id]

                    if (id == null) {
                        val body = createAttractor(change.position)
                        pointerBodies[change.id] = body.id
                        orbitals.addBody(body)
                    } else {
                        val body = orbitals.bodies.find { it.id == id } ?: return@detectDragMultitouch
                        body.position = change.position.toPosition()
                    }
                }
            )
        }
        .pointerInput(Unit) {
            detectTapGestures(
                onTap = { offset ->
                    if (tapConsumed) {
                        return@detectTapGestures
                    }

                    orbitals.addBodies(
                        getTouchRegion(offset)
                    )
                },
                onPress = {
                    tapConsumed = false

                    val job = coroutineScope.launch {
                        delay(tapTimeout)
                        tapConsumed = true
                    }

                    tryAwaitRelease()

                    job.cancel()
                    tapConsumed = false
                }
            )
        }
}


suspend fun PointerInputScope.detectDragMultitouch(
    onDragEnd: () -> Unit = { },
    onDragCancel: () -> Unit = { },
    onDrag: (change: PointerInputChange) -> Unit
) {
    forEachGesture {
        awaitPointerEventScope {
            awaitFirstDown(requireUnconsumed = false)
            do {
                val event = awaitPointerEvent()
                val cancelled = event.changes.any { it.positionChangeConsumed() }
                if (!cancelled) {
                    event.changes.fastForEach { change ->
                        if (change.pressed) {
                            onDrag(change)
                        }
                    }
                } else {
                    onDragCancel()
                }
            } while (!cancelled && event.changes.any { it.pressed })

            onDragEnd()
        }
    }
}
