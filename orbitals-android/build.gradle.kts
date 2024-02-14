plugins {
    id("com.android.application")
    id("kotlin-android")
    alias(libs.plugins.compose)
}


dependencies {
    implementation(libs.kotlin.coroutines)
    implementation(libs.android.coroutines)

    // Compose
    implementation(compose.material3)
    implementation(compose.ui)
    implementation(compose.foundation)
    implementation(compose.runtime)
    implementation(compose.materialIconsExtended)

    implementation(libs.android.activity.compose)
    implementation(libs.android.viewmodel.compose)
    implementation(libs.android.navigation.compose)
    implementation(libs.android.lifecycle.runtime)

    implementation(libs.android.datastore)

    implementation(project(Module.Core))
    implementation(project(Module.Render))
    implementation(project(Module.AndroidRender))
    implementation(project(Module.ComposeRender))
    implementation(project(Module.ComposeApp))

    testImplementation(libs.kotlin.test)
}

android {
    compileSdk = AppConfig.SdkTarget

    defaultConfig {
        applicationId = AppConfig.ID
        minSdk = AppConfig.SdkMin
        targetSdk = AppConfig.SdkTarget
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.android.compose.extension.get()
    }

    compileOptions {
        sourceCompatibility = Versions.Java
        targetCompatibility = Versions.Java
    }

    kotlinOptions {
        jvmTarget = Versions.Java.toString()
        languageVersion = libs.versions.kotlinLanguage.get()
    }
    namespace = "org.beatonma.orbitalslivewallpaper"
}
