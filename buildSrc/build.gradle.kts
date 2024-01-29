import java.io.FileInputStream
import java.util.Properties

val versions = Properties().apply {
    load(FileInputStream(file("versions.properties")))
}
val gradleVersion = versions.getProperty("gradle")
val kotlinVersion = versions.getProperty("kotlin")

plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation("com.android.tools.build:gradle:$gradleVersion")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
}
