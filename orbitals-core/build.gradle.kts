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

    sourceSets {
        val commonMain by getting {}
        val androidMain by getting {}
        val jsMain by getting {}
        val commonTest by getting {
            dependencies {
                implementation(Dependencies.KotlinTest)
                implementation(project(":${Module.Test}"))
            }
        }
    }
}
