import org.javamodularity.moduleplugin.tasks.ModularJavaExec

plugins {
    java
    id("org.javamodularity.moduleplugin")
}

dependencies {
    implementation(project(":kb.core.application"))
}


tasks.register("run", ModularJavaExec::class.java) {
    main = "kb.abc/kb.abc.Main"
    group = "abc"
    description = "Hello World"
}