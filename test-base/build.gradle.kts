plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("de.mannodermaus.android-junit5") version "1.9.3.0"
}

android {
    namespace = "com.zeyadgasser.test_base"
    compileSdk = 33
    defaultConfig.minSdk = 24

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions.sourceCompatibility = JavaVersion.VERSION_17
    compileOptions.targetCompatibility = JavaVersion.VERSION_17
    kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
    packagingOptions.resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
}

dependencies {
    val junit5Version = "5.9.2"
    implementation("org.junit.jupiter:junit-jupiter-api:$junit5Version")
    runtimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit5Version")
    runtimeOnly("org.junit.vintage:junit-vintage-engine:$junit5Version")
    implementation("junit:junit:4.13.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
    implementation("androidx.test:runner:1.5.2")
    implementation("com.google.dagger:hilt-android-testing:2.46")
}
