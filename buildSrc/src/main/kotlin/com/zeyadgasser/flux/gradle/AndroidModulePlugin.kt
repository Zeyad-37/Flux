package com.zeyadgasser.flux.gradle

import com.android.build.gradle.AppExtension
import com.android.builder.core.DefaultApiVersion
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

class AndroidModulePlugin : Plugin<Project> {

    override fun apply(project: Project) = with(project) {
        val isAppModule = path.split("/").last() == "app"
        if (isAppModule) {
            plugins.apply("com.android.application")
        } else {
            plugins.apply("com.android.library")
        }
        plugins.apply("kotlin-android")
        plugins.apply("kotlin-parcelize")
        plugins.apply("kotlin-kapt")
        plugins.apply("com.google.dagger.hilt.android")
        apply<TestingPlugin>()
        apply<CoveragePlugin>()
        apply<DetektPlugin>()
        apply<PipelinePlugin>()

        with(android()) {
            compileSdkVersion(AndroidVersions.compileSdkVersion)
            buildToolsVersion(AndroidVersions.buildToolsVersion)
            defaultConfig {
                if (isAppModule) applicationId = AndroidConfig.appId
                minSdkVersion = DefaultApiVersion(AndroidVersions.minSdkVersion)
                targetSdkVersion = DefaultApiVersion(AndroidVersions.targetSdkVersion)
                versionCode = AndroidConfig.versionCode
                versionName = AndroidConfig.versionName
            }
            buildFeatures.compose = true
            composeOptions {
                kotlinCompilerExtensionVersion = "1.4.2"
            }
            buildTypes {
                getByName("debug") {
                    isDebuggable = true
                }
                getByName("release") {
                    isMinifyEnabled = true
                    proguardFiles(
                        getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro"
                    )
                }
            }
            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_1_8
                targetCompatibility = JavaVersion.VERSION_1_8
            }
            testOptions {
                unitTests.isReturnDefaultValues = true
            }
            packagingOptions {
                exclude("META-INF/DEPENDENCIES")
                exclude("META-INF/NOTICE")
                exclude("META-INF/LICENSE")
            }
        }

        setupIfApplication()
        addDependencies()
    }

    private fun Project.addDependencies() {
        dependencies.add("implementation", "androidx.core:core-ktx:1.9.0")
        dependencies.add("implementation", "androidx.appcompat:appcompat:1.6.1")
        dependencies.add("implementation", "com.google.android.material:material:1.8.0")
        hilt()
        navigate()
        compose()
        lifecycle()
    }

    private fun Project.lifecycle() {
        dependencies.add(
            "implementation",
            "androidx.lifecycle:lifecycle-viewmodel-compose:${DepVersions.lifecycle_version}"
        )
        dependencies.add(
            "implementation",
            "androidx.lifecycle:lifecycle-viewmodel-ktx:${DepVersions.lifecycle_version}"
        )
        dependencies.add(
            "implementation",
            "androidx.lifecycle:lifecycle-runtime-ktx:${DepVersions.lifecycle_version}"
        )
    }

    private fun Project.compose() {
        dependencies.add(
            "debugImplementation",
            "androidx.compose.ui:ui-tooling:${DepVersions.compose_ui_version}"
        )
        dependencies.add(
            "debugImplementation",
            "androidx.compose.ui:ui-test-manifest:${DepVersions.compose_ui_version}"
        )
        dependencies.add("implementation", "androidx.activity:activity-compose:1.6.1")
        dependencies.add(
            "implementation",
            "androidx.compose.ui:ui:${DepVersions.compose_ui_version}"
        )
        dependencies.add(
            "implementation",
            "androidx.compose.ui:ui-tooling-preview:${DepVersions.compose_ui_version}"
        )
        dependencies.add(
            "implementation",
            "androidx.compose.ui:ui-viewbinding:${DepVersions.compose_ui_version}"
        )
        dependencies.add(
            "implementation",
            "androidx.compose.runtime:runtime:${DepVersions.compose_ui_version}"
        )
        dependencies.add("implementation", "androidx.compose.foundation:foundation:1.3.1")
        dependencies.add("implementation", "androidx.compose.material:material:1.3.1")
    }

    private fun Project.navigate() {
        dependencies.add(
            "implementation",
            "androidx.navigation:navigation-compose:${DepVersions.nav_version}"
        )
        dependencies.add(
            "implementation",
            "androidx.navigation:navigation-fragment:${DepVersions.nav_version}"
        )
        dependencies.add(
            "implementation",
            "androidx.navigation:navigation-fragment-ktx:${DepVersions.nav_version}"
        )
        dependencies.add(
            "implementation",
            "androidx.navigation:navigation-ui:${DepVersions.nav_version}"
        )
        dependencies.add(
            "implementation",
            "androidx.navigation:navigation-ui-ktx:${DepVersions.nav_version}"
        )
        dependencies.add("implementation", "androidx.hilt:hilt-navigation-compose:1.1.0-alpha01")
    }

    private fun Project.hilt() {
        dependencies.add(
            "implementation",
            "com.google.dagger:hilt-android:${DepVersions.hilt_version}"
        )
        dependencies.add(
            "kapt",
            "com.google.dagger:hilt-compiler:${DepVersions.hilt_version}"
        )
    }

    private fun Project.setupIfApplication() =
        plugins.withId("com.android.application") { configure<AppExtension> { ensureObfuscations() } }

    private fun AppExtension.ensureObfuscations() {
        applicationVariants.configureEach variant@{
            assembleProvider.configure {
                doLast {
                    check(buildType.isDebuggable || buildType.isMinifyEnabled) {
                        "The build '$${this@variant.name}' is not minified. It must not be released!"
                    }
                }
            }
        }
    }
}
