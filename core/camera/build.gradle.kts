plugins {
    java
    id("org.openjfx.javafxplugin")
}

javafx {
    modules = listOf("javafx.controls", "javafx.swing")
}

dependencies {

    implementation(group = "com.github.sarxos", name = "webcam-capture", version = "0.3.12")
    implementation(group = "com.google.zxing", name = "core", version = "3.4.0")
    implementation(group = "com.google.zxing", name = "javase", version = "3.4.0")
    implementation(group = "org.slf4j", name = "slf4j-simple", version = "1.7.6")
}