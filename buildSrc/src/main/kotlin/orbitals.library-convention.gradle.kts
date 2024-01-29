plugins {
    id("org.jetbrains.kotlin.multiplatform")
}

kotlin {
    jvm {}
    js(IR) {
        browser {}
        binaries.executable()
    }
}
