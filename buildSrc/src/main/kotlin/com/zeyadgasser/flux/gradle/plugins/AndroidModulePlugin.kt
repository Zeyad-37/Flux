package com.zeyadgasser.flux.gradle

//import com.android.build.gradle.AppExtension
import com.zeyadgasser.flux.gradle.plugins.CoveragePlugin
import com.zeyadgasser.flux.gradle.plugins.DetektPlugin
import com.zeyadgasser.flux.gradle.plugins.JUnit5Plugin
import com.zeyadgasser.flux.gradle.plugins.PipelinePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

class AndroidModulePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        with(project) {
            plugins.apply("com.android.library")
            plugins.apply("kotlin-android")
            plugins.apply("kotlin-android-extensions")
            plugins.apply("kotlin-parcelize")
            plugins.apply("kotlin-kapt")
            plugins.apply("com.google.dagger.hilt.android")
            plugins.apply("de.mannodermaus.android-junit5")
            apply<CoveragePlugin>()
            apply<JUnit5Plugin>()
            apply<DetektPlugin>()
            apply<PipelinePlugin>()

//            project.extensions.configure<AppExtension>("android") {
//                compileSdkVersion(AndroidConfig.COMPILE_SDK_VERSION)
//                buildToolsVersion(AndroidVersions.buildToolsVersion)
//                defaultConfig {
//                    applicationId = AndroidConfig.APPLICATION_ID
//                    minSdkVersion(AndroidConfig.MIN_SDK_VERSION)
//                    targetSdkVersion(AndroidConfig.TARGET_SDK_VERSION)
//                    versionCode = AndroidConfig.VERSION_CODE
//                    versionName = AndroidConfig.VERSION_NAME
//                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//                }
//                buildTypes {
//                    getByName("debug") {
//                        isDebuggable = true
//                    }
//                    getByName("release") {
//                        isMinifyEnabled = true
//                        proguardFiles(
//                            getDefaultProguardFile("proguard-android.txt"),
//                            "proguard-rules.pro"
//                        )
//                    }
//                }
//
//                compileOptions {
//                    sourceCompatibility = JavaVersion.VERSION_1_8
//                    targetCompatibility = JavaVersion.VERSION_1_8
//                }
//
//                testOptions {
//                    unitTests.isReturnDefaultValues = true
//                }
//
//                packagingOptions {
//                    exclude("META-INF/DEPENDENCIES")
//                    exclude("META-INF/NOTICE")
//                    exclude("META-INF/LICENSE")
//                }
//            }
        }
    }
}

object AndroidVersions {
    const val compileSdkVersion = 31
    const val buildToolsVersion = "31.0.0"
    const val minSdkVersion = 21
    const val targetSdkVersion = 31
}

object AndroidXDependencies {
    const val appCompat = "androidx.appcompat:appcompat:1.4.1"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.1.1"
    const val material = "com.google.android.material:material:1.5.0"
    const val junit = "androidx.test.ext:junit:1.1.3"
    const val espressoCore = "androidx.test.espresso:espresso-core:3.4.0"
}