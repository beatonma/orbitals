@file:OptIn(ExperimentalResourceApi::class)

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
import androidx.compose.material.icons.filled.Delete
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
import orbitals.`orbitals-compose-ui`.generated.resources.Res
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
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.enums.EnumEntries


private val MaxColumnWidth = 450.dp
private val Spacing = 16.dp

private val ColumnModifier = Modifier.widthIn(max = MaxColumnWidth)

private val SettingModifier: Modifier
    @Composable get() = Modifier
        .background(colorScheme.settingsScrim)
        .padding(Spacing)
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
    onCloseUI: () -> Unit,
    onDisableUI: (() -> Unit)?
) {
    when {
        availableWidth < (MaxColumnWidth + Spacing) * 2 ->
            SettingsSingleColumn(
                options,
                persistence,
                modifier,
                insets + PaddingValues(
                    top = min(availableHeight / 2, 320.dp),
                    bottom = 160.dp,
                    start = Spacing,
                    end = Spacing
                ),
                onCloseUI,
                onDisableUI
            )


        availableWidth < (MaxColumnWidth + Spacing) * 3 ->
            SettingsTwoColumns(
                options,
                persistence,
                modifier,
                PaddingValues(bottom = 32.dp),
                onCloseUI,
                onDisableUI
            )


        else ->
            SettingsThreeColumns(
                options,
                persistence,
                modifier,
                PaddingValues(bottom = 32.dp),
                onCloseUI,
                onDisableUI
            )
    }
}


