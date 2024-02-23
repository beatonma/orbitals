import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.project
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

enum class Module(private val moduleName: String) {
    Core("core"),

    Render("render"),
    AndroidRender("renderAndroid"),
    ComposeRender("renderCompose"),

    ComposeApp("appCompose"),
    AndroidApp("appAndroid"),
    DesktopApp("appDesktop"),
    WebJsApp("appWebJs"),

    Test("test"),
    ;

    override fun toString(): String = moduleName
}

fun DependencyHandler.project(module: Module) =
    project(":$module")

fun KotlinDependencyHandler.project(module: Module) = project(":$module")
