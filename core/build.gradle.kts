plugins {
    conventionLibraryMultiplatform
}

dependencies {
    commonTestImplementation(libs.kotlin.test)
    commonTestImplementation(project(Module.Test))
}
