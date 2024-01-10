package org.beatonma.orbitals.render.compose

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.ui.geometry.Size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.drawscope.DrawScope
import org.beatonma.orbitals.render.options.Options
import org.beatonma.orbitals.render.OrbitalsRenderEngine
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds


@Composable
fun Orbitals(
    options: Options,
    modifier: Modifier = Modifier,
    orbitals: OrbitalsRenderEngine<DrawScope> = rememberOrbitalsRenderEngine(options)
) {
    var size by remember { mutableStateOf(Size(1f, 1f)) }

    LaunchedEffect(size) {
        orbitals.onSizeChanged(
            size.width.roundToInt(),
            size.height.roundToInt()
        )
    }

    LaunchedEffect(options) {
        orbitals.options = options
    }

    val duration = frameMillis.milliseconds

    Canvas(
        modifier = modifier
            .background(options.visualOptions.colorOptions.background.toComposeColor())
            .orbitalsPointerInput(orbitals)
            .clipToBounds()
    ) {
        size = this.size

        orbitals.update(this, duration)
    }
}

@Composable
fun rememberOrbitalsRenderEngine(
    options: Options,
): OrbitalsRenderEngine<DrawScope> {
    val engine = remember {
        OrbitalsRenderEngine(
            ComposeDelegate,
            options = options,
        )
    }

    LaunchedEffect(options) {
        engine.options = options
    }

    return engine
}


private val frameMillis: Long
    @Composable
    get() {
        var previousFrameMillis by remember { mutableStateOf(0L) }
        var frameMillis by remember { mutableStateOf(0L) }
        LaunchedEffect(frameMillis) {
            while (true) {
                withFrameMillis { frameTime ->
                    frameMillis = frameTime - previousFrameMillis
                    previousFrameMillis = frameTime
                }
            }
        }
        return frameMillis
    }
