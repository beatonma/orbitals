import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.project
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

enum class Module(private val moduleName: String) {
    Core("orbitals-core"),

    Render("orbitals-render"),
    AndroidRender("orbitals-render-android"),
    ComposeRender("orbitals-render-compose"),

    ComposeApp("composeApp"),

    Android("orbitals-android"),
    Desktop("orbitals-desktop"),
    Web("orbitals-web"),
    
    Test("test"),
    ;

    override fun toString(): String = moduleName
}

fun DependencyHandler.project(module: Module) =
    project(":$module")

fun KotlinDependencyHandler.project(module: Module) = project(":$module")
