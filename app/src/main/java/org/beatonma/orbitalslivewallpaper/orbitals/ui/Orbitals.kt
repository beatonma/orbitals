package org.beatonma.orbitalslivewallpaper.orbitals.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import org.beatonma.orbitalslivewallpaper.orbitals.OrbitalsRenderEngine
import org.beatonma.orbitalslivewallpaper.orbitals.options.Options
import org.beatonma.orbitalslivewallpaper.orbitals.renderer.diffRenderers
import org.beatonma.orbitalslivewallpaper.orbitals.renderer.getRenderers
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
        println("options updated: $options")
        orbitals.options = options
//        orbitals.renderers = diffRenderers(orbitals.renderers, options.visualOptions.renderLayers, options.visualOptions, orbitals.bodies)
    }

    val animator by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(100), RepeatMode.Restart)
    )

    Canvas(modifier = modifier) {
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
            renderers = getRenderers(options.visualOptions),
            options = options,
            onOptionsChange = {
                renderers = diffRenderers(this)
            }
        )
    }
}
