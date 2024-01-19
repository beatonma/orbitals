package org.beatonma.orbitals.compose.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import org.beatonma.orbitals.compose.ui.components.DraggableColumn
import org.beatonma.orbitals.compose.ui.settings.ColorSetting
import org.beatonma.orbitals.compose.ui.settings.FloatSetting
import org.beatonma.orbitals.compose.ui.settings.IntegerSetting
import org.beatonma.orbitals.compose.ui.settings.MultiSelectSetting
import org.beatonma.orbitals.compose.ui.settings.SingleSelectSetting
import org.beatonma.orbitals.compose.ui.settings.SwitchSetting
import org.beatonma.orbitals.core.engine.SystemGenerator
import org.beatonma.orbitals.core.options.CollisionStyle
import org.beatonma.orbitals.core.options.PhysicsOptions
import org.beatonma.orbitals.render.color.Color
import org.beatonma.orbitals.render.options.BooleanKey
import org.beatonma.orbitals.render.options.ColorKey
import org.beatonma.orbitals.render.options.ColorKeys
import org.beatonma.orbitals.render.options.ColorOptions
import org.beatonma.orbitals.render.options.DrawStyle
import org.beatonma.orbitals.render.options.FloatKey
import org.beatonma.orbitals.render.options.IntKey
import org.beatonma.orbitals.render.options.ObjectColors
import org.beatonma.orbitals.render.options.OptionPersistence
import org.beatonma.orbitals.render.options.Options
import org.beatonma.orbitals.render.options.PhysicsKeys
import org.beatonma.orbitals.render.options.RenderLayer
import org.beatonma.orbitals.render.options.StringKey
import org.beatonma.orbitals.render.options.StringSetKey
import org.beatonma.orbitals.render.options.VisualKeys
import org.beatonma.orbitals.render.options.VisualOptions


private val MaxColumnWidth = 450.dp
private val ColumnSpacing = 16.dp
private val ColumnModifier = Modifier.widthIn(max = MaxColumnWidth)

private val SettingModifier: Modifier
    @Composable get() = Modifier
        .background(colorScheme.settingsScrim)
        .padding(16.dp)
        .fillMaxWidth()

@Composable
private operator fun PaddingValues.plus(other: PaddingValues): PaddingValues {
    val layoutDirection = LocalLayoutDirection.current
    return PaddingValues(
        start = this.calculateStartPadding(layoutDirection) + other.calculateStartPadding(
            layoutDirection
        ),
        top = this.calculateTopPadding() + other.calculateTopPadding(),
        end = this.calculateEndPadding(layoutDirection) + other.calculateEndPadding(layoutDirection),
        bottom = this.calculateBottomPadding() + other.calculateBottomPadding()
    )
}


@Composable
internal fun SettingsUI(
    availableWidth: Dp,
    availableHeight: Dp,
    options: Options,
    persistence: OptionPersistence,
    insets: PaddingValues = PaddingValues(),
    modifier: Modifier = Modifier,
    onClose: () -> Unit,
) {
    when {
        availableWidth < (MaxColumnWidth + ColumnSpacing) * 2 ->
            SettingsSingleColumn(
                options,
                persistence,
                modifier,
                insets + PaddingValues(
                    top = min(availableHeight / 2, 320.dp),
                    bottom = 160.dp,
                    start = 16.dp,
                    end = 16.dp
                ),
                onClose
            )


        availableWidth < (MaxColumnWidth + ColumnSpacing) * 3 ->
            SettingsTwoColumns(
                options,
                persistence,
                modifier,
                PaddingValues(bottom = 32.dp),
                onClose
            )


        else ->
            SettingsThreeColumns(
                options,
                persistence,
                modifier,
                PaddingValues(bottom = 32.dp),
                onClose
            )
    }
}


@Composable
private fun SettingsSingleColumn(
    options: Options,
    persistence: OptionPersistence,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    onClose: () -> Unit,
) {
    DraggableColumn(
        modifier.then(ColumnModifier),
        contentPadding = contentPadding,
    ) {
        item {
            Box(Modifier.fillMaxWidth()) {
                CloseSettings(onClose, Modifier.align(Alignment.BottomEnd))
            }
        }

        visualSettings(options.visualOptions, persistence)
        colorSettings(options.visualOptions.colorOptions, persistence)
        physicsSettings(options.physics, persistence)
    }
}

