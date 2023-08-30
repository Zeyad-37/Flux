plugins {
    id("com.zeyadgasser.gradle.plugins.android-module-plugin") version "1.1.7"
    id("de.mannodermaus.android-junit5") version "1.9.3.0"
}

android {
    namespace = "com.zeyadgasser.domain"
    kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
}

dependencies {
    implementation(project(":core"))
    api(project(":domain-pure"))
}
