import buildSrc.gradle.orbitalsLibrary

plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

android {
    orbitalsLibrary("org.beatonma.orbitals.render")
}

kotlin {
    orbitalsLibrary()
}

dependencies {
    commonMainImplementation(project(Module.Core))
    commonMainImplementation(libs.kotlin.coroutines)

    commonTestImplementation(libs.kotlin.test)
    commonTestImplementation(project(Module.Test))
}
