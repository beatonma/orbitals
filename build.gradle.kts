plugins {
    id(libs.plugins.kotlin.multiplatform.get().pluginId) apply false
    id(libs.plugins.android.library.get().pluginId) apply false
    id(libs.plugins.android.application.get().pluginId) apply false
    alias(libs.plugins.compose) apply false
}
