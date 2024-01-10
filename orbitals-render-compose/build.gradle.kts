plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose") version Versions.Compose
}


repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
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
        val androidMain by getting {}
        val commonMain by getting {
            dependencies {
                implementation(project(":${Module.Core}"))
                implementation(project(":${Module.Render}"))
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
    namespace = "org.beatonma.orbitals.render.compose"
}
