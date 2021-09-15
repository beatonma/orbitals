plugins {
    id("com.android.application")
    id("kotlin-android")
}


val composeVersion = "1.0.1"

android {
    compileSdk = 31

    defaultConfig {
        applicationId = "org.beatonma.orbitalslivewallpaper"
        minSdk = 21
        targetSdk = 31
    }

    buildFeatures {
        compose = true
    }

    kotlinOptions {
        jvmTarget = "1.8"
        languageVersion = "1.5"
    }

    composeOptions {
        kotlinCompilerExtensionVersion = composeVersion
    }

//    buildTypes {
//        release {
//            minifyEnabled true
//            shrinkResources true
//        }
//    }
}


dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.21")
//    implementation("org.jetbrains.kotlin:kotlin-reflect:1.5.21")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")

    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("androidx.core:core-ktx:1.6.0")

    // Compose
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.foundation:foundation:$composeVersion")

    implementation("androidx.activity:activity-ktx:1.3.1")
    implementation("androidx.activity:activity-compose:1.3.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha07")
    implementation("androidx.navigation:navigation-compose:2.4.0-alpha08")
    implementation("com.google.accompanist:accompanist-insets:0.17.0")

    implementation("androidx.datastore:datastore-preferences:1.0.0")

    implementation(project(":orbitals"))
    implementation(project(":orbitals-render"))

    testImplementation("org.jetbrains.kotlin:kotlin-test")
}
