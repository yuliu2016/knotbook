import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm")
}

tasks.withType<KotlinCompile>{
    kotlinOptions.freeCompilerArgs += listOf(
            "-Xno-call-assertions",
            "-Xno-param-assertions"
    )
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(group = "org.json", name = "json", version = "20190722")
}