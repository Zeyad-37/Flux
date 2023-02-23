// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "7.4.0" apply false
    id("com.android.library") version "7.4.0" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    id("com.google.dagger.hilt.android") version "2.44" apply false

    id("java-gradle-plugin")
    id("maven-publish")
    id("com.gradle.plugin-publish") version "0.16.0"
    id("org.jetbrains.kotlin.jvm") version "1.8.0" apply false
}

pluginBundle {
    website = "<substitute your project website>"
    vcsUrl = "<uri to project source repository>"
    tags = listOf("tags", "for", "your", "plugins")
}