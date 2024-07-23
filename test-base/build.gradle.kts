plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("de.mannodermaus.android-junit5") version "1.9.3.0"
}

android {
    namespace = "com.zeyadgasser.test_base"
    compileSdk = 34
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
    implementation(project(":core"))
    val junit5Version = "5.10.2"
    implementation("org.junit.jupiter:junit-jupiter-api:$junit5Version")
    runtimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit5Version")
    runtimeOnly("org.junit.vintage:junit-vintage-engine:$junit5Version")
    implementation("junit:junit:4.13.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    implementation("androidx.test:runner:1.6.1")
    implementation("com.google.dagger:hilt-android-testing:2.47")
    implementation("app.cash.turbine:turbine:1.0.0")
}
