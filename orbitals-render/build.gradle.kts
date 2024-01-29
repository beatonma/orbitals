plugins {
    id("orbitals.library-convention")
    id("orbitals.android-library-convention")
}

dependencies {
    commonMainImplementation(project(Module.Core))
    commonMainImplementation(libs.kotlin.coroutines)

    commonTestImplementation(libs.kotlin.test)
    commonTestImplementation(project(Module.Test))
}
