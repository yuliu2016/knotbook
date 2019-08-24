plugins {
    java
    kotlin("jvm")
}

tasks {
    compileKotlin {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xnew-inference")
            jvmTarget = "1.8"
        }
    }
    compileTestKotlin {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xnew-inference")
            jvmTarget = "1.8"
        }
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = "1.3.0-RC2")
//    implementation(group = "io.ktor", name = "ktor-server-core", version = "1.2.3")
//    implementation(group = "io.ktor", name = "ktor-server-netty", version = "1.2.3")
}