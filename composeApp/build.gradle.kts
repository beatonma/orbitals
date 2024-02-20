plugins {
    conventionLibraryMultiplatform
    alias(libs.plugins.compose)
}

dependencies {
    commonMainImplementation(compose.ui)
    commonMainImplementation(compose.foundation)
    commonMainImplementation(compose.material3)
    commonMainImplementation(compose.components.resources)

    commonMainImplementation(project(Module.Core))
    commonMainImplementation(project(Module.Render))
    commonMainImplementation(project(Module.ComposeRender))
}

kotlin {
    sourceSets.all {
        languageSettings {
            optIn("org.jetbrains.compose.resources.ExperimentalResourceApi")
            optIn("androidx.compose.material3.ExperimentalMaterial3Api")
        }
    }
}
