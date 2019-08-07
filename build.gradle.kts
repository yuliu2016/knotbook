@file:Suppress("SpellCheckingInspection")

plugins {
    kotlin("jvm") version "1.3.41" apply false
    id("org.openjfx.javafxplugin") version "0.0.8" apply false
    id("org.beryx.jlink") version "2.14.0" apply false
}

val Project.rootPath: String get() {
    return parent?.rootPath?.plus("/$name") ?: ""
}

allprojects {
    repositories {
        mavenCentral()
    }
    buildDir = File(rootProject.projectDir, "build/$rootPath")
}
