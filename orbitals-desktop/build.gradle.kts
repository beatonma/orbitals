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
    implementation(compose.material3)

    implementation(project(Module.Core))
    implementation(project(Module.Render))
    implementation(project(Module.ComposeRender))
    implementation(project(Module.ComposeUi))
}

compose.desktop {
    application {
        mainClass = "org.beatonma.orbitals.desktop.MainKt"
    }
}
