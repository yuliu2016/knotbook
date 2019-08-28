plugins {
    java
    application
    id("org.openjfx.javafxplugin")
    id("org.beryx.jlink")
}

javafx {
    modules = listOf("javafx.controls")
}

dependencies {
    implementation(project(":core:view"))
    implementation(project(":core:registry"))
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