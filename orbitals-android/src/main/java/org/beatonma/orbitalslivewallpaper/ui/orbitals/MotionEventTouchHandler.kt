package org.beatonma.orbitalslivewallpaper.ui.orbitals

import android.content.Context
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.MotionEvent
import android.view.ViewConfiguration
import org.beatonma.orbitals.core.physics.Distance
import org.beatonma.orbitals.core.physics.Position
import org.beatonma.orbitals.core.physics.UniqueID
import org.beatonma.orbitals.core.physics.distanceTo
import org.beatonma.orbitals.core.physics.metres
import org.beatonma.orbitals.render.OrbitalsRenderEngine
import org.beatonma.orbitals.render.touch.clearTouchBodies
import org.beatonma.orbitals.render.touch.createTouchAttractor
import org.beatonma.orbitals.render.touch.getTouchRegion


internal class OrbitalsGestureDetector(
    context: Context,
    private val orbitals: OrbitalsRenderEngine<Canvas>
) {
    private val handler = OrbitalsGestureHandler(context.mainLooper)

    private var downPosition: Position? = null
    private var downTimestamp: Long? = null
    private var singlePointerGesture: Boolean = false
    private var mightBeSingleTap: Boolean = false
    private var stillDown: Boolean = false
    private var alwaysInTapRegion: Boolean = false
    private var alwaysInDoubleTapRegion: Boolean = false

    private var bodies: MutableMap<PointerID, UniqueID> = mutableMapOf()
    private val coords = MotionEvent.PointerCoords()

    private val tapTimeout: Long = ViewConfiguration.getTapTimeout().toLong()
    private val doubleTapTimeout: Long = ViewConfiguration.getDoubleTapTimeout().toLong()
    private val touchSlop: Distance
    private val doubleTapSlop: Distance

    init {
        with(ViewConfiguration.get(context)) {
            touchSlop = scaledTouchSlop.toFloat().metres
            doubleTapSlop = scaledDoubleTapSlop.toFloat().metres
        }
    }

    private fun resetTouchHandler() {
        stillDown = false
        mightBeSingleTap = false
        singlePointerGesture = false
        downPosition = null
        alwaysInTapRegion = false
        alwaysInDoubleTapRegion = false

        clearBodies()
    }

    fun onTouchEvent(event: MotionEvent): Boolean {
        val f = when (event.actionMasked) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> ::onUp
            MotionEvent.ACTION_MOVE -> ::onMove
            MotionEvent.ACTION_DOWN -> ::onDown
            MotionEvent.ACTION_POINTER_DOWN -> ::onPointerDown
            else -> null
        }

        f?.invoke(event)

        return true
    }

    private fun onDown(event: MotionEvent): Boolean {
        downTimestamp = System.currentTimeMillis()
        stillDown = true
        downPosition = event.toPosition()

        singlePointerGesture = true
        mightBeSingleTap = true
        alwaysInTapRegion = true
        alwaysInDoubleTapRegion = true

        event.refreshPointers()

        handler.sendEmptyMessageDelayed(GestureMessage.Tap, tapTimeout)

        return true
    }

    private fun onPointerDown(event: MotionEvent): Boolean {
        stillDown = true
        singlePointerGesture = false
        mightBeSingleTap = false

        event.refreshPointers()

        return true
    }

    private fun onMove(event: MotionEvent): Boolean {
        event.refreshPointers()

        val distanceFromDownPosition = downPosition?.distanceTo(event.toPosition()) ?: return false

        if (distanceFromDownPosition > touchSlop) {
            alwaysInTapRegion = false
        }
        if (distanceFromDownPosition > doubleTapSlop) {
            alwaysInDoubleTapRegion = false
        }

        return true
    }

    private fun onUp(event: MotionEvent): Boolean {
        stillDown = false

        if (mightBeSingleTap && alwaysInTapRegion) {
            handler.removeMessages(GestureMessage.Tap)
            return onSingleTapConfirmed(event)
        }

        clearBodies()

        return true
    }

    private fun onSingleTapConfirmed(event: MotionEvent): Boolean {
        orbitals.addBodies(getTouchRegion(event.toPosition()))

        resetTouchHandler()

        return true
    }

    private fun clearBodies() {
        clearTouchBodies(orbitals, bodies)
    }

    private fun MotionEvent.refreshPointers(): List<PointerID> {
        val now = System.currentTimeMillis()
        val delta = now - (downTimestamp ?: now)
        if (delta < tapTimeout) {
            return listOf()
        }

        val pointers = (0 until pointerCount).map { index ->
            getPointerCoords(index, coords)

            val pointerId = PointerID(getPointerId(index))

            val id = bodies[pointerId]
            if (id == null) {
                val body = createTouchAttractor(coords.toPosition())
                bodies[pointerId] = body.id
                orbitals.add(body)
            } else {
                val body =
                    orbitals.bodies.find { it.id == id } ?: throw Exception("Cannot find body: $id")
                body.position = coords.toPosition()
            }

            pointerId
        }

        // Forget pointers that have been removed
        bodies.keys.filter { it !in pointers }.forEach { removedPointerId ->
            val bodyId = bodies[removedPointerId]

            bodies.remove(removedPointerId)
            if (bodyId != null) {
                orbitals.remove(bodyId)
            }
        }

        if (pointers.size > 1) {
            singlePointerGesture = false
        }

        return pointers
    }

    private inner class OrbitalsGestureHandler(looper: Looper) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            val message = GestureMessage.entries.getOrNull(msg.what) ?: return

            when (message) {
                GestureMessage.Tap -> {
                    mightBeSingleTap = false
                }

                GestureMessage.LongPress -> {

                }
            }
            super.handleMessage(msg)
        }

        fun sendEmptyMessageDelayed(type: GestureMessage, delay: Long) {
            sendEmptyMessageDelayed(type.ordinal, delay)
        }

        fun removeMessages(type: GestureMessage) {
            removeMessages(type.ordinal)
        }
    }
}


@JvmInline
private value class PointerID(val value: Int)


private enum class GestureMessage {
    Tap,
    LongPress,
    ;
}

private fun MotionEvent.toPosition() = Position(x.metres, y.metres)
private fun MotionEvent.PointerCoords.toPosition() = Position(x.metres, y.metres)
