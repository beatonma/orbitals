import org.gradle.kotlin.dsl.`kotlin-dsl`
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val gradleVersion = "7.0.2"
val kotlinVersion = "1.5.21"
val kotlinLanguageVersion = "1.5"
val javaVersion = "1.8"

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
}

val compileKotlin: KotlinCompile by tasks
val compileTestKotlin: KotlinCompile by tasks

compileKotlin.kotlinOptions {
    jvmTarget = javaVersion
    languageVersion = kotlinLanguageVersion
}
compileTestKotlin.kotlinOptions {
    jvmTarget = javaVersion
    languageVersion = kotlinLanguageVersion
}
