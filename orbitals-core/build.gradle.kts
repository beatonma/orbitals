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
            }
        }
    }
}

tasks.withType<KotlinCompile>().all {
    kotlinOptions {
        jvmTarget = Versions.Java.toString()
        languageVersion = Versions.KotlinLanguage
        apiVersion = Versions.KotlinLanguage
//        freeCompilerArgs = freeCompilerArgs + listOf(
//            "-Xopt-in=kotlin.RequiresOptIn", // Hide warnings about @OptIn annotations.
//        )
    }
}
