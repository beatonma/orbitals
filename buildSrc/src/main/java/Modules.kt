import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.project
import org.gradle.kotlin.dsl.support.delegates.SettingsDelegate

enum class Module(private val moduleName: String) {
    Android("app"),
    AndroidRender("orbitals-render-android"),
    Compose("orbitals-render-compose"),
    Core("orbitals"),
    Render("orbitals-render"),
    ;

    override fun toString(): String {
        return moduleName
    }
}

fun DependencyHandler.project(module: Module) =
    project(":$module")

fun SettingsDelegate.include(module: Module) = include(":$module")
