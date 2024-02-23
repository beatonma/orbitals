package org.beatonma.orbitals.render.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.unit.dp
import org.beatonma.orbitals.core.OrbitalsBuildConfig
import org.beatonma.orbitals.render.OrbitalsRenderEngine
import org.beatonma.orbitals.render.interaction.OrbitalsGestureHandler
import org.beatonma.orbitals.render.options.Options
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds


@Composable
fun Orbitals(
    options: Options,
    modifier: Modifier = Modifier,
    orbitals: OrbitalsRenderEngine<DrawScope> = rememberOrbitalsRenderEngine(options)
) {
    val touch = rememberOrbitalsTouch(orbitals)
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

    val frameTime = currentFrameDelta()

    Canvas(
        modifier = modifier
            .background(options.visualOptions.colorOptions.background.toComposeColor())
            .orbitalsPointerInput(touch)
            .clipToBounds()
    ) {
        size = this.size
        orbitals.update(this, frameTime)
    }

    DebugOverlay(frameTime)
}

@Composable
private fun currentFrameDelta(): Duration {
    var frameMillis by remember { mutableLongStateOf(0L) }
    LaunchedEffect(Unit) {
        var previousFrameMillis = 0L
        while (true) {
            withFrameMillis { frameTime ->
                frameMillis = frameTime - previousFrameMillis
                previousFrameMillis = frameTime
            }
        }
    }
    return frameMillis.milliseconds
}

@Composable
private fun DebugOverlay(frameTime: Duration) {
    if (OrbitalsBuildConfig.DEBUG) {
        val cacheSize = 30
        var cacheIndex by remember { mutableIntStateOf(0) }
        val cache = remember { LongArray(cacheSize) { 0L } }
        cache[cacheIndex] = frameTime.inWholeMilliseconds
        cacheIndex = (cacheIndex + 1) % cacheSize

        val mean = (cache.reduce { acc, l -> acc + l } / cacheSize).coerceAtLeast(1L)

        Text(
            "${(1000L / mean)}fps",
            Modifier.background(Color.Black, shapes.small).padding(4.dp),
            color = Color.White
        )
    }
}

@Composable
fun rememberOrbitalsTouch(engine: OrbitalsRenderEngine<DrawScope>): OrbitalsGestureHandler<PointerId> {
    val scope = rememberCoroutineScope()
    val touchSlop = LocalViewConfiguration.current.touchSlop
    return remember { OrbitalsGestureHandler(scope, engine, touchSlop) }
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
