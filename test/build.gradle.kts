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
    commonMainImplementation(Dependencies.KotlinTest)
    commonMainImplementation(project(Module.Core))
}
