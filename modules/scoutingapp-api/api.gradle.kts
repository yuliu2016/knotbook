import org.javamodularity.moduleplugin.extensions.TestModuleOptions

plugins {
    java
    kotlin("jvm")
    id("org.openjfx.javafxplugin")
}

tasks.test {
    extensions.configure(TestModuleOptions::class.java) {
        runOnClasspath = true
    }
    useJUnitPlatform {
    }
}

dependencies {
    implementation(kotlin("stdlib"))

    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-api", version = "5.5.1")
    testRuntimeOnly(group = "org.junit.jupiter", name = "junit-jupiter-engine", version = "5.5.1")
    testRuntimeOnly(group = "org.junit.platform", name = "junit-platform-launcher", version = "1.5.1")
}