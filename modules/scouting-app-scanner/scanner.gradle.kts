plugins {
    kotlin("jvm")
    id("org.openjfx.javafxplugin")
}

dependencies {
    implementation(project(":service-api"))
    implementation(project(":camera-fx"))
    implementation(project(":fx-utils"))
    implementation(project(":scoutingapp-api"))
}