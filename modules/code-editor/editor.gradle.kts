plugins {
    java
}

dependencies {
    implementation(project(":service-api"))
    implementation(group = "com.fifesoft", name = "rsyntaxtextarea", version = "3.0.3")
}