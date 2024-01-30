plugins {
    conventionLibraryMultiplatform
}

dependencies {
    commonMainImplementation(libs.kotlin.test)
    commonMainImplementation(project(Module.Core))
}
