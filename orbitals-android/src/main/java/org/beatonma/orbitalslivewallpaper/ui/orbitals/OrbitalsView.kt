package org.beatonma.orbitalslivewallpaper.ui.orbitals

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.lifecycle.get
import org.beatonma.orbitals.render.interaction.OrbitalsGestureHandler
import org.beatonma.orbitals.render.interaction.OrbitalsKeyboardHandler
import org.beatonma.orbitalslivewallpaper.R
import org.beatonma.orbitalslivewallpaper.Settings
import kotlin.time.Duration.Companion.milliseconds

class OrbitalsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private lateinit var viewModel: OrbitalsEngineViewModel

    private val renderEngine get() = viewModel.renderEngine
    private var touchHandler: OrbitalsGestureDetector? = null
    private var keyboardHandler: OrbitalsKeyboardHandler? = null
    private val settings: Settings

    private var lastFrameMillis = System.currentTimeMillis()

    init {
        context.obtainStyledAttributes(attrs, R.styleable.OrbitalsView).apply {
            settings = Settings.entries.toTypedArray()[getInt(R.styleable.OrbitalsView_settings, 0)]
        }.recycle()

        isClickable = true // Enable keyboard events
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        viewModel = findViewTreeViewModelStoreOwner()?.let {
            ViewModelProvider(
                it,
                OrbitalsEngineViewModel.factory(context, settings)
            ).get()
        } ?: throw IllegalStateException("Unable to find ViewModelStoreOwner for view")

        viewModel.onRenderEngineReady { engine ->
            engine.onSizeChanged(width, height)

            touchHandler = OrbitalsGestureDetector(
                OrbitalsGestureHandler(
                    viewScope,
                    engine,
                    ViewConfiguration.get(context).scaledTouchSlop.toFloat()
                )
            )
            keyboardHandler = OrbitalsKeyboardHandler(engine, viewModel)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        touchHandler = null
        keyboardHandler = null
        viewModel.recycle()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        if (w != oldw && h != oldh) {
            renderEngine?.onSizeChanged(w, h)
        }

        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas) {
        val now = System.currentTimeMillis()
        val timeDelta = now - lastFrameMillis
        lastFrameMillis = now

        renderEngine?.apply {
            canvas.drawColor(options.visualOptions.colorOptions.background.toAndroidColor())
            update(canvas, timeDelta.milliseconds)
        }

        postInvalidateOnAnimation()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean =
        touchHandler?.onTouchEvent(event) ?: false

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean =
        keyCode.toKey()?.let { keyboardHandler?.onKeyDown(it) }
            ?: super.onKeyDown(keyCode, event)
}
