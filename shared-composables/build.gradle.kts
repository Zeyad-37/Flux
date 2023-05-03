plugins {
    id("com.zeyadgasser.gradle.plugins.android-module-plugin") version "1.0.5"
    id("de.mannodermaus.android-junit5") version "1.8.2.1"
    id("app.cash.paparazzi") version ("1.2.0")
}

android {
    namespace = "com.zeyadgasser.composables"

    testOptions.unitTests.isIncludeAndroidResources = true
}

dependencies {
    implementation(project(":domain"))
    testImplementation("com.google.testparameterinjector:test-parameter-injector:1.11")
    testImplementation("org.robolectric:robolectric:4.9")
    testImplementation("androidx.compose.ui:ui-test-junit4:1.4.2")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.4.2")
}
