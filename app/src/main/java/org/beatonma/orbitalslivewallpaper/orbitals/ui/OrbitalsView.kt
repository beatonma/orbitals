package org.beatonma.orbitalslivewallpaper.orbitals.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GestureDetectorCompat
import org.beatonma.orbitals.rendering.getRenderers
import org.beatonma.orbitalslivewallpaper.orbitals.MotionEventTouchHandler
import org.beatonma.orbitalslivewallpaper.orbitals.OrbitalsRenderEngine
import org.beatonma.orbitalslivewallpaper.orbitals.diffRenderers
import org.beatonma.orbitalslivewallpaper.orbitals.options.Options
import org.beatonma.orbitalslivewallpaper.orbitals.timeIt

class OrbitalsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val options = Options()
    private val renderEngine = OrbitalsRenderEngine<Canvas>(
        renderers = getRenderers(options.visualOptions),
        options = options,
        onOptionsChange = {
            renderers = diffRenderers(this)
        }
    )
    private val touchHandler = GestureDetectorCompat(context, MotionEventTouchHandler(renderEngine))

    init {
        setOnClickListener {
            renderEngine.addBodies()
        }
    }

    private fun reset() {
        renderEngine.clear()
        renderEngine.addBodies()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        if (w != oldw && h != oldh) {
            renderEngine.onSizeChanged(w, h)
            reset()
        }

        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas?) {
        timeIt(maxMillis = 15, label = "OrbitalsView.onDraw") {
            if (canvas != null) {
                renderEngine.update(canvas)
            }
        }
        postInvalidateOnAnimation()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        reset()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        renderEngine.recycle()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return touchHandler.onTouchEvent(event)
    }
}
