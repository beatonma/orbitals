package org.beatonma.orbitalslivewallpaper.app

import android.app.Application
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import org.beatonma.orbitals.core.engine.SystemGenerator
import org.beatonma.orbitals.core.options.CollisionStyle
import org.beatonma.orbitals.render.options.ColorOptions
import org.beatonma.orbitals.render.options.DrawStyle
import org.beatonma.orbitals.render.options.ObjectColors
import org.beatonma.orbitals.render.options.Options
import org.beatonma.orbitals.core.options.PhysicsOptions
import org.beatonma.orbitals.render.options.RenderLayer
import org.beatonma.orbitals.render.options.VisualOptions
import org.beatonma.orbitals.render.compose.Orbitals
import org.beatonma.orbitalslivewallpaper.app.settings.ColorSetting
import org.beatonma.orbitalslivewallpaper.app.settings.FloatSetting
import org.beatonma.orbitalslivewallpaper.app.settings.IntegerSetting
import org.beatonma.orbitalslivewallpaper.app.settings.MultiSelectSetting
import org.beatonma.orbitalslivewallpaper.app.settings.SingleSelectSetting
import org.beatonma.orbitalslivewallpaper.app.settings.SwitchSetting
import org.beatonma.orbitalslivewallpaper.orbitals.ui.OrbitalsView
import kotlin.time.ExperimentalTime


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SettingsUI(
    viewmodel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(
            LocalContext.current.applicationContext as Application,
            Settings.Wallpaper,
        )
    )
) {
    val options by viewmodel.getOptions().collectAsState(initial = Options())

//    Column(Modifier.fillMaxSize()) {
//        Orbitals(
//            options,
//            Modifier.fillMaxWidth().fillMaxHeight(.5f)
//        )
//
//        AndroidView(
//            factory = { context ->
//                OrbitalsView(context)
//            },
//            modifier = Modifier.background(Color.DarkGray).fillMaxSize()
//        )
//    }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        LazyColumn(
            Modifier.background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color.Transparent,
                        colors.surface,
                    ),
                    startY = 0f,
                    endY = constraints.maxWidth / 16f * 9f,
                )
            )
        ) {
            item {
                Orbitals(
                    options,
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                )
            }

            item {
                AndroidView(
                    factory = { context ->
                        OrbitalsView(context)
                    },
                    modifier = Modifier
                        .background(Color.DarkGray)
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                )
            }


            item {
                VisualSettingsUI(visualOptions = options.visualOptions, viewmodel = viewmodel)
            }

            item {
                PhysicsSettingsUI(physics = options.physics, viewmodel = viewmodel)
            }

            item {
                Spacer(
                    Modifier.height(160.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun VisualSettingsUI(
    visualOptions: VisualOptions,
    viewmodel: SettingsViewModel,
) {
    SettingsGroup("Visual") {
        MultiSelectSetting(
            name = "Layers",
            key = VisualKeys.renderLayers,
            value = visualOptions.renderLayers,
            values = RenderLayer.values(),
            onValueChange = saveSelections(viewmodel),
        )

        SingleSelectSetting(
            name = "Style",
            key = VisualKeys.drawStyle,
            value = visualOptions.drawStyle,
            values = DrawStyle.values(),
            onValueChange = saveSelection(viewmodel),
        )

        Conditional(visualOptions.drawStyle == DrawStyle.Wireframe) {
            FloatSetting(
                name = "Stroke width",
                key = VisualKeys.strokeWidth,
                value = visualOptions.strokeWidth,
                onValueChange = viewmodel::updateOption,
                min = 1f,
                max = 24f,
            )
        }

        Conditional(RenderLayer.Trails in visualOptions.renderLayers) {
            IntegerSetting(
                name = "History length",
                key = VisualKeys.traceLineLength,
                value = visualOptions.traceLineLength,
                onValueChange = viewmodel::updateOption,
                min = 1,
                max = 120,
            )
        }
    }

    ColorSettingsUI(visualOptions.colorOptions, viewmodel)
}

@Composable
private fun ColorSettingsUI(
    colorOptions: ColorOptions,
    viewmodel: SettingsViewModel,
) {
    SettingsGroup(title = "Colors") {
        ColorSetting(
            name = "Background color",
            key = ColorKeys.background,
            value = colorOptions.background,
            onValueChange = viewmodel::updateOption,
        )

        MultiSelectSetting(
            name = "Object colors",
            key = ColorKeys.bodies,
            value = colorOptions.bodies,
            values = ObjectColors.values(),
            onValueChange = saveSelections(viewmodel),
        )
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun PhysicsSettingsUI(
    physics: PhysicsOptions,
    viewmodel: SettingsViewModel,
) {
    SettingsGroup("Physics") {
        SwitchSetting(
            "Auto-add bodies",
            key = PhysicsKeys.autoAddBodies,
            value = physics.autoAddBodies,
            onValueChange = viewmodel::updateOption,
        )

        IntegerSetting(
            name = "Maximum population",
            key = PhysicsKeys.maxEntities,
            value = physics.maxEntities,
            onValueChange = viewmodel::updateOption,
            min = 1,
            max = 200,
        )

        IntegerSetting(
            name = "Maximum age of fixed bodies (minutes)",
            key = PhysicsKeys.maxFixedBodyAgeMinutes,
            value = physics.maxFixedBodyAgeMinutes.inWholeHours.toInt(),
            onValueChange = viewmodel::updateOption,
            min = 1,
            max = 30,
        )

        IntegerSetting(
            name = "Tick delta (seconds)",
            key = PhysicsKeys.tickDelta,
            value = physics.tickDelta.inWholeSeconds.toInt(),
            onValueChange = viewmodel::updateOption,
            min = 1,
            max = 5,
        )

        FloatSetting(
            name = "Gravity multiplier",
            key = PhysicsKeys.gravityMultiplier,
            value = physics.gravityMultiplier,
            onValueChange = viewmodel::updateOption,
            min = .1f,
            max = 10f,
        )

        MultiSelectSetting(
            name = "System generators",
            key = PhysicsKeys.systemGenerators,
            value = physics.systemGenerators,
            values = SystemGenerator.values(),
            onValueChange = saveSelections(viewmodel),
        )

        SingleSelectSetting(
            name = "Collision style",
            key = PhysicsKeys.collisionStyle,
            value = physics.collisionStyle,
            values = CollisionStyle.values(),
            onValueChange = saveSelection(viewmodel),
        )
    }
}

private fun <E : Enum<E>> saveSelection(
    viewmodel: SettingsViewModel,
): (key: Preferences.Key<String>, newValue: E) -> Unit {
    return { key, value ->
        viewmodel.updateOption(key, value.name)
    }
}

private fun <E : Enum<E>> saveSelections(
    viewmodel: SettingsViewModel,
): (key: Preferences.Key<Set<String>>, newValue: Set<E>) -> Unit {
    return { key, value ->
        viewmodel.updateOption(key, value.map { it.name }.toSet())
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun Conditional(
    condition: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
    AnimatedVisibility(
        visible = condition,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        content = content,
    )
}

@Composable
fun Todo(name: String) {
    Text("// TODO: $name", color = Color.Red)
}

@Composable
private fun SettingsGroup(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 8.dp)
    ) {
        Text(title, style = typography.h4)

        content()
    }
}


private class SettingsViewModelFactory(
    private val application: Application,
    private val settings: Settings
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(application, settings) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
