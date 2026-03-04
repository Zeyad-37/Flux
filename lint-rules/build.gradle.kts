plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.0")

    compileOnly("com.android.tools.lint:lint-api:32.0.1")

    testImplementation("com.android.tools.lint:lint-tests:32.0.1")
    testImplementation("junit:junit:4.13.2")
}