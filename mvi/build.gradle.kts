plugins {
    id("com.zeyadgasser.gradle.plugins.android-module-plugin") version "1.1.1"
    id("de.mannodermaus.android-junit5") version "1.9.3.0"
}

android {
    namespace = "com.zeyadgasser.mvi"
    kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
}

dependencies {
    implementation(project(":core"))
    implementation(project(":data"))
    implementation(project(":domain"))
    implementation(project(":shared-composables"))
    testImplementation(project(":test-base"))
}
