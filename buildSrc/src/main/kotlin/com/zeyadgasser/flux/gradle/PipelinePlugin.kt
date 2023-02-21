package com.zeyadgasser.flux.gradle

//import asDocument
//import com.android.build.gradle.LibraryExtension
//import evaluate
//import io.gitlab.arturbosch.detekt.Detekt
//import io.gitlab.arturbosch.detekt.DetektPlugin
//import mergeWith
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.kotlin.dsl.configure
import org.w3c.dom.Document
//import writeTo
import java.io.File

class PipelinePlugin : Plugin<Project> {

    companion object {
        private const val TASK_GROUP = "Pipeline"
    }

    override fun apply(target: Project) = with(target) {

//        addMergeReportsTask(
//            taskName = "mergeLintReports",
//            outFilename = "merged-lint-results.xml"
//        ) {
//            description = "Merges the each individual module Lint report into a single one"
//
//            inputs.files(
//                fileTree(buildDir)
//                    .include("reports/lint-results*.xml")
//            )
//
//            dependsOn("${target.path}:lintRelease")
//        }

//        addMergeReportsTask(
//            taskName = "mergeDetektReports",
//            outFilename = "merged-detekt-results.xml",
//            fileParser = { checkStyleFileParser(it) }
//        ) {
//            description = "Merges the each individual module KLint report into a single one"
//            plugins.withType(DetektPlugin::class.java) {
//                val detekt = target.tasks.getByName("detekt") as Detekt
//
//                inputs.files(fileTree(buildDir).include("reports/detekt/**.xml"))
//                dependsOn(detekt)
//            }
//        }

        plugins.withId("com.android.library") {
//            configure<LibraryExtension> {
//                // for variants with flavors, provide a helper "lint${buildType}" task
//                libraryVariants.configureEach {
//                    if (productFlavors.isNotEmpty()) {
//                        tasks.maybeCreate("lint${buildType.name.capitalize()}").apply {
//                            group = JavaBasePlugin.VERIFICATION_GROUP
//                            description = "Runs lint on all variants of ${buildType.name} build type"
//
//                            dependsOn("lint${this@configureEach.name.capitalize()}")
//                        }
//                    }
//                }
//            }
        }

    }

    private fun Project.addMergeReportsTask(
        taskName: String,
        outFilename: String,
        fileParser: (File) -> Document,// = { it.asDocument() },
        onCreate: (Task.() -> Unit)? = null,
        configure: Task.() -> Unit
    ) {
        // we create just 1 task at root project, but it's configured for each project the plugin is applied
        (rootProject.tasks.findByPath(taskName) ?: rootProject.task(taskName) {
            group = TASK_GROUP

            outputs.file(file("${rootProject.buildDir}/reports/$outFilename"))
            onlyIf { !inputs.files.isEmpty }

            doFirst {
                val mergedDoc = inputs.files.asFileTree
                    .map { logger.info("Merging $it…"); it }
                    .map(fileParser)
//                    .reduce { a, b -> a.documentElement.mergeWith(b.documentElement); a }

                val mergedFile = outputs.files.singleFile
                mergedFile.parentFile.mkdirs()
//                mergedFile.outputStream().use { mergedDoc.writeTo(it) }

                println("Wrote XML merged report to ${mergedFile.toURI()}")
            }

            onCreate?.invoke(this)

        }).apply(configure)

    }

    /**
     * CheckStyle reports has a module-relative file reference, that we need to replace with a root module based one
     */
    private fun Project.checkStyleFileParser(file: File) = Unit
//        file.asDocument().apply {
//        evaluate("//file[@name]").forEach {
//            val nameAttr = it.attributes.getNamedItem("name")
//            val rootBasedFile = file(nameAttr.textContent).relativeTo(rootDir)
//
//            nameAttr.textContent = rootBasedFile.path
//        }
//    }
}
