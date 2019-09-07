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
    implementation(project(":kb.service.api"))
    implementation(project(":kb.core.icon"))
    implementation(project(":kb.core.fx"))
    implementation(kotlin("stdlib"))
    implementation ("com.github.Team865:FRC-Commons-Kotlin:e00ce2ccc6")
    implementation(group = "org.kordamp.ikonli", name = "ikonli-fontawesome5-pack", version = "11.3.4")
}