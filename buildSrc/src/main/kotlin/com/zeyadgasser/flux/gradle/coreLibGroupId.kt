@file:Suppress("unused")

package com.zeyadgasser.flux.gradle
//
//import com.android.build.api.dsl.CommonExtension
//import com.android.build.api.variant.AndroidComponentsExtension
//import com.android.build.gradle.AppExtension
//import com.android.build.gradle.BaseExtension
//import com.android.build.gradle.LibraryExtension
//import com.android.build.gradle.TestExtension
//import com.android.build.gradle.api.BaseVariant
//import org.gradle.api.DomainObjectSet
//import org.gradle.api.Project
//import org.gradle.api.artifacts.dsl.DependencyHandler
//import org.gradle.kotlin.dsl.getByName

//const val coreLibGroupId = "com.glovoapp.android.corelib"
//const val coreLibVersion = CoreLibVersions.artifactVersion
//const val corelibPluginsArtifactId = "corelib-plugins"

//val BaseExtension.variants: DomainObjectSet<out BaseVariant>
//    get() = when (this) {
//        is AppExtension -> applicationVariants
//        is LibraryExtension -> libraryVariants
//        is TestExtension -> applicationVariants
//        else -> error("unsupported module type: $this")
//    }

//val Project.android
//    get() = extensions.getByName<CommonExtension<*, *, *, *>>("android")
//val Project.androidComponents
//    get() = extensions.getByName<AndroidComponentsExtension<*, *, *>>("androidComponents")

