plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    compileSdk = 30

    defaultConfig {
        applicationId = "org.beatonma.orbitalslivewallpaper"
        minSdk = 21
        targetSdk = 30
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
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("androidx.core:core-ktx:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")

    implementation(project(":orbitals"))

    testImplementation("org.jetbrains.kotlin:kotlin-test")
}
