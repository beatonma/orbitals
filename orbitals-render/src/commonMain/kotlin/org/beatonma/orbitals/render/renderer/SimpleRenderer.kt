package org.beatonma.orbitals.render.renderer

import org.beatonma.orbitals.core.engine.Config
import org.beatonma.orbitals.core.mapTo
import org.beatonma.orbitals.core.normalizeIn
import org.beatonma.orbitals.core.physics.Body
import org.beatonma.orbitals.core.physics.BodyState
import org.beatonma.orbitals.core.physics.Distance
import org.beatonma.orbitals.core.physics.GreatAttractor
import org.beatonma.orbitals.core.physics.Supernova
import org.beatonma.orbitals.core.physics.coerceAtLeast
import org.beatonma.orbitals.core.physics.metres
import org.beatonma.orbitals.render.CanvasDelegate
import org.beatonma.orbitals.render.OrbitalsRenderer
import org.beatonma.orbitals.render.color.Color
import org.beatonma.orbitals.render.options.DrawStyle
import org.beatonma.orbitals.render.options.VisualOptions
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.time.Duration


private val EnterAnimationMillis: Float = Config.CollisionMinimumAge.inWholeMilliseconds.toFloat()
private val CollapseMillis: Float = Config.CollapseDuration.inWholeMilliseconds.toFloat()
private val SupernovaMillis: Float = Config.SupernovaDuration.inWholeMilliseconds.toFloat()


class SimpleRenderer<Canvas> internal constructor(
    override val delegate: CanvasDelegate<Canvas>,
    override var options: VisualOptions,
) : OrbitalsRenderer<Canvas> {
    override fun drawBody(canvas: Canvas, body: Body, props: BodyProperties) {
        val collapseProgress = getCollapseProgress(body)
        val renderRadius = getRenderRadius(body, collapseProgress)
        val color = getRenderColor(body, props, collapseProgress)

        if (body.isCollapsing()) {
            drawNova(canvas, body, props, collapseProgress, style = DrawStyle.Solid)
        }

        when (body) {
            is Supernova -> drawSupernova(canvas, body, props)

            is GreatAttractor -> delegate.drawCircle(
                canvas,
                body.position,
                renderRadius,
                color = color,
                strokeWidth = options.strokeWidth,
                style = DrawStyle.Wireframe,
                alpha = options.colorOptions.foregroundAlpha,
            )


            else -> delegate.drawCircle(
                canvas,
                body.position,
                renderRadius,
                color = color,
                strokeWidth = options.strokeWidth,
                style = options.drawStyle,
                alpha = options.colorOptions.foregroundAlpha,
            )
        }
    }

    private fun drawNova(
        canvas: Canvas,
        body: Body,
        props: BodyProperties,
        progress: Float,
        scale: Float = 3f,
        style: DrawStyle = DrawStyle.Wireframe,
    ) {
        val fadeProgress = 1f - progress
        val (h, s, l, _) = props.color.hsla()
        val color = Color.hsla(
            h,
            s,
            fadeProgress.mapTo(l, 1f),
            fadeProgress / 2f,
        )
        delegate.drawCircle(
            canvas,
            body.position,
            body.radius * scale * progress,
            color = color,
            strokeWidth = options.strokeWidth * 4f,
            style = style,
            alpha = options.colorOptions.foregroundAlpha,
        )
    }

    private fun drawSupernova(
        canvas: Canvas,
        body: Body,
        props: BodyProperties,
    ) {
        val progress = scaleSupernova(progress(body.age, SupernovaMillis))
        val seed = props.seed / 2f
        drawNova(canvas, body, props, progress, scale = 6f)

        if (progress > .1f) {
            drawNova(
                canvas,
                body,
                props,
                progress.normalizeIn(.1f, 1f),
                scale = 4.5f,
                DrawStyle.Solid
            )
        }
        if (progress > seed) {
            drawNova(canvas, body, props, progress.normalizeIn(seed, 1f), scale = 3f)
        } else {
            delegate.drawCircle(
                canvas,
                body.position,
                body.radius / 4f,
                color = props.color,
                strokeWidth = options.strokeWidth,
                style = options.drawStyle,
                alpha = options.colorOptions.foregroundAlpha,
            )
        }
    }
}

private fun getRenderColor(body: Body, props: BodyProperties, collapseProgress: Float = 0f): Color =
    when (body.state) {
        BodyState.Collapsing -> {
            val (h, s, l, a) = props.color.hsla()
            Color.hsla(
                h,
                s,
                collapseProgress.mapTo(l, 1f),
                a
            )
        }

        else -> props.color
    }

private fun getRenderRadius(body: Body, collapseProgress: Float = 0f): Distance {
    return when (body.state) {
        BodyState.MainSequence, BodyState.Dead -> body.radius
        BodyState.New -> {
            body.radius * easeRadius(body.age.inWholeMilliseconds / EnterAnimationMillis)
        }

        BodyState.Collapsing -> {
            (body.radius * collapseRadius(1f - collapseProgress)).coerceAtLeast(1f.metres)
        }
    }
}

private fun getCollapseProgress(body: Body) = progress(body.sinceStateChange, CollapseMillis)

fun interface TimingFunction {
    operator fun invoke(value: Float): Float
}

private val easeRadius = TimingFunction { value ->
    when {
        value < 0.5f -> (1f - sqrt(1f - (2f * value).pow(2f))) / 2f
        else -> (sqrt(1f - (-2f * value + 2f).pow(2f)) + 1f) / 2f
    }
}

private val collapseRadius = TimingFunction { value ->
    val c1 = 1.70158f
    val c2 = c1 * 1.525f

    when {
        value < .5f -> 2f * (value.pow(2) * ((c2 + 1) * 2f * value - c2)) / 2f
        else -> ((2 * value - 2).pow(2) * ((c2 + 1) * (value * 2 - 2) + c2) + 2) / 2f
    }
}

private val scaleSupernova = TimingFunction { value ->
    1f - (1f - value).pow(5)
}

private fun progress(numerator: Duration, denominatorMillis: Float): Float =
    numerator.inWholeMilliseconds.toFloat() / denominatorMillis
