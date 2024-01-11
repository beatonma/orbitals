package org.beatonma.orbitals.render.options

import org.beatonma.orbitals.core.engine.SystemGenerator
import org.beatonma.orbitals.core.options.CollisionStyle
import org.beatonma.orbitals.core.options.PhysicsOptions
import org.beatonma.orbitals.core.physics.Density
import org.beatonma.orbitals.render.color.Color
import kotlin.time.Duration.Companion.seconds

interface OptionPersistence {
    fun <E : Enum<E>> updateOption(key: StringKey<E>, value: E)
    fun <E : Enum<E>> updateOption(key: StringSetKey<E>, value: Set<E>)
    fun updateOption(key: IntKey, value: Int)
    fun updateOption(key: FloatKey, value: Float)
    fun updateOption(key: BooleanKey, value: Boolean)
}

interface OptionsStore {
    operator fun <T> get(key: Key<T>): T?

    fun loadPhysics(): PhysicsOptions {
        return PhysicsOptions(
            autoAddBodies = this[PhysicsKey.AutoAddBodies] ?: true,
            maxEntities = this[PhysicsKey.MaxEntities] ?: 25,
            maxFixedBodyAge = (this[PhysicsKey.MaxFixedBodyAgeSeconds] ?: 45).seconds,
            systemGenerators = this[PhysicsKey.Generators]
                ?.map(SystemGenerator::valueOf)
                ?.toSet()
                ?: setOf(
                    SystemGenerator.Randomized,
                    SystemGenerator.StarSystem,
                    SystemGenerator.Asteroids,
                    SystemGenerator.GreatAttractor,
                ),
            gravityMultiplier = this[PhysicsKey.GravityMultiplier] ?: .25f,
            collisionStyle = this[PhysicsKey.CollisionStyle]?.let(CollisionStyle::valueOf)
                ?: CollisionStyle.Merge,
            bodyDensity = Density(this[PhysicsKey.Density] ?: .5f),
        )
    }

    fun loadColors(): ColorOptions {
        return ColorOptions(
            background = Color(this[ColorKey.BackgroundColor] ?: 0x000000),
            foregroundAlpha = this[ColorKey.BodyAlpha] ?: 1f,
            bodies = this[ColorKey.Colors]?.map(ObjectColors::valueOf)?.toSet() ?: setOf(
                ObjectColors.Greyscale,
                ObjectColors.Red
            )
        )
    }

    fun loadVisuals(colors: ColorOptions = loadColors()): VisualOptions {
        return VisualOptions(
            colorOptions = colors,
            renderLayers = this[VisualKey.RenderLayers]?.map(RenderLayer::valueOf)?.toSet()
                ?: setOf(RenderLayer.Default),
            traceLineLength = this[VisualKey.TraceLength] ?: 50,
            drawStyle = this[VisualKey.DrawStyle]?.let(DrawStyle::valueOf) ?: DrawStyle.Solid,
            strokeWidth = this[VisualKey.StrokeWidth] ?: 4f,
        )
    }

    fun loadOptions(): Options {
        val physics = loadPhysics()
        val colors = loadColors()
        val visuals = loadVisuals(colors)

        return Options(physics, visuals)
    }
}
