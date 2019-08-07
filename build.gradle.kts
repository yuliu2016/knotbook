@file:Suppress("SpellCheckingInspection")

plugins {
    kotlin("jvm") version "1.3.41" apply false
    id("org.openjfx.javafxplugin") version "0.0.8" apply false
    id("org.beryx.jlink") version "2.14.0" apply false
    id("com.github.gmazzo.buildconfig") version "1.5.0" apply false
    `build-scan`
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