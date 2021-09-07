package org.beatonma.orbitalslivewallpaper.orbitals

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import org.beatonma.orbitalslivewallpaper.color.getAnyMaterialColor
import org.beatonma.orbitalslivewallpaper.orbitals.options.Options
import org.beatonma.orbitalslivewallpaper.orbitals.options.PhysicsOptions
import org.beatonma.orbitalslivewallpaper.orbitals.physics.Body
import org.beatonma.orbitalslivewallpaper.orbitals.physics.Distance
import org.beatonma.orbitalslivewallpaper.orbitals.physics.FixedBody
import org.beatonma.orbitalslivewallpaper.orbitals.physics.Position
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

private data class RenderBody(val color: Int, val body: Body)

@OptIn(ExperimentalTime::class)
private val TickTimeDelta = Duration.seconds(1)


interface OrbitalsEngine {
    val physics: PhysicsOptions
    var bodies: List<Body>
    val bodyCount: Int get() = bodies.size

    fun onBodiesCreated(newBodies: List<Body>) {}
    fun onBodyDestroyed(body: Body) {}

    fun addBodies(spaceWidth: Int, spaceHeight: Int) {
        bodies = bodies + generateBodies(spaceWidth, spaceHeight)
    }

    fun generateBodies(spaceWidth: Int, spaceHeight: Int): List<Body> {
        val newBodies = physics.systemGenerators
            .random()
            .generate(spaceWidth, spaceHeight)

        onBodiesCreated(newBodies)

        return newBodies
    }

    @OptIn(ExperimentalTime::class)
    fun tick() {
        if (bodyCount > 1) {
            bodies.forEachIndexed { index, body ->
                for (i in (index + 1) until bodyCount) {
                    val other = bodies[i]
                    body.applyGravity(other)
                    other.applyGravity(body)
                }
                body.applyInertia(TickTimeDelta)
            }
        } else {
            bodies.forEach { body -> body.applyInertia(TickTimeDelta) }
        }
    }
}


class AndroidOrbitalsRenderer(
    val options: Options,
) : OrbitalsEngine {
    override val physics: PhysicsOptions = options.physicsOptions
    override var bodies: List<Body> = listOf()

    var width: Int = 1
    var height: Int = 1

    private val backgroundColor = options.visualOptions.colorOptions.backgroundColor

    private var renderBodies: List<RenderBody> = listOf()

    private val alpha: Int = (options.visualOptions.colorOptions.foregroundAlpha * 255f).toInt()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        options.visualOptions.drawStyle.setUp(this)
    }
    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        alpha = 80
    }

    override fun onBodiesCreated(newBodies: List<Body>) {
        renderBodies = renderBodies + newBodies.withColors()
    }

    fun addBodies() {
        addBodies(width, height)
    }


    fun drawBackground(canvas: Canvas) {
        canvas.drawColor(backgroundColor)
//        drawGrid(canvas)
    }

    fun drawForeground(c: Canvas) {
        renderBodies.forEach { (color, body) ->
            paint.color = color
            paint.alpha = alpha

            c.drawCircle(body.position.x.metres, body.position.y.metres, body.radius.metres, paint)
        }
    }

    fun draw(canvas: Canvas) {
        drawBackground(canvas)
        drawForeground(canvas)
    }

    private fun drawGrid(canvas: Canvas) {
        val width = canvas.width
        val height = canvas.height
        for (x in 0..width step 16) {
            canvas.drawLine(x.toFloat(), 0f, x.toFloat(), height.toFloat(), gridPaint)
        }

        for (y in 0..height step 16) {
            canvas.drawLine(0f, y.toFloat(), width.toFloat(), y.toFloat(), gridPaint)
        }
    }

    fun reset() {
        bodies = listOf()
        renderBodies = listOf()
    }
}

interface CanvasDelegate<T> {
    fun drawCircle(canvas: T, position: Position, radius: Distance)
}

class AndroidCanvasDelegate(
    val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG),
) : CanvasDelegate<Canvas> {

    override fun drawCircle(canvas: Canvas, position: Position, radius: Distance) {
        canvas.drawCircle(position.x.metres, position.y.metres, radius.metres, paint)
    }
}


private fun List<Body>.withColors() = map {
    RenderBody(
        when (it) {
            is FixedBody -> Color.WHITE
            else -> getAnyMaterialColor()
        },
        it
    )
}
