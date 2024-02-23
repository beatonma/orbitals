plugins {
    id("kotlin-multiplatform")
    id("com.android.library")
}

dependencies {
    implementation(project(Module.Core))
    implementation(project(Module.Render))

    testImplementation(libs.kotlin.test)
}

android {
    namespace = "org.beatonma.${project.name.replace("-", ".")}"
    compileSdk = AppConfig.SdkTarget
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        minSdk = AppConfig.SdkMin
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility(libs.versions.java.get())
        targetCompatibility(libs.versions.java.get())
    }
}

kotlin {
    androidTarget {}
}
