// Versions object is not accessible here.
val gradleVersion = "8.0.2"
val kotlinVersion = "1.8.20"
val kotlinLanguageVersion = "1.8"
val javaVersion = "17"

plugins {
    `kotlin-dsl`
}

dependencies {
    implementation("com.android.tools.build:gradle:$gradleVersion")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
}

repositories {
    google()
    mavenCentral()
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
}
