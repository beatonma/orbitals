package buildSrc.gradle

import AppConfig
import Versions
import com.android.build.api.dsl.LibraryExtension
import org.gradle.kotlin.dsl.get
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension


fun LibraryExtension.orbitalsLibrary(namespace: String) {
    this.namespace = namespace
    compileSdk = AppConfig.SdkTarget

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        minSdk = AppConfig.SdkMin

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = Versions.Java
        targetCompatibility = Versions.Java
    }
}

fun KotlinMultiplatformExtension.orbitalsLibrary() {
    android {}

    jvm {}

    js(IR) {
        browser {}
        binaries.executable()
    }
}
