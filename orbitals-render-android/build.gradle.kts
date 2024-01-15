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
    }
    namespace = "org.beatonma.orbitals.render.android"
}

dependencies {
    implementation(project(Module.Core))
    implementation(project(Module.Render))

    testImplementation(Dependencies.KotlinTest)
}
