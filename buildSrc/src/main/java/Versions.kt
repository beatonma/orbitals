import org.gradle.api.JavaVersion

object Versions {
    const val GradlePlugin = "7.0.2"

    val Java = JavaVersion.VERSION_1_8

    const val Kotlin = "1.5.21"
    const val KotlinLanguage = "1.5"
    const val KotlinCoroutines = "1.5.2"

    object Jetpack {
        const val Annotation = "1.2.0"
        const val AppCompat = "1.3.1"
        const val CoreKtx = "1.6.0"
        const val Compose = "1.0.1"
        const val DataStore = "1.0.0"
        const val ViewModelCompose= "1.0.0-alpha07"
        const val NavigationCompose = "2.4.0-alpha08"
        const val Accompanist = "0.17.0"
    }

    object Desktop {
        const val Compose = "1.0.0-alpha3"
    }
}
