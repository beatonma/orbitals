@file:SuppressLint("ModifierParameter")

package org.beatonma.orbitalslivewallpaper.app

import android.annotation.SuppressLint
import android.app.Application
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Checkbox
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.RadioButton
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import org.beatonma.orbitals.map
import org.beatonma.orbitals.options.CollisionStyle
import org.beatonma.orbitals.options.PhysicsOptions
import org.beatonma.orbitals.options.SystemGenerator
import org.beatonma.orbitalslivewallpaper.orbitals.options.ColorKeys
import org.beatonma.orbitalslivewallpaper.orbitals.options.ColorOptions
import org.beatonma.orbitalslivewallpaper.orbitals.options.DrawStyle
import org.beatonma.orbitalslivewallpaper.orbitals.options.ObjectColors
import org.beatonma.orbitalslivewallpaper.orbitals.options.Options
import org.beatonma.orbitalslivewallpaper.orbitals.options.PhysicsKeys
import org.beatonma.orbitalslivewallpaper.orbitals.options.RenderLayer
import org.beatonma.orbitalslivewallpaper.orbitals.options.Settings
import org.beatonma.orbitalslivewallpaper.orbitals.options.VisualKeys
import org.beatonma.orbitalslivewallpaper.orbitals.options.VisualOptions
import org.beatonma.orbitalslivewallpaper.orbitals.ui.Orbitals
import kotlin.math.roundToInt
import kotlin.time.ExperimentalTime


