import org.javamodularity.moduleplugin.tasks.ModularJavaExec

plugins {
    java
    `java-library`
    id("org.javamodularity.moduleplugin")
}

dependencies {
    implementation("org.bytedeco", "cpython-platform", "3.7.3-1.5.1")
}

tasks.register("runCP", ModularJavaExec::class.java) {
    main = "kb.core.cpython/kb.core.cpython.CPyTest"
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_12
}