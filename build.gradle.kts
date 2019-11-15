@file:Suppress("SpellCheckingInspection")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    id("org.jetbrains.kotlin.jvm") version "1.3.50"
    id("org.javamodularity.moduleplugin") version "1.6.0"
    id("org.openjfx.javafxplugin") version "0.0.9-SNAPSHOT"
}

allprojects {
    repositories {
        mavenCentral()
        jcenter()
        maven { setUrl("https://jitpack.io") }
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.javamodularity.moduleplugin")
    pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
        tasks.withType<KotlinCompile> {
            kotlinOptions {
                freeCompilerArgs = listOf("-Xnew-inference")
                jvmTarget = "11"
            }
        }
        dependencies {
            implementation(kotlin("stdlib"))
        }
    }
    pluginManager.withPlugin("org.openjfx.javafxplugin") {
        javafx {
            version = "13"
            modules("javafx.base", "javafx.graphics", "javafx.controls")
        }
    }
    buildDir = File(rootProject.projectDir, "build/$name")
    version = "3.3.14"
}