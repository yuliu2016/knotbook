plugins {
    kotlin("jvm")
    id("org.openjfx.javafxplugin")
}

tasks {
    compileKotlin {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xnew-inference")
            jvmTarget = "11"
        }
    }
    compileTestKotlin {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xnew-inference")
            jvmTarget = "11"
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform {
    }
}

javafx {
    modules = listOf("javafx.controls")
}

dependencies {
    implementation(project(":core:utils"))
    implementation(kotlin("stdlib"))

    testImplementation(kotlin("test"))
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-api", version = "5.5.1")
    testRuntimeOnly(group = "org.junit.jupiter", name = "junit-jupiter-engine", version = "5.5.1")
    testRuntime(group = "org.junit.platform", name = "junit-platform-launcher", version = "1.5.1")
}