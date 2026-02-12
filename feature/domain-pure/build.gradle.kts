import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.test {
    useJUnitPlatform {
        // includeTags("fast", "smoke & feature-a")
        // excludeTags("slow", "ci")
        includeEngines("junit-jupiter")
        // excludeEngines("junit-vintage")
    }
    testLogging { events("passed", "skipped", "failed") }
}

tasks.withType<JavaCompile>().configureEach { options.release.set(JavaVersion.VERSION_17.toString().toInt()) }

tasks.withType<KotlinCompile>().configureEach { kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString() }

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.23")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher") {
        because("Only needed to run tests in a version of IntelliJ IDEA that bundles older versions")
    }
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("app.cash.turbine:turbine:1.0.0")
    testImplementation("org.mockito:mockito-core:5.6.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
}
