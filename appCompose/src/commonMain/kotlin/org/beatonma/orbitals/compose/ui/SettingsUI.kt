package org.beatonma.orbitals.compose.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import orbitals.appcompose.generated.resources.Res
import org.beatonma.orbitals.compose.ui.components.Button
import org.beatonma.orbitals.compose.ui.components.ButtonData
import org.beatonma.orbitals.compose.ui.components.DraggableColumn
import org.beatonma.orbitals.compose.ui.components.HintButton
import org.beatonma.orbitals.compose.ui.settings.ColorSetting
import org.beatonma.orbitals.compose.ui.settings.FloatSetting
import org.beatonma.orbitals.compose.ui.settings.IntegerSetting
import org.beatonma.orbitals.compose.ui.settings.MultiSelectSetting
import org.beatonma.orbitals.compose.ui.settings.SingleSelectSetting
import org.beatonma.orbitals.compose.ui.settings.SwitchSetting
import org.beatonma.orbitals.compose.ui.settings.maybeStringResource
import org.beatonma.orbitals.core.engine.SystemGenerator
import org.beatonma.orbitals.core.fastForEach
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


internal class SettingsUiActions(
    val onCloseUI: () -> Unit,
    val onDisableUI: (() -> Unit)?,
    val extras: List<ButtonData>? = null,
)


@Composable
internal fun SettingsUI(
    availableWidth: Dp,
    availableHeight: Dp,
    options: Options,
    persistence: OptionPersistence,
    insets: PaddingValues = PaddingValues(),
    modifier: Modifier = Modifier,
    actions: SettingsUiActions,
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
                actions,
            )

        availableWidth < (MaxColumnWidth + Spacing) * 3 ->
            SettingsTwoColumns(
                options,
                persistence,
                modifier,
                PaddingValues(bottom = 32.dp),
                actions,
            )

        else ->
            SettingsThreeColumns(
                options,
                persistence,
                modifier,
                PaddingValues(bottom = 32.dp),
                actions,
            )
    }
}


