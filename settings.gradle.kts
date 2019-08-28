rootProject.name = "knotbook"
val modulesFiles = file("modules/").listFiles()
        ?.filter { it.isDirectory && it.name.startsWith("kb") }
val submodules = modulesFiles?.map { it.name } ?: listOf()
include(*submodules.toTypedArray())

for (project in rootProject.children) {
    val projectDirName = project.name
    project.projectDir = file("modules/$projectDirName")
    require(project.projectDir.isDirectory) {
        "Project directory ${project.projectDir} for project ${project.name} does not exist."
    }
}