@Composable
private fun CloseSettings(onClick: () -> Unit, modifier: Modifier = Modifier) {
    ExtendedFloatingActionButton(
        text = { Text("Close settings") },
        icon = { Icon(Icons.Default.Close, "") },
        onClick = onClick,
        modifier = modifier,
    )
}

@Composable
private fun MultiColumn(
    modifier: Modifier = Modifier,
    onClose: () -> Unit,
    content: @Composable () -> Unit,
) {
    Column(modifier.padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        CloseSettings(onClose, Modifier.align(Alignment.End).padding(top = 16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            content()
        }
    }
}

@Composable
private fun SettingsTwoColumns(
    options: Options,
    persistence: OptionPersistence,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    onClose: () -> Unit,
) {
    MultiColumn(modifier, onClose) {
        DraggableColumn(ColumnModifier, contentPadding = contentPadding) {
            visualSettings(options.visualOptions, persistence)
            colorSettings(options.visualOptions.colorOptions, persistence)
        }

        DraggableColumn(ColumnModifier, contentPadding = contentPadding) {
            physicsSettings(options.physics, persistence)
        }
    }
}

@Composable
private fun SettingsThreeColumns(
    options: Options,
    persistence: OptionPersistence,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    onClose: () -> Unit,
) {
    MultiColumn(modifier, onClose) {
        DraggableColumn(ColumnModifier, contentPadding = contentPadding) {
            visualSettings(options.visualOptions, persistence)
        }

        DraggableColumn(ColumnModifier, contentPadding = contentPadding) {
            colorSettings(options.visualOptions.colorOptions, persistence)
        }

        DraggableColumn(ColumnModifier, contentPadding = contentPadding) {
            physicsSettings(options.physics, persistence)
        }
    }
}

private fun LazyListScope.visualSettings(
    visualOptions: VisualOptions,
    persistence: OptionPersistence,
) {
    settingsGroup("Visual")
    multiSelectSetting(
        name = "Layers",
        key = VisualKeys.RenderLayers,
        value = visualOptions.renderLayers,
        values = RenderLayer.values(),
        onValueChange = persistence::updateOption,
    )

    singleSelectSetting(
        name = "Style",
        key = VisualKeys.DrawStyle,
        value = visualOptions.drawStyle,
        values = DrawStyle.values(),
        onValueChange = persistence::updateOption,
    )

    conditional(visualOptions.drawStyle == DrawStyle.Wireframe) {
        FloatSetting(
            name = "Stroke width",
            key = VisualKeys.StrokeWidth,
            value = visualOptions.strokeWidth,
            onValueChange = persistence::updateOption,
            min = 1f,
            max = 24f,
        )
    }

    conditional(RenderLayer.Trails in visualOptions.renderLayers) {
        IntegerSetting(
            name = "History length",
            key = VisualKeys.TraceLength,
            value = visualOptions.traceLineLength,
            onValueChange = persistence::updateOption,
            min = 1,
            max = 120,
        )
    }
}

private fun LazyListScope.colorSettings(options: ColorOptions, persistence: OptionPersistence) {
    settingsGroup("Colors")

    colorSetting(
        name = "Background color",
        key = ColorKeys.BackgroundColor,
        value = options.background,
        onValueChange = persistence::updateOption,
    )
    multiSelectSetting(
        name = "Object colors",
        key = ColorKeys.Colors,
        value = options.bodies,
        values = ObjectColors.values(),
        onValueChange = persistence::updateOption,
    )
    floatSetting(
        name = "Opacity",
        key = ColorKeys.BodyAlpha,
        value = options.foregroundAlpha,
        onValueChange = persistence::updateOption,
        min = 0f,
        max = 1f,
    )
}

private fun LazyListScope.physicsSettings(physics: PhysicsOptions, persistence: OptionPersistence) {
    settingsGroup("Physics")

    switchSetting(
        "Auto-add bodies",
        key = PhysicsKeys.AutoAddBodies,
        value = physics.autoAddBodies,
        onValueChange = persistence::updateOption,
    )
    integerSetting(
        name = "Maximum population",
        key = PhysicsKeys.MaxEntities,
        value = physics.maxEntities,
        onValueChange = persistence::updateOption,
        min = 1,
        max = 200,
    )
    integerSetting(
        name = "Maximum age of fixed bodies (seconds)",
        key = PhysicsKeys.MaxFixedBodyAgeSeconds,
        value = physics.maxFixedBodyAge.inWholeSeconds.toInt(),
        onValueChange = persistence::updateOption,
        min = 10,
        max = 300,
    )
    floatSetting(
        name = "Gravity multiplier",
        key = PhysicsKeys.GravityMultiplier,
        value = physics.gravityMultiplier,
        onValueChange = persistence::updateOption,
        min = -10f,
        max = 10f,
    )
    multiSelectSetting(
        name = "System generators",
        key = PhysicsKeys.Generators,
        value = physics.systemGenerators,
        values = SystemGenerator.values(),
        onValueChange = persistence::updateOption,
    )
    singleSelectSetting(
        name = "Collision style",
        key = PhysicsKeys.CollisionStyle,
        value = physics.collisionStyle,
        values = CollisionStyle.values(),
        onValueChange = persistence::updateOption,
    )
    floatSetting(
        name = "Body density",
        key = PhysicsKeys.Density,
        value = physics.bodyDensity.value,
        onValueChange = persistence::updateOption,
        min = .05f,
        max = 10f,
    )
}

private fun LazyListScope.conditional(
    condition: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    item {
        AnimatedVisibility(
            visible = condition,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
            content = content,
            modifier = SettingModifier
        )
    }
}

private fun LazyListScope.colorSetting(
    name: String,
    key: ColorKey,
    value: Color,
    onValueChange: (key: ColorKey, newValue: Color) -> Unit,
) {
    item {
        ColorSetting(
            name = name,
            key = key,
            value = value,
            onValueChange = onValueChange,
            modifier = SettingModifier
        )
    }
}

private fun LazyListScope.integerSetting(
    name: String,
    key: IntKey,
    value: Int,
    onValueChange: (key: IntKey, newValue: Int) -> Unit,
    min: Int,
    max: Int,
) {
    item {
        IntegerSetting(
            name = name,
            key = key,
            value = value,
            onValueChange = onValueChange,
            min = min,
            max = max,
            modifier = SettingModifier,
        )
    }
}

private fun LazyListScope.floatSetting(
    name: String,
    key: FloatKey,
    value: Float,
    onValueChange: (key: FloatKey, newValue: Float) -> Unit,
    min: Float,
    max: Float,
) {
    item {
        FloatSetting(
            name = name,
            key = key,
            value = value,
            onValueChange = onValueChange,
            min = min,
            max = max,
            modifier = SettingModifier,
        )
    }
}

private fun <E : Enum<E>> LazyListScope.singleSelectSetting(
    name: String,
    key: StringKey<E>,
    value: E,
    values: Array<out E>,
    onValueChange: (key: StringKey<E>, newValue: E) -> Unit,
) {
    item {
        SingleSelectSetting(
            name = name,
            key = key,
            value = value,
            values = values,
            onValueChange = onValueChange,
            modifier = SettingModifier
        )
    }
}

private fun <E : Enum<E>> LazyListScope.multiSelectSetting(
    name: String,
    key: StringSetKey<E>,
    value: Set<E>,
    values: Array<out E>,
    onValueChange: (key: StringSetKey<E>, newValue: Set<E>) -> Unit,
) {
    item {
        MultiSelectSetting(
            name = name,
            key = key,
            value = value,
            values = values,
            onValueChange = onValueChange,
            modifier = SettingModifier
        )
    }
}

private fun LazyListScope.switchSetting(
    name: String,
    key: BooleanKey,
    value: Boolean,
    onValueChange: (key: BooleanKey, value: Boolean) -> Unit,
) {
    item {
        SwitchSetting(
            name = name,
            key = key,
            value = value,
            onValueChange = onValueChange,
            modifier = SettingModifier
        )
    }
}

private fun LazyListScope.settingsGroup(title: String) {
    item {
        Text(
            title,
            style = typography.headlineMedium,
            modifier = Modifier.padding(top = 16.dp).then(SettingModifier)
        )
    }
}
