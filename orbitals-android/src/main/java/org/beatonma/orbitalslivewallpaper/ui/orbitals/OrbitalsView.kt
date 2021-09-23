package org.beatonma.orbitalslivewallpaper.ui.orbitals

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import org.beatonma.orbitals.render.android.AndroidCanvasDelegate
import org.beatonma.orbitals.render.OrbitalsRenderEngine
import org.beatonma.orbitals.render.diffRenderers
import org.beatonma.orbitals.render.getRenderers
import org.beatonma.orbitalslivewallpaper.Settings
import org.beatonma.orbitalslivewallpaper.dataStore
import org.beatonma.orbitalslivewallpaper.getSavedOptionsSync
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

class OrbitalsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val options = getSavedOptionsSync(context.dataStore(Settings.Wallpaper))
    private val renderEngine = OrbitalsRenderEngine(
        renderers = getRenderers(options.visualOptions, AndroidCanvasDelegate),
        options = options,
        onOptionsChange = {
            renderers = diffRenderers(this, AndroidCanvasDelegate)
        }
    )
    private val touchHandler = OrbitalsGestureDetector(context, renderEngine)
    private var lastFrameMillis = System.currentTimeMillis()

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

    @OptIn(ExperimentalTime::class)
    override fun onDraw(canvas: Canvas?) {
        val now = System.currentTimeMillis()
        val timeDelta = now - lastFrameMillis
        lastFrameMillis = now

        if (canvas != null) {
            canvas.drawColor((options.visualOptions.colorOptions.background) or 0xff000000.toInt())
            renderEngine.update(canvas, Duration.milliseconds(timeDelta))
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
    override fun onTouchEvent(event: MotionEvent): Boolean =
        touchHandler.onTouchEvent(event)
}
