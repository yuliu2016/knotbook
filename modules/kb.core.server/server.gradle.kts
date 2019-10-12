plugins {
    java
    kotlin("jvm")
}

dependencies {
    implementation(project(":kb.service.api"))
    implementation(kotlin("stdlib"))
}