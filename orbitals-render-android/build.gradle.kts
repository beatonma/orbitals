plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    compileSdk = AppConfig.SdkTarget

    defaultConfig {
        minSdk = AppConfig.SdkMin

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = Versions.Java
        targetCompatibility = Versions.Java
    }

    kotlinOptions {
        jvmTarget = Versions.Java.toString()
        languageVersion = Versions.KotlinLanguage
//        freeCompilerArgs = freeCompilerArgs + listOf(
//            "-Xopt-in=kotlin.RequiresOptIn", // Hide warnings about @OptIn annotations.
//        )
    }
    namespace = "org.beatonma.orbitals.render.android"
}

dependencies {
//    implementation(Dependencies.KotlinStdLib)
//    implementation(Dependencies.KotlinReflect)

    implementation(project(Module.Core))
    implementation(project(Module.Render))

    testImplementation(Dependencies.KotlinTest)
}
