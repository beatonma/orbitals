package org.beatonma.orbitalslivewallpaper.orbitals.renderer.canvas

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BlendMode
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.os.Build
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.beatonma.orbitals.RectangleSpace
import org.beatonma.orbitals.physics.Body
import org.beatonma.orbitals.physics.Distance
import org.beatonma.orbitals.physics.metres
import org.beatonma.orbitalslivewallpaper.orbitals.drawCircle
import org.beatonma.orbitalslivewallpaper.orbitals.options.VisualOptions
import org.beatonma.orbitalslivewallpaper.orbitals.timeIt
import org.beatonma.orbitalslivewallpaper.warn
import kotlin.math.max

class BitmapTrailRenderer(
    options: VisualOptions,
    private val fade: Boolean = true,
    private val dotSize: Distance = 4.metres,
) : OrbitalsRenderer<Canvas> {
    private val persistentCanvas: Canvas = Canvas()
    private var bitmap: Bitmap? = null
    private val nnBitmap: Bitmap get() = bitmap!!
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
    }
    private val backgroundColor: Int = options.colorOptions.background.withAlpha(5)
    private val isSdkQ: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    private val scope = CoroutineScope(Dispatchers.Main)

    private fun recreateBitmap(width: Int, height: Int) {
        warn("createBitmap($width, $height)")
        val w = max(1, width)
        val h = max(1, height)
        bitmap?.recycle()
        bitmap = Bitmap.createBitmap(
            w,
            h,
            Bitmap.Config.ALPHA_8
//            Bitmap.Config.RGB_565
//            Bitmap.Config.ALPHA_8
        )
        persistentCanvas.setBitmap(bitmap)

        scope.launch {
            fadeOutAsync()
        }
    }

    override fun reset(space: RectangleSpace) {
        recreateBitmap(space.width, space.height)
    }

    override fun recycle() {
        bitmap?.recycle()
        bitmap = null
        scope.cancel()
    }

    private fun Int.withAlpha(alpha: Int): Int =
        Color.argb(alpha, Color.red(this), Color.green(this), Color.blue(this))

    override fun drawBackground(canvas: Canvas, bodies: List<Body>) {
        timeIt(enabled = false) {
            if (fade) {
                fadeOut()
            }

            bodies.forEach { body ->
                drawBody(persistentCanvas, body)
            }

            canvas.drawBitmap(nnBitmap, 0f, 0f, paint)
        }
    }

    override fun drawForeground(canvas: Canvas, bodies: List<Body>) {}

    override fun drawBody(canvas: Canvas, body: Body) {
        canvas.drawCircle(
            body.position,
            dotSize,
            paint,
        )
    }

    @SuppressLint("NewApi")
    private fun fadeOut() {
//        timeIt(10, "fadeOut") {
//            if (fadeFrame++ > fadeFrequency) {
//                if (isSdkQ) {
//                    persistentCanvas.drawColor(backgroundColor, BlendMode.DST_OUT)
//                } else {
//                    persistentCanvas.drawColor(backgroundColor, PorterDuff.Mode.DST_OUT)
//                }
//                fadeFrame = 0
//            }
//        }
    }

    @SuppressLint("NewApi")
    suspend fun fadeOutAsync() {
        while(true) {
            if (isSdkQ) {
                persistentCanvas.drawColor(backgroundColor, BlendMode.DST_OUT)
            } else {
                persistentCanvas.drawColor(backgroundColor, PorterDuff.Mode.DST_OUT)
            }
            delay(100)
        }
    }
}
