import org.beatonma.orbitals.render.options.Options
import org.beatonma.orbitals.render.options.ColorOptions
import org.beatonma.orbitals.render.options.ObjectColors
import org.beatonma.orbitals.render.options.VisualOptions
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
import org.beatonma.orbitals.core.options.PhysicsOptions
import org.beatonma.orbitals.core.options.CollisionStyle
import org.beatonma.orbitals.core.engine.SystemGenerator
import org.w3c.dom.url.URLSearchParams
import kotlin.time.Duration
import kotlin.time.ExperimentalTime


fun createOptions(params: URLSearchParams): Options =
    UrlOptionsStore(params)
        .loadOptions()
        .also(::println)

@Suppress("UNCHECKED_CAST")
private class UrlOptionsStore(
    private val params: URLSearchParams,
): OptionsStore {
    override operator fun <T> get(key: Key<T>): T? {
        val value = params.get(key)
        return when (key) {
            is StringKey<*> -> value
            is StringSetKey<*> -> value?.split(",")?.toSet()
            is IntKey -> value?.toInt()
            is FloatKey -> value?.toFloat()
            is BooleanKey -> value?.toBoolean()
        } as? T
    }
}

private fun URLSearchParams.get(key: Key<*>) = get(key.key)
