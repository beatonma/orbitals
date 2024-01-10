import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

object Dependencies {
    val GradlePlugin = dependency("com.android.tools.build:gradle", Versions.GradlePlugin)

    val KotlinStdLib = dependency("org.jetbrains.kotlin:kotlin-stdlib", Versions.Kotlin)
    val KotlinReflect = dependency("org.jetbrains.kotlin:kotlin-reflect", Versions.Kotlin)
    const val KotlinTest = "org.jetbrains.kotlin:kotlin-test"

    val CoroutinesCore =
        dependency("org.jetbrains.kotlinx:kotlinx-coroutines-core", Versions.KotlinCoroutines)

    object Android {
        val CoroutinesAndroid = dependency(
            "org.jetbrains.kotlinx:kotlinx-coroutines-android",
            Versions.KotlinCoroutines
        )

        val ActivityCompose =
            dependency("androidx.activity:activity-compose", Versions.Android.ActivityCompose)
        val Annotations = dependency("androidx.annotation:annotation", Versions.Android.Annotation)
        val DataStore =
            dependency("androidx.datastore:datastore-preferences", Versions.Android.DataStore)

        //        val Material3 = dependency("androidx.compose.material3:material3", Versions.Android.Material3)
        val NavigationCompose =
            dependency("androidx.navigation:navigation-compose", Versions.Android.NavigationCompose)
        val ViewModelCompose = dependency(
            "androidx.lifecycle:lifecycle-viewmodel-compose",
            Versions.Android.ViewModelCompose
        )
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
) : BaseDependency

fun KotlinDependencyHandler.npm(dependency: NpmDependency) =
    npm(dependency.name, dependency.version)
