object Dependencies {
    const val GradlePlugin = dependency("com.android.tools.build:gradle", Versions.GradlePlugin)
    const val KotlinGradlePlugin =
        dependency("org.jetbrains.kotlin:kotlin-gradle-plugin", Versions.Kotlin)

    const val KotlinStdLib = dependency("org.jetbrains.kotlin:kotlin-stdlib-jdk8", Versions.Kotlin)
    const val KotlinTest = "org.jetbrains.kotlin:kotlin-test"
//    const val KotlinSdk = dependency()

    private fun dependency(name: String, version: String) = "$name:$version"
}
