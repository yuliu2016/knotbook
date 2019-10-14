plugins {
    kotlin("jvm")
    id("org.openjfx.javafxplugin")
}

javafx {
    modules = listOf("javafx.controls")
}

dependencies {
    implementation(kotlin("stdlib"))
}