import org.javamodularity.moduleplugin.tasks.ModularJavaExec

plugins {
    java
    id("org.javamodularity.moduleplugin")
}

dependencies {
    implementation(project(":kb.application"))
}


tasks.register("foo", ModularJavaExec::class.java) {
    main = "kb.abc/kb.abc.Main"
    group = "Demo Foo"
    description = "Hello World"
}