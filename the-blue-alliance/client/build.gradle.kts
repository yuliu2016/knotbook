plugins {
    java
    kotlin("jvm")
}

dependencies {
    implementation(kotlin("stdlib"))
    // Kotlin Coroutines
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = "1.2.1")
    // HTTP Requests Library
    implementation(group = "com.github.kittinunf.fuel", name = "fuel", version = "2.0.1")
    // Support Library to integrate Fuel and Coroutines
    implementation(group = "com.github.kittinunf.fuel", name = "fuel-coroutines", version = "2.0.1")
    // JSON Library
    implementation(group = "com.beust", name = "klaxon", version = "5.0.5")
}