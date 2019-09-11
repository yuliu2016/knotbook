plugins {
    java
}

dependencies {
    implementation(project(":kb.service.api"))
    implementation(group = "com.fifesoft", name = "rsyntaxtextarea", version = "3.0.3")
    implementation(group = "com.fifesoft", name = "rstaui", version = "3.0.1")
}