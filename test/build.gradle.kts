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

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(Dependencies.KotlinTest)
            }
        }
        val androidMain by getting {}
        val jsMain by getting {}
        val commonTest by getting {}
    }
}
//
//tasks.withType<KotlinCompile>().all {
//    kotlinOptions {
//        jvmTarget = Versions.Java.toString()
//        languageVersion = Versions.KotlinLanguage
//        apiVersion = Versions.KotlinLanguage
//    }
//}
