plugins {
    java
}


dependencies {
    implementation(group = "org.json", name = "json", version = "20190722")
    implementation(project(":service-abc"))
    implementation(project(":service-api"))

    runtimeOnly(project(":data-view"))
    runtimeOnly(project(":code-editor"))
    runtimeOnly(project(":camera-fx"))
    runtimeOnly(project(":bowline"))
    runtimeOnly(project(":connect-four"))
    runtimeOnly(project(":thebluealliance-provider"))
    runtimeOnly(project(":path-planner"))
}