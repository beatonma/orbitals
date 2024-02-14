pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://plugins.gradle.org/m2/")
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

include(":orbitals-core")
include(":orbitals-render")
include(":orbitals-render-android")
include(":orbitals-render-compose")
include(":composeApp")
include(":orbitals-android")
include(":orbitals-desktop")
include(":orbitals-web")
include(":test")