@OptIn(ExperimentalResourceApi::class)
@Composable
private fun ActionBar(
    expanded: Boolean,
    actions: SettingsUiActions,
    modifier: Modifier = Modifier,
) {
    val backgroundColor by animateColorAsState(if (expanded) colorScheme.scrim else colorScheme.tertiaryContainer)

    Surface(modifier, shape = shapes.medium, color = backgroundColor) {
        Row(
            Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(Spacing, Alignment.End),
            verticalAlignment = Alignment.Bottom,
        ) {
            actions.extras?.fastForEach { button ->
                Button(button)
            }

            actions.onDisableUI?.let { onClick ->
                Button(
                    Icons.Default.Delete,
                    stringResource(Res.string.settings__ui__disable_ui),
                    null,
                    onClick = onClick
                )
            }

            HintButton(
                expanded,
                Icons.Default.Close,
                stringResource(Res.string.settings__ui__close_ui),
                iconContentDescription = null,
                onClick = actions.onCloseUI,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.actionBar(
    actions: SettingsUiActions,
    state: LazyListState,
    modifier: Modifier = Modifier,
) {
    stickyHeader(key = "menu_actions") {
        Box(modifier.fillMaxWidth()) {
            ActionBar(
                state.firstVisibleItemIndex == 0 && state.firstVisibleItemScrollOffset == 0,
                actions,
                Modifier.align(Alignment.BottomEnd).padding(bottom = Spacing, top = Spacing * 2)
            )
        }
    }
}


@Composable
private fun SettingsSingleColumn(
    options: Options,
    persistence: OptionPersistence,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    actions: SettingsUiActions,
) {
    val state = rememberLazyListState()
    DraggableColumn(
        modifier.then(ColumnModifier),
        state = state,
        contentPadding = contentPadding,
    ) {
        actionBar(actions, state)

        aboutOrbitals()
        visualSettings(options.visualOptions, persistence)
        colorSettings(options.visualOptions.colorOptions, persistence)
        physicsSettings(options.physics, persistence)
    }
}

@Composable
private fun MultiColumn(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier.padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(Spacing),
        verticalAlignment = Alignment.Bottom,
        content = content,
    )
}

@Composable
private fun SettingsTwoColumns(
    options: Options,
    persistence: OptionPersistence,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    actions: SettingsUiActions,
) {
    val primaryColumnState = rememberLazyListState()

    MultiColumn(modifier) {
        DraggableColumn(ColumnModifier, contentPadding = contentPadding) {
            visualSettings(options.visualOptions, persistence)
            colorSettings(options.visualOptions.colorOptions, persistence)
        }

        DraggableColumn(
            ColumnModifier,
            state = primaryColumnState,
            contentPadding = contentPadding
        ) {
            actionBar(actions, primaryColumnState)
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
    actions: SettingsUiActions,
) {
    val primaryColumnState = rememberLazyListState()

    MultiColumn(modifier) {
        DraggableColumn(ColumnModifier, contentPadding = contentPadding) {
            visualSettings(options.visualOptions, persistence)
        }

        DraggableColumn(ColumnModifier, contentPadding = contentPadding) {
            colorSettings(options.visualOptions.colorOptions, persistence)
        }

        DraggableColumn(
            ColumnModifier,
            state = primaryColumnState,
            contentPadding = contentPadding
        ) {
            actionBar(actions, primaryColumnState)
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

    conditional(RenderLayer.Trails in visualOptions.renderLayers) {
        IntegerSetting(
            name = stringResource(Res.string.settings__visual__render_layers__trails_history_length),
            helpText = null,
            key = VisualKeys.TraceLength,
            value = visualOptions.traceLineLength,
            onValueChange = persistence::updateOption,
            min = 1,
            max = 120,
        )
    }

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
            helpText = null,
            key = VisualKeys.StrokeWidth,
            value = visualOptions.strokeWidth,
            onValueChange = persistence::updateOption,
            min = 1f,
            max = 24f,
        )
    }

    switchSetting(
        name = Res.string.settings__visual__draw_novae,
        helpText = null,
        key = VisualKeys.drawNovae,
        value = visualOptions.drawNovae,
        onValueChange = persistence::updateOption,
    )
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
        helpText = null,
        key = ColorKeys.BodyAlpha,
        value = options.foregroundAlpha,
        onValueChange = persistence::updateOption,
        min = 0f,
        max = 1f,
    )
}

private fun LazyListScope.physicsSettings(physics: PhysicsOptions, persistence: OptionPersistence) {
    settingsGroup(Res.string.settings__group_title__physics)

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
    switchSetting(
        Res.string.settings__physics__auto_add,
        helpText = Res.string.settings__physics__auto_add__help,
        key = PhysicsKeys.AutoAddBodies,
        value = physics.autoAddBodies,
        onValueChange = persistence::updateOption,
    )
    integerSetting(
        name = Res.string.settings__physics__maximum_population,
        helpText = null,
        key = PhysicsKeys.MaxEntities,
        value = physics.maxEntities,
        onValueChange = persistence::updateOption,
        min = 1,
        max = 200,
    )
    integerSetting(
        name = Res.string.settings__physics__min_fixedbody_age,
        helpText = Res.string.settings__physics__min_fixedbody_age__help,
        key = PhysicsKeys.MinFixedBodyAgeSeconds,
        value = physics.minFixedBodyAge.inWholeSeconds.toInt(),
        onValueChange = persistence::updateOption,
        min = 1,
        max = 300,
    )
    floatSetting(
        name = Res.string.settings__physics__gravity,
        helpText = null,
        key = PhysicsKeys.GravityMultiplier,
        value = physics.gravityMultiplier,
        onValueChange = persistence::updateOption,
        min = -10f,
        max = 10f,
    )
    floatSetting(
        name = Res.string.settings__physics__object_density,
        helpText = Res.string.settings__physics__object_density__help,
        key = PhysicsKeys.Density,
        value = physics.bodyDensity.value,
        onValueChange = persistence::updateOption,
        min = .01f,
        max = 100f,
    )
}

private fun LazyListScope.aboutOrbitals() {
    item(key = "about") {
        AboutOrbitals(SettingModifier)
    }
}

private fun LazyListScope.conditional(
    condition: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
    item {
        AnimatedVisibility(
            visible = condition,
            modifier = SettingModifier,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
            content = content,
        )
    }
}

private fun LazyListScope.colorSetting(
    name: StringResource,
    key: ColorKey,
    value: Color,
    onValueChange: (key: ColorKey, newValue: Color) -> Unit,
) {
    item(key = key.key, contentType = ContentType.Color) {
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
    helpText: StringResource?,
    key: IntKey,
    value: Int,
    onValueChange: (key: IntKey, newValue: Int) -> Unit,
    min: Int,
    max: Int,
) {
    item(key = key.key, contentType = ContentType.Integer) {
        IntegerSetting(
            name = stringResource(name),
            helpText = maybeStringResource(helpText),
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
    helpText: StringResource?,
    key: FloatKey,
    value: Float,
    onValueChange: (key: FloatKey, newValue: Float) -> Unit,
    min: Float,
    max: Float,
) {
    item(key = key.key, contentType = ContentType.Float) {
        FloatSetting(
            name = stringResource(name),
            helpText = maybeStringResource(helpText),
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
    item(key = key.key, contentType = ContentType.SingleSelect) {
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
    item(key = key.key, contentType = ContentType.MultiSelect) {
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
    helpText: StringResource?,
    key: BooleanKey,
    value: Boolean,
    onValueChange: (key: BooleanKey, value: Boolean) -> Unit,
) {
    item(key = key.key, contentType = ContentType.Switch) {
        SwitchSetting(
            name = stringResource(name),
            helpText = maybeStringResource(helpText),
            key = key,
            value = value,
            onValueChange = onValueChange,
            modifier = SettingModifier
        )
    }
}

private fun LazyListScope.settingsGroup(title: StringResource) {
    item(contentType = ContentType.Title) {
        Text(
            stringResource(title),
            style = typography.headlineMedium,
            modifier = Modifier.padding(top = Spacing).then(SettingModifier)
        )
    }
}

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


private enum class ContentType {
    Title,
    Color,
    Switch,
    Integer,
    Float,
    SingleSelect,
    MultiSelect,
}
