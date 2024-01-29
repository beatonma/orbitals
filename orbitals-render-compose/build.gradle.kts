plugins {
    id("orbitals.library-convention")
    id("orbitals.android-library-convention")
    alias(libs.plugins.compose)
}

dependencies {
    commonMainImplementation(project(Module.Core))
    commonMainImplementation(project(Module.Render))
    commonMainImplementation(compose.foundation)
    commonMainImplementation(compose.material3)
    commonMainImplementation(compose.ui)

    commonTestImplementation(libs.kotlin.test)
}
