plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    compileSdk = 31 // AppConfig.SdkTarget

    defaultConfig {
        minSdk = 21 //Versions.Sdk.Min
        targetSdk = 31 //Versions.Sdk.Target

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8" // Versions.JAVA.toString()
        languageVersion = "1.5" //Versions.KOTLIN_LANGUAGE_VERSION
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-Xopt-in=kotlin.RequiresOptIn", // Hide warnings about @OptIn annotations.
        )
    }
}

val composeVersion = "1.0.1"

dependencies {
//    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.Kotlin}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.21")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.5.21")
    implementation("androidx.annotation:annotation:1.2.0")

    implementation("androidx.compose.ui:ui:$composeVersion")
//    implementation("androidx.compose.foundation:foundation:$composeVersion")

    implementation(project(":orbitals"))

    testImplementation("org.jetbrains.kotlin:kotlin-test")
}
