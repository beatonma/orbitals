plugins {
    id("kotlin-multiplatform")
    alias(libs.plugins.compose)
}

// Based on https://github.com/Kotlin/kotlin-wasm-examples/blob/main/compose-imageviewer/webApp/build.gradle.kts
val jsCopyResourcesWorkaround = tasks.register("jsCopyResourcesWorkaround", Copy::class) {
    from(project(":${Module.ComposeApp}").file("src/commonMain/composeResources"))
    into("build/processedResources/js/main")
}
afterEvaluate {
    tasks.getByName("jsProcessResources").dependsOn(jsCopyResourcesWorkaround)
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
    commonMainImplementation(project(Module.ComposeApp))

    commonTestImplementation(project(Module.Test))
    commonTestImplementation(libs.kotlin.test)
}
