plugins {
    java
}


dependencies {
    implementation(group = "org.json", name = "json", version = "20190722")
    implementation(project(":service-abc"))
    implementation(project(":kb.service.api"))

    runtimeOnly(project(":kb.core.view"))
    runtimeOnly(project(":kb.core.code"))
    runtimeOnly(project(":camera-fx"))
    runtimeOnly(project(":bowline"))
    runtimeOnly(project(":connect-four"))
    runtimeOnly(project(":thebluealliance-provider"))
    runtimeOnly(project(":path-planner"))
}