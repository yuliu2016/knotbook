@file:Suppress("SpellCheckingInspection")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.41"
    application
    id("org.openjfx.javafxplugin") version "0.0.8"
    id("org.beryx.jlink") version "2.14.0"
}

application {
    mainClassName = "knotable.main/knotbook.tables.Test"
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xnew-inference")
        kotlinOptions.jvmTarget = "11"
    }
}

repositories {
    mavenCentral()
}

javafx {
    modules = listOf("javafx.controls", "javafx.fxml")
}

dependencies {
    implementation(project("core:snap"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.3.41")

    implementation(group = "org.kordamp.ikonli", name = "ikonli-javafx", version = "11.3.4")
    implementation(group = "org.kordamp.ikonli", name = "ikonli-materialdesign-pack", version = "11.3.4")
    implementation(group = "org.kordamp.ikonli", name = "ikonli-fontawesome5-pack", version = "11.3.4")

    testImplementation(kotlin("test"))
    testImplementation(group = "junit", name = "junit", version = "4.12")
}

jlink {
    launcher {
        name = "knotbook"
        jvmArgs = listOf(
                "-XX:+UseG1GC",
                "-Xms128m",
                "-Xmx1024m"
        )
    }

    addOptions(
            "--no-header-files",
            "--no-man-pages",
            "--strip-debug",
            "--compress=1"
    )

    imageZip.set(project.file("${project.buildDir}/knotbook.zip"))
}
