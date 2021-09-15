plugins {
    id("com.android.application")
    id("kotlin-android")
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

    kotlinOptions {
        jvmTarget = Versions.Java.toString()
        languageVersion = Versions.KotlinLanguage
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Versions.Jetpack.Compose
    }
}


dependencies {
    implementation(Dependencies.KotlinStdLib)

    implementation(Dependencies.CoroutinesCore)
    implementation(Dependencies.CoroutinesAndroid)

    implementation(Dependencies.AppCompat)
    implementation(Dependencies.CoreKtx)

    // Compose
    implementation(Dependencies.ComposeMaterial)
    implementation(Dependencies.ComposeUI)
    implementation(Dependencies.ComposeFoundation)

    implementation(Dependencies.ActivityKtx)
    implementation(Dependencies.ActivityCompose)
    implementation(Dependencies.ViewModelCompose)
    implementation(Dependencies.NavigationCompose)
    implementation(Dependencies.Accompanist)

    implementation(Dependencies.DataStore)

    implementation(project(":orbitals"))
    implementation(project(":orbitals-render"))

    testImplementation(Dependencies.KotlinTest)
}
