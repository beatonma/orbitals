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

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(Module.Core))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(Dependencies.KotlinTest)
                implementation(project(Module.Test))
            }
        }
        val androidMain by getting {}
        val jvmMain by getting {}
        val jsMain by getting {}
    }
}
