plugins {
    java
    kotlin("jvm")
    id("org.openjfx.javafxplugin")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":service-api"))
    implementation(project(":thebluealliance-api"))

    implementation(group = "org.json", name = "json", version = "20190722")
    implementation(group = "org.kordamp.ikonli", name = "ikonli-materialdesign-pack", version = "11.3.4")
}

