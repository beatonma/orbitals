package org.beatonma.orbitals.compose.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.beatonma.orbitals.core.OrbitalsBuildConfig
import org.beatonma.orbitals.render.OrbitalsRenderEngine
import org.beatonma.orbitals.render.compose.Orbitals
import org.beatonma.orbitals.render.compose.toComposeColor
import org.beatonma.orbitals.render.options.OptionPersistence
import org.beatonma.orbitals.render.options.Options


private val ContentPadding = 16.dp


@Composable
fun EditableOrbitals(
    options: Options,
    persistence: OptionPersistence,
    contentPadding: PaddingValues = PaddingValues(),
    engine: OrbitalsRenderEngine<DrawScope>,
) {
    var settingsVisible by remember { mutableStateOf(true) }
    val onBackgroundColor = if (options.visualOptions
            .colorOptions.background
            .toComposeColor()
            .luminance() > .5f
    ) Color.Black else Color.White

    BoxWithConstraints {
        Orbitals(options, Modifier.fillMaxSize(), engine)

        AnimatedVisibility(
            settingsVisible,
            Modifier
                .align(Alignment.BottomEnd)
                .padding(contentPadding),
            enter = slideInVertically { it },
            exit = slideOutVertically { it },
        ) {
            with(LocalDensity.current) {
                SettingsUI(
                    constraints.maxWidth.toDp(),
                    constraints.maxHeight.toDp(),
                    options,
                    persistence,
                ) { settingsVisible = false }
            }
        }

        AnimatedVisibility(
            !settingsVisible,
            Modifier
                .padding(contentPadding)
                .padding(ContentPadding)
                .align(Alignment.BottomEnd),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            IconButton({ settingsVisible = true }) {
                Icon(
                    Icons.Default.Menu,
                    "Show settings",
                    Modifier.alpha(.4f),
                    tint = onBackgroundColor
                )
            }
        }

        if (OrbitalsBuildConfig.DEBUG) {
            val bodyCount = poll(500L) { engine.bodies.size }

            Text("$bodyCount objects", color = onBackgroundColor)
        }
    }
}

@Composable
private fun <T> poll(intervalMillis: Long, update: () -> T): T {
    var value by remember { mutableStateOf(update()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            while (true) {
                delay(intervalMillis)
                value = update()
            }
        }
    }

    return value
}
