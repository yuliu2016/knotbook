import org.javamodularity.moduleplugin.extensions.TestModuleOptions

plugins {
    java
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
    implementation(group = "org.json", name = "json", version = "20190722")
    implementation(group = "org.kordamp.ikonli", name = "ikonli-core", version = "11.3.4")

    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-api", version = "5.5.1")
    testRuntimeOnly(group = "org.junit.jupiter", name = "junit-jupiter-engine", version = "5.5.1")
    testRuntimeOnly(group = "org.junit.platform", name = "junit-platform-launcher", version = "1.5.1")
}