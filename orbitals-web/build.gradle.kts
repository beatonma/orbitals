plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version Versions.JetbrainsCompose
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

compose.experimental {
    web.application {

    }
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

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material3)

                implementation(project(Module.Core))
                implementation(project(Module.Render))
                implementation(project(Module.ComposeRender))
                implementation(project(Module.ComposeUi))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(project(Module.Test))
                implementation(Dependencies.KotlinTest)
            }
        }
        val jsMain by getting
    }
}
