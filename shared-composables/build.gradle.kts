plugins {
    id("com.zeyadgasser.gradle.plugins.android-module-plugin") version "1.1.8"
    id("de.mannodermaus.android-junit5") version "1.9.3.0"
    id("app.cash.paparazzi") version ("1.2.0")
}

android {
    namespace = "com.zeyadgasser.composables"
    defaultConfig.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    testOptions.unitTests.isIncludeAndroidResources = true
    kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
}

dependencies {
    implementation(project(":domain"))
    testImplementation("androidx.compose.ui:ui-test-junit4:1.7.6")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.7.6")
}
