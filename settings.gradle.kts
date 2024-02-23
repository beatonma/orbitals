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

include(":core")
include(":render")
include(":renderAndroid")
include(":renderCompose")
include(":appCompose")
include(":appAndroid")
include(":appDesktop")
include(":appWebJs")
include(":test")
