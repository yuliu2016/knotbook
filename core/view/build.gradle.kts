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
    implementation(project(":bowline"))
    implementation(project(":core:camera"))
    implementation(project(":core:splash"))
    implementation(project(":core:registry"))
    implementation(project(":core:fx"))
    implementation(project(":core:icon"))
    implementation(project(":core:server"))
    implementation(project(":core:code"))
    implementation(project(":fn:path-planner"))
    implementation(kotlin("stdlib"))

    implementation(group = "org.kordamp.ikonli", name = "ikonli-fontawesome5-pack", version = "11.3.4")
}