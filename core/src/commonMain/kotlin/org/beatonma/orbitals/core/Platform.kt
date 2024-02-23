package org.beatonma.orbitals.core

enum class Platform {
    Android,
    Desktop,
    Web,
    ;

    val isWeb: Boolean get() = this == Web
    val isDesktop: Boolean get() = this == Desktop
    val isAndroid: Boolean get() = this == Android
}

expect val platform: Platform
