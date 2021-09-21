import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

object Dependencies {
    val GradlePlugin = dependency("com.android.tools.build:gradle", Versions.GradlePlugin)
    val KotlinGradlePlugin =
        dependency("org.jetbrains.kotlin:kotlin-gradle-plugin", Versions.Kotlin)

    val KotlinStdLib = dependency("org.jetbrains.kotlin:kotlin-stdlib-jdk8", Versions.Kotlin)
    val KotlinReflect = dependency("org.jetbrains.kotlin:kotlin-reflect", Versions.Kotlin)
    const val KotlinTest = "org.jetbrains.kotlin:kotlin-test"

    val CoroutinesCore = dependency("org.jetbrains.kotlinx:kotlinx-coroutines-core", Versions.KotlinCoroutines)

    object Android {
        val CoroutinesAndroid = dependency("org.jetbrains.kotlinx:kotlinx-coroutines-android", Versions.KotlinCoroutines)

        val ActivityCompose = dependency("androidx.activity:activity-compose", Versions.Android.AppCompat)
        val ActivityKtx = dependency("androidx.activity:activity-ktx", Versions.Android.AppCompat)
        val Annotations = dependency("androidx.annotation:annotation", Versions.Android.Annotation)
        val AppCompat = dependency("androidx.appcompat:appcompat", Versions.Android.AppCompat)
        val CoreKtx = dependency("androidx.core:core-ktx", Versions.Android.CoreKtx)
        val DataStore = dependency("androidx.datastore:datastore-preferences", Versions.Android.DataStore)
        val NavigationCompose = dependency("androidx.navigation:navigation-compose", Versions.Android.NavigationCompose)
        val ViewModelCompose = dependency("androidx.lifecycle:lifecycle-viewmodel-compose", Versions.Android.ViewModelCompose)

        val Accompanist = dependency("com.google.accompanist:accompanist-insets", Versions.Android.Accompanist)
    }

    object Web {
        val KotlinReact = dependency("org.jetbrains.kotlin-wrappers:kotlin-react", Versions.Web.KotlinReact)
        val KotlinReactDom = dependency("org.jetbrains.kotlin-wrappers:kotlin-react-dom", Versions.Web.KotlinReact)
        val KotlinHtmlJs = dependency("org.jetbrains.kotlinx:kotlinx-html-js", Versions.Web.KotlinHtmlJs)
        val Styled = dependency("org.jetbrains.kotlin-wrappers:kotlin-styled", Versions.Web.StyledComponents)

        object Npm {
            val React = NpmDependency("react", Versions.Web.Npm.React)
            val ReactDom = NpmDependency("react-dom", Versions.Web.Npm.React)
            val StyledComponents = NpmDependency("styled-components", Versions.Web.Npm.StyledComponents)
        }
    }

    private fun dependency(name: String, version: String) = "$name:$version"
}

interface BaseDependency {
    val name: String
    val version: String
}

data class NpmDependency(
    override val name: String,
    override val version: String
): BaseDependency

fun KotlinDependencyHandler.npm(dependency: NpmDependency) =
    npm(dependency.name, dependency.version)
