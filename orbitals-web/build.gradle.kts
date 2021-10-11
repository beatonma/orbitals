plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version Versions.Compose
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
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
                implementation(compose.web.core)
                implementation(compose.web.widgets)
                implementation(compose.runtime)

                implementation(Dependencies.Web.KotlinReact)
                implementation(Dependencies.Web.KotlinReactDom)
                implementation(Dependencies.Web.KotlinHtmlJs)
                implementation(Dependencies.Web.Styled)

                with(Dependencies.Web.Npm.React) {
                    implementation(npm(name, version))
                }
                with (Dependencies.Web.Npm.ReactDom) {
                    implementation(npm(name, version))
                }
                with (Dependencies.Web.Npm.StyledComponents) {
                    implementation(npm(name, version))
                }

                implementation(project(":${Module.Core}"))
                implementation(project(":${Module.Render}"))
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

// workaround for https://youtrack.jetbrains.com/issue/KT-48273
// (found here: https://github.com/joreilly/PeopleInSpace/blob/53fbd64c55d58c56dce56ecfb6a6b9ea9824e307/compose-web/build.gradle.kts)
afterEvaluate {
    rootProject.extensions.configure<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension> {
        versions.webpackDevServer.version = "4.3.1"
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    kotlinOptions {
        jvmTarget = Versions.Java.toString()
        languageVersion = Versions.KotlinLanguage
        apiVersion = Versions.KotlinLanguage
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-Xopt-in=kotlin.RequiresOptIn", // Hide warnings about @OptIn annotations.
        )
    }
}
