plugins {
    java
    id("org.openjfx.javafxplugin")
    kotlin("jvm")
}

//javafx {
//    modules = listOf("javafx.controls")
//}

dependencies {
    implementation(group = "org.kordamp.ikonli", name = "ikonli-core", version = "11.3.4")
}