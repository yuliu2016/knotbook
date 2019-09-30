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
    implementation(kotlin("reflect"))
    // Kotlin Coroutines
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = "1.3.1")
    // HTTP Requests Library
    implementation(group = "com.github.kittinunf.fuel", name = "fuel", version = "2.2.1")
    // Support Library to integrate Fuel and Coroutines
    implementation(group = "com.github.kittinunf.fuel", name = "fuel-coroutines", version = "2.2.1")
    // JSON Library
    implementation(group = "com.beust", name = "klaxon", version = "5.0.12")
}