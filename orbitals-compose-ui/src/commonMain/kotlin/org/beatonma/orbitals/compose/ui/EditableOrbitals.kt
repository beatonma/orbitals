package org.beatonma.orbitals.compose.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.BoxScope
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
import org.beatonma.orbitals.core.Platform
import org.beatonma.orbitals.core.platform
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
    insets: PaddingValues = PaddingValues(),
    engine: OrbitalsRenderEngine<DrawScope>,
) {
    var settingsVisible by remember { mutableStateOf(false) }

    EditableOrbitals(
        true,
        {},
        settingsVisible,
        { settingsVisible = it },
        options,
        persistence,
        insets,
        engine,
    )
}

@Composable
fun EditableOrbitals(
    settingsEnabled: Boolean,
    onSettingsEnabledChange: (Boolean) -> Unit,
    settingsVisible: Boolean,
    onSettingsVisibleChange: (Boolean) -> Unit,
    options: Options,
    persistence: OptionPersistence,
    insets: PaddingValues = PaddingValues(),
    engine: OrbitalsRenderEngine<DrawScope>,
) {
    if (!settingsEnabled) {
        // Short-circuit if settings UI is disabled
        return Orbitals(
            options,
            Modifier.fillMaxSize(),
            engine,
        )
    }

    var onBackgroundColor by remember { mutableStateOf(Color.Black) }

    LaunchedEffect(options.visualOptions.colorOptions.background) {
        onBackgroundColor = if (options.visualOptions
                .colorOptions.background
                .toComposeColor()
                .luminance() > .5f
        ) Color.Black else Color.White
    }

    BoxWithConstraints(
        Modifier.keyboardHandler(engine, options, persistence) {
            onSettingsVisibleChange(!settingsVisible)
        }
    ) {
        Orbitals(
            options,
            Modifier.fillMaxSize(),
            engine,
        )

        AnimatedVisibility(
            settingsVisible,
            Modifier.align(Alignment.BottomEnd),
            enter = slideInVertically { it },
            exit = slideOutVertically { it },
        ) {
            with(LocalDensity.current) {
                SettingsUI(
                    constraints.maxWidth.toDp(),
                    constraints.maxHeight.toDp(),
                    options,
                    persistence,
                    insets = insets,
                    onCloseUI = { onSettingsVisibleChange(false) },
                    onDisableUI = when (platform) {
                        Platform.Web -> {
                            { onSettingsEnabledChange(false) }
                        }

                        else -> null
                    }
                )
            }
        }

        AnimatedVisibility(
            !settingsVisible,
            Modifier
                .padding(insets)
                .padding(ContentPadding)
                .align(Alignment.BottomEnd),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            IconButton({ onSettingsVisibleChange(true) }) {
                Icon(
                    Icons.Default.Menu,
                    "Show settings",
                    Modifier.alpha(.4f),
                    tint = onBackgroundColor
                )
            }
        }

        if (OrbitalsBuildConfig.DEBUG) {
            DebugOverlay(engine, onBackgroundColor)
        }
    }
}

@Composable
private fun BoxScope.DebugOverlay(engine: OrbitalsRenderEngine<*>, color: Color) {
    val bodyCount = poll(500L) { engine.bodies.size }

    Text("$bodyCount objects", color = color)
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
