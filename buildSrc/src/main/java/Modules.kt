import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.project
import org.gradle.kotlin.dsl.support.delegates.SettingsDelegate

enum class Module(private val moduleName: String) {
    Android("orbitals-android"),
    Desktop("orbitals-desktop"),
    Web("orbitals-web"),
    ComposeUi("orbitals-compose-ui"),

    Render("orbitals-render"),
    AndroidRender("orbitals-render-android"),
    ComposeRender("orbitals-render-compose"),

    Core("orbitals-core"),

    Test("test"),
    ;

    override fun toString(): String = moduleName
}

fun DependencyHandler.project(module: Module) =
    project(":$module")

fun SettingsDelegate.include(module: Module) = include(":$module")
