plugins {
    id("com.zeyadgasser.gradle.plugins.android-module-plugin") version "1.0.5"
    id("de.mannodermaus.android-junit5") version "1.8.2.1"
}

android {
    namespace = "com.zeyadgasser.flux.domain"
}

dependencies {
    implementation(project(":core"))
    api(project(":domain-pure"))
}
