plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("de.mannodermaus.android-junit5") version "1.8.2.1"
}

android {
    namespace = "com.zeyadgasser.test_base"
    compileSdk = 33

    defaultConfig {
        minSdk = 24
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()
    packagingOptions.resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
}

dependencies {
    val junit5Version = "5.9.2"
    implementation("org.junit.jupiter:junit-jupiter-api:$junit5Version")
    runtimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit5Version")
    runtimeOnly("org.junit.vintage:junit-vintage-engine:$junit5Version")
    implementation("junit:junit:4.13.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
    implementation("org.mockito:mockito-core:5.1.1")
    implementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
}
