package org.beatonma.orbitals.render.options

import org.beatonma.orbitals.core.engine.SystemGenerator
import org.beatonma.orbitals.core.options.CollisionStyle
import org.beatonma.orbitals.core.options.PhysicsOptions
import org.beatonma.orbitals.core.physics.Density
import org.beatonma.orbitals.render.color.Color
import kotlin.time.Duration.Companion.seconds

private val DefaultPhysics = PhysicsOptions()
private val DefaultColors = ColorOptions()
private val DefaultVisuals = VisualOptions(colorOptions = DefaultColors)

interface OptionPersistence {
    fun <E : Enum<E>> updateOption(key: StringKey<E>, value: E)
    fun <E : Enum<E>> updateOption(key: StringSetKey<E>, value: Set<E>)
    fun updateOption(key: IntKey, value: Int)
    fun updateOption(key: ColorKey, value: Color)
    fun updateOption(key: FloatKey, value: Float)
    fun updateOption(key: BooleanKey, value: Boolean)
}

interface OptionsStore {
    operator fun <T> get(key: Key<T>): T?

    fun loadPhysics(): PhysicsOptions {
        return PhysicsOptions(
            autoAddBodies = this[PhysicsKeys.AutoAddBodies]
                ?: DefaultPhysics.autoAddBodies,
            maxEntities = this[PhysicsKeys.MaxEntities]
                ?: DefaultPhysics.maxEntities,
            maxFixedBodyAge = this[PhysicsKeys.MaxFixedBodyAgeSeconds]
                ?.seconds
                ?: DefaultPhysics.maxFixedBodyAge,
            systemGenerators = this[PhysicsKeys.Generators]
                ?.mapToEnumOrNull(SystemGenerator::valueOf)
                ?: DefaultPhysics.systemGenerators,
            gravityMultiplier = this[PhysicsKeys.GravityMultiplier]
                ?: DefaultPhysics.gravityMultiplier,
            collisionStyle = this[PhysicsKeys.CollisionStyle]
                ?.toEnumOrNull(CollisionStyle::valueOf)
                ?: DefaultPhysics.collisionStyle,
            bodyDensity = this[PhysicsKeys.Density]
                ?.let(::Density)
                ?: DefaultPhysics.bodyDensity,
        )
    }

    fun loadColors(): ColorOptions {
        return ColorOptions(
            background = this[ColorKeys.BackgroundColor]
                ?: DefaultColors.background,
            foregroundAlpha = this[ColorKeys.BodyAlpha]
                ?: DefaultColors.foregroundAlpha,
            bodies = this[ColorKeys.Colors]
                ?.mapToEnumOrNull(ObjectColors::valueOf)
                ?: DefaultColors.bodies
        )
    }

    fun loadVisuals(colors: ColorOptions = loadColors()): VisualOptions {
        return VisualOptions(
            colorOptions = colors,
            renderLayers = this[VisualKeys.RenderLayers]
                ?.mapToEnumOrNull(RenderLayer::valueOf)
                ?: DefaultVisuals.renderLayers,
            traceLineLength = this[VisualKeys.TraceLength]
                ?: DefaultVisuals.traceLineLength,
            drawStyle = this[VisualKeys.DrawStyle]
                ?.toEnumOrNull(DrawStyle::valueOf)
                ?: DefaultVisuals.drawStyle,
            strokeWidth = this[VisualKeys.StrokeWidth]
                ?: DefaultVisuals.strokeWidth,
        )
    }

    fun loadOptions(): Options {
        val physics = loadPhysics()
        val colors = loadColors()
        val visuals = loadVisuals(colors)

        return Options(physics, visuals)
    }
}


/**
 * Return null if set is empty or
 */
private fun <E : Enum<E>> Set<String>.mapToEnumOrNull(transform: (String) -> E): Set<E>? =
    this.let { it.ifEmpty { null } }
        ?.mapNotNull { it.toEnumOrNull(transform) }
        ?.toSet()
        ?.let { it.ifEmpty { null } }


private fun <E : Enum<E>> String.toEnumOrNull(transform: (String) -> E): E? = try {
    transform(this)
} catch (e: IllegalArgumentException) {
    null
}
