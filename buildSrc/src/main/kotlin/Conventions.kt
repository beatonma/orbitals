import org.gradle.kotlin.dsl.PluginDependenciesSpecScope

val PluginDependenciesSpecScope.conventionLibraryMultiplatform
    get() = this.id("orbitals.library-convention")

val PluginDependenciesSpecScope.conventionLibraryAndroid
    get() = this.id("orbitals.android-library-convention")
