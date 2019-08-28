plugins {
    java
    kotlin("jvm")
    id("org.openjfx.javafxplugin")
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
    implementation(project(":kb.core.bowline"))
    implementation(project(":kb.core.camera.fx"))
    implementation(project(":kb.core.splash"))
    implementation(project(":kb.core.registry"))
    implementation(project(":kb.core.fx"))
    implementation(project(":kb.core.icon"))
    implementation(project(":kb.core.server"))
    implementation(project(":kb.core.code"))
    implementation(project(":kb.path.planner"))
    implementation(kotlin("stdlib"))

    implementation(group = "org.kordamp.ikonli", name = "ikonli-fontawesome5-pack", version = "11.3.4")
}