private val SettingModifier = Modifier
    .padding(16.dp)
    .fillMaxWidth()

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

    Box(Modifier.fillMaxSize()) {
        Orbitals(
            options,
            Modifier
                .fillMaxSize()
        )

        LazyColumn(
            Modifier.background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color.Transparent,
                        colors.surface,
                    ),
                    startY = 0f,
                    endY = 2000F,
                )
            )
        ) {
            item {
                Spacer(
                    modifier = Modifier
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

@Composable
private fun SettingsGroup(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(title, style = typography.h4)

        content()
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

        SwitchSetting(
            name = "Show acceleration",
            value = visualOptions.showAcceleration,
            key = VisualKeys.showAcceleration,
            onValueChange = viewmodel::updateOption,
        )

        SingleSelectSetting(
            name = "Style",
            key = VisualKeys.drawStyle,
            value = visualOptions.drawStyle,
            values = DrawStyle.values(),
            onValueChange = saveSelection(viewmodel),
        )

        FloatSetting(
            name = "Stroke width",
            key = VisualKeys.strokeWidth,
            value = visualOptions.strokeWidth.value,
            onValueChange = viewmodel::updateOption,
            min = 1f,
            max = 16f
        )
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
            value = colorOptions.bodies.toSet(),
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
        IntegerSetting(
            name = "Maximum population",
            key = PhysicsKeys.maxEntities,
            value = physics.maxEntities,
            onValueChange = viewmodel::updateOption,
            min = 1,
            max = 200,
        )

        MultiSelectSetting(
            name = "System generators",
            key = PhysicsKeys.systemGenerators,
            value = physics.systemGenerators.toSet(),
            values = SystemGenerator.values(),
            onValueChange = saveSelections(viewmodel),
        )

        FloatSetting(
            name = "Gravity multiplier",
            key = PhysicsKeys.gravityMultiplier,
            value = physics.gravityMultiplier,
            onValueChange = viewmodel::updateOption,
            min = .1f,
            max = 2f,
        )

        SingleSelectSetting(
            name = "Collision style",
            key = PhysicsKeys.collisionStyle,
            value = physics.collisionStyle,
            values = CollisionStyle.values(),
            onValueChange = saveSelection(viewmodel),
        )

        IntegerSetting(
            name = "Tick delta (seconds)",
            key = PhysicsKeys.tickDelta,
            value = physics.tickDelta.inWholeSeconds.toInt(),
            onValueChange = viewmodel::updateOption,
            min = 1,
            max = 5,
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
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = condition,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        content = content
    )
}

@Composable
private fun SwitchSetting(
    name: String,
    key: Preferences.Key<Boolean>,
    value: Boolean,
    onValueChange: (key: Preferences.Key<Boolean>, value: Boolean) -> Unit,
    modifier: Modifier = SettingModifier,
) {
    Checkable(
        name,
        modifier,
        onClick = { onValueChange(key, !value) }
    ) {
        Switch(checked = value, onCheckedChange = { checked -> onValueChange(key, checked) })
    }
}

@Composable
private fun Checkable(
    name: String,
    modifier: Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val modifierWithClick = if (onClick == null) {
        modifier
    } else {
        Modifier
            .clickable(onClick = onClick)
            .then(modifier)
    }

    Row(
        modifierWithClick,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(name)

        content()
    }
}

@Composable
private fun Todo(name: String) {
    Text("// TODO: $name", color = Color.Red, modifier = SettingModifier)
}

@Composable
private fun IntegerSetting(
    name: String,
    key: Preferences.Key<Int>,
    value: Int,
    onValueChange: (key: Preferences.Key<Int>, newValue: Int) -> Unit,
    min: Int,
    max: Int,
    modifier: Modifier = SettingModifier,
) {
    BoxWithConstraints {
        val maxWidth = constraints.maxWidth.toFloat()
        var offset by remember {
            println("maxWidth $maxWidth")
            mutableStateOf(value.toFloat().map(min.toFloat(), max.toFloat(), 0f, maxWidth))
        }

        Column(
            modifier.draggable(
                orientation = Orientation.Horizontal,
                state = rememberDraggableState { delta ->
                    offset = (offset + delta).coerceIn(0f, maxWidth)
                    onValueChange(
                        key,
                        offset.map(0f, maxWidth, min.toFloat(), max.toFloat()).roundToInt()
                    )
                }
            )
        ) {
            Text("$name: $value", Modifier.align(Alignment.CenterHorizontally))

            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("$min")
                LinearProgressIndicator(
                    progress = value.toFloat().map(min.toFloat(), max.toFloat(), 0f, 1f),
                )
                Text("$max")
            }
        }
    }
}

@Composable
private fun FloatSetting(
    name: String,
    key: Preferences.Key<Float>,
    value: Float,
    onValueChange: (key: Preferences.Key<Float>, newValue: Float) -> Unit,
    min: Float,
    max: Float,
    modifier: Modifier = SettingModifier,
) {
    BoxWithConstraints {
        val maxWidth = constraints.maxWidth.toFloat()
        var offset by remember {
            println("maxWidth $maxWidth")
            mutableStateOf(value.map(min, max, 0f, maxWidth))
        }

        Column(
            modifier.draggable(
                orientation = Orientation.Horizontal,
                state = rememberDraggableState { delta ->
                    offset = (offset + delta).coerceIn(0f, maxWidth)
                    onValueChange(
                        key,
                        offset.map(0f, maxWidth, min, max)
                    )
                }
            )
        ) {
            Text("$name: $value", Modifier.align(Alignment.CenterHorizontally))

            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("$min")
                LinearProgressIndicator(
                    progress = value.map(min, max, 0f, 1f),
                )
                Text("$max")
            }
        }
    }
}

@Composable
private fun <E : Enum<E>> SingleSelectSetting(
    name: String,
    key: Preferences.Key<String>,
    value: E,
    values: Array<out E>,
    onValueChange: (key: Preferences.Key<String>, newValue: E) -> Unit,
    modifier: Modifier = SettingModifier,
) {
    Column(modifier) {
        Text(name, style = typography.h5)

        for (v in values) {
            val onClick = { onValueChange(key, v) }
            Checkable(
                v.name,
                modifier,
                onClick,
            ) {
                RadioButton(
                    selected = value == v,
                    onClick = onClick,
                )
            }
        }
    }
}


@Composable
private fun <E : Enum<E>> MultiSelectSetting(
    name: String,
    key: Preferences.Key<Set<String>>,
    value: Set<E>,
    values: Array<out E>,
    onValueChange: (key: Preferences.Key<Set<String>>, newValue: Set<E>) -> Unit,
    allowEmptySet: Boolean = false,
    modifier: Modifier = SettingModifier,
) {
    Column(modifier) {
        Text(name, style = typography.h5)

        for (v in values) {
            val onClick = {
                if (v in value) {
                    val newValue = value.filter { it != v }.toSet()
                    if (!allowEmptySet && newValue.isEmpty()) {
                        onValueChange(key, setOf(values.first()))
                    } else {
                        onValueChange(key, newValue)
                    }
                } else {
                    onValueChange(key, value + v)
                }
            }

            Checkable(
                v.name,
                modifier,
                onClick,
            ) {
                Checkbox(
                    checked = v in value,
                    onCheckedChange = { onClick() }
                )
            }
        }
    }
}

@Composable
private fun ColorSetting(
    name: String,
    key: Preferences.Key<Int>,
    value: Int,
    onValueChange: (key: Preferences.Key<Int>, newValue: Int) -> Unit,
    modifier: Modifier = SettingModifier,
) {
    Todo(name)
}

private class SettingsViewModelFactory(
    private val application: Application,
    private val settings: Settings
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(application, settings) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
