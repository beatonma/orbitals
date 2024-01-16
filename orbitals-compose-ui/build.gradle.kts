import buildSrc.gradle.orbitalsLibrary

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose") version Versions.Compose
}

android {
    orbitalsLibrary("org.beatonma.orbitals.compose.ui")
}

kotlin {
    orbitalsLibrary()

    sourceSets {
        val androidMain by getting {}
        val commonMain by getting {
            dependencies {
                implementation(project(":${Module.Core}"))
                implementation(project(":${Module.Render}"))
                implementation(project(":${Module.ComposeRender}"))
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material3)
            }
        }
    }
}
