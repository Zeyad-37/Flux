package com.zeyadgasser.flux.gradle

object AndroidVersions {
    const val compileSdkVersion = 33
    const val buildToolsVersion = "33.0.1"
    const val minSdkVersion = 24
    const val targetSdkVersion = 33
}

object AndroidConfig {
    const val appId = "com.zeyadgasser.platform"
    const val versionCode = 1
    const val versionName = "1.0.0"
    const val testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" // de.mannodermaus.junit5.AndroidJUnit5Builder
}

object DepVersions {
    const val lifecycle_version = "2.5.1"
    const val nav_version = "2.5.3"
    const val junit5_version = "5.8.2"
    const val hilt_version = "2.44.2"
    const val compose_ui_version = "1.3.3"
}