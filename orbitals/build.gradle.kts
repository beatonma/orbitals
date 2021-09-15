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

dependencies {
    implementation(Dependencies.KotlinStdLib)
    implementation(Dependencies.Annotations)

    testImplementation(Dependencies.KotlinTest)
}
