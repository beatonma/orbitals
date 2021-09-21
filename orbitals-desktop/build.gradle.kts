plugins {
    kotlin("jvm")
    id("org.jetbrains.compose") version Versions.Compose
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.ui)
    implementation(compose.foundation)

    implementation(project(":${Module.Core}"))
    implementation(project(":${Module.Render}"))
    implementation(project(":${Module.ComposeRender}"))
}

compose.desktop {
    application {
        mainClass = "org.beatonma.orbitals.desktop.MainKt"
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    kotlinOptions {
        jvmTarget = Versions.Java.toString()
        languageVersion = Versions.KotlinLanguage
        apiVersion = Versions.KotlinLanguage
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-Xopt-in=kotlin.RequiresOptIn", // Hide warnings about @OptIn annotations.
        )
    }
}
