plugins {
    id("com.zeyadgasser.gradle.plugins.android-module-plugin") version "1.0.1"
    id("de.mannodermaus.android-junit5") version "1.8.2.1"
}

android {
    namespace = "com.zeyadgasser.flux"
    defaultConfig {
        applicationId = "com.zeyadgasser.flux"
        vectorDrawables.useSupportLibrary = true
    }
    packagingOptions.resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
}

dependencies {
    implementation(project(":mvi"))
    implementation(project(":mvvm"))
    implementation(project(":shared-composables"))
}
