plugins {
    `kotlin-dsl`
}

buildscript {
    repositories {
        mavenCentral()
        google()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.4.1")
        classpath("com.android.tools.build:gradle-api:7.4.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.10")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.44.2")
    }
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    compileOnly("com.android.tools.build:gradle:7.4.1")
//    implementation("com.android.tools.build:gradle:7.4.1")
}

gradlePlugin {
    plugins {
        create("androidModulePlugin") {
            id = "com.zeyadgasser.gradle.android-module-plugin"
            implementationClass = "com.zeyadgasser.flux.gradle.AndroidModulePlugin"
            displayName = "AndroidModulePlugin"
            description = "Plugin for setting up android modules"
        }
    }
}
