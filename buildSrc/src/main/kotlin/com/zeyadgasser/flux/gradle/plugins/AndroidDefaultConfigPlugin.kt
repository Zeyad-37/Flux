package com.zeyadgasser.flux.gradle.plugins

import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.the
import org.gradle.kotlin.dsl.withType

class AndroidDefaultConfigPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        apply(plugin = "org.jetbrains.kotlin.android")
        apply(plugin = "org.jetbrains.kotlin.plugin.parcelize")
        apply(plugin = "com.google.dagger.hilt.android")

        apply<CoveragePlugin>()
        apply<JUnit5Plugin>()
        apply<DetektPlugin>()
        apply<PipelinePlugin>()

        setupBuild()
        setupIfApplication()
    }

    private fun Project.setupBuild(
        javaVersion: JavaVersion = JavaVersion.VERSION_1_8
    ) {
//            compileOptions {
//                sourceCompatibility = javaVersion
//                targetCompatibility = javaVersion
//            }
//
//            (this as ExtensionAware).configure<KotlinJvmOptions> {
//                jvmTarget = javaVersion.toString()
//            }
//
//            defaultConfig {
//                multiDexEnabled = true
//                vectorDrawables.useSupportLibrary = true
//                consumerProguardFiles("proguard-rules.pro")
//            }
//
//            buildFeatures.compose = true
    }

    private fun Project.setupIfApplication() = plugins.withId("com.android.application") {
//        configure<AppExtension> {
//            ensureObfuscations()
//        }
    }

    /**
     * Adds an additional check before assembling each releaseable variant.
     *
     * As Google Play won't accept a debuggable APK, we are assuming any non-debuggable build
     * is potentially a releseable build, therefore it should be obfuscated.
     */
//    private fun AppExtension.ensureObfuscations() {
//        applicationVariants.configureEach variant@{
//            assembleProvider.configure {
//                doLast {
//                    check(buildType.isDebuggable || buildType.isMinifyEnabled) {
//                        "The build '$${this@variant.name}' is not minified. It must not be released!"
//                    }
//                }
//            }
//        }
//    }
}
