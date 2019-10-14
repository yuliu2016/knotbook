plugins {
    java
    kotlin("jvm")
}


dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":kb.service.abc"))
    implementation(project(":kb.service.api"))

    runtimeOnly(project(":kb.core.view"))
    runtimeOnly(project(":kb.core.code"))
    runtimeOnly(project(":kb.core.camera.fx"))
    runtimeOnly(project(":kb.core.server"))
    runtimeOnly(project(":kb.core.bowline"))
    runtimeOnly(project(":kb.tool.cng"))
    runtimeOnly(project(":kb.tool.path.planner"))
    runtimeOnly(project(":kb.tba.client"))
}