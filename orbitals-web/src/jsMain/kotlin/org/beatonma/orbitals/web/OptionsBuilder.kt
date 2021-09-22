import org.beatonma.orbitals.render.options.Options
import org.beatonma.orbitals.render.options.ColorOptions
import org.beatonma.orbitals.render.options.ObjectColors
import org.beatonma.orbitals.render.options.VisualOptions
import org.beatonma.orbitals.render.options.RenderLayer
import org.beatonma.orbitals.render.options.DrawStyle
import org.beatonma.orbitals.core.options.PhysicsOptions
import org.beatonma.orbitals.core.options.CollisionStyle
import org.beatonma.orbitals.core.engine.SystemGenerator
import org.w3c.dom.url.URLSearchParams
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

fun createOptions(params: URLSearchParams): Options {
    val visuals = createVisualOptions(params)
    val physics = createPhysicsOptions(params)

    println(visuals)

    return Options(
        physics = physics,
        visualOptions = visuals,
    )
}


private fun createColorOptions(params: URLSearchParams): ColorOptions =
    ColorOptions(
        background = params.get("background")
            ?.toColorInt()
            ?: "111111".toColorInt(),
        bodies = params.get("colors")
            ?.split(",")
            ?.map(ObjectColors::valueOf)
            ?.toSet()
            ?: setOf(ObjectColors.Greyscale),
    )


private fun createVisualOptions(params: URLSearchParams): VisualOptions {
    val colors = createColorOptions(params)

    return VisualOptions(
        colorOptions = colors,
        drawStyle = params.get("drawStyle")
            ?.let { DrawStyle.valueOf(it) }
            ?: DrawStyle.Solid,
        strokeWidth = params.get("strokeWidth")?.toFloat() ?: 4f,
        renderLayers = params.get("layers")
            ?.split(",")
            ?.map(RenderLayer::valueOf)
            ?.toSet()
            ?: setOf(RenderLayer.Default)
    )
}


@OptIn(ExperimentalTime::class)
private fun createPhysicsOptions(params: URLSearchParams): PhysicsOptions {
    return PhysicsOptions(
        autoAddBodies = params.get("autoAddBodies")?.toBoolean() ?: true,
        systemGenerators = params.get("generators")
            ?.split(",")
            ?.map(SystemGenerator::valueOf)
            ?.toSet()
            ?: setOf(
                SystemGenerator.StarSystem,
                SystemGenerator.Randomized,
                SystemGenerator.Asteroids,
            ),
        gravityMultiplier = params.get("gravityMultiplier")?.toFloat() ?: 1f,
        maxEntities = params.get("maxEntities")?.toInt() ?: 15,
        maxFixedBodyAge = Duration.seconds(20),
        collisionStyle = params.get("collisionStyle")
            ?.let(CollisionStyle::valueOf)
            ?: CollisionStyle.None,

    )
}

private fun String.toColorInt(): Int {
    try {
        return this.toInt(16)
    } catch (e: NumberFormatException) {
        return 0xff0000
    }
}
