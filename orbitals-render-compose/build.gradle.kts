import buildSrc.gradle.orbitalsLibrary

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose") version Versions.JetbrainsCompose
}

android {
    orbitalsLibrary("org.beatonma.orbitals.render.compose")
}

kotlin {
    orbitalsLibrary()
}

dependencies {
    commonMainImplementation(project(Module.Core))
    commonMainImplementation(project(Module.Render))
    commonMainImplementation(compose.foundation)
    commonMainImplementation(compose.material3)
    commonMainImplementation(compose.ui)

    commonTestImplementation(Dependencies.KotlinTest)
}
