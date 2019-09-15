plugins {
    java
    application
    id("org.openjfx.javafxplugin")
    id("org.beryx.jlink")
    kotlin("jvm")
}

javafx {
    modules("javafx.controls")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":kb.service.api"))
    implementation(project(":kb.core.view"))
}

val appJVMArgs = listOf(
        "-XX:+UseG1GC",
        "-Xms64m",
        "-Xmx1024m"
)

application {
    applicationDefaultJvmArgs = appJVMArgs
    mainClassName = "kb.application/kb.application.Main"
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