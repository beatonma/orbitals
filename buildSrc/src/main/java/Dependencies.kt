object Dependencies {
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

        val NavigationCompose =
            dependency("androidx.navigation:navigation-compose", Versions.Android.NavigationCompose)
        val ViewModelCompose = dependency(
            "androidx.lifecycle:lifecycle-viewmodel-compose",
            Versions.Android.ViewModelCompose
        )
        val CoroutinesLifecycle = dependency(
            "androidx.lifecycle:lifecycle-runtime-ktx",
            Versions.Android.LifecycleCoroutines
        )
    }

    private fun dependency(name: String, version: String) = "$name:$version"
}
