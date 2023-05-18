package com.zeyadgasser.flux.gradle

import org.gradle.api.Project
import java.io.ByteArrayOutputStream
import java.io.File

fun getUncomittedFiles(project: Project): List<String> {
    val stdout = ByteArrayOutputStream()
    project.exec {
        workingDir = project.rootProject.projectDir
        commandLine("git", "status", "--porcelain")
        standardOutput = stdout
    }
    return stdout.toString().trim().split('\n')
}

fun getChangedModules(project: Project): Set<String> =
    getUncomittedFiles(project).map { getModuleName(it.split(" ").last()) }.toHashSet()

fun getChangedFiles(project: Project): List<String> = getUncomittedFiles(project)
    .filter { file -> file.split(" ").last().endsWith(".kt") }
    .map { removePrefix(it) }

val editorConfRules: () -> Map<String, String> = {
    val map = HashMap<String, String>()
    File(".editorconfig").readText(Charsets.UTF_8).split("\n").filter { it.contains("=") }
        .map { it.split("=").let { map[it[0].trim()] = it[1].trim() } }
    map
}

private fun getModuleName(file: String): String = when {
    file.startsWith("feature") || file.startsWith("foundation") || file.startsWith("api") ->
        file.split("/")[1]// feature/delivery/src/main/.. -> delivery
    file.indexOf("/") > 0 -> file.substring(0, file.indexOf("/") + 1)// app/src/main/.. -> app
    else -> file // example: build.gradle
}

private fun removePrefix(file: String): String =
    if (file.startsWith("feature") || file.startsWith("foundation") || file.startsWith("api")) {
        /* feature/delivery/src/main/.. -> src/main/.. */
        val secondOccurrence = file.indexOf("/", file.indexOf("/") + 1)
        file.substring(secondOccurrence + 1)
    } else if (file.indexOf("/") > 0) {
        /* app/src/main/.. -> src/main/.. */
        file.substring(file.indexOf("/") + 1)
    } else file
