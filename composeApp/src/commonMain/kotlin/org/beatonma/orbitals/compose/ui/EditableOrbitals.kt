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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import orbitals.composeapp.generated.resources.Res
import org.beatonma.orbitals.compose.ui.components.ButtonData
import org.beatonma.orbitals.compose.ui.components.HintButton
import org.beatonma.orbitals.core.Platform
import org.beatonma.orbitals.core.platform
import org.beatonma.orbitals.render.OrbitalsRenderEngine
import org.beatonma.orbitals.render.compose.Orbitals
import org.beatonma.orbitals.render.compose.toComposeColor
import org.beatonma.orbitals.render.options.OptionPersistence
import org.beatonma.orbitals.render.options.Options
import org.jetbrains.compose.resources.stringResource


private val ContentPadding = 16.dp


@Composable
fun EditableOrbitals(
    options: Options,
    persistence: OptionPersistence,
    insets: PaddingValues = PaddingValues(),
    engine: OrbitalsRenderEngine<DrawScope>,
    uiButtons: List<ButtonData>? = null,
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
        uiButtons,
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
    uiButtons: List<ButtonData>? = null,
) {
    if (!settingsEnabled) {
        // Short-circuit if settings UI is disabled
        return Orbitals(
            options,
            Modifier.fillMaxSize(),
            engine,
        )
    }

    var onBackgroundColor by remember { mutableStateOf(Color.White) }
    val actions = remember {
        SettingsUiActions(
            onCloseUI = { onSettingsVisibleChange(false) },
            onDisableUI = when (platform) {
                Platform.Web -> {
                    { onSettingsEnabledChange(false) }
                }

                else -> null
            },
            extras = uiButtons,
        )
    }

    LaunchedEffect(options.visualOptions.colorOptions.background) {
        onBackgroundColor = if (options.visualOptions
                .colorOptions.background
                .toComposeColor()
                .luminance() > .5f
        ) Color.Black else Color.White
    }

    BoxWithConstraints(
        Modifier.keyboardHandler(engine, persistence) {
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
                    actions = actions,
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
            HintButton(
                Icons.Default.Menu,
                stringResource(Res.string.settings),
                null
            ) { onSettingsVisibleChange(true) }
        }
    }
}
