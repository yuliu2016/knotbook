plugins {
    java
    kotlin("jvm")
    id("org.openjfx.javafxplugin")
    id("org.javamodularity.moduleplugin")
}

javafx {
    modules = listOf("javafx.controls")
}

dependencies {
    implementation(project(":service-api"))
    implementation(project(":kotlin-fx"))
    implementation(project(":icon-utils"))
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    implementation(files(File(rootDir, "tools/controlsfx-12.0.0-SNAPSHOT.jar")))
    implementation(group = "org.kordamp.ikonli", name = "ikonli-materialdesign-pack", version = "11.3.4")
}