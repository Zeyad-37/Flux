package com.zeyadgasser.flux.gradle

//import com.android.build.gradle.LibraryExtension
//import com.android.build.gradle.TestedExtension
//import com.android.build.gradle.api.BaseVariant
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.compile.AbstractCompile
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.*
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoReport
//import taskAlias
import java.util.Locale

// based on https://raw.githubusercontent.com/gmazzo/glovo-challenge-mobile/master/client/jacoco.gradle
class CoveragePlugin : Plugin<Project> {

    companion object {
        const val TASK_GROUP = "Reporting"
    }

    override fun apply(project: Project) = with(project) {
        val extension = extensions.create("coverage", CoveragePluginExtension::class.java)

        apply(plugin = "kotlin-android")
        apply(plugin = "jacoco")

        // variants are lazily initialized, forcing a dependency here sometime causes task not found
//        afterEvaluate { taskAlias("jacocoTestReport", "jacocoDebugUnitTestReport") }

        fun <Task : AbstractCompile> Provider<Task>.filtered() = map {
            it.destinationDirectory.asFileTree.matching {
                include(extension.includes)
                exclude(extension.excludes)
            }
        }

//        configure<TestedExtension> {
//            testOptions.unitTests {
//                isIncludeAndroidResources = true
//
//                all {
//                    it.configure<JacocoTaskExtension> {
//                        isIncludeNoLocationClasses = true
//                        excludes = listOf("jdk.internal.*")
//                    }
//                }
//            }

//            unitTestVariants.configureEach {
//                val capitalizedName = name.capitalize(Locale.ROOT)
//                val variant = testedVariant as BaseVariant
//                val capitalizedVariantName = variant.name.capitalize(Locale.ROOT)
//
//                val reportTask = tasks.register<JacocoReport>("jacoco${capitalizedName}Report") {
//                    group = TASK_GROUP
//                    description =
//                        "Generates Jacoco unit tests coverage reports on the $capitalizedVariantName build"
//
//                    reports {
//                        xml.required.set(true)
//                        html.required.set(true)
//                    }
//
//                    variant.sourceSets.forEach {
//                        sourceDirectories.from(it.javaDirectories)
//                        sourceDirectories.from(it.kotlinDirectories)
//                    }
//
//                    classDirectories.from(variant.javaCompileProvider.filtered())
//
//                    doLast {
//                        println("Wrote HTML coverage report to ${reports.html.outputLocation.file("index.html").get()}")
//                        println("Wrote XML coverage report to ${reports.xml.outputLocation.get()}")
//                    }
//                }
//
//                // delays the wiring with other tasks due creation order of AGP
//                afterEvaluate {
//                    val testTask = tasks.named<Test>("test$capitalizedName")
//                    val kotlinCompileTask =
//                        tasks.named<AbstractCompile>("compile${capitalizedVariantName}Kotlin")
//
//                    reportTask.configure {
//                        dependsOn(testTask)
//
//                        classDirectories.from(kotlinCompileTask.filtered())
//
//                        executionData.from(testTask.map {
//                            it.the<JacocoTaskExtension>().destinationFile!!
//                        })
//                    }
//                }
//            }

//            if (this is LibraryExtension) {
//                // for variants with flavors, provide a helper "jacoco${buildType}UnitTestReport" task
//                unitTestVariants.configureEach {
//                    if (productFlavors.isNotEmpty()) {
//                        tasks.maybeCreate("jacoco${buildType.name.capitalize(Locale.ROOT)}UnitTestReport")
//                            .apply {
//                                group = TASK_GROUP
//                                description =
//                                    "Generates Jacoco unit tests coverage reports for all variants of ${buildType.name} build type"
//
//                                dependsOn("jacoco${this@configureEach.name.capitalize(Locale.ROOT)}Report")
//                            }
//                    }
//                }
//            }
        }
    }
