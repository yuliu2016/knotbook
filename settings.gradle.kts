pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        maven {
            setUrl("https://oss.sonatype.org/content/repositories/snapshots")
        }
    }
}

rootProject.name = "KnotBook"

val submodules = file("modules/").listFiles()
        ?.filter { it.isDirectory }
        ?.map { it.name }
        ?: listOf()

include(*submodules.toTypedArray())

for (descriptor in rootProject.children) {
    val projectName = descriptor.name
    val subModuleName = projectName.split(".").last()

    descriptor.projectDir = file("modules/$projectName")
    descriptor.buildFileName = "$subModuleName.gradle.kts"

    require(descriptor.projectDir.isDirectory) {
        "Project directory ${descriptor.projectDir} for project ${descriptor.name} does not exist."
    }
}