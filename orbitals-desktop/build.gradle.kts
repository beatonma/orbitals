plugins {
    kotlin("jvm")
    alias(libs.plugins.compose)
}

compose.desktop {
    application {
        mainClass = "org.beatonma.orbitals.desktop.MainKt"
    }
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.ui)
    implementation(compose.foundation)
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)

    implementation(project(Module.Core))
    implementation(project(Module.Render))
    implementation(project(Module.ComposeRender))
    implementation(project(Module.ComposeApp))
}
