plugins {
    id("com.zeyadgasser.gradle.plugins.android-module-plugin") version "1.0.6"
    id("de.mannodermaus.android-junit5") version "1.9.3.0"
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
