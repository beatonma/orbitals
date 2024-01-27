import buildSrc.gradle.orbitalsLibrary

plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

android {
    orbitalsLibrary("org.beatonma.orbitals.test")
}

kotlin {
    orbitalsLibrary()
}

dependencies {
    commonMainImplementation(libs.kotlin.test)
    commonMainImplementation(project(Module.Core))
}
