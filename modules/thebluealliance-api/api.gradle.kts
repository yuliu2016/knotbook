plugins {
    java
    kotlin("jvm")
}

dependencies {
    implementation(kotlin("stdlib"))

    // JSON Library
    implementation(group = "com.beust", name = "klaxon", version = "5.2")
}