import org.gradle.api.JavaVersion
import org.gradle.api.Project
import java.io.File
import java.io.FileInputStream
import java.util.Properties


object Versions {
    private var _properties: Properties? = null
    private fun properties(project: Project): Properties = _properties ?: Properties().apply {
        load(
            FileInputStream(
                File(
                    project.rootProject.projectDir,
                    "buildSrc/versions.properties"
                )
            )
        )
    }

    fun gradlePlugin(project: Project): String = properties(project).getProperty("gradle")
    fun kotlin(project: Project): String = properties(project).getProperty("kotlin")
    fun kotlinLanguage(project: Project): String = properties(project).getProperty("kotlinLanguage")

    val Java = JavaVersion.VERSION_17

    const val KotlinCoroutines = "1.7.3"
    const val JetbrainsCompose = "1.6.0-alpha01"

    object Android {
        const val ActivityCompose = "1.8.2"
        const val Annotation = "1.2.0"
        const val ComposeExtension = "1.5.8"
        const val DataStore = "1.0.0"
        const val NavigationCompose = "2.7.6"
        const val ViewModelCompose = "2.6.2"
        const val LifecycleCoroutines = "2.4.0"
    }
}