@Composable
private fun SettingsSingleColumn(
    options: Options,
    persistence: OptionPersistence,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    onCloseUI: () -> Unit,
    onDisableUI: (() -> Unit)?,
) {
    DraggableColumn(
        modifier.then(ColumnModifier),
        contentPadding = contentPadding,
    ) {
        item {
            Box(Modifier.fillMaxWidth()) {
                SettingsOverlayButtons(
                    onCloseUI = onCloseUI,
                    onDisableUI = onDisableUI,
                    modifier = Modifier.align(Alignment.BottomEnd).padding(vertical = Spacing)
                )
            }
        }

        aboutOrbitals()
        visualSettings(options.visualOptions, persistence)
        colorSettings(options.visualOptions.colorOptions, persistence)
        physicsSettings(options.physics, persistence)
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun SettingsOverlayButtons(
    onCloseUI: () -> Unit,
    onDisableUI: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(Spacing, Alignment.End)) {
        onDisableUI?.let {
            ExtendedFloatingActionButton(
                text = { Text(stringResource(Res.string.settings__ui__disable_ui)) },
                icon = { Icon(Icons.Default.Delete, "") },
                onClick = it,
                containerColor = colorScheme.surface,
                contentColor = colorScheme.onSurface,
            )
        }

        ExtendedFloatingActionButton(
            text = { Text(stringResource(Res.string.settings__ui__close_ui)) },
            icon = { Icon(Icons.Default.Close, "") },
            onClick = onCloseUI,
        )
    }
}

@Composable
private fun MultiColumn(
    modifier: Modifier = Modifier,
    onCloseUI: () -> Unit,
    onDisableUI: (() -> Unit)?,
    content: @Composable () -> Unit,
) {
    Column(
        modifier.padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(Spacing)
    ) {
        SettingsOverlayButtons(
            onCloseUI = onCloseUI,
            onDisableUI = onDisableUI,
            modifier = Modifier.align(Alignment.End).padding(top = Spacing)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing),
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
    onCloseUI: () -> Unit,
    onDisableUI: (() -> Unit)?,
) {
    MultiColumn(modifier, onCloseUI, onDisableUI) {
        DraggableColumn(ColumnModifier, contentPadding = contentPadding) {
            visualSettings(options.visualOptions, persistence)
            colorSettings(options.visualOptions.colorOptions, persistence)
        }

        DraggableColumn(ColumnModifier, contentPadding = contentPadding) {
            aboutOrbitals()
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
    onCloseUI: () -> Unit,
    onDisableUI: (() -> Unit)?,
) {
    MultiColumn(modifier, onCloseUI, onDisableUI) {
        DraggableColumn(ColumnModifier, contentPadding = contentPadding) {
            visualSettings(options.visualOptions, persistence)
        }

        DraggableColumn(ColumnModifier, contentPadding = contentPadding) {
            colorSettings(options.visualOptions.colorOptions, persistence)
        }

        DraggableColumn(ColumnModifier, contentPadding = contentPadding) {
            aboutOrbitals()
            physicsSettings(options.physics, persistence)
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
private fun LazyListScope.visualSettings(
    visualOptions: VisualOptions,
    persistence: OptionPersistence,
) {
    settingsGroup(Res.string.settings__group_title__visual)
    multiSelectSetting(
        name = Res.string.settings__visual__render_layers,
        key = VisualKeys.RenderLayers,
        value = visualOptions.renderLayers,
        values = RenderLayer.entries,
        onValueChange = persistence::updateOption,
    )

    singleSelectSetting(
        name = Res.string.settings__visual__drawstyle,
        key = VisualKeys.DrawStyle,
        value = visualOptions.drawStyle,
        values = DrawStyle.entries,
        onValueChange = persistence::updateOption,
    )

    conditional(visualOptions.drawStyle == DrawStyle.Wireframe) {
        FloatSetting(
            name = stringResource(Res.string.settings__visual__stroke_width),
            key = VisualKeys.StrokeWidth,
            value = visualOptions.strokeWidth,
            onValueChange = persistence::updateOption,
            min = 1f,
            max = 24f,
        )
    }

    conditional(RenderLayer.Trails in visualOptions.renderLayers) {
        IntegerSetting(
            name = stringResource(Res.string.settings__visual__render_layers__trails_history_length),
            key = VisualKeys.TraceLength,
            value = visualOptions.traceLineLength,
            onValueChange = persistence::updateOption,
            min = 1,
            max = 120,
        )
    }
}

private fun LazyListScope.colorSettings(options: ColorOptions, persistence: OptionPersistence) {
    settingsGroup(Res.string.settings__group_title__color)

    colorSetting(
        name = Res.string.settings__color__background,
        key = ColorKeys.BackgroundColor,
        value = options.background,
        onValueChange = persistence::updateOption,
    )
    multiSelectSetting(
        name = Res.string.settings__color__objects,
        key = ColorKeys.Colors,
        value = options.bodies,
        values = ObjectColors.entries,
        onValueChange = persistence::updateOption,
    )
    floatSetting(
        name = Res.string.settings__color__opacity,
        key = ColorKeys.BodyAlpha,
        value = options.foregroundAlpha,
        onValueChange = persistence::updateOption,
        min = 0f,
        max = 1f,
    )
}

private fun LazyListScope.physicsSettings(physics: PhysicsOptions, persistence: OptionPersistence) {
    settingsGroup(Res.string.settings__group_title__physics)

    switchSetting(
        Res.string.settings__physics__auto_add,
        key = PhysicsKeys.AutoAddBodies,
        value = physics.autoAddBodies,
        onValueChange = persistence::updateOption,
    )
    integerSetting(
        name = Res.string.settings__physics__maximum_population,
        key = PhysicsKeys.MaxEntities,
        value = physics.maxEntities,
        onValueChange = persistence::updateOption,
        min = 1,
        max = 200,
    )
    integerSetting(
        name = Res.string.settings__physics__min_fixedbody_age,
        key = PhysicsKeys.MinFixedBodyAgeSeconds,
        value = physics.minFixedBodyAge.inWholeSeconds.toInt(),
        onValueChange = persistence::updateOption,
        min = 1,
        max = 300,
    )
    floatSetting(
        name = Res.string.settings__physics__gravity,
        key = PhysicsKeys.GravityMultiplier,
        value = physics.gravityMultiplier,
        onValueChange = persistence::updateOption,
        min = -10f,
        max = 10f,
    )
    multiSelectSetting(
        name = Res.string.settings__physics__system_generators,
        key = PhysicsKeys.Generators,
        value = physics.systemGenerators,
        values = SystemGenerator.entries,
        onValueChange = persistence::updateOption,
    )
    singleSelectSetting(
        name = Res.string.settings__physics__collision_style,
        key = PhysicsKeys.CollisionStyle,
        value = physics.collisionStyle,
        values = CollisionStyle.entries,
        onValueChange = persistence::updateOption,
    )
    floatSetting(
        name = Res.string.settings__physics__object_density,
        key = PhysicsKeys.Density,
        value = physics.bodyDensity.value,
        onValueChange = persistence::updateOption,
        min = .01f,
        max = 100f,
    )
}

private fun LazyListScope.aboutOrbitals() {
    item {
        AboutOrbitals(SettingModifier)
    }
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
    name: StringResource,
    key: ColorKey,
    value: Color,
    onValueChange: (key: ColorKey, newValue: Color) -> Unit,
) {
    item {
        ColorSetting(
            name = stringResource(name),
            key = key,
            value = value,
            onValueChange = onValueChange,
            modifier = SettingModifier
        )
    }
}

private fun LazyListScope.integerSetting(
    name: StringResource,
    key: IntKey,
    value: Int,
    onValueChange: (key: IntKey, newValue: Int) -> Unit,
    min: Int,
    max: Int,
) {
    item {
        IntegerSetting(
            name = stringResource(name),
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
    name: StringResource,
    key: FloatKey,
    value: Float,
    onValueChange: (key: FloatKey, newValue: Float) -> Unit,
    min: Float,
    max: Float,
) {
    item {
        FloatSetting(
            name = stringResource(name),
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
    name: StringResource,
    key: StringKey<E>,
    value: E,
    values: EnumEntries<E>,
    onValueChange: (key: StringKey<E>, newValue: E) -> Unit,
) {
    item {
        SingleSelectSetting(
            name = stringResource(name),
            key = key,
            value = value,
            values = values,
            onValueChange = onValueChange,
            modifier = SettingModifier
        )
    }
}

private fun <E : Enum<E>> LazyListScope.multiSelectSetting(
    name: StringResource,
    key: StringSetKey<E>,
    value: Set<E>,
    values: EnumEntries<E>,
    onValueChange: (key: StringSetKey<E>, newValue: Set<E>) -> Unit,
) {
    item {
        MultiSelectSetting(
            name = stringResource(name),
            key = key,
            value = value,
            values = values,
            onValueChange = onValueChange,
            modifier = SettingModifier
        )
    }
}

private fun LazyListScope.switchSetting(
    name: StringResource,
    key: BooleanKey,
    value: Boolean,
    onValueChange: (key: BooleanKey, value: Boolean) -> Unit,
) {
    item {
        SwitchSetting(
            name = stringResource(name),
            key = key,
            value = value,
            onValueChange = onValueChange,
            modifier = SettingModifier
        )
    }
}

private fun LazyListScope.settingsGroup(title: StringResource) {
    item {
        Text(
            stringResource(title),
            style = typography.headlineMedium,
            modifier = Modifier.padding(top = Spacing).then(SettingModifier)
        )
    }
}
