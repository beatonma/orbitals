plugins {
    id("com.android.application")
    id("kotlin-android")
    id("org.jetbrains.compose") version Versions.Desktop.Compose
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
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
    implementation(compose.material)
    implementation(compose.ui)
    implementation(compose.foundation)
    implementation(compose.runtime)

    implementation(Dependencies.ActivityKtx)
    implementation(Dependencies.ActivityCompose)
    implementation(Dependencies.ViewModelCompose)
    implementation(Dependencies.NavigationCompose)
    implementation(Dependencies.Accompanist)

    implementation(Dependencies.DataStore)

    implementation(project(Module.Core))
    implementation(project(Module.Render))
    implementation(project(Module.AndroidRender))
    implementation(project(Module.ComposeRender))

    testImplementation(Dependencies.KotlinTest)
}
