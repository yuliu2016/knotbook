@file:Suppress("SpellCheckingInspection")

plugins {
    // Applied to root
    `build-scan`

    // Applied to specific modules
    id("org.jetbrains.kotlin.jvm") version "1.3.50" apply false
    id("org.javamodularity.moduleplugin") version "1.5.0" apply false
    id("org.openjfx.javafxplugin") version "0.0.8" apply false
    id("org.beryx.jlink") version "2.14.1" apply false
    id("com.github.gmazzo.buildconfig") version "1.5.0" apply false
    id("org.jetbrains.dokka") version "0.9.18" apply false
}

val Project.rootPath: String get() {
    return parent?.rootPath?.plus("/$name") ?: ""
}

subprojects {
    apply(plugin="java")
    apply(plugin="org.javamodularity.moduleplugin")
    repositories {
        mavenCentral()
        jcenter()
        maven { setUrl("https://jitpack.io") }
    }
    ext {
        set("ikonli-version", "11.3.4")
        set("kotlin-coroutines-version", "1.3.0-RC2")
        set("junit-version", "5.5.1")
        set("javafx-version", "12.0.1")
        set("kotlin-jvm-target", "11")
    }
    buildDir = File(rootProject.projectDir, "build/$rootPath")
}

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
}