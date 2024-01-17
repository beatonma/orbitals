import buildSrc.gradle.orbitalsLibrary

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose") version Versions.Compose
}

android {
    orbitalsLibrary("org.beatonma.orbitals.render.compose")
}

kotlin {
    orbitalsLibrary()

    sourceSets {
        val androidMain by getting {}
        val commonMain by getting {
            dependencies {
                implementation(project(Module.Core))
                implementation(project(Module.Render))
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(Dependencies.KotlinTest)
            }
        }
        val jsMain by getting {}
    }
}
