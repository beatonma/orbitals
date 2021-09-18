plugins {
    id("com.android.library")
    id("kotlin-multiplatform")
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

    sourceSets["commonMain"].dependencies {
        implementation(project(":${Module.Core}"))
        implementation(Dependencies.KotlinReflect)
    }
}

android {
    compileSdk = AppConfig.SdkTarget

    defaultConfig {
        minSdk = AppConfig.SdkMin
        targetSdk = AppConfig.SdkTarget

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = Versions.Java
        targetCompatibility = Versions.Java
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    kotlinOptions {
        jvmTarget = Versions.Java.toString()
        languageVersion = Versions.KotlinLanguage
        apiVersion = Versions.KotlinLanguage
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-Xopt-in=kotlin.RequiresOptIn", // Hide warnings about @OptIn annotations.
        )
    }
}
