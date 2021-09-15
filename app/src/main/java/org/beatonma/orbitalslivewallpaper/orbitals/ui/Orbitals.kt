package org.beatonma.orbitalslivewallpaper.orbitals.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import org.beatonma.orbitals.engine.Region
import org.beatonma.orbitalslivewallpaper.orbitals.OrbitalsRenderEngine
import org.beatonma.orbitalslivewallpaper.orbitals.diffRenderers
import org.beatonma.orbitalslivewallpaper.orbitals.options.Options
import kotlin.math.roundToInt
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

    val animator by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(100), RepeatMode.Restart)
    )

    Canvas(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { offset ->
                        val x = offset.x.toInt()
                        val y = offset.y.toInt()
                        orbitals.addBodies(
                            Region(
                                x - 250,
                                y - 250,
                                x + 250,
                                y + 250,
                            )
                        )
                    },
                    onDoubleTap = { offset ->
                        orbitals.clear()
                    }
                )
            },
    ) {
        animator
        size = this.size

        orbitals.update(this)
    }
}

@Composable
private fun rememberRenderEngine(
    options: Options,
): OrbitalsRenderEngine<DrawScope> {
    return remember {
        OrbitalsRenderEngine(
            renderers = org.beatonma.orbitals.rendering.getRenderers(options.visualOptions),
            options = options,
            onOptionsChange = {
                renderers = diffRenderers(this)
            }
        )
    }
}
