plugins {
    java
    kotlin("jvm")
    id("org.openjfx.javafxplugin")
    id("org.javamodularity.moduleplugin")
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
    modules = listOf("javafx.controls")
}

dependencies {
    implementation(project(":kb.service.api"))
    implementation(project(":kb.core.bowline"))
    implementation(project(":kb.core.splash"))
    implementation(project(":kb.core.fx"))
    implementation(project(":kb.core.icon"))
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    implementation(files(File(rootDir, "tools/controlsfx-12.0.0-SNAPSHOT.jar")))
    implementation(group = "org.kordamp.ikonli", name = "ikonli-materialdesign-pack", version = "11.3.4")
    implementation(group = "de.mpicbg.scicomp", name = "krangl", version = "0.11")
    implementation(group = "org.apache.commons", name = "commons-csv", version = "1.6")
}