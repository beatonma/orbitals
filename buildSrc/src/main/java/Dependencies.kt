object Dependencies {
    val GradlePlugin = dependency("com.android.tools.build:gradle", Versions.GradlePlugin)
    val KotlinGradlePlugin =
        dependency("org.jetbrains.kotlin:kotlin-gradle-plugin", Versions.Kotlin)

    val KotlinStdLib = dependency("org.jetbrains.kotlin:kotlin-stdlib-jdk8", Versions.Kotlin)
    val KotlinReflect = dependency("org.jetbrains.kotlin:kotlin-reflect", Versions.Kotlin)
    const val KotlinTest = "org.jetbrains.kotlin:kotlin-test"

    val Annotations = dependency("androidx.annotation:annotation", Versions.Jetpack.Annotation)

    val ComposeUI = dependency("androidx.compose.ui:ui", Versions.Jetpack.Compose)
    val ComposeMaterial = dependency("androidx.compose.material:material", Versions.Jetpack.Compose)
    val ComposeFoundation = dependency("androidx.compose.foundation:foundation", Versions.Jetpack.Compose)

    val CoroutinesCore = dependency("org.jetbrains.kotlinx:kotlinx-coroutines-core", Versions.KotlinCoroutines)
    val CoroutinesAndroid = dependency("org.jetbrains.kotlinx:kotlinx-coroutines-android", Versions.KotlinCoroutines)

    val AppCompat = dependency("androidx.appcompat:appcompat", Versions.Jetpack.AppCompat)
    val CoreKtx = dependency("androidx.core:core-ktx", Versions.Jetpack.CoreKtx)

    val ActivityKtx = dependency("androidx.activity:activity-ktx", Versions.Jetpack.AppCompat)
    val ActivityCompose = dependency("androidx.activity:activity-compose", Versions.Jetpack.AppCompat)
    val ViewModelCompose = dependency("androidx.lifecycle:lifecycle-viewmodel-compose", Versions.Jetpack.ViewModelCompose)
    val NavigationCompose = dependency("androidx.navigation:navigation-compose", Versions.Jetpack.NavigationCompose)
    val Accompanist = dependency("com.google.accompanist:accompanist-insets", Versions.Jetpack.Accompanist)

    val DataStore = dependency("androidx.datastore:datastore-preferences", Versions.Jetpack.DataStore)

    private fun dependency(name: String, version: String) = "$name:$version"
}
