package org.beatonma.orbitals.compose.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.beatonma.orbitals.compose.ui.settings.ColorSetting
import org.beatonma.orbitals.compose.ui.settings.FloatSetting
import org.beatonma.orbitals.compose.ui.settings.IntegerSetting
import org.beatonma.orbitals.compose.ui.settings.MultiSelectSetting
import org.beatonma.orbitals.compose.ui.settings.SingleSelectSetting
import org.beatonma.orbitals.compose.ui.settings.SwitchSetting
import org.beatonma.orbitals.core.engine.SystemGenerator
import org.beatonma.orbitals.core.options.CollisionStyle
import org.beatonma.orbitals.core.options.PhysicsOptions
import org.beatonma.orbitals.render.options.ColorKey
import org.beatonma.orbitals.render.options.ColorOptions
import org.beatonma.orbitals.render.options.DrawStyle
import org.beatonma.orbitals.render.options.Key
import org.beatonma.orbitals.render.options.ObjectColors
import org.beatonma.orbitals.render.options.Options
import org.beatonma.orbitals.render.options.PhysicsKey
import org.beatonma.orbitals.render.options.RenderLayer
import org.beatonma.orbitals.render.options.VisualKey
import org.beatonma.orbitals.render.options.VisualOptions
import org.beatonma.orbitals.render.compose.Orbitals
import org.beatonma.orbitals.render.options.OptionPersistence
import kotlin.time.ExperimentalTime


@Composable
fun SettingsUi(
    options: Options,
    persistence: OptionPersistence,
) {
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
                VisualSettingsUI(options.visualOptions, persistence)
            }

            item {
                PhysicsSettingsUI(options.physics, persistence)
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
    persistence: OptionPersistence,
) {
    SettingsGroup("Visual") {
        MultiSelectSetting(
            name = "Layers",
            key = VisualKey.RenderLayers,
            value = visualOptions.renderLayers,
            values = RenderLayer.values(),
            onValueChange = persistence::updateOption,
        )

        SingleSelectSetting(
            name = "Style",
            key = VisualKey.DrawStyle,
            value = visualOptions.drawStyle,
            values = DrawStyle.values(),
            onValueChange = persistence::updateOption,
        )

        FloatSetting(
            name = "Body scale",
            key = VisualKey.BodyScale,
            value = visualOptions.bodyScale,
            onValueChange = persistence::updateOption,
            min = .25f,
            max = 5f,
        )

        Conditional(visualOptions.drawStyle == DrawStyle.Wireframe) {
            FloatSetting(
                name = "Stroke width",
                key = VisualKey.StrokeWidth,
                value = visualOptions.strokeWidth,
                onValueChange = persistence::updateOption,
                min = 1f,
                max = 24f,
            )
        }

        Conditional(RenderLayer.Trails in visualOptions.renderLayers) {
            IntegerSetting(
                name = "History length",
                key = VisualKey.TraceLength,
                value = visualOptions.traceLineLength,
                onValueChange = persistence::updateOption,
                min = 1,
                max = 120,
            )
        }
    }

    ColorSettingsUI(visualOptions.colorOptions, persistence)
}

@Composable
private fun ColorSettingsUI(
    colorOptions: ColorOptions,
    persistence: OptionPersistence,
) {
    SettingsGroup(title = "Colors") {
        ColorSetting(
            name = "Background color",
            key = ColorKey.BackgroundColor,
            value = colorOptions.background,
            onValueChange = persistence::updateOption,
        )

        MultiSelectSetting(
            name = "Object colors",
            key = ColorKey.Colors,
            value = colorOptions.bodies,
            values = ObjectColors.values(),
            onValueChange = persistence::updateOption,
        )
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun PhysicsSettingsUI(
    physics: PhysicsOptions,
    persistence: OptionPersistence,
) {
    SettingsGroup("Physics") {
        SwitchSetting(
            "Auto-add bodies",
            key = PhysicsKey.AutoAddBodies,
            value = physics.autoAddBodies,
            onValueChange = persistence::updateOption,
        )

        IntegerSetting(
            name = "Maximum population",
            key = PhysicsKey.MaxEntities,
            value = physics.maxEntities,
            onValueChange = persistence::updateOption,
            min = 1,
            max = 200,
        )

        IntegerSetting(
            name = "Maximum age of fixed bodies (seconds)",
            key = PhysicsKey.MaxFixedBodyAgeSeconds,
            value = physics.maxFixedBodyAge.inWholeSeconds.toInt(),
            onValueChange = persistence::updateOption,
            min = 30,
            max = 300,
        )

        FloatSetting(
            name = "Gravity multiplier",
            key = PhysicsKey.GravityMultiplier,
            value = physics.gravityMultiplier,
            onValueChange = persistence::updateOption,
            min = .1f,
            max = 10f,
        )

        MultiSelectSetting(
            name = "System generators",
            key = PhysicsKey.Generators,
            value = physics.systemGenerators,
            values = SystemGenerator.values(),
            onValueChange = persistence::updateOption,
        )

        SingleSelectSetting(
            name = "Collision style",
            key = PhysicsKey.CollisionStyle,
            value = physics.collisionStyle,
            values = CollisionStyle.values(),
            onValueChange = persistence::updateOption,
        )
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
