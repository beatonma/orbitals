package org.beatonma.orbitals.render.options

import org.beatonma.orbitals.core.engine.SystemGenerator
import org.beatonma.orbitals.core.options.CollisionStyle
import org.beatonma.orbitals.core.options.PhysicsOptions
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

interface OptionPersistence {
    fun <E: Enum<E>> updateOption(key: StringKey<E>, value: E)
    fun <E: Enum<E>> updateOption(key: StringSetKey<E>, value: Set<E>)
    fun updateOption(key: IntKey, value: Int)
    fun updateOption(key: FloatKey, value: Float)
    fun updateOption(key: BooleanKey, value: Boolean)
}

interface OptionsStore {
    operator fun <T> get(key: Key<T>): T?

    @OptIn(ExperimentalTime::class)
    fun loadPhysics(): PhysicsOptions {
        return PhysicsOptions(
            autoAddBodies = this[PhysicsKey.AutoAddBodies] ?: true,
            maxEntities = this[PhysicsKey.MaxEntities] ?: 25,
            maxFixedBodyAge = Duration.seconds(this[PhysicsKey.MaxFixedBodyAgeSeconds] ?: 45),
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
        )
    }

    fun loadColors(): ColorOptions {
        return ColorOptions(
            background = this[ColorKey.BackgroundColor] ?: 0xff000000.toInt(),
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
            bodyScale = this[VisualKey.BodyScale] ?: 1f,
        )
    }

    fun loadOptions(): Options {
        val physics = loadPhysics()
        val colors = loadColors()
        val visuals = loadVisuals(colors)

        return Options(physics, visuals)
    }
}
