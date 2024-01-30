plugins {
    conventionLibraryAndroid
}

dependencies {
    implementation(project(Module.Core))
    implementation(project(Module.Render))

    testImplementation(libs.kotlin.test)
}
