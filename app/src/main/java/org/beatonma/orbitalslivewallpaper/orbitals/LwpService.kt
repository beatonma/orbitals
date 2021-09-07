package org.beatonma.orbitalslivewallpaper.orbitals

import android.graphics.Canvas
import android.os.Handler
import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import android.view.SurfaceHolder
import org.beatonma.orbitalslivewallpaper.info
import org.beatonma.orbitalslivewallpaper.orbitals.options.Options
import org.beatonma.orbitalslivewallpaper.warn

private const val FPS = 60
private const val FrameDelay: Long = 1000L / FPS

class LwpService : WallpaperService() {
    override fun onCreateEngine() = LwpEngine()

    inner class LwpEngine : Engine() {
        private val persistent: Boolean = false
        private val options = Options()
        private val renderer: AndroidOrbitalsRenderer = AndroidOrbitalsRenderer(options)
        private val width: Int get() = renderer.width
        private val height: Int get() = renderer.height

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

        private val persistence = Persistence(
            fade = true,
            backgroundColor = options.visualOptions.colorOptions.backgroundColor
        )


        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)

            setTouchEventsEnabled(true)
            visible = true
            info("Lwp engine created")
        }

        override fun onTouchEvent(event: MotionEvent?) {
            super.onTouchEvent(event)

            reset()
        }

        private fun reset() {
            persistence.reset(width, height)

            renderer.reset()
            renderer.addBodies()
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
            renderer.width = width
            renderer.height = height
            super.onSurfaceChanged(holder, format, width, height)

            renderer.reset()
            renderer.addBodies()
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder?) {
            super.onSurfaceDestroyed(holder)
            visible = false
            persistence.recycle()
        }

        private fun draw() {
            var canvas: Canvas? = null
            try {
                canvas = surfaceHolder.lockCanvas()

                renderer.tick()

                if (canvas != null) {
                    if (persistent) {
                        persistence.draw(renderer, canvas)
                    } else {
                        renderer.draw(canvas)
                    }
                }
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
