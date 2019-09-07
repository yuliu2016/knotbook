plugins {
    java
    kotlin("jvm")
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

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation(group = "org.apache.commons", name = "commons-csv", version = "1.6")
    implementation(group = "com.beust", name = "klaxon", version = "5.0.12")

    testImplementation(kotlin("test"))
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-api", version = "5.5.1")
    testRuntimeOnly(group = "org.junit.jupiter", name = "junit-jupiter-engine", version = "5.5.1")
    testRuntimeOnly(group = "org.junit.platform", name = "junit-platform-launcher", version = "1.5.1")
}