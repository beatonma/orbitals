plugins {
    id("kotlin-multiplatform")
}

kotlin {
    jvm {}
    js(IR) {
        browser {}
        binaries.executable()
    }
}
