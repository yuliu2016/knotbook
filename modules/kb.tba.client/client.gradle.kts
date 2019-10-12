plugins {
    java
    kotlin("jvm")
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

dependencies {
    implementation(project(":kb.service.api"))
    implementation(kotlin("stdlib"))

    // JSON Library
    implementation(group = "com.beust", name = "klaxon", version = "5.0.13")
}