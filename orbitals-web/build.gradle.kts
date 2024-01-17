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
                outputFileName = versionedFilename
            }
        }
        binaries.executable()
    }

    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.html.core)
                implementation(project(Module.Core))
                implementation(project(Module.Render))
            }
        }
    }

    afterEvaluate {
        val taskName = "copyToStandardFilename"
        val dirPath = "build/distributions"

        tasks.register<Copy>(taskName) {
            from(dirPath)
            include("$versionedFilenameRoot*")
            rename("($filenameRoot)(-\\d+)(.*?)", "$1$3")
            into(dirPath)
        }

        tasks.named("jsBrowserWebpack").get().finalizedBy(taskName)
    }
}
