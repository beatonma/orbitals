plugins {
    id("com.android.application")
    id("kotlin-android")
    id("org.jetbrains.compose") version Versions.Compose
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}


dependencies {
    implementation(Dependencies.CoroutinesCore)
    implementation(Dependencies.Android.CoroutinesAndroid)

    // Compose
    implementation(compose.material)
    implementation(compose.ui)
    implementation(compose.foundation)
    implementation(compose.runtime)

    implementation(Dependencies.Android.ActivityCompose)
    implementation(Dependencies.Android.Material3)
    implementation(Dependencies.Android.ViewModelCompose)
    implementation(Dependencies.Android.NavigationCompose)

    implementation(Dependencies.Android.DataStore)

    implementation(project(Module.Core))
    implementation(project(Module.Render))
    implementation(project(Module.AndroidRender))
    implementation(project(Module.ComposeRender))
    implementation(project(Module.ComposeUi))

    testImplementation(Dependencies.KotlinTest)
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

    compileOptions {
        sourceCompatibility = Versions.Java
        targetCompatibility =  Versions.Java
    }

    kotlinOptions {
        jvmTarget = Versions.Java.toString()
        languageVersion = Versions.KotlinLanguage
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Versions.Android.Compose
    }
    namespace = "org.beatonma.orbitalslivewallpaper"
}
