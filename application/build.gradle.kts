plugins {
    java
    kotlin("jvm")
    application
    id("org.openjfx.javafxplugin")
    id("org.beryx.jlink")
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
    implementation(project(":core:snap"))
    implementation(project(":core:splash"))
    implementation(kotlin("stdlib"))

    implementation(group = "org.kordamp.ikonli", name = "ikonli-javafx", version = "11.3.4")
    implementation(group = "org.kordamp.ikonli", name = "ikonli-materialdesign-pack", version = "11.3.4")
    implementation(group = "org.kordamp.ikonli", name = "ikonli-fontawesome5-pack", version = "11.3.4")

    testImplementation(kotlin("test"))
    testImplementation(group = "junit", name = "junit", version = "4.12")
}

application {
    mainClassName = "knotbook.application/knotbook.application.Main"
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