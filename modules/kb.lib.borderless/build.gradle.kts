plugins {
    java
    id("org.openjfx.javafxplugin")
}

javafx {
    modules = listOf("javafx.controls")
}

dependencies {
    implementation(project(":kb.core.icon"))
    implementation(group = "org.kordamp.ikonli", name = "ikonli-materialdesign-pack", version = "11.3.4")
}