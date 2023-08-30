plugins {
    id("com.zeyadgasser.gradle.plugins.android-module-plugin") version "1.1.7"
    id("de.mannodermaus.android-junit5") version "1.9.3.0"
}

android {
    namespace = "com.zeyadgasser.flux"
    defaultConfig {
        versionCode = 1
        versionName = "1.0.0"
        applicationId = "com.zeyadgasser.flux"
    }
    kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
    buildTypes {
        create("benchmark") {
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
            isDebuggable = false
        }
    }
}

dependencies {
    implementation(project(":mvi"))
    implementation(project(":mvvm"))
    implementation(project(":shared-composables"))
    implementation("androidx.metrics:metrics-performance:1.0.0-alpha04")
    implementation("androidx.tracing:tracing:1.1.0")
    implementation("androidx.tracing:tracing-ktx:1.1.0")
}
