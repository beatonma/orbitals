plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version Versions.JetbrainsCompose
}

compose.experimental {
    web.application {}
}

kotlin {
    val git = Git.resolveData(project)

    val filenameRoot = "orbitals-web"
    val versionedFilenameRoot = "$filenameRoot-${git.commitCount}"
    val versionedFilename = "$versionedFilenameRoot.js"

    js(IR) {
        browser {
            webpackTask {
                mainOutputFileName = versionedFilename
            }
        }
        binaries.executable()
    }
}

dependencies {
    commonMainImplementation(compose.runtime)
    commonMainImplementation(compose.ui)
    commonMainImplementation(compose.foundation)
    commonMainImplementation(compose.material3)
    commonMainImplementation(project(Module.Core))
    commonMainImplementation(project(Module.Render))
    commonMainImplementation(project(Module.ComposeRender))
    commonMainImplementation(project(Module.ComposeUi))

    commonTestImplementation(project(Module.Test))
    commonTestImplementation(libs.kotlin.test)
}
