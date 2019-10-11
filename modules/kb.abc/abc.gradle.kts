import org.javamodularity.moduleplugin.tasks.ModularJavaExec

plugins {
    java
    id("org.javamodularity.moduleplugin")
}

dependencies {
    implementation(project(":kb.core.application"))
}


tasks.register("run", ModularJavaExec::class.java) {
    group = "abc"
    main = "kb.abc/kb.abc.Main"
}

tasks.register("collectJars", Copy::class.java){
    group = "abc"
    from(configurations.runtimeClasspath) {
        exclude("javafx-*")
    }
    into(File(buildDir, "collected"))
}