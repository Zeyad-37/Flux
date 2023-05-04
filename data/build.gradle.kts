plugins {
    id("com.zeyadgasser.gradle.plugins.android-module-plugin") version "1.0.9"
    id("de.mannodermaus.android-junit5") version "1.9.3.0"
}

android {
    namespace = "com.zeyadgasser.data"
    kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
}

dependencies {
    implementation(project(":domain"))
}
