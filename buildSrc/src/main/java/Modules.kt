import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.project
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

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

fun KotlinDependencyHandler.project(module: Module) = project(":$module")
