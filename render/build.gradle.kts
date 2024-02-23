plugins {
    conventionLibraryMultiplatform
}

dependencies {
    commonMainImplementation(project(Module.Core))
    commonMainImplementation(libs.kotlin.coroutines)

    commonTestImplementation(libs.kotlin.test)
    commonTestImplementation(project(Module.Test))
}
