plugins {
    id("orbitals.android-library-convention")
}

dependencies {
    implementation(project(Module.Core))
    implementation(project(Module.Render))

    testImplementation(libs.kotlin.test)
}
