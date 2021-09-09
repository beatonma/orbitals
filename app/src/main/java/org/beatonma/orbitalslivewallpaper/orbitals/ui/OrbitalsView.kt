package org.beatonma.orbitalslivewallpaper.orbitals.ui

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import org.beatonma.orbitalslivewallpaper.orbitals.OrbitalsRenderEngine
import org.beatonma.orbitalslivewallpaper.orbitals.options.Options
import org.beatonma.orbitalslivewallpaper.orbitals.renderer.canvas.AndroidOrbitalsRenderer
import org.beatonma.orbitalslivewallpaper.orbitals.renderer.canvas.TrailRenderer
import org.beatonma.orbitalslivewallpaper.orbitals.timeIt

class OrbitalsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val options = Options()
    private val renderEngine = OrbitalsRenderEngine<Canvas>(
        renderers = listOf(
            TrailRenderer(15),
            AndroidOrbitalsRenderer(options.visualOptions),
        ),
        options = options,
    )

    init {
        setOnClickListener {
            renderEngine.engine.addBodies()
        }
    }

    private fun reset() {
        renderEngine.reset()
        renderEngine.engine.addBodies()
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
}
