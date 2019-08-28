plugins {
    id("org.openjfx.javafxplugin")
}

javafx {
    modules = listOf("javafx.graphics", "javafx.swing")
}

dependencies {
    implementation(group = "com.fifesoft", name = "rsyntaxtextarea", version = "3.0.3")
}