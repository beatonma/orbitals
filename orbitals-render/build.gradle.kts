plugins {
    id("com.android.library")
    id("kotlin-android")
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

    kotlinOptions {
        jvmTarget = Versions.Java.toString()
        languageVersion = Versions.KotlinLanguage
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-Xopt-in=kotlin.RequiresOptIn", // Hide warnings about @OptIn annotations.
        )
    }
}

val composeVersion = Versions.Jetpack.Compose

dependencies {
    implementation(Dependencies.KotlinStdLib)
    implementation(Dependencies.KotlinReflect)
    implementation(Dependencies.Annotations)

    implementation(Dependencies.ComposeUI)

    implementation(project(":orbitals"))

    testImplementation(Dependencies.KotlinTest)
}
