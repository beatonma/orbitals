package org.beatonma.orbitals.rendering

import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import org.beatonma.orbitals.options.DrawStyle
import org.beatonma.orbitals.physics.Distance
import org.beatonma.orbitals.physics.Position
import org.beatonma.orbitals.rendering.util.toComposeColor
import org.beatonma.orbitals.rendering.util.toOffset
import kotlin.math.roundToInt

interface CanvasDelegate<T> {
    fun drawCircle(
        canvas: T,
        position: Position,
        radius: Distance,
        color: Int,
        alpha: Float,
        strokeWidth: Float,
        style: DrawStyle,
    )

    fun drawLine(
        canvas: T,
        color: Int,
        start: Offset,
        end: Offset,
        strokeWidth: Float,
        cap: StrokeCap,
        alpha: Float,
    )

    fun drawLine(
        canvas: T,
        color: Int,
        start: Position,
        end: Position,
        strokeWidth: Float,
        cap: StrokeCap,
        alpha: Float,
    ) {
        drawLine(
            canvas,
            color = color,
            start = start.toOffset(),
            end = end.toOffset(),
            strokeWidth = strokeWidth,
            cap = cap,
            alpha = alpha,
        )
    }
}


object ComposeDelegate : CanvasDelegate<DrawScope> {
    override fun drawCircle(
        canvas: DrawScope,
        position: Position,
        radius: Distance,
        color: Int,
        alpha: Float,
        strokeWidth: Float,
        style: DrawStyle
    ) {
        canvas.drawCircle(
            center = position.toOffset(),
            radius = radius.value,
            color = color.toComposeColor(),
            alpha = alpha,
            style = style.toComposeDrawStyle(strokeWidth),
        )
    }

    override fun drawLine(
        canvas: DrawScope,
        color: Int,
        start: Offset,
        end: Offset,
        strokeWidth: Float,
        cap: StrokeCap,
        alpha: Float,
    ) {
        canvas.drawLine(
            color = color.toComposeColor(),
            start = start,
            end = end,
            strokeWidth = strokeWidth,
            cap = cap,
            alpha = alpha,
        )
    }
}


object AndroidCanvasDelegate : CanvasDelegate<Canvas> {
    val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun drawCircle(
        canvas: Canvas,
        position: Position,
        radius: Distance,
        color: Int,
        alpha: Float,
        strokeWidth: Float,
        style: DrawStyle,
    ) {
        canvas.drawCircle(
            position.x.value,
            position.y.value,
            radius.value,
            paint.apply {
                style.setUp(paint)
                this.strokeWidth = strokeWidth
                this.color = color
                this.alpha = (alpha * 255f).roundToInt()
            }
        )
    }

    override fun drawLine(
        canvas: Canvas,
        color: Int,
        start: Offset,
        end: Offset,
        strokeWidth: Float,
        cap: StrokeCap,
        alpha: Float,
    ) {
        canvas.drawLine(
            start.x, start.y,
            end.x, end.y,
            paint.apply {
                this.color = color
                this.alpha = (alpha * 255f).roundToInt()
                this.strokeWidth = strokeWidth
                this.strokeCap = cap.asPaintCap
            }
        )
    }
}


private val StrokeCap.asPaintCap: Paint.Cap
    get() = when (this) {
        StrokeCap.Round -> Paint.Cap.ROUND
        StrokeCap.Butt -> Paint.Cap.BUTT
        StrokeCap.Square -> Paint.Cap.SQUARE
        else -> Paint.Cap.ROUND
    }

private val StrokeJoin.asPaintJoin: Paint.Join
    get() = when (this) {
        StrokeJoin.Round -> Paint.Join.ROUND
        StrokeJoin.Bevel -> Paint.Join.BEVEL
        StrokeJoin.Miter -> Paint.Join.MITER
        else -> Paint.Join.ROUND
    }
