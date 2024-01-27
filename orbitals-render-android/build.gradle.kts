import buildSrc.gradle.orbitalsLibrary

plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    orbitalsLibrary("org.beatonma.orbitals.render.android")
}

dependencies {
    implementation(project(Module.Core))
    implementation(project(Module.Render))

    testImplementation(libs.kotlin.test)
}
