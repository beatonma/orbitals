pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
    }
}

include(":orbitals-core")
include(":orbitals-render")
include(":orbitals-render-android")
include(":orbitals-render-compose")
include(":orbitals-compose-ui")
include(":orbitals-android")
include(":orbitals-desktop")
include(":orbitals-web")
