import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.browser.window
import org.beatonma.orbitals.compose.ui.settings.format
import org.beatonma.orbitals.render.color.Color
import org.beatonma.orbitals.render.color.toColor
import org.beatonma.orbitals.render.options.BooleanKey
import org.beatonma.orbitals.render.options.ColorKey
import org.beatonma.orbitals.render.options.FloatKey
import org.beatonma.orbitals.render.options.IntKey
import org.beatonma.orbitals.render.options.Key
import org.beatonma.orbitals.render.options.OptionPersistence
import org.beatonma.orbitals.render.options.Options
import org.beatonma.orbitals.render.options.OptionsStore
import org.beatonma.orbitals.render.options.StringKey
import org.beatonma.orbitals.render.options.StringSetKey
import org.w3c.dom.url.URLSearchParams


/**
 * Read and write options via the URL ?search parameters.
 */
class UrlOptionsPersistence : OptionPersistence {
    var options: Options by mutableStateOf(createOptions(URLSearchParams(window.location.search)))

    private fun set(key: Key<*>, value: String) {
        val params = URLSearchParams(window.location.search)
        params.set(key.key, value)
        window.history.replaceState(null, "options", "?$params")
        options = createOptions(params)
    }

    private fun createOptions(params: URLSearchParams) =
        UrlOptionsStore(params).loadOptions()

    override fun updateOption(key: ColorKey, value: Color) {
        set(key, value.toStringRgb())
    }

    override fun <E : Enum<E>> updateOption(key: StringKey<E>, value: E) {
        set(key, value.name)
    }

    override fun updateOption(key: BooleanKey, value: Boolean) {
        set(key, value.toString().lowercase())
    }

    override fun <E : Enum<E>> updateOption(key: StringSetKey<E>, value: Set<E>) {
        set(key, value.joinToString(","))
    }

    override fun updateOption(key: FloatKey, value: Float) {
        set(key, value.format())
    }

    override fun updateOption(key: IntKey, value: Int) {
        set(key, value.toString())
    }
}


private class UrlOptionsStore(
    private val params: URLSearchParams,
) : OptionsStore {
    override operator fun <T> get(key: Key<T>): T? {
        val value = params.get(key)

        @Suppress("UNCHECKED_CAST")
        return when (key) {
            is BooleanKey -> value?.toBoolean() as? T
            is ColorKey -> value?.toColor() as? T
            is FloatKey -> value?.toFloat() as? T
            is IntKey -> value?.toInt() as? T
            is StringKey<*> -> value as? T
            is StringSetKey<*> -> value?.split(",")?.toSet() as? T
        }
    }

    private fun URLSearchParams.get(key: Key<*>) = get(key.key)
}
