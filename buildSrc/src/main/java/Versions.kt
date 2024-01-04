import org.gradle.api.JavaVersion

object Versions {
    const val GradlePlugin = "8.0.2"

    val Java = JavaVersion.VERSION_17

    const val Kotlin = "1.8.20"
    const val KotlinLanguage = "1.8"
    const val KotlinCoroutines = "1.7.3"
    const val Compose = "1.5.11"

    object Android {
        const val ActivityCompose = "1.8.2"
        const val Annotation = "1.2.0"
        const val Compose = "1.4.6"
        const val DataStore = "1.0.0"
        const val Material3 = "1.2.0-beta01"
        const val NavigationCompose = "2.7.6"
        const val ViewModelCompose= "2.6.2"
    }
}
