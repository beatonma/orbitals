plugins {
    id(libs.plugins.kotlin.multiplatform.get().pluginId) apply false // version libs.versions.kotlin apply false
    id(libs.plugins.compose.get().pluginId) version libs.versions.compose.multiplatform apply false
    id(libs.plugins.android.library.get().pluginId) apply false
    id(libs.plugins.android.application.get().pluginId) apply false
    id(libs.plugins.kotlin.jvm.get().pluginId) apply false
}
