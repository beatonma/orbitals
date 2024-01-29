plugins {
    id("orbitals.library-convention")
    id("orbitals.android-library-convention")
    alias(libs.plugins.compose)
}


dependencies {
    commonMainImplementation(compose.ui)
    commonMainImplementation(compose.foundation)
    commonMainImplementation(compose.material3)

    commonMainImplementation(project(Module.Core))
    commonMainImplementation(project(Module.Render))
    commonMainImplementation(project(Module.ComposeRender))
}
