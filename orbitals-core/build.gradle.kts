import buildSrc.gradle.orbitalsLibrary

plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

android {
    orbitalsLibrary("org.beatonma.orbitals.core")
}

kotlin {
    orbitalsLibrary()
}

dependencies {
    commonTestImplementation(libs.kotlin.test)
    commonTestImplementation(project(Module.Test))
}
