plugins {
    java
    kotlin("jvm")
    application
    id("org.openjfx.javafxplugin")
    id("org.beryx.jlink")
    id("org.jetbrains.dokka")
}

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

javafx {
    modules = listOf("javafx.controls", "javafx.fxml")
}

dependencies {
    implementation(fileTree(mapOf("dir" to "../libs", "include" to listOf("*.jar"))))
    implementation(project(":core:camera"))
    implementation(project(":core:splash"))
    implementation(project(":core:registry"))
    implementation(project(":core:table"))
    implementation(project(":core:fx"))
    implementation(project(":core:icon"))
    implementation(kotlin("stdlib"))

//    implementation(group = "org.kordamp.ikonli", name = "ikonli-javafx", version = "11.3.4")
    implementation(group = "org.kordamp.ikonli", name = "ikonli-materialdesign-pack", version = "11.3.4")
    implementation(group = "org.kordamp.ikonli", name = "ikonli-fontawesome5-pack", version = "11.3.4")

    testImplementation(kotlin("test"))
    testImplementation(group = "junit", name = "junit", version = "4.12")
}

val appJVMArgs = listOf(
        "-XX:+UseG1GC",
        "-Xms64m",
        "-Xmx1024m"
)

application {
    applicationDefaultJvmArgs = appJVMArgs
    mainClassName = "knotbook.application/knotbook.application.Main"
}

jlink {
    launcher {
        name = "run"
        jvmArgs = appJVMArgs
    }

    addOptions(
            "--no-header-files",
            "--no-man-pages",
            "--strip-debug",
            "--compress=1"
    )

    imageZip.set(project.file("${project.buildDir}/knotbook.zip"))
}