@file:Suppress("SpellCheckingInspection")

plugins {
    `build-scan`
    java
    id("org.jetbrains.kotlin.jvm") version "1.3.50"
    id("org.javamodularity.moduleplugin") version "1.5.0"
    id("org.openjfx.javafxplugin") version "0.0.8"
    id("org.beryx.jlink") version "2.15.1" apply false
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.javamodularity.moduleplugin")
    repositories {
        mavenCentral()
        jcenter()
        maven { setUrl("https://jitpack.io") }
    }
    dependencies {
        compileOnly("org.jetbrains", "annotations", "13.0")
    }
    pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
        tasks {
            compileKotlin {
                kotlinOptions {
                    freeCompilerArgs = listOf("-Xnew-inference")
                    jvmTarget = "11"
                }
            }
            compileTestKotlin {
                kotlinOptions {
                    freeCompilerArgs = listOf("-Xnew-inference")
                    jvmTarget = "11"
                }
            }
        }
    }
    pluginManager.withPlugin("org.openjfx.javafxplugin") {
        javafx {
            version = "13"
        }
    }
    buildDir = File(rootProject.projectDir, "build/$name")
}

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
}