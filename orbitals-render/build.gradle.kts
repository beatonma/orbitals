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
    commonMainImplementation(Dependencies.CoroutinesCore)

    commonTestImplementation(Dependencies.KotlinTest)
    commonTestImplementation(project(Module.Test))
}
