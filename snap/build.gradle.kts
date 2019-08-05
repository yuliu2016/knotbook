plugins {
    java
    id("org.openjfx.javafxplugin")
}

repositories {
    mavenCentral()
}

javafx {
    modules = listOf("javafx.controls", "javafx.fxml")
}