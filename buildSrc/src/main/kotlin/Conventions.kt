import org.gradle.kotlin.dsl.PluginDependenciesSpecScope

val PluginDependenciesSpecScope.conventionLibraryMultiplatform
    get() = this.id("orbitals.library-convention")
