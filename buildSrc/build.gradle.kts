import java.io.FileInputStream
import java.util.Properties

// Versions object is not accessible here.
val versions = Properties().apply {
    load(FileInputStream(file("versions.properties")))
}
val gradleVersion = versions.getProperty("gradle")
val kotlinVersion = versions.getProperty("kotlin")

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
