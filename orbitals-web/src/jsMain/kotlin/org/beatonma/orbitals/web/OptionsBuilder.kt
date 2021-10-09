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

private enum class Key(val key: String) {
    Debugging("debugging"),
    BackgroundColor("background"),
    Colors("colors"),
    DrawStyle("drawStyle"),
    StrokeWidth("strokeWidth"),
    RenderLayers("layers"),
    SystemGenerators("generators"),
    AutoAddBodies("auto"),
    GravityMultiplier("gravity"),
    MaxEntities("max"),
    CollisionStyle("collision")
    ;
}

private fun URLSearchParams.get(key: Key) = get(key.key)

fun createOptions(params: URLSearchParams): Options {
    return if (params.get(Key.Debugging) != null) {
        createDebugOptions(params)
    } else {
        Options(
            physics = createPhysicsOptions(params),
            visualOptions = createVisualOptions(params),
        )
    }.also(::println)
}


private fun createColorOptions(params: URLSearchParams): ColorOptions =
    ColorOptions(
        background = params.get(Key.BackgroundColor)
            ?.toColorInt()
            ?: "111111".toColorInt(),
        bodies = params.mapToSet(Key.Colors, transform = ObjectColors::valueOf)
            ?: setOf(
                ObjectColors.Greyscale,
            ),
    )


private fun createVisualOptions(params: URLSearchParams): VisualOptions {
    val colors = createColorOptions(params)

    return VisualOptions(
        colorOptions = colors,
        drawStyle = params.toType(Key.DrawStyle, transform = DrawStyle::valueOf)
            ?: DrawStyle.Solid,
        strokeWidth = params.toType(Key.StrokeWidth) ?: 4f,
        renderLayers = params.mapToSet(Key.RenderLayers, transform = RenderLayer::valueOf)
            ?: setOf(RenderLayer.Default)
    )
}


@OptIn(ExperimentalTime::class)
private fun createPhysicsOptions(params: URLSearchParams): PhysicsOptions {
    return PhysicsOptions(
        autoAddBodies = params.toType(Key.AutoAddBodies) ?: true,
        systemGenerators = params.mapToSet(Key.SystemGenerators, transform = SystemGenerator::valueOf)
            ?: setOf(
                SystemGenerator.StarSystem,
                SystemGenerator.Randomized,
                SystemGenerator.Asteroids,
            ),
        gravityMultiplier = params.toType(Key.GravityMultiplier) ?: 1f,
        maxEntities = params.toType(Key.MaxEntities) ?: 50,
        maxFixedBodyAge = Duration.seconds(20),
        collisionStyle = params.toType(Key.CollisionStyle, transform = CollisionStyle::valueOf) ?: CollisionStyle.Merge,
        )
}

private fun String.toColorInt(): Int {
    try {
        return this.toInt(16)
    } catch (e: NumberFormatException) {
        return 0xff0000
    }
}


/**
 * Options for debugging specific things
 */
private fun createDebugOptions(params: URLSearchParams): Options {
    return Options(
        physics = createDebugPhysicsOptions(params),
        visualOptions = createDebugVisualOptions(params),
    )
}

private fun createDebugColorOptions(params: URLSearchParams): ColorOptions =
    ColorOptions(
        background = params.get(Key.BackgroundColor)
            ?.toColorInt()
            ?: debugBackgroundColor(),
        bodies = params.mapToSet(Key.Colors, transform = ObjectColors::valueOf)
            ?: setOf(ObjectColors.Red),
    )

private fun debugBackgroundColor(): Int =
    (0..5).map { kotlin.random.Random.nextInt(0, 3) }.joinToString("").toColorInt()

private fun createDebugVisualOptions(params: URLSearchParams): VisualOptions {
    val colors = createDebugColorOptions(params)

    return VisualOptions(
        colorOptions = colors,
        drawStyle = params.toType(Key.DrawStyle, transform = DrawStyle::valueOf)
            ?: DrawStyle.Solid,
        strokeWidth = params.toType(Key.StrokeWidth) ?: 4f,
        renderLayers = params.mapToSet(Key.RenderLayers, transform = RenderLayer::valueOf)
            ?: setOf(
                RenderLayer.Default,
            )
    )
}


@OptIn(ExperimentalTime::class)
private fun createDebugPhysicsOptions(params: URLSearchParams): PhysicsOptions {
    return PhysicsOptions(
        autoAddBodies = params.toType(Key.AutoAddBodies) ?: false,
        systemGenerators = params.mapToSet(Key.SystemGenerators, transform = SystemGenerator::valueOf)
            ?: setOf(
                SystemGenerator.CollisionTester,
            ),
        gravityMultiplier = params.toType(Key.GravityMultiplier) ?: 0.25f,
        maxEntities = params.toType(Key.MaxEntities) ?: 10,
        maxFixedBodyAge = Duration.seconds(20),
        collisionStyle = params.toType(Key.CollisionStyle, transform = CollisionStyle::valueOf)
            ?: CollisionStyle.Sticky,
    )
}


private inline fun <reified T> URLSearchParams.toType(key: Key): T? {
    return get(key.key)?.let { value ->
        return when (T::class) {
            Int::class -> value.toInt() as? T
            Boolean::class -> value.toBoolean() as? T
            Float::class -> value.toFloat() as? T
            else -> throw Exception("Unhandled type ${T::class}: toType($key)")
        }
    }
}

private fun <T> URLSearchParams.toType(key: Key, transform: (String) -> T): T? {
    return get(key.key)?.let(transform)
}

private fun <T> URLSearchParams.mapToSet(key: Key, transform: (String) -> T): Set<T>? {
    return get(key.key)?.split(",")?.map(transform)?.toSet()
}
