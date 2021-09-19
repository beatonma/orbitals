package org.beatonma.orbitals.render.compose

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.graphics.drawscope.DrawScope
import org.beatonma.orbitals.render.options.Options
import org.beatonma.orbitals.render.OrbitalsRenderEngine
import org.beatonma.orbitals.render.diffRenderers
import org.beatonma.orbitals.render.getRenderers
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.ExperimentalTime


@OptIn(ExperimentalTime::class)
@Composable
fun Orbitals(
    options: Options,
    modifier: Modifier = Modifier,
) {
    var size by remember { mutableStateOf(Size(1f, 1f)) }
    val orbitals = rememberRenderEngine(options)

    LaunchedEffect(size) {
        orbitals.onSizeChanged(
            size.width.roundToInt(),
            size.height.roundToInt()
        )
    }

    LaunchedEffect(options) {
        orbitals.options = options
    }

    val duration = Duration.milliseconds(frameMillis)

    Canvas(
        modifier = modifier.orbitalsPointerInput(orbitals)
    ) {
        size = this.size

        orbitals.update(this, duration)
    }
}

@Composable
private fun rememberRenderEngine(
    options: Options,
): OrbitalsRenderEngine<DrawScope> {
    return remember {
        OrbitalsRenderEngine(
            renderers = getRenderers(options.visualOptions, ComposeDelegate),
            options = options,
            onOptionsChange = {
                renderers = diffRenderers(this, ComposeDelegate)
            }
        )
    }
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
