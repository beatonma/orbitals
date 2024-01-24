package org.beatonma.orbitalslivewallpaper.services

import android.graphics.Canvas
import android.os.Handler
import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.ViewConfiguration
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.lifecycleScope
import org.beatonma.orbitals.core.util.warn
import org.beatonma.orbitals.render.OrbitalsRenderEngine
import org.beatonma.orbitals.render.android.AndroidCanvasDelegate
import org.beatonma.orbitals.render.interaction.OrbitalsGestureHandler
import org.beatonma.orbitals.render.options.Options
import org.beatonma.orbitalslivewallpaper.Settings
import org.beatonma.orbitalslivewallpaper.dataStore
import org.beatonma.orbitalslivewallpaper.getSavedOptionsSync
import org.beatonma.orbitalslivewallpaper.ui.orbitals.OrbitalsGestureDetector
import org.beatonma.orbitalslivewallpaper.ui.orbitals.toAndroidColor
import kotlin.time.Duration.Companion.milliseconds

private const val FPS = 60
private const val FrameDelay: Long = 1000L / FPS

class LwpService : WallpaperService() {
    override fun onCreateEngine() = LwpEngine()

    inner class LwpEngine : Engine(), LifecycleOwner {
        override val lifecycle: LifecycleRegistry = LifecycleRegistry(this)
        private var options: Options = getSavedOptionsSync(dataStore(Settings.Wallpaper))
            set(value) {
                field = value
                renderEngine.options = value
            }
        private val handler: Handler = Handler(mainLooper)
        private val drawRunnable: Runnable = Runnable(this@LwpEngine::draw)
        private var lastFrameMillis = System.currentTimeMillis()
        private var visible = true
            set(value) {
                field = value
                when (value) {
                    true -> postInvalidate()
                    false -> handler.removeCallbacks(drawRunnable)
                }
            }

        private val renderEngine = OrbitalsRenderEngine(
            AndroidCanvasDelegate,
            options = options,
        )

        private val touchHandler = OrbitalsGestureDetector(
            OrbitalsGestureHandler(
                lifecycleScope,
                renderEngine,
                ViewConfiguration.get(this@LwpService).scaledTouchSlop.toFloat()
            )
        )

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)

            lifecycle.currentState = Lifecycle.State.CREATED

            setTouchEventsEnabled(true)
            visible = true
        }

        override fun onTouchEvent(event: MotionEvent) {
            touchHandler.onTouchEvent(event)
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)

            this.visible = visible

            if (visible) {
                options = getSavedOptionsSync(dataStore(Settings.Wallpaper))
                lifecycle.currentState = Lifecycle.State.STARTED
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
            lifecycle.currentState = Lifecycle.State.DESTROYED
            visible = false
            renderEngine.recycle()
        }

        private fun draw() {
            val now = System.currentTimeMillis()
            val timeDelta = now - lastFrameMillis
            lastFrameMillis = now

            var canvas: Canvas? = null
            try {
                canvas = surfaceHolder.lockCanvas()
                canvas.drawColor(options.visualOptions.colorOptions.background.toAndroidColor())
                renderEngine.update(canvas, timeDelta.milliseconds)
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
