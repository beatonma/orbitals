package org.beatonma.orbitalslivewallpaper.orbitals

import android.graphics.Canvas
import android.os.Handler
import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import android.view.SurfaceHolder
import org.beatonma.orbitalslivewallpaper.info
import org.beatonma.orbitalslivewallpaper.orbitals.options.Options
import org.beatonma.orbitalslivewallpaper.orbitals.renderer.AndroidOrbitalsRenderer
import org.beatonma.orbitalslivewallpaper.warn

private const val FPS = 60
private const val FrameDelay: Long = 1000L / FPS

class LwpService : WallpaperService() {
    override fun onCreateEngine() = LwpEngine()

    inner class LwpEngine : Engine() {
        private val options: Options = Options()
        private val handler: Handler = Handler(mainLooper)
        private val drawRunnable: Runnable = Runnable(this@LwpEngine::draw)
        private var visible = true
            set(value) {
                field = value
                when (value) {
                    true -> handler.post(drawRunnable)
                    false -> handler.removeCallbacks(drawRunnable)
                }
            }

        private val renderEngine = OrbitalsRenderEngine<Canvas>(
            renderers = listOf(
//                TrailRenderer(30),
                AndroidOrbitalsRenderer(options.visualOptions),
            ),
            options = options,
        )


        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)

            setTouchEventsEnabled(true)
            visible = true
            info("Lwp engine created")
        }

        override fun onTouchEvent(event: MotionEvent?) {
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> renderEngine.engine.addBodies()
                else -> super.onTouchEvent(event)
            }
        }

        private fun reset() {
            renderEngine.reset()
            renderEngine.engine.addBodies()
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            this.visible = visible
        }

        override fun onSurfaceChanged(
            holder: SurfaceHolder?,
            format: Int,
            width: Int,
            height: Int
        ) {
            renderEngine.onSizeChanged(width, height)
            reset()

            super.onSurfaceChanged(holder, format, width, height)
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder?) {
            super.onSurfaceDestroyed(holder)
            visible = false
            renderEngine.recycle()
        }

        private fun draw() {
            var canvas: Canvas? = null
            try {
                canvas = surfaceHolder.lockCanvas()
                canvas.drawColor(options.visualOptions.colorOptions.background)
                renderEngine.update(canvas)
            } catch (e: Exception) {
                warn(e)
            } finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas)
                }
            }

            scheduleNextFrame()
        }

        private fun scheduleNextFrame() {
            handler.removeCallbacks(drawRunnable)
            if (visible) {
                handler.postDelayed(drawRunnable, FrameDelay)
            }
        }
    }
}
