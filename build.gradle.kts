buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:${Versions.gradlePlugin(project)}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin(project)}")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
