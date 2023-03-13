plugins {
    id("com.zeyadgasser.gradle.plugins.android-module-plugin") version "1.0.5"
    id("de.mannodermaus.android-junit5") version "1.8.2.1"
}

android {
    namespace = "com.zeyadgasser.flux"
    defaultConfig {
        versionCode = 1
        versionName = "1.0.0"
        applicationId = "com.zeyadgasser.flux"
    }
}

dependencies {
    implementation(project(":mvi"))
    implementation(project(":mvvm"))
    implementation(project(":shared-composables"))
}
