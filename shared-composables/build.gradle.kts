plugins {
    id("com.zeyadgasser.gradle.plugins.android-module-plugin") version "1.0.5"
    id("de.mannodermaus.android-junit5") version "1.8.2.1"
}

android {
    namespace = "com.zeyadgasser.composables"

    testOptions.unitTests.isIncludeAndroidResources = true
}

dependencies {
    implementation(project(":domain"))
    testImplementation("org.robolectric:robolectric:4.9")
    testImplementation("androidx.compose.ui:ui-test-junit4:1.4.1")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.4.1")
}
