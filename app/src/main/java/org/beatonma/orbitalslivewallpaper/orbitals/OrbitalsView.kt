package org.beatonma.orbitalslivewallpaper.orbitals

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import org.beatonma.orbitalslivewallpaper.orbitals.options.Options

class OrbitalsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val persistent: Boolean = true
    private val options = Options()
    private val persistence = Persistence(
        fade = true,
        backgroundColor = options.visualOptions.colorOptions.backgroundColor
    )

    private val renderer = AndroidOrbitalsRenderer(options)

    init {
        setOnClickListener {
            reset()
        }
    }

    private fun reset() {
        persistence.reset(width, height)

        renderer.reset()
        renderer.addBodies()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        with(renderer) {
            width = w
            height = h
        }

        super.onSizeChanged(w, h, oldw, oldh)

        reset()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        renderer.tick()

        if (canvas != null) {
            if (persistent) {
                persistence.draw(renderer, canvas)
            } else {
                renderer.draw(canvas)
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
        persistence.recycle()
    }
}
