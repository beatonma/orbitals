plugins {
    id("orbitals.library-convention")
    id("orbitals.android-library-convention")
}

dependencies {
    commonTestImplementation(libs.kotlin.test)
    commonTestImplementation(project(Module.Test))
}
