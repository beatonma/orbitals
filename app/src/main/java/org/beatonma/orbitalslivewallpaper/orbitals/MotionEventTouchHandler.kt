package org.beatonma.orbitalslivewallpaper.orbitals

import android.graphics.Canvas
import android.view.GestureDetector
import android.view.MotionEvent
import org.beatonma.orbitals.engine.Region


class MotionEventTouchHandler(
    private val renderEngine: OrbitalsRenderEngine<Canvas>,
) : GestureDetector.OnGestureListener,
    GestureDetector.OnDoubleTapListener {
    override fun onDown(position: MotionEvent?): Boolean {
        return false
    }

    override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
        return false
    }

    override fun onDoubleTap(position: MotionEvent?): Boolean {
        return false
    }

    override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
        return false
    }

    override fun onLongPress(position: MotionEvent?) {
        renderEngine.clear()
    }

    override fun onShowPress(e: MotionEvent?) {

    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        if (e != null) {
            val x = e.x.toInt()
            val y = e.y.toInt()
            renderEngine.addBodies(
                Region(
                    x - 250,
                    y - 250,
                    x + 250,
                    y + 250,
                )
            )
            return true
        }
        return false
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        return false
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        return false
    }
}
