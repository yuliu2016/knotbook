plugins {
    java
    kotlin("jvm")
    id("org.openjfx.javafxplugin")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":kb.service.api"))
    implementation(project(":kb.tba.client"))
    implementation(group = "org.kordamp.ikonli", name = "ikonli-materialdesign-pack", version = "11.3.4")
}

