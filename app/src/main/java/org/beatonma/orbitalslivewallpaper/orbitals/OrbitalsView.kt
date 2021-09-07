package org.beatonma.orbitalslivewallpaper.orbitals

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import org.beatonma.orbitalslivewallpaper.orbitals.options.Options
import kotlin.math.max

class OrbitalsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val persistent: Boolean = true
    private val fadePersistence: Boolean = true
    private val persistentCanvas: Canvas = Canvas()
    private var bitmap: Bitmap? = null
    private val persistentPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val options = Options()
    private val backgroundColor = options.visualOptions.colorOptions.backgroundColor

    private val renderer = AndroidOrbitalsRenderer(options)

    init {
        setOnClickListener {
            reset()
        }
    }

    private fun reset() {
        bitmap?.recycle()
        if (persistent) {
            bitmap = Bitmap.createBitmap(max(1, width), max(1, height), Bitmap.Config.RGBA_F16)
            persistentCanvas.setBitmap(bitmap)
            persistentCanvas.drawColor(backgroundColor)
        }

        renderer.reset()
        renderer.addBodies()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        with(renderer) {
            width = w
            height = h
        }

        reset()

        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        renderer.tick()

        if (canvas != null) {
            val bm = bitmap
            if (persistent && bm != null) {
                drawPersistent(canvas, bm)
            } else {
                renderer.draw(canvas)
            }
        }

        postInvalidateOnAnimation()
    }

    private fun drawPersistent(canvas: Canvas, bitmap: Bitmap) {
        renderer.drawBackground(canvas)

        if (fadePersistence) {
            persistentCanvas.drawColor(backgroundColor.withAlpha(5), BlendMode.DST_OUT)
        }
        renderer.drawForeground(persistentCanvas)

        canvas.drawBitmap(bitmap, 0f, 0f, persistentPaint)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        reset()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        bitmap?.recycle()
        bitmap = null
    }
}

private fun Int.withAlpha(alpha: Int): Int =
    Color.argb(alpha, Color.red(this), Color.green(this), Color.blue(this))
