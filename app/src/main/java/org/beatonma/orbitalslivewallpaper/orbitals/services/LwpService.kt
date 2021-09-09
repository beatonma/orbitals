package org.beatonma.orbitalslivewallpaper.orbitals.services

import android.graphics.Canvas
import android.os.Handler
import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import android.view.SurfaceHolder
import org.beatonma.orbitalslivewallpaper.dataStore
import org.beatonma.orbitalslivewallpaper.orbitals.OrbitalsRenderEngine
import org.beatonma.orbitalslivewallpaper.orbitals.options.Options
import org.beatonma.orbitalslivewallpaper.orbitals.options.Settings
import org.beatonma.orbitalslivewallpaper.orbitals.options.getSavedOptionsSync
import org.beatonma.orbitalslivewallpaper.orbitals.renderer.canvas.chooseRenderers
import org.beatonma.orbitalslivewallpaper.warn

private const val FPS = 60
private const val FrameDelay: Long = 1000L / FPS

class LwpService : WallpaperService() {
    override fun onCreateEngine() = LwpEngine()

    inner class LwpEngine : Engine() {
        private var options: Options = getSavedOptionsSync(dataStore(Settings.Wallpaper))
        private val handler: Handler = Handler(mainLooper)
        private val drawRunnable: Runnable = Runnable(this@LwpEngine::draw)
        private var visible = true
            set(value) {
                field = value
                when (value) {
                    true -> postInvalidate()
                    false -> handler.removeCallbacks(drawRunnable)
                }
            }

        private val renderEngine = OrbitalsRenderEngine(
            renderers = chooseRenderers(options),
            options = options,
        )

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)

            setTouchEventsEnabled(true)
            visible = true
        }

        override fun onTouchEvent(event: MotionEvent?) {
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (event.pointerCount == 3) {
                        reset()
                    } else {
                        renderEngine.engine.addBodies()
                    }
                }
                else -> super.onTouchEvent(event)
            }
        }

        private fun reset() {
            renderEngine.reset()
//            renderEngine.engine.addBodies()
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

            postInvalidate()
        }

        private fun postInvalidate() {
            handler.removeCallbacks(drawRunnable)
            if (visible) {
                handler.postDelayed(drawRunnable, FrameDelay)
            }
        }
    }
}
