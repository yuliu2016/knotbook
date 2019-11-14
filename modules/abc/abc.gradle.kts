import org.javamodularity.moduleplugin.tasks.ModularJavaExec

plugins {
    java
    id("org.javamodularity.moduleplugin")
}

dependencies {
    implementation(project(":service-abc"))
    runtimeOnly(project(":application"))
}


tasks.register("run", ModularJavaExec::class.java) {
    group = "abc"
    main = "kb.abc/kb.abc.Main"
    jvmArgs = listOf(
            "--add-exports=javafx.controls/com.sun.javafx.scene.control.behavior=org.controlsfx.controls",
            "--add-exports=javafx.base/com.sun.javafx.event=org.controlsfx.controls",
            // For accessing VirtualFlow field from the base class in GridViewSkin
            "--add-opens=javafx.controls/javafx.scene.control.skin=org.controlsfx.controls",
            // For accessing getChildren in ImplUtils
            "--add-opens=javafx.graphics/javafx.scene=org.controlsfx.controls"
    )
    args = listOf("debug")
}

tasks.register("cleanJars", Delete::class.java) {
    dependsOn("jar")
    delete(File(buildDir, "collected"))
}

tasks.register("collectJars", Copy::class.java){
    group = "abc"
    dependsOn("jar", "cleanJars")
    from(configurations.runtimeClasspath) {
        exclude("javafx-*", "kotlin-*", "controlsfx-*", "rsyntaxtextarea-*")
    }
    from(File(buildDir, "libs"))
    into(File(buildDir, "collected"))
}