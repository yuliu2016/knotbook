import org.javamodularity.moduleplugin.extensions.TestModuleOptions

plugins {
    kotlin("jvm")
    id("org.openjfx.javafxplugin")
}

tasks.withType<Test> {
    useJUnitPlatform {
    }
}

javafx {
    modules = listOf("javafx.controls")
}

tasks {
    test {
        extensions.configure(TestModuleOptions::class.java) {
            runOnClasspath = true
        }
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":kb.service.api"))

    testImplementation(kotlin("test"))
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-api", version = "5.5.1")
    testRuntimeOnly(group = "org.junit.jupiter", name = "junit-jupiter-engine", version = "5.5.1")
    testRuntimeOnly(group = "org.junit.platform", name = "junit-platform-launcher", version = "1.5.1")
}