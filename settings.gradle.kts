rootProject.name = "knotbook"
val modulesFiles = file("modules/").listFiles()
        ?.filter { it.isDirectory && it.name.startsWith("kb") }
val submodules = modulesFiles?.map { it.name } ?: listOf()
include(*submodules.toTypedArray())

for (descriptor in rootProject.children) {
    val projectName = descriptor.name
    descriptor.projectDir = file("modules/$projectName")
    val subModuleName = projectName.split(".").last()
    descriptor.buildFileName = "$subModuleName.gradle.kts"
    require(descriptor.projectDir.isDirectory) {
        "Project directory ${descriptor.projectDir} for project ${descriptor.name} does not exist."
    }
}