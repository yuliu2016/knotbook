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
    implementation(project(":core:camera"))
    implementation(project(":core:splash"))
    implementation(project(":core:registry"))
    implementation(project(":core:table"))
    implementation(project(":core:fx"))
    implementation(project(":core:icon"))
    implementation(project(":core:server"))
    implementation(project(":core:code"))
    implementation(project(":fn:path-planner"))
    implementation(kotlin("stdlib"))

    implementation(group = "org.kordamp.ikonli", name = "ikonli-fontawesome5-pack", version = "11.3.4")
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
}