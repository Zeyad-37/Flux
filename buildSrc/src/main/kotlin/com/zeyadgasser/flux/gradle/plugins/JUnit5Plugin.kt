package com.zeyadgasser.flux.gradle.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test

class JUnit5Plugin : Plugin<Project> {

    override fun apply(project: Project) = with(project) {
        plugins.withId("com.android.application") {
            addDependenciesForAndroid()
            setupTestTask()
        }
        plugins.withId("com.android.library") {
            addDependenciesForAndroid()
            setupTestTask()
        }
    }

    private fun Project.addDependenciesForKotlin() {
        dependencies.add("testImplementation", "org.junit.jupiter:junit-jupiter:5.8.2")
        dependencies.add("testImplementation", "org.junit.jupiter:junit-jupiter:5.8.2")
        dependencies.add("testRuntimeOnly", "org.junit.jupiter:junit-jupiter-params:5.8.2")
        dependencies.add("testRuntimeOnly", "org.junit.vintage:junit-vintage-engine:5.8.2")
    }

    private fun Project.addDependenciesForAndroid() {
        addDependenciesForKotlin()
        dependencies.add("androidTestImplementation", "androidx.test:runner:1.5.2")
        dependencies.add("androidTestImplementation", "org.junit.jupiter:junit-jupiter-api:5.8.2")
        dependencies.add(
            "androidTestImplementation",
            "de.mannodermaus.junit5:android-test-core:1.2.2"
        )
        dependencies.add(
            "androidTestRuntimeOnly",
            "de.mannodermaus.junit5:android-test-runner:1.2.2"
        )
    }

    private fun Project.setupTestTask() {
        // Configure JUnitPlatformOptions to run JUnit 5 tests
        tasks.withType(Test::class.java) {
            useJUnitPlatform()
//                {
//                    val options = JUnitPlatformOptions()
//                    options.includeEngines("junit-jupiter")
//                    options(options)
//                }
        }
    }
}