package org.beatonma.orbitalslivewallpaper.orbitals.services

import android.graphics.Canvas
import android.os.Handler
import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import android.view.SurfaceHolder
import androidx.core.view.GestureDetectorCompat
import org.beatonma.orbitals.rendering.getRenderers
import org.beatonma.orbitalslivewallpaper.app.Settings
import org.beatonma.orbitalslivewallpaper.app.dataStore
import org.beatonma.orbitalslivewallpaper.app.getSavedOptionsSync
import org.beatonma.orbitalslivewallpaper.orbitals.MotionEventTouchHandler
import org.beatonma.orbitalslivewallpaper.orbitals.OrbitalsRenderEngine
import org.beatonma.orbitalslivewallpaper.orbitals.diffRenderers
import org.beatonma.orbitalslivewallpaper.orbitals.options.Options
import org.beatonma.orbitalslivewallpaper.warn

private const val FPS = 60
private const val FrameDelay: Long = 1000L / FPS

class LwpService : WallpaperService() {

    override fun onCreateEngine() = LwpEngine()

    inner class LwpEngine : Engine() {
        private var options: Options = getSavedOptionsSync(dataStore(Settings.Wallpaper))
            set(value) {
                field = value
                renderEngine.options = value
            }
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

        private val renderEngine = OrbitalsRenderEngine<Canvas>(
            renderers = getRenderers(options.visualOptions),
            options = options,
            onOptionsChange = {
                renderers = diffRenderers(this)
            }
        )

        private val touchHandler = GestureDetectorCompat(
            this@LwpService,
            MotionEventTouchHandler(renderEngine)
        )

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)

            setTouchEventsEnabled(true)
            visible = true
        }

        override fun onTouchEvent(event: MotionEvent?) {
            touchHandler.onTouchEvent(event)
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            this.visible = visible

            if (visible) {
                options = getSavedOptionsSync(dataStore(Settings.Wallpaper))
            }
        }

        override fun onSurfaceChanged(
            holder: SurfaceHolder?,
            format: Int,
            width: Int,
            height: Int
        ) {
            renderEngine.onSizeChanged(width, height)

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
