import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

android {
    compileSdk = AppConfig.SdkTarget

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        minSdk = AppConfig.SdkMin

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = Versions.Java
        targetCompatibility = Versions.Java
    }
    namespace = "org.beatonma.orbitals.core"
}

kotlin {
    android {

    }

    jvm {

    }

    js(IR) {
        browser {

        }
        binaries.executable()
    }

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

tasks.withType<KotlinCompile>().all {
    kotlinOptions {
        jvmTarget = Versions.Java.toString()
        languageVersion = Versions.KotlinLanguage
        apiVersion = Versions.KotlinLanguage
    }
}
