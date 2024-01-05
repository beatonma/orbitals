import org.beatonma.orbitals.render.options.Options
import org.beatonma.orbitals.render.options.ObjectColors
import org.beatonma.orbitals.render.options.RenderLayer
import org.beatonma.orbitals.render.options.Key
import org.beatonma.orbitals.render.options.StringKey
import org.beatonma.orbitals.render.options.StringSetKey
import org.beatonma.orbitals.render.options.IntKey
import org.beatonma.orbitals.render.options.FloatKey
import org.beatonma.orbitals.render.options.BooleanKey
import org.beatonma.orbitals.render.options.DrawStyle
import org.beatonma.orbitals.render.options.VisualKey
import org.beatonma.orbitals.render.options.PhysicsKey
import org.beatonma.orbitals.render.options.ColorKey
import org.beatonma.orbitals.render.options.OptionsStore
import org.beatonma.orbitals.core.options.CollisionStyle
import org.beatonma.orbitals.core.engine.SystemGenerator
import org.beatonma.orbitals.core.util.info
import org.beatonma.orbitals.core.util.warn
import org.w3c.dom.url.URLSearchParams


fun createOptions(params: URLSearchParams): Options {
    return UrlOptionsStore(params)
        .also { it.printCustomisationInstructions() }
        .loadOptions()
        .also { info("Loaded options: $it") }
}


@Suppress("UNCHECKED_CAST")
private class UrlOptionsStore(
    private val params: URLSearchParams,
) : OptionsStore {
    override operator fun <T> get(key: Key<T>): T? {
        val value = params.get(key)

        if (key == ColorKey.BackgroundColor) {
            return value?.toColorInt() as? T
        }

        return when (key) {
            is StringKey<*> -> value as? T
            is StringSetKey<*> -> value?.split(",")?.toSet() as? T
            is IntKey -> value?.toInt() as? T
            is FloatKey -> value?.toFloat() as? T
            is BooleanKey -> value?.toBoolean() as? T
        }
    }

    fun printCustomisationInstructions() {
        info(
            """|Customisation:
           |
           |- Lists separated by '|' mean you can choose only one item.
           |- Lists separated by ',' mean you can choose as many as you like.
           |
           |Add the options you want to edit to the URL path
           |  e.g. https://beatonma.org/webapp/orbitals?gravity=2.5&colors=Purple,Greyscale&style=Wireframe&layers=Default,Trails&background=220033
           | 
           |${aboutPhysicsOptions()}
           |
           |${aboutVisualOptions()}
           |
           |${aboutColorOptions()}
           |""".trimMargin()
        )
    }

    private fun String.toColorInt(): Int = try {
        this.toInt(16)
    } catch (e: NumberFormatException) {
        warn(e)
        0xff0000
    }
}

private fun URLSearchParams.get(key: Key<*>) = get(key.key)

private fun <T> Array<T>.chooseOne() = joinToString("|") + " (choose one)"
private fun <T> Array<T>.chooseNultiple() = joinToString(",") + " (choose many)"
private fun Map<Key<*>, Any>.join(title: String) =
    "$title:\n " + this.map { (key, value) -> "${key.key}=${value}" }.joinToString("\n ")

private fun int(value: Any) = "$value (integer)"
private fun float(value: Any) = "$value (float)"
private fun bool(value: Any) = "$value (true|false)"

private fun aboutPhysicsOptions(): String {
    return mapOf<Key<*>, Any>(
        PhysicsKey.MaxFixedBodyAgeSeconds to int(45),
        PhysicsKey.Generators to SystemGenerator.values().chooseNultiple(),
        PhysicsKey.AutoAddBodies to bool(true),
        PhysicsKey.GravityMultiplier to float(.5f),
        PhysicsKey.MaxEntities to int(90),
        PhysicsKey.CollisionStyle to CollisionStyle.values().chooseOne(),
    ).join("Physics options")
}

private fun aboutVisualOptions(): String {
    return mapOf<Key<*>, Any>(
        VisualKey.DrawStyle to DrawStyle.values().chooseOne(),
        VisualKey.TraceLength to int(50),
        VisualKey.StrokeWidth to float(4f),
        VisualKey.RenderLayers to RenderLayer.values().chooseNultiple(),
        VisualKey.BodyScale to float(1.2f),
    ).join("Visual options")
}

private fun aboutColorOptions(): String {
    return mapOf<Key<*>, Any>(
        ColorKey.BackgroundColor to "220033 (hex color code)",
        ColorKey.Colors to ObjectColors.values().chooseNultiple(),
        ColorKey.BodyAlpha to float(.8f),
    ).join("Color options")
}
