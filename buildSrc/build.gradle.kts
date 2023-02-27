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

//tasks.withType<Detekt>().configureEach {
//    reports {
//        html.required.set(true) // observe findings in your browser with structure and code snippets
//        xml.required.set(true) // checkstyle like format mainly for integrations like Jenkins
//        txt.required.set(true) // similar to the console output, contains issue signature to manually edit baseline files
//        sarif.required.set(true) // standardized SARIF format (https://sarifweb.azurewebsites.net/) to support integrations with Github Code Scanning
//        md.required.set(true) // simple Markdown format
//    }
//}
//
//tasks.withType<Detekt>().configureEach {
//    jvmTarget = "1.8"
//}
//tasks.withType<DetektCreateBaselineTask>().configureEach {
//    jvmTarget = "1.8"
//}

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
