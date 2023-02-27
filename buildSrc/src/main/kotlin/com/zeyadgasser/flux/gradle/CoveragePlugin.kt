package com.zeyadgasser.flux.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.compile.AbstractCompile
import org.gradle.kotlin.dsl.apply

class CoveragePlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        val extension = extensions.create("coverage", CoveragePluginExtension::class.java)
        apply(plugin = "org.jetbrains.kotlinx.kover")

        if (plugins.hasPlugin("kotlin-android")) {
            with(android()) {
                testOptions.unitTests.all {
                    if (it.name == "testDebugUnitTest") {
//                        it.extensions.configure(kotlinx.kover.api.KoverTaskExtension::class) {
//                            // set to true to disable instrumentation of this task,
//                            // Kover reports will not depend on the results of its execution
//                            isDisabled.set(false)
//                            // set file name of binary report
//                            reportFile.set(file("$buildDir/custom/debug-report.bin"))
//                            // for details, see "Instrumentation inclusion rules" below
//                            includes.addAll(listOf("com.example.*"))
//                            // for details, see "Instrumentation exclusion rules" below
//                            excludes.addAll(listOf("com.example.subpackage.*"))
//                        }
                    }
                }
            }
        }

        fun <Task : AbstractCompile> Provider<Task>.filtered() = map {
            it.destinationDirectory.asFileTree.matching {
                include(extension.includes)
                exclude(extension.excludes)
            }
        }
    }
}
