@file:Suppress("SpellCheckingInspection")

plugins {
    `build-scan`
    kotlin("jvm") version "1.3.50" apply false
    id("org.openjfx.javafxplugin") version "0.0.8" apply false
    id("org.beryx.jlink") version "2.14.1" apply false
    id("com.github.gmazzo.buildconfig") version "1.5.0" apply false
    id("org.jetbrains.dokka") version "0.9.18" apply false
}

val Project.rootPath: String get() {
    return parent?.rootPath?.plus("/$name") ?: ""
}

allprojects {
    repositories {
        mavenCentral()
        jcenter()
    }
    buildDir = File(rootProject.projectDir, "build/$rootPath")
}

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
}