plugins {
    id("orbitals.library-convention")
}

dependencies {
    commonMainImplementation(libs.kotlin.test)
    commonMainImplementation(project(Module.Core))
}
