@file:Suppress("SpellCheckingInspection")

plugins {
    java
    kotlin("jvm") version "1.3.41"
    application
    id("org.openjfx.javafxplugin") version "0.0.8"
    id("org.beryx.jlink") version "2.14.0"
}

allprojects {
    repositories {
        mavenCentral()
    }
    buildDir = File(rootProject.projectDir, "build/$name")
}

tasks {
    compileKotlin {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xnew-inference")
            jvmTarget = "1.8"
        }
    }
}

javafx {
    modules = listOf("javafx.controls")
}

dependencies {
    implementation(project("core:snap"))
    implementation(project("core:splash"))
    implementation(kotlin("stdlib"))

    implementation(group = "org.kordamp.ikonli", name = "ikonli-javafx", version = "11.3.4")
    implementation(group = "org.kordamp.ikonli", name = "ikonli-materialdesign-pack", version = "11.3.4")
    implementation(group = "org.kordamp.ikonli", name = "ikonli-fontawesome5-pack", version = "11.3.4")

    testImplementation(kotlin("test"))
    testImplementation(group = "junit", name = "junit", version = "4.12")
}

application {
    mainClassName = "knotbook.main/knotbook.tables.Test"
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
